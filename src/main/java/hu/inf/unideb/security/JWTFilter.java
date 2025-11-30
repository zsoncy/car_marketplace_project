package hu.inf.unideb.security;

import hu.inf.unideb.service.CustomUserDetailService;
import hu.inf.unideb.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    ApplicationContext context;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader !=null && authHeader.startsWith("Bearer ")){
            token = jwtService.extractTokenFromRequest(request);
            username = jwtService.getUsernameFromToken(token);
        }
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null &&
                !jwtService.getBlackList().contains(token)){
            UserDetails userDetails = context.getBean(CustomUserDetailService.class).loadUserByUsername(username);
            if (jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}

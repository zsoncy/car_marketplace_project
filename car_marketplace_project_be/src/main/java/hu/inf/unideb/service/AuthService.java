package hu.inf.unideb.service;

import hu.inf.unideb.DTOs.LoginRequestDto;
import hu.inf.unideb.DTOs.LoginResponseDto;
import hu.inf.unideb.DTOs.RegisterRequestDto;
import hu.inf.unideb.DTOs.RegisterResponseDto;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.model.User;
import hu.inf.unideb.repository.UserRepo;
import hu.inf.unideb.security.PasswordEncrypter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncrypter passwordEncrypter;
    private final JWTService jwtService;


    public RegisterResponseDto register(RegisterRequestDto req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncrypter.passwordEncoder().encode(req.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);


        String token = jwtService.generateToken(req.getUsername());

        return RegisterResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .accessToken(token)
                .build();
    }


    public LoginResponseDto login(LoginRequestDto req) {
        User user = userRepo.findByUsername(req.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        if (!passwordEncrypter.passwordEncoder().matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }


        String token = jwtService.generateToken(user.getUsername());

        return LoginResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .accessToken(token)
                .build();
    }

    public String Logout(HttpServletRequest request){
        jwtService.addToBlackList(request);
        return "Successfully logged out";
    }
}

package hu.inf.unideb.controller;

import hu.inf.unideb.DTOs.LoginRequestDto;
import hu.inf.unideb.DTOs.LoginResponseDto;
import hu.inf.unideb.DTOs.RegisterRequestDto;
import hu.inf.unideb.DTOs.RegisterResponseDto;
import hu.inf.unideb.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@NonNull @RequestBody RegisterRequestDto request) {
        var resp = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@NonNull @RequestBody LoginRequestDto request) {
        var resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }

    // egyszerű hibakezelés (finomítható később)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        String m = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
        if (m.contains("taken")) return ResponseEntity.status(409).body(ex.getMessage());
        if (m.contains("invalid") || m.contains("not found"))
            return ResponseEntity.status(401).body(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        var response = authService.Logout(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}

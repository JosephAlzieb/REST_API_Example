package com.example.advanced_rest_api_example.security.controller;

import com.example.advanced_rest_api_example.logging.HasLogger;
import com.example.advanced_rest_api_example.security.model.LoginRequest;
import com.example.advanced_rest_api_example.security.model.RefreshRequest;
import com.example.advanced_rest_api_example.security.model.RegisterRequest;
import com.example.advanced_rest_api_example.security.model.TokenResponse;
import com.example.advanced_rest_api_example.security.model.User;
import com.example.advanced_rest_api_example.security.repository.UserRepository;
import com.example.advanced_rest_api_example.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// web/AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController implements HasLogger {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager, JwtService jwt,
                          UserRepository repo, PasswordEncoder encoder) {
        this.authManager = authManager; this.jwt = jwt; this.repo = repo; this.encoder = encoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login mit Username & Passwort")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user.getUsername());

      getLogger().info("User mit Username {} ist erfolgreich eingeloggt", req.username());
      return ResponseEntity.ok(new TokenResponse(access, refresh, "Bearer"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        String token = req.refreshToken();
        try {
          if (!jwt.isRefreshToken(token)) {
            return ResponseEntity.status(401).build();
          }
            String username = jwt.getUsername(token);
            // User neu laden (kann inzwischen Rollenänderungen haben)
            UserDetails user = new org.springframework.security.core.userdetails.User(
                    repo.findByUsername(username).orElseThrow().getUsername(),
                    "n/a",
                    repo.findByUsername(username).orElseThrow()
                        .getRoles().stream().map(SimpleGrantedAuthority::new).toList()
            );
            String access = jwt.generateAccessToken(user);
            String refresh = jwt.generateRefreshToken(username);
            return ResponseEntity.ok(new TokenResponse(access, refresh, "Bearer"));
        } catch (JwtException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
      if (auth == null) {
        return ResponseEntity.status(401).build();
      }
      Map<String, Object> me = Map.of(
              "username", auth.getName(),
              "roles", auth.getAuthorities().stream()
                      .map(GrantedAuthority::getAuthority).toList()
      );
      return ResponseEntity.ok(me);
    }

    // optional: Registrierung (Demo)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (repo.existsByUsername(req.username())) {
            return ResponseEntity.status(409).body(Map.of("error", "username exists"));
        }
        getLogger().info("Neue Registrierung mit Username {} und Passwort {}", req.username(), req.password());
        User u = new User();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.getRoles().add("ROLE_USER");
        repo.save(u);
        return ResponseEntity.ok().build();
    }
}

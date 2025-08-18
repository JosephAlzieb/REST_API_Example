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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints für Login, Token-Refresh, User-Infos und Registrierung")
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
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login erfolgreich, Token wird zurückgegeben"),
      @ApiResponse(responseCode = "401", description = "Ungültige Zugangsdaten")
  })
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
    @Operation(summary = "Refresh Token", description = "Gibt ein neues Access- und Refresh-Token zurück")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens erfolgreich erneuert"),
        @ApiResponse(responseCode = "401", description = "Ungültiger oder abgelaufener Refresh Token")
    })
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

          getLogger().info("Token für User '{}' erfolgreich erneuert", username);
          return ResponseEntity.ok(new TokenResponse(access, refresh, "Bearer"));
        } catch (JwtException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Eigene User-Daten", description = "Gibt den eingeloggten Benutzer zurück")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User erfolgreich zurückgegeben"),
        @ApiResponse(responseCode = "401", description = "Kein gültiger Token im Header")
    })
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
      if (auth == null) {
        getLogger().warn("Unautorisierter Zugriff auf /me");
        return ResponseEntity.status(401).build();
      }
      Map<String, Object> me = Map.of(
              "username", auth.getName(),
              "roles", auth.getAuthorities().stream()
                      .map(GrantedAuthority::getAuthority).toList()
      );

      getLogger().info("User '{}' ruft /me auf", auth.getName());
      return ResponseEntity.ok(me);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrierung", description = "Registriert einen neuen Benutzer mit Username & Passwort")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User erfolgreich registriert"),
        @ApiResponse(responseCode = "409", description = "Username existiert bereits")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
      getLogger().info("Registrierungsversuch für '{}'", req.username());

      if (repo.existsByUsername(req.username())) {
        getLogger().warn("Registrierung fehlgeschlagen: Username '{}' existiert bereits", req.username());
        return ResponseEntity.status(409).body(Map.of("error", "username exists"));
      }

      User u = new User();
      u.setUsername(req.username());
      u.setPassword(encoder.encode(req.password()));
      u.getRoles().add("ROLE_USER");
      repo.save(u);

      getLogger().info("User '{}' erfolgreich registriert", req.username());
      return ResponseEntity.ok().build();
    }
}

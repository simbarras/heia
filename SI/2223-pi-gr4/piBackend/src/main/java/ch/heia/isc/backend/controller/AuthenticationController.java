package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.PiBackendApplication;
import ch.heia.isc.backend.model.auth.AuthenticationRequest;
import ch.heia.isc.backend.model.auth.AuthenticationResponse;
import ch.heia.isc.backend.model.auth.RegisterRequest;
import ch.heia.isc.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PiBackendApplication.BASE_PATH + "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService service;

    /**
     * Register a new user
     * @param request the request containing the user's email and password
     * @return the response containing the user's email and a JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Authenticate a user
     * @param request the request containing the user's email and password
     * @return the response containing the JWT token
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}

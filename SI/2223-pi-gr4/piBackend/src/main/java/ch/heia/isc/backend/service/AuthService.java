package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.ParticipantEntity;

import ch.heia.isc.backend.model.auth.AuthenticationRequest;
import ch.heia.isc.backend.model.auth.AuthenticationResponse;
import ch.heia.isc.backend.model.auth.RegisterRequest;
import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.repository.UserRepository;
import ch.heia.isc.backend.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user and generate a JWT token. If the user already exists, throw an exception.
     * @param request the request containing the user's email password, firstname and lastname
     * @return the response containing the user's email and a JWT token
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthenticationResponse register(RegisterRequest request) {
        ParticipantEntity participant = new ParticipantEntity();
        // Check if the user already exists
        if (participantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        participant.setEmail(request.getEmail());
        participant.setFirstname(request.getFirstname());
        participant.setLastname(request.getLastname());
        participant.setPassword(passwordEncoder.encode(request.getPassword()));
        participantRepository.save(participant);
        String jwtToken = jwtService.generateToken(buildValues(participant), participant);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Authenticate a user and generate a JWT token
     * @param request the request containing the user's email and password
     * @return the response containing the JWT token
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        System.out.println("Authenticate: " + user.getEmail() + " " + user.getRole());
        String jwtToken = jwtService.generateToken(buildValues(user), user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /** Return the current user
     *
     * @param token the JWT token
     * @return the current user
     */
    public User getCurrentUser(String token) {
        // Remove the "Bearer " prefix
        token = token.substring(7);
        return userRepository.findByEmail(jwtService.extractUsername(token)).orElseThrow();
    }

    /**
     * Build the claims to be stored in the JWT token
     * @param participant the participant
     * @return the values to be stored in the JWT token
     */
    private Map<String, Object> buildValues(ParticipantEntity participant) {
        return buildValues(User.builder()
                .email(participant.getEmail())
                .firstname(participant.getFirstname())
                .lastname(participant.getLastname())
                .password(participant.getPassword())
                .role("participant")
                .build());
    }

    /**
     * Build the claims to be stored in the JWT token
     * @param user the user
     * @return the values to be stored in the JWT token
     */
    private Map<String, Object> buildValues(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("firstName", user.getFirstname());
        values.put("lastName", user.getLastname());
        values.put("role", user.getRole());
        return values;
    }
}

package ch.heia.isc.backend.repository;


import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.repository.AdminRepository;
import ch.heia.isc.backend.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final ParticipantRepository participantRepository;
    private final AdminRepository adminRepository;

    public Optional<User> findByEmail(String email) {
        Optional<User> user = participantRepository.findByEmail(email).map(
                p -> User.builder()
                        .email(p.getEmail())
                        .firstname(p.getFirstname())
                        .lastname(p.getLastname())
                        .password(p.getPassword())
                        .role("participant")
                        .build()
        );
        if (user.isPresent()) {
            return user;
        }
        return adminRepository.findByEmail(email).map(
                a -> User.builder()
                        .email(a.getEmail())
                        .firstname(a.getFirstname())
                        .lastname(a.getLastname())
                        .password(a.getPassword())
                        .role("admin")
                        .build());
    }
}

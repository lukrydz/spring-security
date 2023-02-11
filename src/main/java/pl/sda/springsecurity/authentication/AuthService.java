package pl.sda.springsecurity.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sda.springsecurity.config.JwtService;
import pl.sda.springsecurity.repo.UserRepository;
import pl.sda.springsecurity.user.RoleType;
import pl.sda.springsecurity.user.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(RoleType.ADMIN)
                .build();
        repository.save(user);

        return jwtService.generateToken(user);
    }


    public String authenticate(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        User user = repository.findByUsername(authRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return jwtService.generateToken(user);

    }

}

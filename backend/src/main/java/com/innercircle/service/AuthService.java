package com.innercircle.service;

import com.innercircle.dto.auth.LoginRequest;
import com.innercircle.dto.auth.LoginResponse;
import com.innercircle.dto.auth.RegisterRequest;
import com.innercircle.dto.auth.RegisterResponse;
import com.innercircle.entity.Circle;
import com.innercircle.entity.User;
import com.innercircle.exception.ConflictException;
import com.innercircle.repository.CircleRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CircleRepository circleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       CircleRepository circleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.circleRepository = circleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String handle = request.getHandle().trim().toLowerCase(Locale.ROOT);
        LocalDate dob = request.getDateOfBirth();

        if (!handle.matches("^[a-z0-9_]{3,30}$")) {
            throw new IllegalArgumentException("Handle may contain only letters, numbers, and underscores.");
        }
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 13) {
            throw new IllegalArgumentException("You must be at least 13 years old to create an account.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists.");
        }
        if (userRepository.existsByHandle(handle)) {
            throw new ConflictException("That handle is already taken.");
        }

        Circle circle = circleRepository.findByAge(age)
                .orElseThrow(() -> new IllegalStateException("No age circle is configured for this age."));

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setHandle(handle);
        user.setDateOfBirth(dob);
        user.setCircle(circle);
        user.setBio(null);
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);

        return new RegisterResponse(
                "Registration successful",
                new RegisterResponse.UserSummary(saved.getId(), saved.getHandle(), circle.getName())
        );
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user could not be found."));

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new LoginResponse(
                token,
                "Bearer",
                new LoginResponse.UserSummary(user.getId(), user.getHandle(), user.getCircle().getName())
        );
    }
}

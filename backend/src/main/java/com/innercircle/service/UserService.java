package com.innercircle.service;

import com.innercircle.dto.user.UpdateProfileRequest;
import com.innercircle.dto.user.UserProfileResponse;
import com.innercircle.entity.User;
import com.innercircle.exception.ConflictException;
import com.innercircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        if (request.getHandle() != null) {
            String handle = request.getHandle().trim().toLowerCase(Locale.ROOT);
            if (!handle.matches("^[a-zA-Z0-9_]{3,30}$")) {
                throw new IllegalArgumentException("Handle must be 3-30 characters and contain only letters, numbers, or underscores.");
            }
            if (!handle.equals(user.getHandle()) && userRepository.existsByHandle(handle)) {
                throw new ConflictException("That handle is already taken.");
            }
            user.setHandle(handle);
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }

        return toResponse(userRepository.save(user));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getHandle(),
                user.getBio(),
                user.getCircle().getName(),
                user.getStatus()
        );
    }
}

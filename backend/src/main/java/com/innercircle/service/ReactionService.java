package com.innercircle.service;

import com.innercircle.dto.reaction.ReactionResponse;
import com.innercircle.entity.Post;
import com.innercircle.entity.Reaction;
import com.innercircle.entity.User;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.ReactionRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class ReactionService {
    private static final String I_HEAR_YOU = "I_HEAR_YOU";
    private static final String RELATE = "RELATE";
    private static final String ROOTING_FOR_YOU = "ROOTING_FOR_YOU";

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionService(ReactionRepository reactionRepository,
                           PostRepository postRepository,
                           UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReactionResponse add(UUID userId, UUID postId, String requestedType) {
        String type = normalizeType(requestedType);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // A user can have at most one of each reaction type, matching the DB constraint.
        // Switching reactions is handled by removing the user's other reaction first.
        reactionRepository.findByPost_IdAndUser_IdAndReactionType(postId, userId, type)
                .ifPresent(reactionRepository::delete);

        for (String other : new String[]{I_HEAR_YOU, RELATE, ROOTING_FOR_YOU}) {
            if (!other.equals(type)) {
                reactionRepository.findByPost_IdAndUser_IdAndReactionType(postId, userId, other)
                        .ifPresent(reactionRepository::delete);
            }
        }

        Reaction reaction = new Reaction();
        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setReactionType(type);
        reactionRepository.save(reaction);

        return get(userId, postId);
    }

    @Transactional
    public ReactionResponse remove(UUID userId, UUID postId, String requestedType) {
        String type = normalizeType(requestedType);
        reactionRepository.findByPost_IdAndUser_IdAndReactionType(postId, userId, type)
                .ifPresent(reactionRepository::delete);
        return get(userId, postId);
    }

    @Transactional(readOnly = true)
    public ReactionResponse get(UUID userId, UUID postId) {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put(I_HEAR_YOU, 0L);
        counts.put(RELATE, 0L);
        counts.put(ROOTING_FOR_YOU, 0L);
        for (Object[] row : reactionRepository.countByPostId(postId)) {
            counts.put((String) row[0], ((Number) row[1]).longValue());
        }

        String myReaction = null;
        for (String type : counts.keySet()) {
            if (reactionRepository.findByPost_IdAndUser_IdAndReactionType(postId, userId, type).isPresent()) {
                myReaction = type;
                break;
            }
        }
        return new ReactionResponse(counts, myReaction);
    }

    private String normalizeType(String value) {
        String type = value.trim().toUpperCase(Locale.ROOT);
        if (!type.equals(I_HEAR_YOU) && !type.equals(RELATE) && !type.equals(ROOTING_FOR_YOU)) {
            throw new IllegalArgumentException("Reaction must be I_HEAR_YOU, RELATE, or ROOTING_FOR_YOU.");
        }
        return type;
    }
}

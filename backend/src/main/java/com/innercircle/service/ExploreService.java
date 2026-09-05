package com.innercircle.service;

import com.innercircle.dto.explore.ExplorePostResponse;
import com.innercircle.dto.explore.ExploreUserResponse;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.UUID;

@Service
public class ExploreService {
    private static final String PUBLISHED="PUBLISHED"; private final PostRepository postRepository; private final UserRepository userRepository;
    public ExploreService(PostRepository postRepository,UserRepository userRepository){this.postRepository=postRepository;this.userRepository=userRepository;}
    @Transactional(readOnly=true) public Page<ExplorePostResponse> posts(UUID viewerId,String emotion,Pageable pageable){User viewer=activeUser(viewerId);String filter=emotion==null||emotion.isBlank()?null:emotion.trim().toLowerCase(Locale.ROOT);return postRepository.findExplorePosts(viewerId,viewer.getCircle().getId(),filter,pageable).map(this::toPostResponse);}
    @Transactional(readOnly=true) public Page<ExploreUserResponse> people(UUID viewerId,String query,Pageable pageable){User viewer=activeUser(viewerId);if(query==null||query.isBlank())return userRepository.findByStatusOrderByHandleAsc("ACTIVE",pageable).map(u->toUserResponseIfSameCircle(u,viewer));return userRepository.findByHandleContainingIgnoreCaseAndStatusOrderByHandleAsc(query.trim(),"ACTIVE",pageable).map(u->toUserResponseIfSameCircle(u,viewer));}
    private User activeUser(UUID id){User u=userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User not found."));if(!"ACTIVE".equals(u.getStatus()))throw new IllegalArgumentException("This account is not available.");return u;}
    private ExplorePostResponse toPostResponse(Post p){String author=p.isAnonymous()?"Anonymous":"@"+p.getUser().getHandle();return new ExplorePostResponse(p.getId(),author,p.getEmotion(),p.getCircle().getName(),p.getContent(),p.getCreatedAt());}
    private ExploreUserResponse toUserResponseIfSameCircle(User u,User viewer){return u.getCircle().getId().equals(viewer.getCircle().getId())?toUserResponse(u):null;}
    private ExploreUserResponse toUserResponse(User u){return new ExploreUserResponse(u.getId(),"@"+u.getHandle(),u.getCircle().getName());}
}

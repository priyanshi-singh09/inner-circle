package com.innercircle.service;

import com.innercircle.dto.post.CreatePostRequest;
import com.innercircle.dto.post.PostResponse;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository; private final UserRepository userRepository; private final BlockRepository blockRepository;
    public PostService(PostRepository postRepository,UserRepository userRepository,BlockRepository blockRepository){this.postRepository=postRepository;this.userRepository=userRepository;this.blockRepository=blockRepository;}
    @Transactional public PostResponse create(UUID userId,CreatePostRequest request){User user=activeUser(userId);Post post=new Post();post.setUser(user);post.setCircle(user.getCircle());post.setContent(request.getContent().trim());post.setEmotion(request.getEmotion().trim());post.setAnonymous(request.isAnonymous());post.setContentWarning(false);post.setStatus("PUBLISHED");return toResponse(postRepository.save(post));}
    @Transactional(readOnly=true) public Page<PostResponse> feed(UUID viewerId,Pageable pageable){User viewer=activeUser(viewerId);return postRepository.findPersonalizedFeed(viewerId,viewer.getCircle().getId(),pageable).map(this::toResponse);}
    @Transactional(readOnly=true) public PostResponse get(UUID viewerId,UUID postId){User viewer=activeUser(viewerId);Post post=postRepository.findById(postId).filter(p->"PUBLISHED".equals(p.getStatus())).orElseThrow(()->new IllegalArgumentException("Post not found."));if(!viewer.getCircle().getId().equals(post.getCircle().getId()))throw new SecurityException("This post is not available in your Circle.");if(blockRepository.existsBetween(viewerId,post.getUser().getId()))throw new SecurityException("This post is not available.");return toResponse(post);}
    @Transactional public void delete(UUID userId,UUID postId){Post post=postRepository.findById(postId).orElseThrow(()->new IllegalArgumentException("Post not found."));if(!post.getUser().getId().equals(userId))throw new SecurityException("You can only delete your own post.");post.setStatus("REMOVED");postRepository.save(post);}
    private User activeUser(UUID id){User user=userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User not found."));if(!"ACTIVE".equals(user.getStatus()))throw new IllegalArgumentException("This account is not available.");return user;}
    private PostResponse toResponse(Post post){String author=post.isAnonymous()?"Anonymous":"@"+post.getUser().getHandle();return new PostResponse(post.getId(),author,post.getEmotion(),post.getCircle().getName(),post.getContent(),post.getCreatedAt());}
}

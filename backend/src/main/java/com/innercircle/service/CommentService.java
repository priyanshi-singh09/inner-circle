package com.innercircle.service;

import com.innercircle.dto.comment.CommentResponse;
import com.innercircle.dto.comment.CreateCommentRequest;
import com.innercircle.entity.Comment;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.CommentRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CommentService {
    private static final String PUBLISHED="PUBLISHED";
    private final CommentRepository commentRepository; private final PostRepository postRepository; private final UserRepository userRepository; private final NotificationService notificationService; private final BlockRepository blockRepository;
    public CommentService(CommentRepository commentRepository,PostRepository postRepository,UserRepository userRepository,NotificationService notificationService,BlockRepository blockRepository){this.commentRepository=commentRepository;this.postRepository=postRepository;this.userRepository=userRepository;this.notificationService=notificationService;this.blockRepository=blockRepository;}
    @Transactional public CommentResponse create(UUID userId,UUID postId,CreateCommentRequest request){Post post=postRepository.findById(postId).orElseThrow(()->new IllegalArgumentException("Post not found."));if(!PUBLISHED.equals(post.getStatus()))throw new IllegalArgumentException("Comments are not available for this post.");User user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("User not found."));if(blockRepository.existsBetween(userId,post.getUser().getId()))throw new SecurityException("You cannot comment on this post.");if(!user.getCircle().getId().equals(post.getUser().getCircle().getId()))throw new IllegalArgumentException("You can only comment on posts from your Circle.");Comment comment=new Comment();comment.setPost(post);comment.setUser(user);comment.setContent(request.getContent().trim());comment.setAnonymous(request.isAnonymous());comment.setStatus(PUBLISHED);Comment saved=commentRepository.save(comment);if(!post.getUser().getId().equals(userId)){String actor=comment.isAnonymous()?"Someone":"@"+user.getHandle();notificationService.create(post.getUser().getId(),"COMMENT",postId,actor+" commented on your post.");}return toResponse(saved);}
    @Transactional(readOnly=true) public Page<CommentResponse> getForPost(UUID postId,Pageable pageable){if(!postRepository.existsById(postId))throw new IllegalArgumentException("Post not found.");return commentRepository.findByPost_IdAndStatusOrderByCreatedAtAsc(postId,PUBLISHED,pageable).map(this::toResponse);}
    @Transactional public void delete(UUID userId,UUID commentId){Comment comment=commentRepository.findById(commentId).orElseThrow(()->new IllegalArgumentException("Comment not found."));if(!comment.getUser().getId().equals(userId))throw new IllegalArgumentException("You can only delete your own comment.");comment.setStatus("REMOVED");comment.setUpdatedAt(java.time.Instant.now());}
    private CommentResponse toResponse(Comment comment){String author=comment.isAnonymous()?"Anonymous":"@"+comment.getUser().getHandle();return new CommentResponse(comment.getId(),author,comment.getContent(),comment.getCreatedAt());}
}

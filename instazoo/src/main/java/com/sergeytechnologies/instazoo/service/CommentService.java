package com.sergeytechnologies.instazoo.service;

import com.sergeytechnologies.instazoo.dto.CommentDTO;
import com.sergeytechnologies.instazoo.entity.Comment;
import com.sergeytechnologies.instazoo.entity.Post;
import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.exceptions.PostNotFoundException;
import com.sergeytechnologies.instazoo.repo.CommentRepo;
import com.sergeytechnologies.instazoo.repo.PostRepo;
import com.sergeytechnologies.instazoo.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public CommentService(CommentRepo commentRepo, PostRepo postRepo, UserRepo userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }


    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {

        User user = getUserByPrincipal(principal);
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("Post can not be found for username: " + user.getEmail()));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDTO.getMessage());

        LOG.info("Saving comment for Post: {}", post.getId());
        return commentRepo.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post can not be found"));
        List<Comment> comments = commentRepo.findAllByPost(post);

        return comments;
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepo.findById(commentId);
        comment.ifPresent(commentRepo::delete);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }
}

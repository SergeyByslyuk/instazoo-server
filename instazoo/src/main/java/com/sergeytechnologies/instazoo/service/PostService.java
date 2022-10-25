package com.sergeytechnologies.instazoo.service;

import com.sergeytechnologies.instazoo.dto.PostDTO;
import com.sergeytechnologies.instazoo.entity.ImageModel;
import com.sergeytechnologies.instazoo.entity.Post;
import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.exceptions.PostNotFoundException;
import com.sergeytechnologies.instazoo.repo.ImageRepo;
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
public class PostService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final ImageRepo imageRepo;

    public PostService(PostRepo postRepo, UserRepo userRepo, ImageRepo imageRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.imageRepo = imageRepo;
    }

    public Post createPost(PostDTO postDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("Saving post for User: {}", user.getEmail());
        return postRepo.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepo.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepo.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post can't be found for username: " + user.getEmail()));
    }

    public List<Post> getAllPostsForUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepo.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post can't be found"));
        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username)).findAny();
        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepo.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        Post post = getPostById(postId, principal);
        Optional<ImageModel> imageModel = imageRepo.findByPostId(postId);
        postRepo.delete(post);
        imageModel.ifPresent(imageRepo::delete);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

}

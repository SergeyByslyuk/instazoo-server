package com.sergeytechnologies.instazoo.repo;

import com.sergeytechnologies.instazoo.entity.Comment;
import com.sergeytechnologies.instazoo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    Comment findByIdAndUserId(Long commentId, Long userId);

}

package com.sergeytechnologies.instazoo.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String location;
    private String userName;
    private Integer likes;
    private String caption;
    private Set<String> usersLiked;
}

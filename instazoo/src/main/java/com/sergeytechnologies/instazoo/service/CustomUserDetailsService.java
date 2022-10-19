package com.sergeytechnologies.instazoo.service;


import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.repo.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username: " + username));
        return build(user);
    }

    public User loadUserById(Long id) {
        return userRepo.findUserById(id).orElse(null);
    }
    public static User build(User user) {

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(eRole -> new SimpleGrantedAuthority(eRole.name()))
                .collect(Collectors.toList());

        return new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }


}

package com.sergeytechnologies.instazoo.service;

import com.sergeytechnologies.instazoo.entity.ImageModel;
import com.sergeytechnologies.instazoo.entity.Post;
import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.exceptions.ImageNotFoundException;
import com.sergeytechnologies.instazoo.repo.ImageRepo;
import com.sergeytechnologies.instazoo.repo.PostRepo;
import com.sergeytechnologies.instazoo.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {


    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);
    private final ImageRepo imageRepo;
    private final UserRepo userRepo;
    private final PostRepo postRepo;

    public ImageUploadService(ImageRepo imageRepo, UserRepo userRepo, PostRepo postRepo) {
        this.imageRepo = imageRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
    }

    public ImageModel uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        User user = getUserByPrincipal(principal);
        LOG.info("Uploading image profile to User {}", user.getUsername());

        ImageModel userProfileImage = imageRepo.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepo.delete(userProfileImage);
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        return imageRepo.save(imageModel);
    }

    private ImageModel uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {

        User user = getUserByPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(post.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        LOG.info("Uploading image to Post {}", post.getId());

        return imageRepo.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal) {
        User user = getUserByPrincipal(principal);

        ImageModel imageModel = imageRepo.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decomposeBytes(imageModel.getImageBytes()));
        }

        return imageModel;
    }

    public ImageModel getImageToPost(Long postId) {
        ImageModel imageModel = imageRepo.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Can't find image to Post:" + postId));
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decomposeBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count  = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
            try {
                outputStream.close();
            } catch (IOException e) {
                LOG.error("Can't compress Bytes");
            }
            System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        }
        return outputStream.toByteArray();
    }

    private static byte[] decomposeBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream= new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            LOG.error("Can't decompose Bytes");
        }
        return outputStream.toByteArray();
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                });
    }

}

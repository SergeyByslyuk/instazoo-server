package com.sergeytechnologies.instazoo.web;

import com.sergeytechnologies.instazoo.entity.ImageModel;
import com.sergeytechnologies.instazoo.payload.response.MessageResponse;
import com.sergeytechnologies.instazoo.service.ImageUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile multipartFile,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToUser(multipartFile, principal);
        return new ResponseEntity<>(new MessageResponse("Image Uploaded Successfully"), HttpStatus.OK);
    }

    @PostMapping("/postId/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToPost(file, principal, Long.parseLong(postId));
        return new ResponseEntity<>(new MessageResponse("Image Uploadede Succesfully"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageToUser(Principal principal) {
        ImageModel userImage = imageUploadService.getImageToUser(principal);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("postId") String postId) {
        ImageModel postImage = imageUploadService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }

}

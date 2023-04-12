package com.niziolekp.awsimageuploadlearning.service;

import com.niziolekp.awsimageuploadlearning.bucket.BucketName;
import com.niziolekp.awsimageuploadlearning.model.profile.UserProfile;
import com.niziolekp.awsimageuploadlearning.repo.UserProfileRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final FileStore fileStore;
    private UserProfileRepo userProfileRepo;
    public UserProfileService(FileStore fileStore, UserProfileRepo userProfileRepo) {
        this.fileStore = fileStore;
        this.userProfileRepo = userProfileRepo;
    }

    public List<UserProfile> getUserProfiles(){
        return userProfileRepo.findAll();
    }
    public void uploadUserProfileImage(String userProfileId, MultipartFile file) {

        isFileEmpty(file); // 1. Check if image is not empty
        isImage(file); // 2. If file is an image


        UserProfile user = getUserProfileOrThrow(userProfileId); // 3. The user exists in our database


        Map<String, String> metadata = extractMetadata(file); // 4. Grab some metadata from file if any
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());// 5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(filename);
            userProfileRepo.save(user);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }
    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }
    private UserProfile getUserProfileOrThrow(String userProfileId) {
        return userProfileRepo.findById(userProfileId)
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }
    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    public byte[] downloadUserProfileImage(String userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);

    }

}

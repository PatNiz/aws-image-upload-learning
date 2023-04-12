package com.niziolekp.awsimageuploadlearning.service;

import com.niziolekp.awsimageuploadlearning.model.profile.UserProfile;
import com.niziolekp.awsimageuploadlearning.repo.UserProfileRepo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserManager {
    private UserProfileRepo userProfileRepo;

    public UserManager(UserProfileRepo userProfileRepo) {
        this.userProfileRepo = userProfileRepo;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        UserProfile userProfile = new UserProfile("niziolekp",null);
        userProfileRepo.save(userProfile);
    }



}

package com.niziolekp.awsimageuploadlearning.repo;

import com.niziolekp.awsimageuploadlearning.model.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, String> {
}

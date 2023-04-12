package com.niziolekp.awsimageuploadlearning.bucket;

public enum BucketName {
    PROFILE_IMAGE("niziolekpcode-image-upload1");
    private final String bucketName;
    BucketName(String bucketName){
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}

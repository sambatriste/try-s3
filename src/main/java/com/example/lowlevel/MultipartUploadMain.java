package com.example.lowlevel;

import com.amazonaws.services.s3.AmazonS3;
import com.example.Parameters;

import com.example.AmazonS3ClientFactory;

public class MultipartUploadMain {

    public static void main(String[] args) {

        Parameters params = new Parameters(args);

        AmazonS3ClientFactory amazonS3ClientFactory = new AmazonS3ClientFactory(params.maxErrorRetry);
        AmazonS3 amazonS3 = amazonS3ClientFactory.createAmazonS3();

        MultipartUploader uploader = new MultipartUploader(params.bucketName, amazonS3);
        uploader.upload(params.fileToUpload);
    }
}

package com.example;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;

public class Uploader {

    public static void main(String[] args) throws Exception {
        String bucketName = System.getProperty("bucketName");
        assert bucketName != null : "System Property 'bucketName' must be set.";
        Uploader uploader = new Uploader(bucketName);

        assert args.length > 0 : "args must be set.";
        String fileName =  args[0];
        uploader.upload(new File(fileName));
    }


    private final String bucketName;

    private final AWSCredentialsProvider credentialsProvider;

    private final ClientConfiguration clientConfiguration;


    public Uploader(String bucketName) {
        this(bucketName, new SystemPropertiesCredentialsProvider(), new ClientConfiguration());
    }

    public Uploader(String bucketName, AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration) {
        this.bucketName = bucketName;
        this.credentialsProvider = credentialsProvider;
        this.clientConfiguration = clientConfiguration;
    }

    private void upload(File file) throws Exception {
        TransferManager t = createTransferManager();
        try {
            System.out.println("Object upload started");
            Upload upload = t.upload(bucketName, file.getName(), file);
            upload.waitForCompletion();
            System.out.println("Object upload complete");
        } finally {
            if (t != null) t.shutdownNow();
        }
    }

    private TransferManager createTransferManager() {
        AmazonS3 amazonS3 = createAmazonS3();
        return TransferManagerBuilder.standard()
                                     .withS3Client(amazonS3)
                                     .build();
    }

    private AmazonS3 createAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                                    .withClientConfiguration(clientConfiguration)
                                    .withRegion(Regions.AP_NORTHEAST_1)
                                    .withCredentials(credentialsProvider)
                                    .build();
    }
}

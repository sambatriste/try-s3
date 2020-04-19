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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class Uploader {

    private static final Log log = LogFactory.getLog(Uploader.class);



    private final String bucketName;

    private final AWSCredentialsProvider credentialsProvider;

    private final ClientConfiguration clientConfiguration;


    public Uploader(String bucketName, AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration) {
        this.bucketName = bucketName;
        this.credentialsProvider = (credentialsProvider == null) ?
            new SystemPropertiesCredentialsProvider() :
            credentialsProvider;
        this.clientConfiguration = (clientConfiguration == null) ?
            new ClientConfiguration() :
            clientConfiguration;
    }

    public void upload(File file) throws Exception {
        TransferManager t = createTransferManager();
        try {
            log.debug("Object upload started");
            Upload upload = t.upload(bucketName, file.getName(), file);
            upload.waitForCompletion();
            log.debug("Object upload complete");
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

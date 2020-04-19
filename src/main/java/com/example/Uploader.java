package com.example;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
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

    private final Settings settings;

    public Uploader(String bucketName, Settings settings) {
        this.bucketName = bucketName;
        this.settings = settings;
    }

    public void upload(File file) throws Exception {
        upload(file.getName(), file);
    }

    public void upload(String key, File file) throws Exception {
        TransferManager t = settings.createTransferManager();
        try {
            log.debug("Object upload started");
            Upload upload = t.upload(bucketName, key, file);
            upload.waitForCompletion();
            log.debug("Object upload complete");
        } finally {
            if (t != null) t.shutdownNow();
        }
    }


    public static class Settings {

        private final AWSCredentialsProvider credentialsProvider;

        private final ClientConfiguration clientConfiguration;

        private final long minimumUploadPartSize;

        public Settings(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration,
                        long minimumUploadPartSize) {
            this.credentialsProvider = credentialsProvider;
            this.clientConfiguration = clientConfiguration;
            this.minimumUploadPartSize = minimumUploadPartSize;
        }

        private AmazonS3 createAmazonS3() {
            return AmazonS3ClientBuilder.standard()
                                        .withClientConfiguration(clientConfiguration)
                                        .withRegion(Regions.AP_NORTHEAST_1)
                                        .withCredentials(credentialsProvider)
                                        .build();
        }

        private TransferManager createTransferManager() {
            AmazonS3 amazonS3 = createAmazonS3();
            return TransferManagerBuilder.standard()
                                         .withS3Client(amazonS3)
                                         .withMinimumUploadPartSize(minimumUploadPartSize)
                                         .build();
        }
    }
}

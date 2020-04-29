package com.example.highlevel;

import com.amazonaws.client.builder.ExecutorFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.example.AmazonS3ClientFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TransferManagerFactory {

    private final long minimumUploadPartSize;

    private final AmazonS3ClientFactory amazonS3ClientFactory;

    private final int numberOfThread;

    TransferManagerFactory(long minimumUploadPartSize, int maxErrorRetry, int numberOfThread) {
        this.minimumUploadPartSize = minimumUploadPartSize;
        this.amazonS3ClientFactory = new AmazonS3ClientFactory(maxErrorRetry);
        this.numberOfThread = numberOfThread;
    }

    TransferManager create() {
        AmazonS3 amazonS3 = amazonS3ClientFactory.createAmazonS3();
        return TransferManagerBuilder.standard()
                                     .withS3Client(amazonS3)
                                     .withMinimumUploadPartSize(minimumUploadPartSize)
                                     .withExecutorFactory(new ExecutorFactory() {
                                         @Override
                                         public ExecutorService newExecutor() {
                                             return Executors.newFixedThreadPool(numberOfThread);
                                         }
                                     })
                                     .build();
    }

}

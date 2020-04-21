package com.example.highlevel;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.example.AmazonS3ClientFactory;

class TransferManagerFactory {

    private final long minimumUploadPartSize;

    private final AmazonS3ClientFactory amazonS3ClientFactory;

    TransferManagerFactory(long minimumUploadPartSize, int maxErrorRetry) {
        this.minimumUploadPartSize = minimumUploadPartSize;
        this.amazonS3ClientFactory = new AmazonS3ClientFactory(maxErrorRetry);
    }

    TransferManager create() {
        AmazonS3 amazonS3 = amazonS3ClientFactory.createAmazonS3();
        return TransferManagerBuilder.standard()
                                     .withS3Client(amazonS3)
                                     .withMinimumUploadPartSize(minimumUploadPartSize)
                                     .build();
    }


}

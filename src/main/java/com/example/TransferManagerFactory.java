package com.example;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.example.RetryConditionDecorator.LoggingDecorator;

import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY;
import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;

class TransferManagerFactory {

    private final long minimumUploadPartSize;

    private final int maxErrorRetry;

    TransferManagerFactory(long minimumUploadPartSize, int maxErrorRetry) {
        this.minimumUploadPartSize = minimumUploadPartSize;
        this.maxErrorRetry = maxErrorRetry;
    }

    TransferManager create() {
        AmazonS3 amazonS3 = createAmazonS3();
        return TransferManagerBuilder.standard()
                                     .withS3Client(amazonS3)
                                     .withMinimumUploadPartSize(minimumUploadPartSize)
                                     .build();
    }

    private AmazonS3 createAmazonS3() {
        ClientConfiguration clientConfiguration = createClientConfiguration();
        return AmazonS3ClientBuilder.standard()
                                    .withClientConfiguration(clientConfiguration)
                                    .withRegion(Regions.AP_NORTHEAST_1)
                                    .withCredentials(new SystemPropertiesCredentialsProvider())
                                    .build();
    }

    private ClientConfiguration createClientConfiguration() {
        RetryCondition retryCond = new LoggingDecorator(DEFAULT_RETRY_CONDITION);
        RetryPolicy retryPolicy = new RetryPolicy(retryCond,
                                                  DEFAULT_BACKOFF_STRATEGY,
                                                  maxErrorRetry,
                                                  false);
        ClientConfiguration clientConf = new ClientConfiguration();
        //clientConf.setMaxErrorRetry(params.maxErrorRetry);
        clientConf.setRetryPolicy(retryPolicy);
        return clientConf;
    }

}

package com.example;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.example.RetryConditionDecorator.LoggingDecorator;

import java.io.File;

import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY;
import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;


public class UploadMain {

    public static void main(String[] args) throws Exception {
        Parameters params = new Parameters(args);

        RetryCondition retryCond = new LoggingDecorator(DEFAULT_RETRY_CONDITION);
        RetryPolicy retryPolicy = new RetryPolicy(retryCond,
                                                  DEFAULT_BACKOFF_STRATEGY,
                                                  5,
                                                  true);
        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setRetryPolicy(retryPolicy);

        Uploader uploader = new Uploader(params.bucketName,
                                         new SystemPropertiesCredentialsProvider(),
                                         clientConf);

        uploader.upload(params.fileToUpload);
    }

    private static class Parameters {

        private final String bucketName;

        private final File fileToUpload;

        private Parameters(String[] args) {
            assert args.length > 0 : "1st argument must be set.";
            String fileName = args[0];
            this.fileToUpload = new File(fileName);

            String bucketName = System.getProperty("bucketName");
            assert bucketName != null : "System Property 'bucketName' must be set.";
            this.bucketName = bucketName;
        }
    }

}

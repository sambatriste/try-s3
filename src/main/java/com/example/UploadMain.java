package com.example;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.example.RetryConditionDecorator.LoggingDecorator;
import com.example.Uploader.Settings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY;
import static com.amazonaws.retry.PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;
import static com.amazonaws.services.s3.internal.Constants.MB;


public class UploadMain {

    private static final Log log = LogFactory.getLog(UploadMain.class);

    public static void main(String[] args) throws Exception {
        Parameters params = new Parameters(args);
        log.debug(params);

        ClientConfiguration clientConfiguration = createClientConfiguration(params);
        Uploader.Settings settings = new Settings(new SystemPropertiesCredentialsProvider(),
                                                  clientConfiguration,
                                                  params.minimumUploadPartSize);

        Uploader uploader = new Uploader(params.bucketName, settings);
        uploader.upload(params.fileToUpload);
    }

    private static ClientConfiguration createClientConfiguration(Parameters params) {
        RetryCondition retryCond = new LoggingDecorator(DEFAULT_RETRY_CONDITION);
        RetryPolicy retryPolicy = new RetryPolicy(retryCond,
                                                  DEFAULT_BACKOFF_STRATEGY,
                                                  params.maxErrorRetry,
                                                  false);
        ClientConfiguration clientConf = new ClientConfiguration();
        //clientConf.setMaxErrorRetry(params.maxErrorRetry);
        clientConf.setRetryPolicy(retryPolicy);
        return clientConf;
    }

    private static class Parameters {

        private static final long DEFAULT_MINIMUM_UPLOAD_PART_SIZE = 5 * MB;

        private final String bucketName;

        private final long minimumUploadPartSize;

        private final File fileToUpload;

        private final int maxErrorRetry;

        private Parameters(String[] args) {
            assert args.length > 0 : "1st argument must be set.";
            String fileName = args[0];
            this.fileToUpload = new File(fileName);

            String bucketName = System.getProperty("bucketName");
            assert bucketName != null : "System Property 'bucketName' must be set.";
            this.bucketName = bucketName;

            String minimumUploadPartSize = System.getProperty("minimumUploadPartSize");
            this.minimumUploadPartSize = minimumUploadPartSize == null ?
                DEFAULT_MINIMUM_UPLOAD_PART_SIZE :
                Long.parseLong(minimumUploadPartSize);

            String maxErrorRetry = System.getProperty("maxErrorRetry");
            this.maxErrorRetry = maxErrorRetry == null ?
                PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY :
                Integer.parseInt(maxErrorRetry);
        }

        @Override
        public String toString() {
            return "Parameters{" +
                "bucketName='" + bucketName + '\'' +
                ", minimumUploadPartSize=" + minimumUploadPartSize +
                ", fileToUpload=" + fileToUpload +
                ", maxErrorRetry=" + maxErrorRetry +
                '}';
        }
    }

}

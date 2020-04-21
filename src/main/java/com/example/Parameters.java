package com.example;

import com.amazonaws.retry.PredefinedRetryPolicies;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import static com.amazonaws.services.s3.internal.Constants.MB;

public class Parameters {
    private static final Log log = LogFactory.getLog(Parameters.class);

    private static final long DEFAULT_MINIMUM_UPLOAD_PART_SIZE = 5 * MB;

    public final String bucketName;

    public final long minimumUploadPartSize;

    public final File fileToUpload;

    public final int maxErrorRetry;

    public Parameters(String[] args) {
        this.fileToUpload = getFileToUploadFrom(args);
        this.bucketName = getBucketName();
        this.minimumUploadPartSize = getMinimumUploadPartSize();
        this.maxErrorRetry = getMaxRetryError();
        log.debug(this);
    }

    private static File getFileToUploadFrom(String[] args) {
        assert args.length > 0 : "1st argument must be set.";
        String filePath = args[0];
        return new File(filePath);
    }

    private static String getBucketName() {
        String bucketName = System.getProperty("bucketName");
        assert bucketName != null : "System Property 'bucketName' must be set.";
        return bucketName;
    }

    private static long getMinimumUploadPartSize() {
        String minimumUploadPartSize = System.getProperty("minimumUploadPartSize");
        return minimumUploadPartSize == null ?
            DEFAULT_MINIMUM_UPLOAD_PART_SIZE :
            Long.parseLong(minimumUploadPartSize);
    }

    private static int getMaxRetryError() {
        String maxErrorRetry = System.getProperty("maxErrorRetry");
        return maxErrorRetry == null ?
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

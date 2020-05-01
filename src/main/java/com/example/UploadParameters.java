package com.example;

import com.amazonaws.retry.PredefinedRetryPolicies;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import static com.amazonaws.services.s3.internal.Constants.MB;

public class UploadParameters {
    private static final Log log = LogFactory.getLog(UploadParameters.class);

    private static final long DEFAULT_MINIMUM_UPLOAD_PART_SIZE = 5 * MB;

    public final String bucketName;

    public final long minimumUploadPartSize;

    public final File fileToUpload;

    public final int maxErrorRetry;

    public final int numberOfThread;

    public UploadParameters(String[] args) {
        Variables src = new Variables();
        this.fileToUpload = getFileToUploadFrom(args);
        this.bucketName = src.get("bucketName");
        this.minimumUploadPartSize = src.getNumber("minimumUploadPartSize",
                                                   DEFAULT_MINIMUM_UPLOAD_PART_SIZE).longValue();
        this.maxErrorRetry = src.getNumber("maxErrorRetry",
                                           PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY).intValue();
        this.numberOfThread = src.getNumber("numberOfThread", 5).intValue();
        log.debug(this);
    }

    private static File getFileToUploadFrom(String[] args) {
        assert args.length > 0 : "1st argument must be set.";
        String filePath = args[0];
        return new File(filePath);
    }

    @Override
    public String toString() {
        return "Parameters{" +
            "bucketName='" + bucketName + '\'' +
            ", minimumUploadPartSize=" + minimumUploadPartSize +
            ", fileToUpload=" + fileToUpload +
            ", maxErrorRetry=" + maxErrorRetry +
            ", numberOfThread=" + numberOfThread +
            '}';
    }
}

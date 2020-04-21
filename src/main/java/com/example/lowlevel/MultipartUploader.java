package com.example.lowlevel;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultipartUploader {


    private final String bucketName;

    private final AmazonS3 amazonS3;

    public MultipartUploader(String bucketName, AmazonS3 amazonS3) {
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
    }


    public void upload(File file) {
        upload(file.getName(), file);
    }

    public void upload(String key, File file) {
        final long contentLength = file.length();
        final long defaultPartSize = 5 * 1024 * 1024; // Set part size to 5 MB.

        // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
        // then, after each individual part has been uploaded, pass the list of ETags to
        // the request to complete the upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Initiate the multipart upload.
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);

        // Upload the file parts.
        long filePosition = 0;
        for (int i = 1; filePosition < contentLength; i++) {
            // Because the last part could be less than 5 MB, adjust the part size as needed.
            long partSize = Math.min(defaultPartSize, (contentLength - filePosition));

            // Create the request to upload a part.
            UploadPartRequest uploadRequest = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(key)
                .withUploadId(initResponse.getUploadId())
                .withPartNumber(i)
                .withFileOffset(filePosition)
                .withFile(file)
                .withPartSize(partSize);
            LoggingProgressListener listener = new LoggingProgressListener(i);
            uploadRequest.setGeneralProgressListener(listener);

            // Upload the part and add the response's ETag to our list.
            UploadPartResult uploadResult = amazonS3.uploadPart(uploadRequest);
            partETags.add(uploadResult.getPartETag());

            filePosition += partSize;
        }

        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
            bucketName,
            key,
            initResponse.getUploadId(),
            partETags
        );
        amazonS3.completeMultipartUpload(compRequest);

    }

}

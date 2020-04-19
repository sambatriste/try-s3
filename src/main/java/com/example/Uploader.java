package com.example;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class Uploader {

    private static final Log log = LogFactory.getLog(Uploader.class);

    private final String bucketName;

    private final TransferManager transferManager;

    public Uploader(String bucketName, TransferManager transferManager) {
        this.bucketName = bucketName;
        this.transferManager = transferManager;
    }

    public void upload(File file) throws Exception {
        upload(file.getName(), file);
    }

    public void upload(String key, File file) throws Exception {

        try {
            log.debug("Object upload started");
            Upload upload = transferManager.upload(bucketName, key, file);
            upload.waitForCompletion();
            log.debug("Object upload complete");
        } finally {
            transferManager.shutdownNow();
        }
    }

}

package com.example;

import com.amazonaws.services.s3.transfer.TransferManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class UploadMain {

    private static final Log log = LogFactory.getLog(UploadMain.class);

    public static void main(String[] args) throws Exception {

        Parameters params = new Parameters(args);

        TransferManagerFactory factory = new TransferManagerFactory(params.minimumUploadPartSize,
                                                                    params.maxErrorRetry);
        TransferManager transferManager = factory.create();

        Uploader uploader = new Uploader(params.bucketName, transferManager);
        uploader.upload(params.fileToUpload);
    }

}

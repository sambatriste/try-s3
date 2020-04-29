package com.example.highlevel;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.example.UploadParameters;


public class UploadMain {


    public static void main(String[] args) throws Exception {

        UploadParameters params = new UploadParameters(args);

        TransferManagerFactory factory = new TransferManagerFactory(params.minimumUploadPartSize,
                                                                    params.maxErrorRetry,
                                                                    params.numberOfThread
        );
        TransferManager transferManager = factory.create();

        Uploader uploader = new Uploader(params.bucketName, transferManager);
        uploader.upload(params.fileToUpload);
    }

}

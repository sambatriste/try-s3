package com.example.highlevel;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.example.Parameters;


public class UploadMain {


    public static void main(String[] args) throws Exception {

        Parameters params = new Parameters(args);

        TransferManagerFactory factory = new TransferManagerFactory(params.minimumUploadPartSize,
                                                                    params.maxErrorRetry);
        TransferManager transferManager = factory.create();

        Uploader uploader = new Uploader(params.bucketName, transferManager);
        uploader.upload(params.fileToUpload);
    }

}

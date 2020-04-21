package com.example.lowlevel;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class LoggingProgressListener implements ProgressListener {

    private static final Log log = LogFactory.getLog(LoggingProgressListener.class);


    private final int partNumber;

    LoggingProgressListener(int partNumber) {
        this.partNumber = partNumber;
    }


    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        String msg = format(progressEvent);
        log.debug(msg);
    }

    private String format(ProgressEvent e) {
        return "partNumber=[" + partNumber + "] " + e ;
    }

}

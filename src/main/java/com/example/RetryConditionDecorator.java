package com.example;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class RetryConditionDecorator implements RetryCondition {

    private static final Log log = LogFactory.getLog(RetryConditionDecorator.class);

    protected final RetryCondition component;

    protected RetryConditionDecorator(RetryCondition component) {
        this.component = component;
    }

    @Override
    public boolean shouldRetry(AmazonWebServiceRequest originalRequest,
                               AmazonClientException exception,
                               int retriesAttempted) {
        before(originalRequest, exception, retriesAttempted);
        boolean shouldRetry = component.shouldRetry(originalRequest, exception, retriesAttempted);
        after(originalRequest, exception, retriesAttempted, shouldRetry);
        return shouldRetry;
    }

    protected abstract void before(AmazonWebServiceRequest originalRequest,
                                   AmazonClientException exception,
                                   int retriesAttempted);

    protected abstract void after(AmazonWebServiceRequest originalRequest,
                                  AmazonClientException exception,
                                  int retriesAttempted,
                                  boolean shouldRetry);

    public static class LoggingDecorator extends RetryConditionDecorator {

        public LoggingDecorator(RetryCondition delegate) {
            super(delegate);
        }

        @Override
        protected void before(AmazonWebServiceRequest originalRequest, AmazonClientException exception,
                              int retriesAttempted) {
            log.debug("exception=[" + exception.getClass() + "], message=[" + exception.getMessage() + "]");
            log.debug("retriesAttempted=" + retriesAttempted);
        }

        @Override
        protected void after(AmazonWebServiceRequest originalRequest, AmazonClientException exception,
                             int retriesAttempted, boolean shouldRetry) {
            log.debug("shouldRetry=" + shouldRetry);
        }
    }
}

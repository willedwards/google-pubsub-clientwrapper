/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.travellazy.google.pubsub.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.*;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * RetryHttpInitializerWrapper will automatically retry upon RPC
 * failures, preserving the auto-refresh behavior of the Google
 * Credentials.
 */
class RetryHttpInitializerWrapper implements HttpRequestInitializer {

    private static final Logger LOG = Logger.getLogger(RetryHttpInitializerWrapper.class.getName());
    private static final int ONEMINITUES = 60000;

    /**
     * Intercepts the request for filling in the "Authorization"
     * header field, as well as recovering from certain unsuccessful
     * error codes wherein the Credential must refresh its token for a
     * retry.
     */
    private final Credential wrappedCredential;

    /**
     * A sleeper; you can replace it with a mock in your test.
     */
    private final Sleeper sleeper;

    /**
     * A constructor.
     *
     * @param wrappedCredential Credential which will be wrapped and
     * used for providing auth header.
     */
    RetryHttpInitializerWrapper(final Credential wrappedCredential) {
        this(wrappedCredential, Sleeper.DEFAULT);
    }

    /**
     * A constructor only for testing.
     *
     * @param wrappedCredential Credential which will be wrapped and
     * used for providing auth header.
     * @param sleeper Sleeper for easy testing.
     */
    RetryHttpInitializerWrapper(
            final Credential wrappedCredential, final Sleeper sleeper) {
        this.wrappedCredential = Preconditions.checkNotNull(wrappedCredential);
        this.sleeper = sleeper;
    }

    /**
     * Initializes the given request.
     */
    @Override
    public final void initialize(final HttpRequest request) {
        request.setReadTimeout(2 * ONEMINITUES);
        final HttpUnsuccessfulResponseHandler backoffHandler =
                new HttpBackOffUnsuccessfulResponseHandler(
                        new ExponentialBackOff())
                        .setSleeper(sleeper);
        request.setInterceptor(wrappedCredential);
        request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler()
        {
            @Override
            public boolean handleResponse(HttpRequest request1, HttpResponse response, boolean supportsRetry) throws IOException
            {
                if (wrappedCredential.handleResponse(request1, response, supportsRetry))
                {
                    // If credential decides it can handle it,
                    // the return code or message indicated
                    // something specific to authentication,
                    // and no backoff is desired.
                    return true;
                }
                else if (backoffHandler.handleResponse(request1, response, supportsRetry))
                {
                    // Otherwise, we defer to the judgement of
                    // our internal backoff handler.
                    LOG.info("Retrying " + request1.getUrl().toString());
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
        request.setIOExceptionHandler(new HttpBackOffIOExceptionHandler(new ExponentialBackOff()).setSleeper(sleeper));
    }
}


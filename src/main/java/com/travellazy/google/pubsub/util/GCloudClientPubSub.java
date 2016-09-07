package com.travellazy.google.pubsub.util;


import java.io.IOException;

public interface GCloudClientPubSub {

    /**
     *
     * @param fullCallbackUrlEndpoint
     * @param fullTopicName
     * @param fullSubscriptionName
     * @throws IOException
     */
    void createAsyncCallbackURLForTopic(final String fullCallbackUrlEndpoint,
                                        final String fullTopicName,
                                        final String fullSubscriptionName) throws IOException;

    /**
     *
     * @param fullTopicName
     * @param message
     * @throws IOException
     */
    void sendMessage(final String fullTopicName, final String message) throws IOException;
}

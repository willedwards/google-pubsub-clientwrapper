package com.travellazy.google.pubsub.util;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.logging.Logger;

public class GloudPubSubClientWrapper implements GCloudClientPubSub
{
    private final Pubsub client;
    private static final java.util.logging.Logger logger = Logger.getLogger(GloudPubSubClientWrapper.class.getName());
    private final int subscriptionAckDeadlineSeconds;

    /**
     * The application name is like "google-cloud-pubsub-appengine-sample/1.0"
     * @param applicationName
     */
    public GloudPubSubClientWrapper(final String applicationName)  {
       this(applicationName,10);
    }

    public GloudPubSubClientWrapper(final String applicationName, final int subscriptionAckDeadlineSeconds)  {
        this.subscriptionAckDeadlineSeconds = subscriptionAckDeadlineSeconds;
        client =  new PubsubUtils(applicationName).getClient();
    }

    @Override
    public void createAsyncCallbackURLForTopic(final String fullCallbackUrlEndpoint,
                                               final String fullTopicName,
                                               final String fullSubscriptionName) throws IOException{
            createTopicIfDoesntExist(fullTopicName);

            createSubscriptionIfDoesntExist(
                    fullCallbackUrlEndpoint,
                    fullSubscriptionName,
                    fullTopicName);

        logger.info("Created: " + fullSubscriptionName);

    }

    @Override
    public void sendMessage(final String fullTopicName, final String message) throws IOException {

        PubsubMessage pubsubMessage = new PubsubMessage();
        pubsubMessage.encodeData(message.getBytes("UTF-8"));
        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setMessages(ImmutableList.of(pubsubMessage));

        logger.fine("about to send to topic " + fullTopicName);

        client.projects().topics().publish(fullTopicName, publishRequest).execute();

        logger.fine("message sent to topic " + fullTopicName);
    }

    /**
     * Creates a Cloud Pub/Sub topic if it doesn't exist.
     * @throws IOException when API calls to Cloud Pub/Sub fails.
     */
    private void createTopicIfDoesntExist(String fullTopicName) throws IOException {
        try {
            client.projects().topics().get(fullTopicName).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                // Create the topic if it doesn't exist
                client.projects().topics().create(fullTopicName, new Topic()).execute();
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates a Cloud Pub/Sub subscription if it doesn't exist.
     * @throws IOException when API calls to Cloud Pub/Sub fails.
     */
    private void createSubscriptionIfDoesntExist(final String fullCallbackUrlEndpoint, final String fullSubscriptionName, final String fullTopicName) throws IOException {
        try {
            client.projects().subscriptions().get(fullSubscriptionName).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                // Create the subscription if it doesn't exist
                createNewAsyncCallbackSubscription(fullCallbackUrlEndpoint,fullSubscriptionName,fullTopicName);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param fullCallbackUrlEndpoint like https://<projectId>.appspot.com/messages/async
     * @param fullSubscriptionName
     * @param fullTopicName
     * @throws IOException
     */
    private void createNewAsyncCallbackSubscription(final String fullCallbackUrlEndpoint,
                                                    final String fullSubscriptionName,
                                                    final String fullTopicName) throws IOException {

        PushConfig pushConfig = new PushConfig().setPushEndpoint(fullCallbackUrlEndpoint);


        Subscription subscription = new Subscription()
                .setTopic(fullTopicName)
                .setAckDeadlineSeconds(subscriptionAckDeadlineSeconds)
                .setPushConfig(pushConfig);

        client.projects().subscriptions()
                .create(fullSubscriptionName, subscription)
                .execute();
    }

}

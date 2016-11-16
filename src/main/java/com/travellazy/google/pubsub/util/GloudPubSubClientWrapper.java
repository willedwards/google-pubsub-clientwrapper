/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.travellazy.google.pubsub.util;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.logging.Logger;

public class GloudPubSubClientWrapper implements GCloudClientPubSub {
    private static final int DEFAULT_SUBSCRIPTION_ACK_DEADLINE_SECONDS = 10;
    private final Pubsub client;
    private static final java.util.logging.Logger logger = Logger.getLogger(GloudPubSubClientWrapper.class.getName());
    private final int subscriptionAckDeadlineSeconds;
    private final String projectTopicPrefix;
    private final String projectSubscriptionPrefix;
    private final String projectId;
    /**
     * The application name is like "google-cloud-pubsub-appengine-sample/1.0"
     *
     * @param projectId your projectId
     */
    public GloudPubSubClientWrapper(final String projectId) {
        this(projectId, DEFAULT_SUBSCRIPTION_ACK_DEADLINE_SECONDS);
    }

    public GloudPubSubClientWrapper(final String projectId, final int subscriptionAckDeadlineSeconds) {
        this.subscriptionAckDeadlineSeconds = subscriptionAckDeadlineSeconds;
        this.projectId = projectId;
        projectTopicPrefix = "projects/" + projectId + "/topics/";
        projectSubscriptionPrefix = "projects/" + projectId + "/subscriptions/";
        client = new PubsubUtils(projectId).getClient();
    }


    @Override
    public void sendMessage(final String topicKey, final String message) throws IOException {

        PubsubMessage pubsubMessage = new PubsubMessage();
        pubsubMessage.encodeData(message.getBytes("UTF-8"));
        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setMessages(ImmutableList.of(pubsubMessage));

        String fullTopicName = projectTopicPrefix + topicKey;

        logger.fine("about to send to topic " + fullTopicName);

        client.projects().topics().publish(fullTopicName, publishRequest).execute();

        logger.fine("message sent to topic " + fullTopicName);
    }

    @Override
    public TopicValue createTopic(String topicName) throws IOException {
        String fullTopicName = projectTopicPrefix + topicName;
        TopicValue topicValue;
        try {

            logger.fine("about to create to topic " + fullTopicName);

            client.projects().topics().get(fullTopicName).execute();

            topicValue = new TopicValue(projectTopicPrefix, topicName, State.ALREADY_EXISTS);
            logger.fine("topic " + fullTopicName + " already exists");
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                logger.fine("topic " + fullTopicName + " about to be created");

                client.projects().topics().create(fullTopicName, new Topic()).execute();
                topicValue = new TopicValue(projectTopicPrefix, topicName, State.CREATED);
                logger.fine("topic " + fullTopicName + " created");
            } else {
                throw e;
            }
        }
        return topicValue;
    }

    @Override
    public Pubsub.Projects.Topics.List listTopics() throws IOException {
        return client.projects().topics().list("projects/"+projectId);
    }

    @Override
    public Pubsub.Projects.Subscriptions.List listSubscriptions() throws IOException {
        return client.projects().subscriptions().list("projects/"+projectId);
    }

    @Override
    public SubscriptionValue createSubscriptionForTopic(final TopicValue topic, final String subscriptionName, final String fullCallbackUrlEndpoint) throws IOException {

        final String fullSubscriptionName = projectSubscriptionPrefix + subscriptionName;
        SubscriptionValue subscriptionValue;
        try {
            client.projects().subscriptions().get(fullSubscriptionName).execute();

            subscriptionValue = new SubscriptionValue(fullSubscriptionName,
                                                    fullCallbackUrlEndpoint,
                                                    topic,
                                                    State.ALREADY_EXISTS);

        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                // Create the subscription if it doesn't exist
                createNewAsyncCallbackSubscription(fullCallbackUrlEndpoint, fullSubscriptionName, topic.getFullTopicName());

                subscriptionValue = new SubscriptionValue(fullSubscriptionName,
                                                            fullCallbackUrlEndpoint,
                                                            topic,
                                                            State.CREATED);
            } else {
                throw e;
            }
        }
        return subscriptionValue;
    }


    /**
     * Creates a Cloud Pub/Sub subscription if it doesn't exist.
     *
     * @throws IOException when API calls to Cloud Pub/Sub fails.
     */
    private void createSubscriptionIfDoesntExist(final String fullCallbackUrlEndpoint, final String fullSubscriptionName, final String topicName) throws IOException {
        String fullTopicName = projectTopicPrefix + topicName;
        try {
            client.projects().subscriptions().get(fullSubscriptionName).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                // Create the subscription if it doesn't exist
                createNewAsyncCallbackSubscription(fullCallbackUrlEndpoint, fullSubscriptionName, fullTopicName);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param fullCallbackUrlEndpoint like https://mprojectId.appspot.com/messages/async
     * @param fullSubscriptionName    like projects/myprojectId/subscriptions/subscription-myprojectId
     * @param fullTopicName           like projects/myprojectId/topics/topic-pubsub-api-appengine-sample
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

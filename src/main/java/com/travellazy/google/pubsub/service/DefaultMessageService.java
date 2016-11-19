package com.travellazy.google.pubsub.service;

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

/**
 * @author William Edwards
 */
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import com.travellazy.google.pubsub.util.exceptions.NoTopicFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultMessageService implements MessageService {
    private final GCloudClientPubSub client;
    private final CallbackHook callbackHook;

    public DefaultMessageService(final GCloudClientPubSub client,CallbackHook callbackHook) {
        this.client = client;
        this.callbackHook = callbackHook;
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage)  {
            callbackHook.receiveMessage(pubsubMessage);
    }


    @Override
    public void broadcastMessage(String topicName, String message) throws IOException, NoTopicFoundException {
        client.sendMessage(topicName, message);
    }

    @Override
    public TopicValue createOrFindTopic(String topicKey) throws IOException {
        return client.createTopic(topicKey);
    }

    @Override
    public SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionName, String urlCallback) throws IOException {
        return client.createSubscriptionForTopic(topicValue, subscriptionName, urlCallback);
    }

    @Override
    public Collection<String> getAllTopics() throws IOException {
        List<String> topicNames = extractTopicNames();
        return topicNames;
    }

    List<String> extractTopicNames() throws IOException {
        List<String> topicNames = new ArrayList<>();
        Pubsub.Projects.Topics.List listMethod = client.listTopics();
        String nextPageToken = null;
        do {
            if (nextPageToken != null) {
                listMethod.setPageToken(nextPageToken);
            }
            ListTopicsResponse response = listMethod.execute();
            if (!response.isEmpty()) {
                for (Topic topic : response.getTopics()) {
                    topicNames.add(topic.getName());
                }
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null);

        return topicNames;
    }

    @Override
    public Collection<String> getAllSubscriptions() throws IOException {
        List<String> subscriptionNames = new ArrayList<>();
        Pubsub.Projects.Subscriptions.List listMethod = client.listSubscriptions();
        String nextPageToken = null;
        do {
            if (nextPageToken != null) {
                listMethod.setPageToken(nextPageToken);
            }
            ListSubscriptionsResponse response = listMethod.execute();
            if (!response.isEmpty()) {
                for (Subscription subscription : response.getSubscriptions()) {
                    subscriptionNames.add(subscription.toPrettyString());
                }
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null);


        return subscriptionNames;
    }

}

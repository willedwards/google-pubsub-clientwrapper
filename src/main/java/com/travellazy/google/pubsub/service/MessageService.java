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
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import com.travellazy.google.pubsub.util.exceptions.NoTopicFoundException;

import java.io.IOException;
import java.util.Collection;

public interface MessageService
{
    /**
     * Will call your callbackHook under the covers (and does so in the default implementation provided here).
     * However this allows you to roll your own if you so wish.
     * @param message
     */
    void receiveMessage(PubsubMessage message);

    /**
     * Will trigger all subscribers to this topic to be called.
     * @param topicKey
     * @param message
     * @throws IOException
     * @throws NoTopicFoundException
     */
    void broadcastMessage(String topicKey, String message) throws IOException, NoTopicFoundException;

    /**
     *
     * @param topicKey the name of the topic to be created or found
     * @return  the immutable Topic object encapsulating the topic with its full url
     * @throws IOException
     */

    TopicValue createOrFindTopic(String topicKey) throws IOException;

    SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionKey, String urlCallback) throws IOException;

    Collection<String> getAllTopics() throws IOException;

    Collection<String> getAllSubscriptions() throws IOException;
}

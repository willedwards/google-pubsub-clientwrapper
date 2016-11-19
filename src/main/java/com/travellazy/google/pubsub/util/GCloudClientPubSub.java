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

/**
 * @author William Edwards
 */
package com.travellazy.google.pubsub.util;
import com.google.api.services.pubsub.Pubsub;
import java.io.IOException;

public interface GCloudClientPubSub {

    /**
     *
     * @param topicName    like projects/myprojectId/topics/topic-pubsub-api-appengine-sample
     *                         if the topic exists, it will not be created again, no exception will be thrown
     * @return              the full topic name
     * @throws IOException
     */
    TopicValue createTopic(final String topicName) throws IOException;

    Pubsub.Projects.Topics.List listTopics() throws IOException;

    Pubsub.Projects.Subscriptions.List listSubscriptions() throws IOException;

    /**
     *
     * @param topicValue     the topic subscribed to
     * @param subscription   the subscription name
     * @return               an object encapsulating these two.
     */
    SubscriptionValue createSubscriptionForTopic(final TopicValue topicValue, final String subscription, final String endpointUrl) throws IOException;


    /**
     * @param topicKey            like topic-pubsub-api-appengine-sample
     * @param base64EncodedMessage  the base64Encoded message
     * @throws IOException
     */
    void sendMessage(final String topicKey, final String base64EncodedMessage) throws IOException;




}

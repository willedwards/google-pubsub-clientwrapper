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


import java.io.IOException;

public interface GCloudClientPubSub {

    /**
     * @param fullCallbackUrlEndpoint like https://mprojectId.appspot.com/messages/async
     * @param fullSubscriptionName    like projects/myprojectId/subscriptions/subscription-myprojectId
     * @param fullTopicName           like projects/myprojectId/topics/topic-pubsub-api-appengine-sample
     * @throws IOException
     */
    void createAsyncCallbackURLForTopic(final String fullCallbackUrlEndpoint,
                                        final String fullTopicName,
                                        final String fullSubscriptionName) throws IOException;

    /**
     * @param fullTopicName         like projects/myprojectId/topics/topic-pubsub-api-appengine-sample
     * @param base64EncodedMessage  the base64Encoded message
     * @throws IOException
     */
    void sendMessage(final String fullTopicName, final String base64EncodedMessage) throws IOException;

    /**
     *
     * @param fullTopicName    like projects/myprojectId/topics/topic-pubsub-api-appengine-sample
     *                         if the topic exists, it will not be created again, no exception will be thrown
     * @throws IOException
     */
    void createTopic(final String fullTopicName) throws IOException;
}

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
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.State;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class MessageServiceTest implements CallbackHook{

    GCloudClientPubSub clientPubSubMock = Mockito.mock(GCloudClientPubSub.class);
    MessageService messageService = new DefaultMessageService(clientPubSubMock,this);

    TopicValue cannedTopic;
    SubscriptionValue cannedSubscription;

    PubsubMessage actualMessageReceived;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cannedTopic = new TopicValue("projects/projectId/topic", "testTopic", State.CREATED);
        cannedSubscription = new SubscriptionValue("testSubKey", "http://otherprojectId.appspot.com", cannedTopic, State.CREATED);
    }

    @Test
    public void createOrFindTopic() throws Exception {
        Mockito.when(clientPubSubMock.createTopic("testTopic")).thenReturn(cannedTopic);
        TopicValue actual = messageService.createOrFindTopic("testTopic");
        assertEquals(cannedTopic, actual);
    }

    @Test
    public void createSubscription() throws Exception {
        Mockito.when(clientPubSubMock.createSubscriptionForTopic(cannedTopic, "testSubKey", "http://otherprojectId.appspot.com")).thenReturn(cannedSubscription);
        SubscriptionValue actual = messageService.createSubscription(cannedTopic, "testSubKey", "http://otherprojectId.appspot.com");
        assertEquals(cannedSubscription, actual);
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage) {
        actualMessageReceived = pubsubMessage;
    }
}
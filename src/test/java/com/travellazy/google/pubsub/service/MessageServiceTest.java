package com.travellazy.google.pubsub.service;

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
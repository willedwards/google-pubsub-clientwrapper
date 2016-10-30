package com.travellazy.google.pubsub.util;/**
 * Author: wge
 * Date: 30/10/2016
 * Time: 15:21
 */

public class SubscriptionValue {

    private final String subscriptionKey;
    public String getEndpointCallback() {
        return endpointCallback;
    }

    private final String endpointCallback;

    @Override
    public String toString() {
        return "SubscriptionValue{" +
                "subscriptionKey='" + subscriptionKey + '\'' +
                ", endpointCallback='" + endpointCallback + '\'' +
                ", state=" + state +
                ", topicSubscribedTo=" + topicSubscribedTo +
                '}';
    }

    private final State state;

    private final TopicValue topicSubscribedTo;

    public SubscriptionValue(final String subscriptionKey,String endpointCallback, final TopicValue topicSubscribedTo, final State state) {
        this.subscriptionKey = subscriptionKey;
        this.endpointCallback = endpointCallback;
        this.state = state;
        this.topicSubscribedTo = topicSubscribedTo;
    }

    public boolean wasCreated(){
        return state == State.CREATED;
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public TopicValue getTopicSubscribedTo() {
        return topicSubscribedTo;
    }
}

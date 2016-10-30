package com.travellazy.google.pubsub.util;

public class TopicValue {

    private final State state;
    private final String topicPrefix;

    public String getTopicKey() {
        return topicKey;
    }

    private final String topicKey;

    public TopicValue(final String topicPrefix, final String topicKey, final State state) {
        this.topicPrefix = topicPrefix;
        this.topicKey = topicKey;
        this.state = state;
    }

    public String getFullTopicName(){
        return topicPrefix + topicKey;
    }

    public boolean wasCreated(){
        return state == State.CREATED;
    }

     public String getStatus(){
         return state.getValue();
     }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TopicValue)) return false;

        TopicValue that = (TopicValue) o;

        return topicKey != null ? topicKey.equals(that.topicKey) : that.topicKey == null;
    }

    @Override
    public int hashCode() {
        return  (topicKey != null ? topicKey.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "TopicValue{" +
                "state=" + state +
                ", topicPrefix='" + topicPrefix + '\'' +
                ", topicName='" + topicKey + '\'' +
                '}';
    }
}

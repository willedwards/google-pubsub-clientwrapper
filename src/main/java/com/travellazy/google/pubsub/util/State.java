package com.travellazy.google.pubsub.util;

public enum State {
    CREATED("created"),
    ALREADY_EXISTS("exists");

    private String value;
    private State(String value){
        this.value = value;
    }

    public String getValue(){
        return  value;
    }
}

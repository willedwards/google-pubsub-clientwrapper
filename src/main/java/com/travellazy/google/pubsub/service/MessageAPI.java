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

import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.GloudPubSubClientWrapper;


public class MessageAPI {

    private static MessageAPI instance;
    private final MessageService messageService;

    private MessageAPI(String projectId, CallbackHook callbackHook) {
        GCloudClientPubSub pubSubClient = new GloudPubSubClientWrapper(projectId);
        messageService = new DefaultMessageService(pubSubClient, callbackHook);
    }

    public static synchronized MessageAPI getMessageFactory(final String projectId, CallbackHook callbackHook) {
        if (instance == null) {
            instance = new MessageAPI(projectId, callbackHook);
        }
        return instance;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}

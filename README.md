# google-pubsub-clientwrapper ![Build Status](https://travis-ci.org/willedwards/google-pubsub-clientwrapper.svg?branch=master)

A handy wrapper to abstract away Google pubsub client
=======
# google-pubsub-clientwrapper
A handy wrapper to abstract away Google pubsub client into a [MessageService](https://github.com/willedwards/google-pubsub-clientwrapper/blob/master/src/main/java/com/travellazy/google/pubsub/service/MessageService.java
) interface which you simply call.




This will allow you to do the following from your appengine:

 - create or find a topic
 - list all topics
 - subscribe to a topic, and set a URL to callback
 - list all subscriptions
 - send a message to a topic

Only 2 places required for usage in your project.

### Pom Usage
======
```
       <dependency>
            <groupId>com.travellazy.google.pubsub</groupId>
            <artifactId>client</artifactId>
            <version>1.1.0</version>
        </dependency>
```

### Spring boot
===========
```

import com.travellazy.google.pubsub.service.CallbackHook;
import com.travellazy.google.pubsub.service.MessageAPI;
import com.travellazy.google.pubsub.service.MessageService;
...

@Configuration
public class GcloudConfig
{

    private String projectId = "myProjectId" ;//where http://myProjectId.appspot.com

    @Bean
    public CallbackHook buildReceiveMessageCallback(){
        return new ReceiveMessageCallback(); // your custom message handler
    }

    @Bean
    public MessageService getMessagesService(CallbackHook callbackHook){
        return MessageAPI.getMessageFactory(projectId,callbackHook).getMessageService();
    }

    ....
}
```

  

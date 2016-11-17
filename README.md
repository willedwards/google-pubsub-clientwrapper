# google-pubsub-clientwrapper
A handy wrapper to abstract away Google pubsub client

Only 2 places required for usage in your project.

### Pom Usage
======
```
       <dependency>
            <groupId>com.travellazy.google.pubsub</groupId>
            <artifactId>client</artifactId>
            <version>1.1.0-SNAPSHOT</version>
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

  

# ChatBotUtils

Utils for creating chat bots in Telegram/Viber or any other.

## Installation

Load repository from GitHub

```bash
 <repositories>
      <repository>
          <id>ChatBotUtils</id>
          <url>https://github.com/LiashenkoAndrey/ChatBotUtils.git</url>
      </repository>
  </repositories>
```

Import dependency

```bash
  <dependency>
      <groupId>org.chatBotUtils</groupId>
      <artifactId>ChatBotUtils</artifactId>
      <version>1.0</version>
  </dependency>
```

## Usage
You need to implement two interfaces:

1. [org.chatBotUtils.util.BeanService](https://github.com/LiashenkoAndrey/ChatBotUtils/blob/main/src/main/java/org/chatBotUtils/util/BeanService.java)

2. [org.chatBotUtils.util.UpdateService](https://github.com/LiashenkoAndrey/ChatBotUtils/blob/main/src/main/java/org/chatBotUtils/util/UpdateService.java)

This example uses [SpringFramework](https://spring.io/projects/spring-framework) as a context provider and [Telegram bot library](https://github.com/rubenlagus/TelegramBots)
```java
import lombok.RequiredArgsConstructor;
import org.chatBotUtils.util.BeanService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BeanServiceImpl implements BeanService {

    private final ApplicationContext context;


    @Override
    public Object getBean(String name) {
        return context.getBean(name);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }
}
```

```java
import lombok.AllArgsConstructor;
import org.chatBotUtils.util.MethodExecutor;
import org.chatBotUtils.util.UpdateService;
import org.chatBotUtils.util.exceptions.MethodExecutionException;
import org.chatBotUtils.util.exceptions.UpdateProcessingException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@AllArgsConstructor
@Component
public class UpdateServiceImpl implements UpdateService<Update> {


    private final MethodExecutor executor;

    private final UpdateDetails updateDetails; // see further

    public void processUpdate(Update update) throws UpdateProcessingException {
        try {

            updateDetails.update(update);

            if (update.hasCallbackQuery()) {
                executor.invokeCallBack(update);
            } else if (getMessage(update).getText().charAt(0) == '/'){
                executor.invokeCommand(update);
            }

        } catch (MethodExecutionException e) {
            throw new UpdateProcessingException(e);
        }
    }

    private Message getMessage(Update update) {
        Message message;
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }
        return message;
    }
}
```

For getting updates we've created a util class UpdateDetails
```java
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class UpdateDetails {

    private Long chatId;

    private Update update;

    public void update(Update update) {
        Message message = UpdateServiceImpl.getMessage(update);
        this.update = update;
        this.chatId = message.getChatId();
    }

    // Your custom fields and util methods...
}
```

Simple controller
```java
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.chatBotUtils.util.annotations.CallBack;
import org.chatBotUtils.util.annotations.Command;
import org.chatBotUtils.util.annotations.Controller;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Controller
@Component
@RequiredArgsConstructor
public class CommandController {

    private final Bot bot;
    private final UpdateDetails updateDetails;

    @Command
    @SneakyThrows
    public void hello() {
        bot.execute(SendMessage.builder()
                .chatId(updateDetails.getChatId())
                .text("Hi, I'm alive!")
                .replyMarkup(new InlineKeyboardMarkup(List.of(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .callbackData("callback_data_1")
                                        .text("Button 1")
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .callbackData("callback_data_2")
                                        .text("Button 2")
                                        .build()
                        )
                )))
                .build());
    }

    @CallBack
    @SneakyThrows
    public void callback_data_1() {
        bot.execute(SendMessage.builder()
                .chatId(updateDetails.getChatId())
                .text("You've pressed Button 1")
                .build());
    }

    @CallBack
    @SneakyThrows
    public void callback_data_2() {
        bot.execute(SendMessage.builder()
                .chatId(updateDetails.getChatId())
                .text("Amazing!\nYou've pressed Button 2")
                .build());
    }
}
```
[org.chatBotUtils.util.annotations.Controller](https://github.com/LiashenkoAndrey/ChatBotUtils/blob/main/src/main/java/org/chatBotUtils/util/annotations/Controller.java) annotation need for specifying controllers class

[org.chatBotUtils.util.annotations.Command](https://github.com/LiashenkoAndrey/ChatBotUtils/blob/main/src/main/java/org/chatBotUtils/util/annotations/Command.java)
annotation using for handling of commands

[org.chatBotUtils.util.annotations.Command](https://github.com/LiashenkoAndrey/ChatBotUtils/blob/main/src/main/java/org/chatBotUtils/util/annotations/CallBack.java)
annotation using for handling of callbacks

## The result of the example
![The San Juan Mountains are beautiful!](/example/example-photo-1.jpeg)
![The San Juan Mountains are beautiful!](/example/example-photo-2.jpeg)
## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[GNU General Public License v3.0](https://choosealicense.com/licenses/gpl-3.0/)
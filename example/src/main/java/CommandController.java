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
package gov.miloverada.telegram_bot;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chatBotUtils.util.UpdateService;
import org.chatBotUtils.util.exceptions.UpdateProcessingException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LogManager.getLogger(Bot.class);


    private final ApplicationContext context;

    @Override
    public String getBotToken() {
        return "6381306175:AAHj5EIKCDZjraoz2rjnFBaqxDa4UUrr9No";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            UpdateService<Update> service = context.getBean(UpdateService.class);
            service.processUpdate(update);

        } catch (UpdateProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Bluadki";
    }
}
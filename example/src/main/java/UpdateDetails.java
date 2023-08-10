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
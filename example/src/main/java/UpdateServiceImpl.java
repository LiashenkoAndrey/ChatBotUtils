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

    public static Message getMessage(Update update) {
        Message message;
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }
        return message;
    }
}
package org.example.mybooksbot.tg.bot;

import lombok.RequiredArgsConstructor;
import org.example.mybooksbot.books.BookService;
import org.example.mybooksbot.books.dto.BookResponseDto;
import org.example.mybooksbot.user.UserService;
import org.example.mybooksbot.user.entity.User;
import org.example.mybooksbot.user.entity.UserState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.mybooksbot.user.entity.UserState.*;

@Component
@RequiredArgsConstructor
public class MyBooksBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String USERNAME_BOT;

    @Value("${telegram.bot.token}")
    private String TOKEN_BOT;

    private final String searchBook = "KITOB QIDIRISH";
    private final String getALlBook = "BARCHA KITOBLAR";

    private final UserService userService;
    private final BookService bookService;

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, String> searchQueries = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();

        UserState userState = userStates.getOrDefault(chatId, START);

        if (userState == REGISTER && message.hasContact()) {
            processContact(message.getContact(), chatId);
        } else {
            processTextMessage(text, chatId, userState);
        }
    }

    private void processContact(Contact contact, Long chatId) {
        User user = User.builder()
                .chatId(chatId)
                .firstname(contact.getFirstName())
                .lastname(contact.getLastName())
                .phoneNumber(contact.getPhoneNumber())
                .userState(REGISTER)
                .build();
        userService.save(user);
        userStates.put(chatId, REGISTER);
        try {
            execute(menu(String.valueOf(chatId)));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void processTextMessage(String text, Long chatId, UserState userState) {
        switch (userState) {
            case START:
                try {
                    sendMessage(chatId, "Assalomu Aleykum botga xush kelibsiz ");
                    execute(register(String.valueOf(chatId)));

                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                userStates.put(chatId, REGISTER);
                break;
            case REGISTER:
                processRegisterCommand(text, chatId);
                try {
                    execute(menu(String.valueOf(chatId)));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                break;
            case SEARCH_BOOK:
                processSearchBook(text, chatId);
                break;
        }
    }

    private void processRegisterCommand(String text, Long chatId) {
        switch (text) {
            case searchBook:
                userStates.put(chatId, SEARCH_BOOK);
                sendMessage(chatId, "Iltimos kitob nomini kiriting: ");
                break;
            case getALlBook:
                sendMessage(chatId, String.valueOf(  bookService.getAll()));
                break;

        }
    }

    private void processSearchBook(String text, Long chatId) {
        searchQueries.put(chatId, text);
        List<BookResponseDto> books = bookService.searchByName(text);
        sendMessage(chatId, String.valueOf(books));
        userStates.put(chatId, REGISTER);
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage menu(String chatId) {

        SendMessage sendMessage = new SendMessage(chatId, "menu");
        sendMessage.setReplyMarkup(menu());
        return sendMessage;
    }

    public ReplyKeyboardMarkup menu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow buttons = new KeyboardRow();
        buttons.add(searchBook);
        buttons.add(getALlBook);
        rows.add(buttons);

        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }


    public SendMessage register(String chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId, "Iltimos telefon raqamingizni kiriting ");
        sendMessage.setReplyMarkup(shareContact());


        return sendMessage;
    }

    private ReplyKeyboardMarkup shareContact() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("Aloqani almashish");
        keyboardButton.setRequestContact(true);
        keyboardButtons.add(keyboardButton);
        replyKeyboardMarkup.setKeyboard(List.of(keyboardButtons));
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return USERNAME_BOT;
    }

    @Override
    public String getBotToken() {
        return TOKEN_BOT;
    }


}
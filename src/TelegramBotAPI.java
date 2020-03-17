public class TelegramBotAPI {
    private String botToken;
    private String ChatID;
    private final String mainAPI = "https://api.telegram.org/bot";

    public String getBotToken() {
        return botToken;
    }

    public String getChatID() {
        return ChatID;
    }

    public String getMainAPI() {
        return mainAPI;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }
}

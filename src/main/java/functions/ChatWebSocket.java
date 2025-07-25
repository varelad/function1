package functions;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;

@WebSocket(path = "/chat/{username}")  
public class ChatWebSocket {

    // Declare the type of messages that can be sent and received
    public enum MessageType {USER_JOINED, USER_LEFT, CHAT_MESSAGE}
    public static class ChatMessage {
        private MessageType type;
        private String from;
        private String message;

        public ChatMessage(MessageType type, String from, String message) {
            this.type = type;
            this.from = from;
            this.message = message;
        }

        public MessageType getType() {
            return type;
        }

        public String getFrom() {
            return from;
        }

        public String getMessage() {
            return message;
        }
    }

    @Inject
    WebSocketConnection connection;  

    @OnOpen(broadcast = true)       
    public ChatMessage onOpen() {
        return new ChatMessage(MessageType.USER_JOINED, connection.pathParam("username"), null);
    }

    @OnClose                    
    public void onClose() {
        ChatMessage departure = new ChatMessage(MessageType.USER_LEFT, connection.pathParam("username"), null);
        connection.broadcast().sendTextAndAwait(departure);
    }

    @OnTextMessage(broadcast = true)  
    public ChatMessage onMessage(ChatMessage message) {
        return message;
    }

}

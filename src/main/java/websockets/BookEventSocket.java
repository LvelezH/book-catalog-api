package websockets;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@ServerEndpoint("/notifications")
public class BookEventSocket {
    private static final Logger logger = LogManager.getLogger(BookEventSocket.class);

    List<Session> sessions = Collections.synchronizedList(new ArrayList<Session>());

    @OnOpen
    public void open(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        broadcast("User left on error: ");
        sessions.remove(session);
    }

    public void broadcast(String message) {
        sessions.forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    logger.log(Level.ERROR,"Unable to send message: " + result.getException());
                }
            });
        });
    }
}

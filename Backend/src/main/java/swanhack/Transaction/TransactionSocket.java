package swanhack.Transaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import swanhack.User2.User2;
import swanhack.User2.User2Repository;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/transaction/{userId}")
public class TransactionSocket{

    //map for session and username id
    public static Map < Long, Session > userIdSessionMap = new HashMap< >();

    private static User2Repository user2Repository;

    private final Logger logger = LoggerFactory.getLogger(TransactionSocket.class);

    //username id of recieving user
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId) throws IOException{
        userIdSessionMap.putIfAbsent(userId, session);
        logger.info(userId + " has joined");
    }

//    //userId of recieving user
//    @OnMessage
//    public void onMessage(Session session, String message, @PathParam("userId") long userId) throws IOException{
//
//        sendNotification(message, userId);
//    }

    public void sendTransaction(Transaction transaction){
        try{
            long userId = transaction.getUser2().getId();

            //only send notification if there is user in websocket session
            if(userIdSessionMap.containsKey(userId)){
                userIdSessionMap.get(userId).getBasicRemote().sendText(transaction.toString());
            }

        }catch(IOException e) {
            logger.info("[Notification] " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId){
        userIdSessionMap.remove(userId);
    }

    @Autowired
    public void setUser2Repository(User2Repository repo) {
        user2Repository = repo;
    }
}






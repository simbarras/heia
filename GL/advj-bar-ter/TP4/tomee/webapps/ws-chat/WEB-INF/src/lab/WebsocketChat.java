//--------------------------------
//  WebsocketChat
//  ADVJ Lab 2022/23
//--------------------------------

//import org.json.JSONArray;
package lab;

import jakarta.json.JsonArray;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import jakarta.websocket.*;

import java.util.*;

@ServerEndpoint("/ws/{nickname}")
public class WebsocketChat {

    private static final String NAME_KEY = "nickname";
    // declare thread-safe map
    private static Map<String, Session> activeSessions = Collections.synchronizedMap(new LinkedHashMap<String, Session>());

    //--- WEBSOCKET HANDLERS

    @OnOpen
    public void onOpen(Session session, @PathParam(NAME_KEY) String nickname) throws Exception {

        // check if nickname is already used
        if (nicknameExists(nickname)) {
            session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(4000), "nickname already in use"));
            return;
        }


        System.out.println("new user : " + nickname);

        // remember username in user properties of session
        session.getUserProperties().put(NAME_KEY, nickname);

        // add session to activeSessions
        activeSessions.put(nickname, session);

        // broadcast new user
        JSONObject jobj = new JSONObject();
        jobj.put("cmd", "newUser");
        jobj.put("user", nickname);
        jobj.put("userList", getCurrentNicknames());
        broadcast(jobj.toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {

        System.out.println("message from '" + getNicknameFromSession(session) + "' : " + message);
        broadcast(message);

    }

    @OnClose
    public void onClose(Session session) throws Exception {
        String nickname = getNicknameFromSession(session);
        // Get nickname
        System.out.println("user has left : " + nickname); // Add nickname
        // Remove related session
        session.close();
        activeSessions.remove(nickname);
        // Broadcast user that left
        JSONObject jobj = new JSONObject();
        jobj.put("cmd", "delUser");
        jobj.put("user", nickname);
        jobj.put("userList", getCurrentNicknames());
        broadcast(jobj.toString());

    }

    // broadcast message to all active sessions
    private void broadcast(String msg) {

        // iterate over sessions to send msg
        for (Session session : activeSessions.values()) {
            if (!session.isOpen()) continue;
            try {
                session.getBasicRemote().sendText(msg);
            } catch (Exception e) {
                String nickname = getNicknameFromSession(session);
                System.out.println("Broadcast error: failed to send message to user '" + nickname + "'");
            }
        }

    }

    //--- UTILITY METHODS

    // retrieve nickname stored in session properties
    private String getNicknameFromSession(Session session) {
        return (String) session.getUserProperties().get(NAME_KEY);
    }

    //--- STATIC METHODS

    // return set of currently connected user nicknames
    static Set<String> getCurrentNicknames() {
        return activeSessions.keySet();
    }

    // check if given nickname is already used
    static boolean nicknameExists(String nickname) {
        return activeSessions.containsKey(nickname);
    }

}
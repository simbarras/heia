package lab;//--------------------------------
//  WebsocketChat
//  ADVJ Lab 2022/23
//--------------------------------

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@ApplicationPath("/api")
public class NameCheckREST extends Application {

    @Path("/checkName")
    public static class NameResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response checkName(@QueryParam("nickname") String nickname) {
            JSONObject jobj = new JSONObject();

            // if a nickname is given, then check if it is already used
            if (nickname != null) {
                System.out.println("checkName for : '" + nickname + "'");
                String reason = null;
                if (nickname.trim().length() == 0) {
                    reason = "nickname with spaces only";
                } else if (WebsocketChat.nicknameExists(nickname)) {
                    reason = "nickname already used";
                }
                jobj.put("available", (reason == null));
                if (reason != null)
                    jobj.put("reason", reason);
            }
            // otherwise return the list of currently connected users
            else {
                System.out.println("checkName: no nickname given");
                JSONArray jarr = new JSONArray();
                for (String name : WebsocketChat.getCurrentNicknames()) {
                    jarr.put(name);
                }
                jobj.put("nicknames", jarr);
            }
            return Response.ok(jobj.toString()).build();
        }
    }
}

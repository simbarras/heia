package lab;//--------------------------------
//  WebsocketChat
//  ADVJ Lab 2022/23
//--------------------------------

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/checkName")
public class NameCheck extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json; charset=utf-8");
        final PrintWriter out = resp.getWriter();

        JSONObject jobj = new JSONObject();

        // get nickname parameter
        String nickname = (String) req.getParameter("nickname");

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
            out.print(jobj.toString());
        }

        // otherwise return the list of currently connected users
        else {
            System.out.println("checkName: no nickname given");
            JSONArray jarr = new JSONArray();
            for (String name : WebsocketChat.getCurrentNicknames()) {
                jarr.put(name);
            }
            jobj.put("nicknames", jarr);
            out.print(jobj.toString());
        }

        out.flush();
    }
}

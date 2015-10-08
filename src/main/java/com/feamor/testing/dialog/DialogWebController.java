package com.feamor.testing.dialog;

import com.gs.collections.api.tuple.primitive.IntObjectPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by feamor on 01.10.2015.
 */
@Controller
@Transactional

public class DialogWebController {
    @Autowired
    private DialogManager controller;

    @RequestMapping("/dialog")
    @ResponseBody
    public String startWindow() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h1>Welcome to simple test chat</h1>")
                .append("<hr/><h2>Register new client</h2><br/>")
                .append("<form action=\"dialog/register\">")
                .append("Enter you name<br/>")
                .append("<input type = \"text\" name = \"name\"/><br>")
                .append("Enter password</br>")
                .append("<input type = \"password\" name = \"password\"/><br/>")
                .append("<input type = \"submit\" text = \"Register\"/><br/>")
                .append("</form>")
                .append("<h2>Alrady have login</h2><br/>")
                .append("<form action=\"dialog/login\">")
                .append("Enter login<br/>")
                .append("<input type = \"text\" name = \"name\"/><br>")
                .append("Enter password<br>")
                .append("<input type = \"password\" name = \"password\"><br/>")
                .append("<input type = \"submit\" text = \"Login\"><br/>")
                .append("</form>")
                .append("</body></html>");
        return builder.toString();
    }

    @RequestMapping( value = "/dialog/register")
    public void registerNewClient(@RequestParam(required = true, value = "name") String userNmae,
                                    @RequestParam(required = true, value = "password") String password,
                                    HttpServletResponse httpServletResponse) throws IOException {
        int result = controller.createNewUser(userNmae, password);

        String redirect;
        if (result == DialogManager.Operations.NEW_CLIENT_SUCCESS) {
            redirect = "/dialog/login?name="+userNmae+"&password="+password;
        } else {
            redirect = "/dialog/error?code="+result;
        }

//        httpServletResponse.setHeader("Location", redirect);
        httpServletResponse.sendRedirect(redirect);
    }

    @RequestMapping("/dialog/login")
    public void loginClient(@RequestParam(required = true, value = "name") String userNmae,
                                    @RequestParam(required = true, value = "password") String password,
                                    HttpServletResponse httpServletResponse) throws IOException {
        IntObjectPair<IntObjectPair<String>> result = controller.loginClient(userNmae, password);
        String redirect;
        if (result.getOne() == DialogManager.Operations.LOGIN_SUCCESS) {
            redirect = "/dialog/mypage";
            httpServletResponse.addCookie(new Cookie("session", result.getTwo().getTwo()));
            httpServletResponse.addCookie(new Cookie("client", Integer.toString(result.getTwo().getOne())));
        } else {
            redirect = "/dialog/error?code="+result.getOne();
        }
        httpServletResponse.sendRedirect(redirect);
    }

    @RequestMapping("/dialog/error")
    @ResponseBody
    public String showError(@RequestParam(required = true, value = "code") int errorCode) {
        return "Occour some problem code = "+errorCode;
    }

    private DialogClient checkAuth(String clientId, String session) {

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(session))
            return null;

        int id = -1;
        try {
            id = Integer.parseInt(clientId);
        } catch (NumberFormatException ex) {
            return null;
        }

        return controller.getActiveUser(id, session);
    }

    @RequestMapping("/dialog/mypage")
    public void showMyPage(
            @CookieValue(required = false, value = "session", defaultValue = "") String session,
            @CookieValue(required = false, value = "client", defaultValue = "") String clientCookie,
            HttpServletResponse httpServletResponse) throws IOException {

        DialogClient client = checkAuth(clientCookie, session);
        if (client == null) {
            httpServletResponse.sendRedirect("/dialog/error?code=" + DialogManager.Operations.ERROR_USER_NOT_LOGIN);
        } else {
            IntObjectPair<ArrayList<DialogMessage>> newMessages = controller.readNewMessages(client);
            IntObjectPair<ArrayList<DialogMessage>> receivedMessages = controller.getReceivedMessages(client, 0, 10);
            IntObjectPair<ArrayList<DialogMessage>> sendMessages = controller.getSendedMessages(client, 0, 10);

            StringBuilder builder = new StringBuilder();
            builder.append("<html><body><h1>User messages</h1><hr>")
                    .append("<h2>Hi " + client.getName() + "</h2><br>");
            if (newMessages.getOne() == DialogManager.Operations.NEW_MESSAGE_NO_MESSAGES) {
                builder.append("<h3>There is no new messages</h3><br><hr>");
            }
            if (newMessages.getOne() == DialogManager.Operations.NEW_MESSAGES_READ_ALL) {
                if (newMessages.getTwo() == null) {
                    builder.append("<h3>There is no new messages</h3><br><hr>");
                } else {
                    builder.append("<h3>You have " + newMessages.getTwo().size() + " new messages</h3><br>");
                    for (DialogMessage message : newMessages.getTwo()) {
                        builder.append("<p><b>#" + message.getId() + "</b> " + message.getMessage() + "</p><br>");
                    }
                    builder.append("<hr>");
                }
            }
            if (receivedMessages.getOne() == DialogManager.Operations.GET_RECEIVED_MESSAGES) {
                if (receivedMessages.getTwo() == null || receivedMessages.getTwo().size() == 0) {
                    builder.append("<h3>There is no received messages</h3><br><hr>");
                } else {
                    builder.append("<h3>Received: </h3><br");
                    for (DialogMessage message : receivedMessages.getTwo()) {
                        builder.append("<p><b>#" + message.getId() + "</b> " + message.getMessage() + "</p><br>");
                    }
                    builder.append("<hr>");
                }
            }
            if (sendMessages.getOne() == DialogManager.Operations.GET_SENDED_MESSAGES) {
                if (sendMessages.getTwo() == null || sendMessages.getTwo().size() == 0) {
                    builder.append("<h3>There is no sended messages</h3><br><hr>");
                } else {
                    builder.append("<h3>Sened: </h3><br");
                    for (DialogMessage message : sendMessages.getTwo()) {
                        builder.append("<p><b>#" + message.getId() + "</b> " + message.getMessage() + "</p><br>");
                    }
                    builder.append("<hr>");
                }
            }
            builder.append("<br><hr><br><h2>Actions</h2><br>")
                    .append("<a href=\"/dialog/activeClients\">Show active clients</a><br>")
                    .append("<a href=\"/dialog/send_message\">Send message</a><br>");

            builder.append("</body></html>");
            httpServletResponse.getWriter().write(builder.toString());
        }
    }

    @RequestMapping("/dialog/activeClients")
    public void showActiveClients(
            @CookieValue(required = false, value = "session", defaultValue = "") String session,
            @CookieValue(required = false, value = "client", defaultValue = "") String clientCookie,
            HttpServletResponse httpServletResponse) throws IOException {
        DialogClient client = checkAuth(clientCookie, session);
        if (client == null) {
            httpServletResponse.sendRedirect("/dialog/error?code=" + DialogManager.Operations.ERROR_USER_NOT_LOGIN);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("<html><body><h1>Active clients</h1>");

            int[] activeClients = controller.getActiveClientsIds();
            if (activeClients == null || activeClients.length == 0) {
                builder.append("<p>No active clients</p>");
            } else {
                for (int i = 0; i < activeClients.length; i++) {
                    IntObjectPair<DialogClient.ClientInfo> info = controller.getClientInfo(client, activeClients[i]);
                    if (info != null && info.getOne() == DialogManager.Operations.GET_CLIENT_INFO_SUCCESS && info.getTwo() != null) {
                        builder.append("# "+info.getTwo().id+" : "+info.getTwo().name+"<br>");
                    }
                }
            }

            builder.append("</body></html>");
            httpServletResponse.getWriter().write(builder.toString());
        }

    }

    @RequestMapping("/dialog/send_result")
    public void sendResult(
            @RequestParam(required = true, value = "result") int result,
            HttpServletResponse httpServletResponse) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h1>Send message result</h1><br>");
        switch(result){
            case DialogManager.Operations.SEND_MESSAGE_SUCCESS:
                builder.append("<h2>Message send successfully!</h2><br>");
                break;
            case DialogManager.Operations.SEND_MESSAGE_ERROR_NO_SUCH_RECEIVER:
                builder.append("<h2>Fail to send message. No user with such id.</h2><br>");
                break;
            default:
                builder.append("<h2>Occur some problem.</h2><br>");
                break;
        }
        builder.append("<hr>");
        builder.append("<h2>Next actions: </h2><br>");
        builder.append("<a href = \"/dialog/mypage\">Back to start page</a><br>");
        builder.append("<a href = \"/dialog/send_message\">Send message</a><br>");
        builder.append("<a href = \"/dialog/activeClients\">Show active clients</a><br>");

        builder.append("</body></html>");
        httpServletResponse.getWriter().write(builder.toString());
    }

    @RequestMapping("/dialog/send")
    public void sendMessage(
            @CookieValue(required = false, value = "session", defaultValue = "") String session,
            @CookieValue(required = false, value = "client", defaultValue = "") String clientCookie,
            @RequestParam(required = true, value = "to_client") int toClient,
            @RequestParam(required = true, value = "message") String message,
            HttpServletResponse httpServletResponse) throws IOException {
        DialogClient client = checkAuth(clientCookie, session);
        if (client == null) {
            httpServletResponse.sendRedirect("/dialog/error?code=" + DialogManager.Operations.ERROR_USER_NOT_LOGIN);
        } else {
            //String sendedMessage =
            int result = controller.sendMessage(client, toClient, message);
            httpServletResponse.sendRedirect("/dialog/send_result?result=" + result);
        }
    }

    @RequestMapping("/dialog/send_message")
    public void showSendMessage(
            @CookieValue(required = false, value = "session", defaultValue = "") String session,
            @CookieValue(required = false, value = "client", defaultValue = "") String clientCookie,
            HttpServletResponse httpServletResponse) throws IOException {
        DialogClient client = checkAuth(clientCookie, session);
        if (client == null) {
            httpServletResponse.sendRedirect("/dialog/error?code=" + DialogManager.Operations.ERROR_USER_NOT_LOGIN);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("<html><body><h1>Send message</h1>");

            builder.append("<form action=\"/dialog/send\">")
                    .append("Enter client id<br/>")
                    .append("<input type = \"text\" name = \"to_client\"/><br>")
                    .append("Write message<br/>")
                    .append("<input type = \"text\" name = \"message\"/><br>")
                    .append("<input type = \"submit\" text = \"send\"><br/>")
                    .append("</form>");

            builder.append("</body></html>");
            httpServletResponse.getWriter().write(builder.toString());
        }

    }


}

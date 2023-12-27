package com.sky.webSocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接成功建立调用的方法
     * @param session
     * @param sid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid){
        System.out.println("客户端:"+sid+"建立连接");
        sessionMap.put(sid,session);
    }

    /**
     * 收到消息调用的方法
     * @param message
     * @param sid
     */
    @OnMessage
    public void onMessaage(String message,@PathParam("sid") String sid){
        System.out.println("收到来自客户端:"+sid+"的信息:"+message);
    }

    /**
     * 关闭连接调用的方法
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid")String sid){
        System.out.println("连接断开:"+sid);
        sessionMap.remove(sid);
    }

    public void sendToAllClient(String message){
        Collection<Session> sessions = sessionMap.values();
        for (Session session:sessions){
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

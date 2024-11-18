package com.side.study.chat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.side.study.util.MyLogger.log;


public class SessionManager {

    private List<Session> sessions = new ArrayList<>();

    public synchronized void add(Session session) {
        sessions.add(session);
    }

    public synchronized void remove(Session session) {
        sessions.remove(session);
    }

    public synchronized void closeAll() {
        for (Session session : sessions) {
            session.close();
        }
        sessions.clear();
    }

    public synchronized void sendAll(String message) {
        for (Session session : sessions) {
            try {
                session.send(message);
            } catch (IOException e) {
                log(e);
            }
        }
    }

    public synchronized List<String> getAllUsername() {
        return sessions.stream()
                .filter(session -> session.getUsername() != null)
                .map(Session::getUsername)
                .toList();

    }
}

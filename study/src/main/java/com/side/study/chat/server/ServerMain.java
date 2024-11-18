package com.side.study.chat.server;

import java.io.IOException;

public class ServerMain {

    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        SessionManager sessionManager = new SessionManager();

        // CommandManager
        // CommandManager commandManager = new CommandManagerV1(sessionManager);
        // 디자인 패턴 적용 리펙토링 (커멘드패턴)
        CommandManager commandManager = new CommandManagerV2(sessionManager);

        Server server = new Server(PORT, commandManager, sessionManager);
        server.start();
    }
}

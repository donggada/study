package com.side.study.chat.server;

import java.io.IOException;

public interface CommandManager {
    void execute(String totalMessage, Session session) throws IOException;
}

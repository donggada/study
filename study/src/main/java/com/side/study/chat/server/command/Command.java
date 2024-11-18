package com.side.study.chat.server.command;



import com.side.study.chat.server.Session;

import java.io.IOException;

public interface Command {
    void execute(String[] args, Session session) throws IOException;
}

package com.tony.automationserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
    public static void main( String[] args ) throws IOException
    {
        ServerSocket listener = new ServerSocket(9909);

        try {
            while (true) {
                Socket socket = listener.accept();
                ClientSession session = new ClientSession(socket);
                session.start();
            }
        } finally {
            if (listener != null)
                listener.close();
        }
    }
}

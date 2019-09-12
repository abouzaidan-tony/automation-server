package com.tony.automationserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.tony.automationserver.sqlhelper.SQLHelper;

public class Server 
{
    public static void main( String[] args ) throws IOException
    {

        SQLHelper.GetInstance().ExecuteNonQuery("UPDATE user SET connected = 0", null);
        SQLHelper.GetInstance().ExecuteNonQuery("UPDATE device SET connected = 0", null);

        Thread cc = new CacheCleaner();
        Thread sc = new SessionCleaner();

        cc.start();
        sc.start();

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

package com.tony.automationserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.tony.sqlhelper.helper.SQLHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        try {
            run(args);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.warn("Closing App");
    }

    public static void run(String[] args) throws Exception {
        logger.info("Starting App");
        SQLHelper.GetInstance().ExecuteNonQuery("UPDATE user SET connected = 0", null);
        SQLHelper.GetInstance().ExecuteNonQuery("UPDATE device SET connected = 0", null);
        logger.info("End updating database");

        Thread cc = new CacheCleaner();
        Thread sc = new SessionCleaner();

        cc.start();
        sc.start();

        ServerSocket listener = new ServerSocket(9909);

        SessionPool pool = SessionPool.getInstance();

        try {
            while (true) {
                Socket socket = listener.accept();
                logger.info("Client accepted from IP " + socket.getInetAddress());
                ClientSession session = new ClientSession(socket);
                pool.addSession(session);
            }
        } finally {
            if (listener != null)
                listener.close();
        }
    }
}

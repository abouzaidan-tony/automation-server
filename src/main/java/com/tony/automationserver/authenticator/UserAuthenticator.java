package com.tony.automationserver.authenticator;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Application;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.User;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAuthenticator implements Authenticator<Client> {

    private static Logger logger = LogManager.getLogger(UserAuthenticator.class.getName());

    @Override
    public Client Authenticate(byte[] data) {
        logger.debug(() -> "Authenticating Device");
        if (data.length != 36) {
            logger.debug(() -> "Invalid authentication data");
            return null;
        }
        String userToken = new String(data, 1, 15);
        String appToken = new String(data, 16, 15);
        String deviceCode = new String(data, 31, 5);

        Account account = EntityManager.GetInstance().GetRepository(Account.class)
                .findOneBy(new FilterTuple("token", userToken));

        if (account == null) {
            logger.debug(() -> "Authentication failed : account not found");
            return null;
        }

        logger.debug(() -> "Authentication : " + account);

        boolean found = false;
        for (Application var : account.getSubscriptions()) {
            if (var.token.equals(appToken)) {
                found = true;
                break;
            }
        }

        if (!found)
            return null;

        User d = null;

        for (Client uD : account.getUsers()) {
            if (uD.getKey().equals(deviceCode)) {
                d = (User) uD;
                break;
            }
        }
        if (d == null)
            return null;

        if (d.isConnected() == true) {
            ClientSession s = ClientSession.getUserSessions().get(d.getId());
            if (s != null)
                s.close();
        }

        final String deviceString = d.toString();
        logger.debug(() -> "Authentication : " + deviceString);

        d.setConnected(true);

        EntityManager.GetInstance().Update(d);
        EntityManager.GetInstance().flush();

        return d;
    }
}
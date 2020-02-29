package com.tony.automationserver.authenticator;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Application;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.Device;
import com.tony.sqlhelper.EntityManager;
import com.tony.sqlhelper.query.filter.FilterTuple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeviceAuthenticator implements Authenticator<Client> {

    private static Logger logger = LogManager.getLogger(DeviceAuthenticator.class.getName());

    @Override
    public Client Authenticate(byte[] data) {
        logger.info(() -> "Authenticating Device");
        if (data.length != 36) {
            logger.info(() -> "Invalid authentication data");
            return null;
        }

        String userToken = new String(data, 1, 15);
        String appToken = new String(data, 16, 15);
        String deviceCode = new String(data, 31, 5);

        Account account = EntityManager.GetInstance().GetRepository(Account.class)
                .findOneBy(new FilterTuple("token", userToken));

        if (account == null) {
            logger.info(() -> "Authentication failed : account not found");
            return null;
        }

        logger.info(() -> "Authentication : " + account);

        boolean found = false;
        for (Application var : account.getSubscriptions()) {
            if (var.token.equals(appToken)) {
                found = true;
                break;
            }
        }

        if (!found)
            return null;

        Device d = null;

        for (Client uD : account.getDevices()) {
            if (uD.getKey().equals(deviceCode)) {
                d = (Device) uD;
                break;
            }
        }

        if (d == null)
            return null;
            
        if(d.isConnected() == true) {
            ClientSession s = ClientSession.getDevicesSessions().get(d.getId());
            if(s != null){
                logger.info(() -> "Removing old session " + s.getSocket().getInetAddress());
                s.close();
            }
        }

        final String deviceString = d.toString();
        logger.debug(() -> "Authentication : " + deviceString);

        d.setConnected(true);
        
        EntityManager.GetInstance().Update(d);
        EntityManager.GetInstance().flush();
        
        return d;
    }
}
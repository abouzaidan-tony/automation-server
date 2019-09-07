package com.tony.automationserver.authenticator;

import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.Device;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;

public class DeviceAuthenticator implements Authenticator<Client> {


    @Override
    public Client Authenticate(byte[] data) {
        if(data.length != 41)
            return null;
        String userToken = new String(data, 1, 30);
        String deviceCode = new String(data, 31, 10);

        Account account = EntityManager.GetInstance().GetRepository(Account.class)
                .findOneBy(new FilterTuple("token", userToken));

        if(account == null)
            return null;

        Device d = null;

        for(Client uD : account.devices){
            if(uD.getKey().equals(deviceCode)) {
                d = (Device)uD; break;
            }
        }

        if(d == null)
            return null;

        d.connected = true;
        
        EntityManager.GetInstance().Update(d);
        EntityManager.GetInstance().flush();
        
        return d;
    }
}
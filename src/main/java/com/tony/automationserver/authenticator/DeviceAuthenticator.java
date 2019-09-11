package com.tony.automationserver.authenticator;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Application;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.Device;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;

public class DeviceAuthenticator implements Authenticator<Client> {


    @Override
    public Client Authenticate(byte[] data) {
        if(data.length != 36)
            return null;
        String userToken = new String(data, 1, 15);
        String appToken = new String(data, 16, 15);
        String deviceCode = new String(data, 31, 5);

        Account account = EntityManager.GetInstance().GetRepository(Account.class)
                .findOneBy(new FilterTuple("token", userToken));

        if(account == null)
            return null;

        boolean found = false;
        for (Application var : account.subscriptions) {
            if(var.token.equals(appToken)){
                found = true;
                break;
            }
        }
        
        if(!found)
            return null;

        Device d = null;

        for(Client uD : account.devices){
            if(uD.getKey().equals(deviceCode)) {
                d = (Device)uD; break;
            }
        }

        if(d == null)
            return null;

        if(d.connected == true) {
            ClientSession s = ClientSession.getDevicesSessions().get(d.id);
            if(s != null)
                s.close();
        }

        d.connected = true;
        
        EntityManager.GetInstance().Update(d);
        EntityManager.GetInstance().flush();
        
        return d;
    }
}
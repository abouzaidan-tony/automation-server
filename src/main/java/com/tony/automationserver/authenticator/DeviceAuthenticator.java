package com.tony.automationserver.authenticator;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.Device;
import com.tony.automationserver.client.User;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;

public class DeviceAuthenticator implements Authenticator<Client> {


    @Override
    public Client Authenticate(byte[] data) {

        if(data.length != 41)
            return null;
        String userToken = new String(data, 1, 30);
        String deviceKey = new String(data, 31, 10);

        User c = EntityManager.GetInstance().GetRepository(User.class)
                .findOneBy(new FilterTuple("token", userToken));

        if(c == null)
            return null;

        Device d = null;

        for(Device uD : c.devices){
            if(uD.deviceKey.equals(deviceKey)) {
                d = uD;break;
            }
        }
        if(d == null)
            return null;
        
        return d;
    }

}
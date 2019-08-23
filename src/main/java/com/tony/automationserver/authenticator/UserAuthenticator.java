package com.tony.automationserver.authenticator;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.User;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;

public class UserAuthenticator implements Authenticator<Client> {

    @Override
    public Client Authenticate(byte[] data) {
        Client c = EntityManager.GetInstance().GetRepository(User.class)
                .findOneBy(new FilterTuple("token", new String(data, 1, data.length - 1)));
        return c;
    }

}
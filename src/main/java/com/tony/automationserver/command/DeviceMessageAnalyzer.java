package com.tony.automationserver.command;

import java.util.List;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.Session;
import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Client;
import com.tony.sqlhelper.EntityManager;

public class DeviceMessageAnalyzer extends AbstractMessageAnalyzer {

    @Override
    public List<Client> getCandidates(Client client) {
        Account account = EntityManager.GetInstance().GetRepository(Account.class).find(client.getAccount().getId());
        return account.getUsers();
    }

    @Override
    public Session getSessionById(long id) {
        return ClientSession.getUserSessions().get(id);
    }
}
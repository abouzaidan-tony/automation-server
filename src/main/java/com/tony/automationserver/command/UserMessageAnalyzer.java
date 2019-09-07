package com.tony.automationserver.command;

import java.util.List;

import com.tony.automationserver.client.Account;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.sqlhelper.EntityManager;

public class UserMessageAnalyzer extends AbstractMessageAnalyzer {

    @Override
    public List<Client> getCandidates(Client client) {
        Account account = EntityManager.GetInstance().GetRepository(Account.class).find(client.account.id);
        return account.devices;
    }
}
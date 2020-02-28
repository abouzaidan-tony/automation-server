package com.tony.automationserver.authenticator;

public interface Authenticator<T> {

    public T Authenticate(byte[] data);
}
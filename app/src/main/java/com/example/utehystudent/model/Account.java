package com.example.utehystudent.model;

public class Account {
    private String username, password, account_type, device_token;

    public Account() {
    }

    public Account(String username, String password, String account_type, String device_token) {
        this.username = username;
        this.password = password;
        this.account_type = account_type;
        this.device_token = device_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}

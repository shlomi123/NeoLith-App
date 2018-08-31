package com.shlomi123.chocolith;

public class Admin {
    private String Username;
    private String Password;
    private String AuthUID;

    Admin(){}

    Admin(String username, String password, String authUID)
    {
        Username = username;
        Password = password;
        AuthUID = authUID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getAuthUID() {
        return AuthUID;
    }

    public void setAuthUID(String authUID) {
        AuthUID = authUID;
    }
}

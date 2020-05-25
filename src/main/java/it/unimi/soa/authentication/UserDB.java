package it.unimi.soa.authentication;

import java.util.concurrent.ConcurrentHashMap;

public class UserDB {
    private static UserDB instance;
    private ConcurrentHashMap<String, String> userDB;

    private UserDB() {
        userDB = new ConcurrentHashMap<>();
    }

    public static UserDB getInstance() {
        if(instance == null) {
            synchronized (UserDB.class) {
                if(instance == null) instance = new UserDB();
            }
        }
        return instance;
    }

    public String getPassword(String username) {
        // TODO: STUB, PLS FIX
        return "password";
        //return userDB.get(username);
    }
}

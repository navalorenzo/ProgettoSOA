package it.unimi.soa.as;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 */
public class UserDB {
    private static UserDB instance;
    private ConcurrentHashMap<String, String> userDB;

    private UserDB() {
        userDB = new ConcurrentHashMap<>();
    }

    public static UserDB getInstance() {
        if (instance == null) {
            synchronized (UserDB.class) {
                if (instance == null) instance = new UserDB();
            }
        }
        return instance;
    }

    public String getPassword(String username) {
        return userDB.get(username);
    }

    public void register(String userid, String key) {
        userDB.put(userid, key);
    }
}

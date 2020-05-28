package it.unimi.soa.rest.as;

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
        if(!userDB.containsKey(userid)) {
            userDB.put(userid, key);
        } else {
            throw new IllegalStateException();
        }
    }
}

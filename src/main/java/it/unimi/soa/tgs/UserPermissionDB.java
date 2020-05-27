package it.unimi.soa.tgs;

import it.unimi.soa.service.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class UserPermissionDB {
    private static UserPermissionDB instance;
    private ConcurrentHashMap<String, ArrayList<Service>> userPermissionDB;

    private UserPermissionDB() {
        userPermissionDB = new ConcurrentHashMap<>();
    }

    public static UserPermissionDB getInstance() {
        if(instance == null) {
            synchronized (UserPermissionDB.class) {
                if(instance == null) instance = new UserPermissionDB();
            }
        }
        return instance;
    }

    public boolean isAllowed(String username, Service service) {
        // TODO: STUB, PLS FIX
        return true;
        //return userPermissionDB.get(username).contains(service);
    }
}

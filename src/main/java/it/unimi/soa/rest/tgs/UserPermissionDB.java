package it.unimi.soa.rest.tgs;

import it.unimi.soa.rest.service.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

// TODO: questo componente non è nè aggiornato nè usato bene, da fixare
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

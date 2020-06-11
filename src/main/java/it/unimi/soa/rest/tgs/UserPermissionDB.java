package it.unimi.soa.rest.tgs;

import it.unimi.soa.rest.service.Service;

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
        return userPermissionDB.get(username).contains(service);
    }

    public void addPermission(String username, Service service) {
        if(userPermissionDB.containsKey(username)) {
            userPermissionDB.get(username).add(service);
        } else {
            ArrayList<Service> services = new ArrayList<Service>();
            services.add(service);
            userPermissionDB.put(username, services);
        }
    }
}

package it.unimi.soa.utilities;

import java.util.concurrent.ConcurrentHashMap;

public class SharedPassword {
    private static SharedPassword sharedPassword;
    private String astgskey;
    private ConcurrentHashMap<String, String> serviceskeys;

    private SharedPassword() {
        this.serviceskeys = new ConcurrentHashMap<>();
    }

    public static synchronized SharedPassword getInstance() {
        if (sharedPassword == null)
            sharedPassword = new SharedPassword();
        return sharedPassword;
    }


    public String getTGSSSKey(String service) {
        return serviceskeys.get(service);
    }

    public String getASTGSKey() {
        return astgskey;
    }

    public void registerAstgskey(String astgskey) {
        this.astgskey = astgskey;
    }

    public void registerService(String service, String key) {
        serviceskeys.put(service, key);
    }
}

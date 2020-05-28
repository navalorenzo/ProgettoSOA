package it.unimi.soa.utilities;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 */
public class SharedPassword {
    private static SharedPassword sharedPassword;
    private String astgskey;
    private volatile PublicKey registrationPublicKey;
    private volatile PrivateKey registrationPrivateKey;
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

    public PrivateKey getRegistrationPrivateKey() {
        return registrationPrivateKey;
    }

    public PublicKey getRegistrationPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        return registrationPublicKey;
    }

    public void registerAstgskey(String astgskey) {
        this.astgskey = astgskey;
    }

    public void registerService(String service, String key) {
        serviceskeys.put(service, key);
    }

    public synchronized void initRegistrationPublicAndPrivateKey(String privateKeyFile, String publicKeyFile) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        if(registrationPublicKey == null || registrationPrivateKey == null) {
            registrationPrivateKey = CipherModule.readAsymmetricPrivateKey(privateKeyFile);
            registrationPublicKey = CipherModule.readAsymmetricPublicKey(publicKeyFile);
        }
    }
}

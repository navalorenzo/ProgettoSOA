package it.unimi.soa.otp.server;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps all the backup codes in a map with userid and token. This is stored in the server.
 */
public class BackupCodesDb {

    private static BackupCodesDb backupCodesDb;
    private ConcurrentHashMap<String, ArrayList<String>> db;

    private BackupCodesDb() {
        this.db = new ConcurrentHashMap<>();
    }

    public synchronized static BackupCodesDb getInstance() {
        if (backupCodesDb == null)
            backupCodesDb = new BackupCodesDb();
        return backupCodesDb;
    }

    public int addToken(String userid, String token) {
        db.get(userid).add(DigestUtils.sha1Hex(token));
        return db.get(userid).size();
    }

    public int addUserTokens(String userid, ArrayList<String> tokens) {
        if (containsUser(userid))
            return -1;
        db.put(userid, tokens);
        return tokens.size();
    }

    public int deleteToken(String userid, String token) {
        db.get(userid).remove(token);
        return db.get(userid).size();
    }

    public boolean dropUser(String userid) {
        db.remove(userid);
        return (!db.contains(userid));
    }

    public boolean containsUser(String userid) {
        return db.containsKey(userid);
    }

    public ArrayList<String> getTokens(String userid) {
        return db.get(userid);
    }

    public boolean match(String userid, String token) {
        if (!containsUser(userid))
            return false;
        boolean authorized = db.get(userid).contains(DigestUtils.sha1Hex(token));
        if (authorized)
            deleteToken(userid, DigestUtils.sha1Hex(token));
        return authorized;
    }

    public ConcurrentHashMap getDb() {
        return db;
    }

    public int remainingTokens(String userid) {
        return getTokens(userid).size();
    }
}

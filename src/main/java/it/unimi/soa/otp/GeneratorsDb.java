package it.unimi.soa.otp;

import java.util.concurrent.ConcurrentHashMap;

public class GeneratorsDb {

    private static GeneratorsDb generatorsDb;
    private ConcurrentHashMap<String, Generator> db;

    private GeneratorsDb() {
        this.db = new ConcurrentHashMap<>();
    }

    public static GeneratorsDb getInstance() {
        if (generatorsDb == null)
            generatorsDb = new GeneratorsDb();
        return generatorsDb;
    }

    public String register(String userid) {

        if (!db.containsKey(userid)) {

            // Generate a new random key
            String newSecret = Base32Secret.generate();

            // Setup a configuration
            TOTPConf conf = new TOTPConf(newSecret, "Label");
            conf.setAlgorihtm("SHA1");
            conf.setDigits(6);
            conf.setIssuer("Issuer");
            conf.setPeriod(30);

            Generator newgen = new Generator(conf);

            db.put(userid, newgen);
            BackupCodesDb.getInstance().addUserTokens(userid, conf.genBackupCodes());
        }

        return db.get(userid).getConf().toString();
    }

}

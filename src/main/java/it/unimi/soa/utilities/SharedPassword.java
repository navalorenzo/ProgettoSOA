package it.unimi.soa.utilities;

public class SharedPassword {
    private static final String ASTGS = "passwordDiASeTGS";
    private static final String TGSSS = "passwordDiTGSeSS";

    public static String getASTGSKey() {
        return ASTGS;
    }

    // TODO: magari mettiamo una mappa
    public static String getTGSSSKey() {
        return TGSSS;
    }
}

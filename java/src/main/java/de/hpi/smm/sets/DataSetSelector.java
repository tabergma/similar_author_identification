package de.hpi.smm.sets;

import de.hpi.smm.Config;

public class DataSetSelector {
    public static final int GERMAN_SET = 0;
    public static final int SMM_SET = 1;
    public static final int SPINNER_SET = 2;

    public static AbstractDataSet getDataSet(int selectedSet, int minLength, int limit) {
        LocalSet localSet = new LocalSet(getLocalSetPath(selectedSet), getSetName(selectedSet));
        if (!localSet.hasSize(limit)){
            System.out.println(String.format("Set %s does not have size %d, downloading local copy...", getSetName(selectedSet), limit));
            HanaSet hanaSet = new HanaSet(selectedSet, limit, getSetName(selectedSet));
            hanaSet.applyMinimumLength(minLength);
            SetDownloader setDownloader = new SetDownloader(hanaSet, getLocalSetPath(selectedSet));
            setDownloader.run();
            return new LocalSet(getLocalSetPath(selectedSet), getSetName(selectedSet));
        }
        return localSet;
    }

    private static String getSetName(int selectedSet) {
        switch(selectedSet){
            case GERMAN_SET:
            {
                return "german_set";
            }
            case SMM_SET:
            {
                return "smm_set";
            }
            case SPINNER_SET:
            {
                return "spinner_set";
            }
            default:
            {
                return null;
            }
        }
    }

    private static String getLocalSetPath(int selectedSet) {
        switch(selectedSet){
            case GERMAN_SET:
            {
                return Config.LOCAL_GERMAN_SET_PATH;
            }
            case SMM_SET:
            {
                return Config.LOCAL_SMM_SET_PATH;
            }
            case SPINNER_SET:
            {
                return Config.LOCAL_SPINNER_SET_PATH;
            }
            default:
            {
                return null;
            }
        }
    }
}

package de.hpi.smm.sets;

import de.hpi.smm.Config;

public class DataSetSelector {
    public static final int LOCAL_SET = 0;
    public static final int HANA_SET_SMM = 1;
    public static final int HANA_SET_SPINNER = 2;

    public static AbstractDataSet getDataSet(int selectedSet) {
        switch(selectedSet){
            case LOCAL_SET:
            {
                return new LocalSet(Config.LOCAL_TEST_SET_PATH);
            }
            case HANA_SET_SMM:
            {
                return new HanaSet(HanaSet.DATA_SET_SMM, 100);
            }
            case HANA_SET_SPINNER:
            {
                return new HanaSet(HanaSet.DATA_SET_SPINNER, 100);
            }
            default:
            {
                return null;
            }
        }
    }
}

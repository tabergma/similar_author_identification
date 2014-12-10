package de.hpi.smm.sets;

import de.hpi.smm.Config;

public class DataSetSelector {
    public static final int LOCAL_SET = 0;
    public static final int HANA_SET = 1;

    public static AbstractDataSet getDataSet(int selectedSet) {
        switch(selectedSet){
            case LOCAL_SET:
            {
                return new LocalSet(Config.LOCAL_TEST_SET_PATH);
            }
            case HANA_SET:
            {
                return new HanaSet(10000);
            }
            default:
            {
                return null;
            }
        }
    }
}

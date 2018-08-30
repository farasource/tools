package ghasemi.abbas.abzaar.db.soundmeter;

import android.util.Log;

/**
 * Created by bodekjan on 2016/8/8.
 */
class World {
    static float dbCount = 40;
    static float minDB =100;
    static float maxDB =0;
    static float lastDbCount = dbCount;

    static void setDbCount(float dbValue) {
        //Set the minimum sound change
        float min = 0.5f;
        // Sound decibel value
        float value = 0;
        if (dbValue > lastDbCount) {
            value = dbValue - lastDbCount > min ? dbValue - lastDbCount : min;
        }else{
            value = dbValue - lastDbCount < -min ? dbValue - lastDbCount : -min;
        }
        dbCount = lastDbCount + value * 0.2f ; //To prevent the sound from changing too fast
        lastDbCount = dbCount;
        if(dbCount<minDB) minDB=dbCount;
        if(dbCount>maxDB) maxDB=dbCount;
    }

}
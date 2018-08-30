package ghasemi.abbas.abzaar.db.soundmeter;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by bodekjan on 2016/8/8.
 */
class FileUtil {

    private static final String LOCAL = "abz";

    private static final String LOCAL_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;

    /**
     * Recording file directory
     */
    private static final String REC_PATH = LOCAL_PATH + LOCAL + File.separator;


    /**
     * Automatically create the relevant directory on the SD card
     */
    static {
        File dirRootFile = new File(LOCAL_PATH);
        if (!dirRootFile.exists()) {
            dirRootFile.mkdirs();
        }
        File recFile = new File(REC_PATH);
        if (!recFile.exists()) {
            recFile.mkdirs();
        }
    }

    static File createFile(String fileName) {

        File myCaptureFile = new File(REC_PATH + fileName);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }


}

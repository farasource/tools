package ghasemi.abbas.abzaar;

import androidx.multidex.MultiDexApplication;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * on 04/19/2018.
 */

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //  MultiDex.install(this);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/sans.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }
}

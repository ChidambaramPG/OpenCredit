package chidhu.opencredit;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Author   : Chidambaram P G
 * Date     : 17-04-2018
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Oswald-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}

package youga.tamingtask;

import android.app.Application;

import youga.tamingtask.taming.TamingUtil;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/12 0012-17:17
 */

public class TamingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        TamingUtil.init(this);
    }
}

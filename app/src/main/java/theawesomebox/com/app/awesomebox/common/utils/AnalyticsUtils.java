package theawesomebox.com.app.awesomebox.common.utils;

import com.flurry.android.FlurryAgent;


public class AnalyticsUtils {

    public final static void logAnalytics(String text) {
        // if (!isAdded()) return;
        try {
            FlurryAgent.logEvent(text);
            Logger.info("Flurry", text);
        } catch (Exception e) {
            Logger.caughtException(e);
        }
    }
}

package theawesomebox.com.app.awesomebox.common.utils;

/**
 * Contains constants used in application
 */
public class AppConstants {

    public static final String SERVER_URL = "https://atinybox.space/api/";
    //Server flags
    public static final String FLAG_SUCCESS = "success";

    //
    public static final String JOB_ID = "jobId";
    public static final String JOB_OFFER_ACCEPTED = "Job Accepted";
    public static final String NOTIFICATION_TYPE = "notificationType";
    //Permissions
    public static final int PERMISSION_ACCESS_NETWORK_STATE = 0;
    public static final int NOTIFICATION_ID = 100;
    //  Http requests
    public static final String LOGIN = "login";
    public static final String RESET_BADGE = "user/";
    public static final String SEND_TOKEN_TO_SERVER = "user/";
    public static final String LOGGED_IN = "loggedin?token=";
    public static final String BACKUP_HOME = "user/";
    public static final String SCRIPTS = "script?search_filter=";
    public static final String RUN_SCRIPTS = "socket/run";


    //image type
    public static String IMAGE_THUMB = "_thumb";
    public static String IMAGE_MINI_THUMB = "_mini_thumb";

    /**
     * Create a full server rest api url by embedding provide end point with the server url
     *
     * @param api Rest api endpoint
     * @return Full rest api url
     */
    public static String getServerUrl(String api) {
        return SERVER_URL + api;
    }
}

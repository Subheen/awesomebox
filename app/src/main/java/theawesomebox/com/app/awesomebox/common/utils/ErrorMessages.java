package theawesomebox.com.app.awesomebox.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Administrator on 4/16/2015.
 */
public class ErrorMessages {

    public static void showPromptForTitleAndMessage(String title, String message, Context context) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showPromptForInternet(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("No network connection")
                .setMessage("Can't connect to the network. Check your data connection and try again.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showPromptForInvalidJSON(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Web Service Error")
                .setMessage("Invalid data by webservice. Please contact application vendor")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showPromptForJSONParse(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("JSON Error")
                .setMessage("Error in parsing JSON. Please contact application vendor")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showPromptForUnknownError(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("An Unknown Error has occured. Please try again")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}

package theawesomebox.com.app.awesomebox.apps.module.ui.views;

import android.content.Context;
import android.widget.ImageView;

import theawesomebox.com.app.awesomebox.R;


/**
 * Nothing but an ImageView with a preset image resource
 *
 * @author yildizkabaran
 */
public class ContentView extends ImageView {

    public ContentView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        // set the dummy content image here
        // setImageResource(R.drawable.screenshot);
        setBackgroundResource(R.color.colorPrimary);
    }
}
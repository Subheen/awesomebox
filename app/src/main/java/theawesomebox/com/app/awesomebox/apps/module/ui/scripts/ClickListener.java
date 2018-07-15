package theawesomebox.com.app.awesomebox.apps.module.ui.scripts;

import android.view.View;

public interface ClickListener {

    void onPositionClicked(View view, int position);

    void onLongClicked(int position);
}
package theawesomebox.com.app.awesomebox.apps.module.ui.scripts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        DefaultScriptFragment defaultScriptFragment = new DefaultScriptFragment();
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                args.putString("scriptType", "default");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;

            case 1:
                args.putString("scriptType", "favorite");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;

            case 2:
                args.putString("scriptType", "optimize");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;

            case 3:
                args.putString("scriptType", "security");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;
            case 4:
                args.putString("scriptType", "health");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;
            case 5:
                args.putString("scriptType", "network");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;
            case 6:
                args.putString("scriptType", "connection");
                defaultScriptFragment.setArguments(args);
                return defaultScriptFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Default";
            case 1:
                return "Favorite";
            case 2:
                return "Optimize";
            case 3:
                return "Security";
            case 4:
                return "Health";
            case 5:
                return "Network";
            case 6:
                return "Connection";

        }
        return super.getPageTitle(position);
    }

}

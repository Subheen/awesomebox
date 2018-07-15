package theawesomebox.com.app.awesomebox.apps.business;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import theawesomebox.com.app.awesomebox.common.base.BaseActivity;


/***
 *  Communicate between fragment and activity
 */
public interface OnContentChangeListener {

    /**
     * Get {@link Toolbar} of {@link BaseActivity}
     *
     * @return Toolbar
     */
    Toolbar getToolbar();

    /**
     * Remove current fragment from {@link android.support.v4.app.FragmentTransaction}
     */
    void onRemoveCurrentFragment();

    /**
     * Remove given fragment from {@link android.support.v4.app.FragmentTransaction}
     *
     * @param fragment {@link Fragment}
     */
    void onRemoveCurrentFragment(Fragment fragment);

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment  {@link Fragment}
     * @param contentId Id of {@link android.widget.FrameLayout}
     */
    void onAddFragment(Fragment fragment, int contentId);

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment       {@link Fragment}
     * @param addToBackStack true add else do not add
     */
    void onAddFragment(Fragment fragment, boolean addToBackStack);

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment       {@link Fragment}
     * @param contentId      Id of {@link android.widget.FrameLayout}
     * @param addToBackStack true add else do not add
     */
    void onAddFragment(Fragment fragment, int contentId, boolean addToBackStack);

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment  {@link Fragment}
     * @param contentId Id of {@link android.widget.FrameLayout}
     */
    void onReplaceFragment(Fragment fragment, int contentId);

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment       {@link Fragment}
     * @param addToBackStack true add else do not add
     */
    void onReplaceFragment(Fragment fragment, boolean addToBackStack);

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment       {@link Fragment}
     * @param contentId      Id of {@link android.widget.FrameLayout}
     * @param addToBackStack true add else do not add
     */
    void onReplaceFragment(Fragment fragment, int contentId, boolean addToBackStack);

    /**
     * Get fragment from given id of {@link android.widget.FrameLayout}
     *
     * @param contentId Id of {@link android.widget.FrameLayout}
     * @return Fragment
     */
    Fragment getCurrentFragment(int contentId);

    /**
     * Get Fragment from default {@link android.widget.FrameLayout}
     *
     * @return Fragment
     */
    Fragment getCurrentFragment();

    /**
     * Remove all fragments from the stack
     *
     * @return Fragment
     */
    void onClearBackStack();

    /**
     * @param title title of {@link android.support.design.widget.NavigationView}
     */
    void setDrawerTitle(CharSequence title);

    /**
     * Get parent view of {@link BaseActivity}
     *
     * @return View
     */
    View getParentView();
}

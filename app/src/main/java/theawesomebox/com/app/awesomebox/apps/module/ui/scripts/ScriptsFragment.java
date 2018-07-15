package theawesomebox.com.app.awesomebox.apps.module.ui.scripts;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScriptsFragment extends BaseFragment {

    private String SCRIPT_URL;
    private String searchText;
    private int selectedTab = 0;
    DefaultScriptFragment defaultScriptFragment;



    public ScriptsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the row_backup_files for this fragment
        return inflater.inflate(R.layout.fragment_scripts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTabs(view);
        bindListeners(view);
        defaultScriptFragment = new DefaultScriptFragment();

    }

    private void bindListeners(View view) {


        view.findViewById(R.id.img_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideSoftKeyboard(getActivity());
                searchText = ((EditText) getView().findViewById(R.id.edt_search)).getText().toString();
                callToScriptsSearch();
            }
        });

        ((EditText) view.findViewById(R.id.edt_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AppUtils.hideSoftKeyboard(getActivity());
                    searchText = ((EditText) getView().findViewById(R.id.edt_search)).getText().toString();
                    callToScriptsSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void callToScriptsSearch() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        for (int i = 0; i < 6; i++) {
//            BaseFragment fragment = (BaseFragment) viewPager
//                    .getAdapter()
//                    .instantiateItem(viewPager, i);
//            if (fragment != null)
//                defaultScriptFragment.sendSearchTextToFrags(searchText, selectedTab);
//        }

        //   defaultScriptFragment.sendSearchTextToFrags(searchText, selectedTab);

        if (viewPager.getCurrentItem() == 0) {
            DefaultScriptFragment frag1 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag1.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 1) {
            DefaultScriptFragment frag2 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag2.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 2) {
            DefaultScriptFragment frag3 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag3.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 3) {
            DefaultScriptFragment frag4 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag4.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 4) {
            DefaultScriptFragment frag5 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag5.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 5) {
            DefaultScriptFragment frag6 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag6.sendSearchTextToFrags(searchText, selectedTab);
        } else if (viewPager.getCurrentItem() == 6) {
            DefaultScriptFragment frag7 = (DefaultScriptFragment) viewPager
                    .getAdapter()
                    .instantiateItem(viewPager, viewPager.getCurrentItem());
            frag7.sendSearchTextToFrags(searchText, selectedTab);
        }

    }

    private void initTabs(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new TabsPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.white));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here

                selectedTab = tab.getPosition();
                ((EditText) getView().findViewById(R.id.edt_search)).setText("");
                searchText = "";

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return getString(R.string.scripts);
    }


}

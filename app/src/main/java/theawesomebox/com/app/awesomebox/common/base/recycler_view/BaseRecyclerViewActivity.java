package theawesomebox.com.app.awesomebox.common.base.recycler_view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import theawesomebox.com.app.awesomebox.common.base.BaseActivity;
import theawesomebox.com.app.awesomebox.common.utils.Logger;


public class BaseRecyclerViewActivity extends BaseActivity implements
        OnRecyclerViewItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    // region VARIABLES
    protected static final int LINEAR_LAYOUT_MANAGER = 0;
    protected static final int GRID_LAYOUT_MANAGER = 1;
    protected static final int STAGGERED_GRID_LAYOUT_MANAGER = 2;
    // endregion

    // region LIFE_CYCLE

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    // endregion


    // region LAYOUT_MANAGER_INFO
    protected BaseRecyclerViewActivity.CurrentLayoutManagerInfo getLayoutManagerInfo(RecyclerView recyclerView) {
        if (recyclerView == null)
            return null;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        BaseRecyclerViewActivity.CurrentLayoutManagerInfo currentLayoutManagerInfo =
                new BaseRecyclerViewActivity.CurrentLayoutManagerInfo();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;
            currentLayoutManagerInfo.setFirstVisiblePosition(manager.findFirstVisibleItemPosition());
            currentLayoutManagerInfo.setLastVisiblePosition(manager.findLastVisibleItemPosition());
            currentLayoutManagerInfo.setCurrentLayoutManager(GRID_LAYOUT_MANAGER);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
            currentLayoutManagerInfo.setFirstVisiblePosition(manager.findFirstVisibleItemPosition());
            currentLayoutManagerInfo.setLastVisiblePosition(manager.findLastVisibleItemPosition());
            currentLayoutManagerInfo.setCurrentLayoutManager(LINEAR_LAYOUT_MANAGER);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            currentLayoutManagerInfo.setFirstVisiblePosition(manager.findFirstVisibleItemPositions(null)[0]);
            currentLayoutManagerInfo.setLastVisiblePosition(manager.findLastVisibleItemPositions(null)[0]);
            currentLayoutManagerInfo.setCurrentLayoutManager(STAGGERED_GRID_LAYOUT_MANAGER);
        }
        return currentLayoutManagerInfo;
    }

    @Override
    public void onRefresh() {
    }

    /**
     * Returns name of current fragment
     *
     * @return BaseRecyclerViewFragment name
     */
    public String getRecyclerViewTag() {
        return getClass().getSimpleName();
    }
    // endregion

    // region RECYCLER_VIEW

    /**
     * @param holder view holder on clicked item
     */
    @Override
    public void onRecyclerViewItemClick(BaseRecyclerViewHolder holder) {
        Logger.info(getRecyclerViewTag(), "onRecyclerViewItemClick adapter position " +
                holder.getAdapterPosition() + " row_backup_files position " + holder.getLayoutPosition());
    }

    /**
     * @param holder     view holder on clicked item
     * @param resourceId resource id of clicked item
     */
    @Override
    public void onRecyclerViewChildItemClick(BaseRecyclerViewHolder holder, int resourceId) {
        Logger.info(getRecyclerViewTag(), "onRecyclerViewChildItemClick adapter position " +
                holder.getAdapterPosition() + " row_backup_files position " + holder.getLayoutPosition() +
                ", resourceId " + resourceId);
    }

    final protected class CurrentLayoutManagerInfo {

        private int firstVisiblePosition = 0, lastVisiblePosition = 0;
        private int currentLayoutManager = LINEAR_LAYOUT_MANAGER;

        public int getFirstVisiblePosition() {
            return firstVisiblePosition;
        }

        public void setFirstVisiblePosition(int firstVisiblePosition) {
            this.firstVisiblePosition = firstVisiblePosition;
        }

        public int getLastVisiblePosition() {
            return lastVisiblePosition;
        }

        public void setLastVisiblePosition(int lastVisiblePosition) {
            this.lastVisiblePosition = lastVisiblePosition;
        }

        public int getCurrentLayoutManager() {
            return currentLayoutManager;
        }

        public void setCurrentLayoutManager(int currentLayoutManager) {
            this.currentLayoutManager = currentLayoutManager;
        }
    }
    // endregion

}

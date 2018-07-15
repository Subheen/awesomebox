package theawesomebox.com.app.awesomebox.common.base.recycler_view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

/**
 * Base fragment for whole application to manage basic functions
 */
abstract public class BaseRecyclerViewFragment extends BaseFragment implements OnRecyclerViewItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    protected static final int LINEAR_LAYOUT_MANAGER = 0;
    protected static final int GRID_LAYOUT_MANAGER = 1;
    protected static final int STAGGERED_GRID_LAYOUT_MANAGER = 2;
    // region VARIABLES
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    // endregion

    // region LIFE_CYCLE
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        if (refreshLayout != null)
            refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            BaseRecyclerViewAdapter adapter = (BaseRecyclerViewAdapter) recyclerView.getAdapter();
            if (adapter != null)
                adapter.clearResources();
            recyclerView.setAdapter(null);
        }
    }
    // endregion

    // region SCROLL
    protected void addOnScrollListener() {
        if (recyclerView != null)
            recyclerView.addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }
            });
    }
    // endregion

    // region LAYOUT_MANAGER_INFO
    protected CurrentLayoutManagerInfo getLayoutManagerInfo(RecyclerView recyclerView) {
        if (recyclerView == null)
            return null;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        CurrentLayoutManagerInfo currentLayoutManagerInfo = new CurrentLayoutManagerInfo();
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

    @Override
    public String getTitle() {
        return null;
    }

    /**
     * Returns name of current fragment
     *
     * @return BaseRecyclerViewFragment name
     */
    public String getRecyclerViewFragmentTag() {
        return getClass().getSimpleName();
    }
    // endregion

    // region RECYCLER_VIEW

    /**
     * Get reference of RecyclerView
     *
     * @return {@link RecyclerView}
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * Get reference of SwipeRefreshLayout
     *
     * @return {@link SwipeRefreshLayout}
     */
    public SwipeRefreshLayout getSwipeLayout() {
        return refreshLayout;
    }

    /**
     * Set adapter
     *
     * @param adapter {@link BaseRecyclerViewAdapter}
     */
    protected void setAdapter(BaseRecyclerViewAdapter adapter) {
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * @param holder view holder on clicked item
     */
    @Override
    public void onRecyclerViewItemClick(BaseRecyclerViewHolder holder) {
        Logger.info(getRecyclerViewFragmentTag(), "onRecyclerViewItemClick adapter position " +
                holder.getAdapterPosition() + " row_backup_files position " + holder.getLayoutPosition());
    }

    /**
     * @param holder     view holder on clicked item
     * @param resourceId resource id of clicked item
     */
    @Override
    public void onRecyclerViewChildItemClick(BaseRecyclerViewHolder holder, int resourceId) {
        Logger.info(getRecyclerViewFragmentTag(), "onRecyclerViewChildItemClick adapter position " +
                holder.getAdapterPosition() + " row_backup_files position " + holder.getLayoutPosition() +
                ", resourceId " + resourceId);
    }

    public void stopRefresh() {
        if (refreshLayout == null)
            return;
        refreshLayout.setRefreshing(false);
    }
    // endregion

    public void startRefresh() {
        if (refreshLayout == null)
            return;
        refreshLayout.setRefreshing(true);
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

}
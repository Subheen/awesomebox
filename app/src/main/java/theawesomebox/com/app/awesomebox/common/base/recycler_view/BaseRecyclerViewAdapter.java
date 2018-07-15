package theawesomebox.com.app.awesomebox.common.base.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import theawesomebox.com.app.awesomebox.apps.business.BaseItem;


/**
 * Base view adapter for managing {@link RecyclerView.Adapter}
 */
abstract public class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements View.OnClickListener {

    /**
     * Contains all items for recycler view
     */
    private List<BaseItem> items = null;
    /**
     * RecyclerView single item view and/or (maybe) single element inside RecyclerView click listener
     */
    private OnRecyclerViewItemClickListener itemClickListener = null;

    public BaseRecyclerViewAdapter(List<BaseItem> items, OnRecyclerViewItemClickListener itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * If an item view has large or expensive data bound to it such as large bitmaps,
     * this may be a good place to release those resources.
     *
     * @param holder The ViewHolder for the view being recycled
     */
    @Override
    public void onViewRecycled(BaseRecyclerViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /**
     * Override this method if you want to implement click listener for your recycler view's resources
     * like if you want to add click listener for Button/Image inside your single recycler view item.
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
    }

    protected String getClassName() {
        return getClass().getSimpleName();
    }

    /**
     * @return get all adapter's items
     */
    public List<BaseItem> getAdapterItems() {
        return items;
    }

    public int getAdapterCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * @param item add single adapter into list
     */
    public void add(BaseItem item) {
        if (items == null)
            items = new ArrayList<>();
        items.add(item);
    }

    /**
     * @param newItems add list of items into adapter
     */
    public void addAll(List<BaseItem> newItems) {
        if (items == null)
            items = new ArrayList<>();
        items.addAll(newItems);
    }

    /**
     * @param position to update
     * @param item     update item at given position
     */
    public void update(int position, BaseItem item) {
        items.set(position, item);
    }


    public void clearItems() {
        if (items != null) {
            int size = items.size();
            if (size > 0) {
                for (int i = 0; i < size; i++)
                    items.remove(0);
                notifyItemRangeRemoved(0, size);
            }
        }
    }

    /**
     * Remove item from recycler view
     *
     * @param item {@link BaseItem}
     */
    public void remove(BaseItem item) {
        if (items == null)
            return;
        items.remove(item);
    }

    /**
     * Remove item from recycler view
     *
     * @param position {@link BaseItem position}
     */
    public void remove(int position) {
        if (items == null)
            return;
        items.remove(position);
    }

    /**
     * Get {@link BaseItem} at given index
     *
     * @param position index
     * @return Adapter item
     */
    public BaseItem getItemAt(int position) {
        return items == null ? null : items.get(position);
    }

    public OnRecyclerViewItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void clearResources() {
        clearItems();
        items = null;
        itemClickListener = null;
    }
}

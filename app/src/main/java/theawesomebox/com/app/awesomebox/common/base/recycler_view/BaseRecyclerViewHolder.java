package theawesomebox.com.app.awesomebox.common.base.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Base view adapter for managing {@link RecyclerView.ViewHolder}
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    /**
     * Default constructor
     *
     * @param view {@link View}
     */
    public BaseRecyclerViewHolder(View view) {
        this(view, false);
    }

    /**
     * Set click listen of given view
     *
     * @param view            view
     * @param isViewClickable true clickable view, else not
     */
    public BaseRecyclerViewHolder(View view, boolean isViewClickable) {
        super(view);
        if (isViewClickable)
            view.setOnClickListener(this);
    }

    /**
     * Initialize view if necessary set their click listeners.
     *
     * @return view holder
     */
    protected abstract BaseRecyclerViewHolder populateView();

    /**
     * Override this method if you want to implement click listener for your recycler view items
     * a.k.a recycler view single item click listener
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {

    }

    public TextView findTextViewById(View view, int resourceId) {
        return (TextView) view.findViewById(resourceId);
    }
}

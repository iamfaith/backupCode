package naco_siren.github.ui_utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

public abstract class EndlessRecyclerOnScrollListener extends OnScrollListener {
    public static String LOG_TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private int mCurrentPage;
    private int mFirstVisibleItem;
    private boolean mIsLoading;
    private LinearLayoutManager mLinearLayoutManager;
    private int mPreviousTotal;
    private int mTotalItemCount;
    private int mVisibleItemCount;
    private int mVisibleThreshold;

    public abstract void onLoadMore(int i);

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold) {
        this.mPreviousTotal = 0;
        this.mIsLoading = true;
        this.mCurrentPage = 1;
        this.mLinearLayoutManager = linearLayoutManager;
        this.mVisibleThreshold = visibleThreshold;
    }

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this(linearLayoutManager, 6);
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        this.mVisibleItemCount = recyclerView.getChildCount();
        this.mTotalItemCount = this.mLinearLayoutManager.getItemCount();
        this.mFirstVisibleItem = this.mLinearLayoutManager.findFirstVisibleItemPosition();
        if (this.mIsLoading && this.mTotalItemCount > this.mPreviousTotal) {
            this.mIsLoading = false;
            this.mPreviousTotal = this.mTotalItemCount;
        }
        if (!this.mIsLoading && this.mTotalItemCount - this.mVisibleItemCount <= this.mFirstVisibleItem + this.mVisibleThreshold) {
            this.mCurrentPage++;
            onLoadMore(this.mCurrentPage);
            this.mIsLoading = true;
        }
    }
}

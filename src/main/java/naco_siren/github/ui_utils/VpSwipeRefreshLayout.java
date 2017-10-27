package naco_siren.github.ui_utils;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class VpSwipeRefreshLayout extends SwipeRefreshLayout {
    private boolean mIsVpDragger;
    private final int mTouchSlop;
    private float startX;
    private float startY;

    public VpSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.startY = ev.getY();
                this.startX = ev.getX();
                this.mIsVpDragger = false;
                break;
            case 1:
            case 3:
                this.mIsVpDragger = false;
                break;
            case 2:
                if (this.mIsVpDragger) {
                    return false;
                }
                float endY = ev.getY();
                float distanceX = Math.abs(ev.getX() - this.startX);
                float distanceY = Math.abs(endY - this.startY);
                if (distanceX > ((float) this.mTouchSlop) && distanceX > distanceY) {
                    this.mIsVpDragger = true;
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
    }
}

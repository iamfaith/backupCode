package naco_siren.github.a1point3acres.activities.thread_activity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.activities.thread_activity.ThreadActivity;
import naco_siren.github.a1point3acres.contents.ThreadComment;
import naco_siren.github.a1point3acres.contents.ThreadCommentAdapter;
import naco_siren.github.a1point3acres.contents.ThreadCommentAdapter.IThreadCommentViewHolderOnClick;
import naco_siren.github.ui_utils.EndlessRecyclerOnScrollListener;

public class ThreadCommentFragment extends Fragment implements IThreadCommentViewHolderOnClick {
    private static final String LOG_TAG = ThreadCommentFragment.class.getSimpleName();
    private OnCommentFragmentInteractionListener mCommentInteractionListener;
    private LinearLayoutManager mLinearLayoutLayoutManager;
    private RecyclerView mRecyclerView;
    private final int mRecyclerViewVisibleThreshold = 1;
    private View mRootView;
    private ThreadCommentAdapter mThreadCommentAdapter;
    private ArrayList<ThreadComment> mThreadComments;

    public interface OnCommentFragmentInteractionListener {
        void onClickThreadCommentContent(ThreadComment threadComment);
    }

    public static ThreadCommentFragment newInstance() {
        return new ThreadCommentFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommentFragmentInteractionListener) {
            this.mCommentInteractionListener = (OnCommentFragmentInteractionListener) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnContentFragmentInteractionListener");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_thread_comment, container, false);
        this.mRecyclerView = (RecyclerView) this.mRootView.findViewById(R.id.thread_comment_recyclerview);
        return this.mRootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mLinearLayoutLayoutManager = new LinearLayoutManager(getContext());
    }

    public void onResume() {
        super.onResume();
        Context context = getContext();
        if (context != null) {
            this.mLinearLayoutLayoutManager = new LinearLayoutManager(context);
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mCommentInteractionListener = null;
    }

    public void onClickThreadCommentContent(View caller, int position) {
        if (this.mCommentInteractionListener != null) {
            this.mCommentInteractionListener.onClickThreadCommentContent((ThreadComment) this.mThreadComments.get(position));
        }
    }

    public void onClickThreadCommentUserAvatar(ImageView callerImage, int position) {
        Log.d(LOG_TAG, "Clicked #" + position + " avatar");
    }

    public void resetData(@NonNull ArrayList<ThreadComment> threadComments) {
        this.mThreadComments = threadComments;
        this.mRecyclerView.setLayoutManager(this.mLinearLayoutLayoutManager);
        this.mThreadCommentAdapter = new ThreadCommentAdapter(getContext(), this.mThreadComments, this);
        this.mRecyclerView.setAdapter(this.mThreadCommentAdapter);
    }

    public int addOnScrollListener() {
        if (this.mRecyclerView == null) {
            return 1;
        }
        this.mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this.mLinearLayoutLayoutManager, 1) {
            public void onLoadMore(int current_page) {
                ((ThreadActivity) ThreadCommentFragment.this.getActivity()).fetchThread(ThreadCommentFragment.this.getString(R.string.toast_load_more_thread_comment_progress), true, false);
            }
        });
        return 0;
    }

    public int notifyDataChanged() {
        if (this.mThreadCommentAdapter == null) {
            return -1;
        }
        this.mThreadCommentAdapter.notifyDataSetChanged();
        return 0;
    }

    @TargetApi(13)
    public void showProgress(boolean showProgress, boolean needHidingExistingContent) {
        int i = 0;
        boolean hideExistingContent = true;
        Log.v(LOG_TAG, "Content: Show Background Progress Animation: " + (showProgress ? "GO!" : "STOP"));
        int shortAnimTime = getResources().getInteger(17694720);
        if (!(showProgress && needHidingExistingContent)) {
            hideExistingContent = false;
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (hideExistingContent) {
            i = 8;
        }
        recyclerView.setVisibility(i);
        this.mRecyclerView.animate().setDuration((long) shortAnimTime).alpha(hideExistingContent ? 0.0f : 1.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ThreadCommentFragment.this.mRecyclerView.setVisibility(hideExistingContent ? 8 : 0);
            }
        });
    }
}

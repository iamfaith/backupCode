package naco_siren.github.a1point3acres.activities.thread_activity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;

public class ThreadContentFragment extends Fragment {
    private static final String ARG_IMAGE_HREFS = "image_hrefs";
    private static final String ARG_PURE_TEXT = "pure_text";
    private static final String LOG_TAG = ThreadContentFragment.class.getSimpleName();
    private OnContentFragmentInteractionListener mContentInteractionListener;
    private float mDP;
    private ArrayList<String> mImageHrefs;
    private LinearLayout mLinearLayout;
    private String mPureText;
    private View mRootView;
    private TextView mTextView;

    public interface OnContentFragmentInteractionListener {
        void onContentFragmentInteraction(Uri uri);
    }

    public static ThreadContentFragment newInstance() {
        return new ThreadContentFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContentFragmentInteractionListener) {
            this.mContentInteractionListener = (OnContentFragmentInteractionListener) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnContentFragmentInteractionListener");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_thread_content, container, false);
        this.mLinearLayout = (LinearLayout) this.mRootView.findViewById(R.id.thread_content_linearlayout);
        this.mTextView = (TextView) this.mRootView.findViewById(R.id.thread_content_puretext);
        return this.mRootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    public void onDetach() {
        super.onDetach();
        this.mContentInteractionListener = null;
    }

    public int refreshUI(String pureText, ArrayList<String> imageHrefs) {
        this.mDP = getContext().getResources().getDisplayMetrics().density;
        this.mPureText = pureText;
        this.mImageHrefs = imageHrefs;
        this.mTextView.setText(pureText);
        if (this.mImageHrefs != null) {
            int imageCount = this.mImageHrefs.size();
            for (int i = 0; i < imageCount; i++) {
                ImageView imageView = newContentImageView();
                Picasso.with(getContext()).load((String) this.mImageHrefs.get(i)).into(imageView);
                LayoutParams lp = new LayoutParams(-1, -1);
                lp.setMargins(0, (int) (this.mDP * getResources().getDimension(R.dimen.thread_content_imageview_margin_medium)), 0, 0);
                this.mLinearLayout.addView(imageView, i + 1, lp);
            }
        }
        return 0;
    }

    private ImageView newContentImageView() {
        return new ImageView(getContext());
    }

    @TargetApi(13)
    public void showProgress(boolean showProgress, boolean needHidingExistingContent) {
        int i = 0;
        boolean hideExistingContent = true;
        Log.v(LOG_TAG, "Content: Show Background Progress Animation: " + (showProgress ? "GO!" : "STOP") + "\n\tHide existing content: " + needHidingExistingContent);
        int shortAnimTime = getResources().getInteger(17694720);
        if (!(showProgress && needHidingExistingContent)) {
            hideExistingContent = false;
        }
        LinearLayout linearLayout = this.mLinearLayout;
        if (hideExistingContent) {
            i = 8;
        }
        linearLayout.setVisibility(i);
        this.mLinearLayout.animate().setDuration((long) shortAnimTime).alpha(hideExistingContent ? 0.0f : 1.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ThreadContentFragment.this.mLinearLayout.setVisibility(hideExistingContent ? 8 : 0);
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (this.mContentInteractionListener != null) {
            this.mContentInteractionListener.onContentFragmentInteraction(uri);
        }
    }
}

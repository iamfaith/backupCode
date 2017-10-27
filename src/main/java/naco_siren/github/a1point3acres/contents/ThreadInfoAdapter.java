package naco_siren.github.a1point3acres.contents;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils.AvatarSize;

public class ThreadInfoAdapter extends Adapter {
    public static final int ERROR_NOT_INITIALIZED = 1;
    private final String LOG_TAG = ThreadInfoAdapter.class.getSimpleName();
    private Context mContext;
    public IThreadInfoViewHolderOnClick mThreadInfoViewHolderOnClick;
    private ArrayList<ThreadInfo> mThreadInfos;

    public interface IThreadInfoViewHolderOnClick {
        void onClickThreadInfoContent(View view, int i);

        void onClickThreadInfoUserAvatar(ImageView imageView, int i);
    }

    public static class ThreadInfoViewHolder extends ViewHolder implements OnClickListener {
        private final String LOG_TAG = ThreadInfo.class.getSimpleName();
        public View mContentView;
        public IThreadInfoViewHolderOnClick mIThreadInfoViewHolderOnClick;
        public View mRootView;
        public ImageView mThreadAgreedImageView;
        public ImageView mThreadAttachedImageView;
        public CircularImageView mThreadAuthorUserAvatarImageView;
        public TextView mThreadAuthorUserNameTextView;
        public TextView mThreadCommentCountTextView;
        public TextView mThreadDateTimeTextView;
        public ImageView mThreadDigestImageView;
        public ImageView mThreadHotImageView;
        public ImageView mThreadLockedImageView;
        public ImageView mThreadNewImageView;
        public ImageView mThreadPinnedImageView;
        public TextView mThreadReadCountTextView;
        public ImageView mThreadRecommendedImageView;
        public TextView mThreadTitleTextView;
        public TextView mThreadTypeTextView;

        public ThreadInfoViewHolder(View v, IThreadInfoViewHolderOnClick threadInfoViewHolderOnClick) {
            super(v);
            this.mRootView = v;
            this.mIThreadInfoViewHolderOnClick = threadInfoViewHolderOnClick;
            this.mContentView = v.findViewById(R.id.main_threadinfo_content);
            this.mThreadAuthorUserAvatarImageView = (CircularImageView) v.findViewById(R.id.main_threadinfo_author_useravatar);
            this.mThreadAuthorUserNameTextView = (TextView) v.findViewById(R.id.main_threadinfo_author_username);
            this.mThreadDateTimeTextView = (TextView) v.findViewById(R.id.main_threadinfo_datetime);
            this.mThreadLockedImageView = (ImageView) v.findViewById(R.id.main_threadinfo_locked);
            this.mThreadNewImageView = (ImageView) v.findViewById(R.id.main_threadinfo_newcomment);
            this.mThreadAttachedImageView = (ImageView) v.findViewById(R.id.main_threadinfo_attached);
            this.mThreadPinnedImageView = (ImageView) v.findViewById(R.id.main_threadinfo_pin);
            this.mThreadDigestImageView = (ImageView) v.findViewById(R.id.main_threadinfo_digest);
            this.mThreadRecommendedImageView = (ImageView) v.findViewById(R.id.main_threadinfo_recommend);
            this.mThreadHotImageView = (ImageView) v.findViewById(R.id.main_threadinfo_hot);
            this.mThreadAgreedImageView = (ImageView) v.findViewById(R.id.main_threadinfo_agree);
            this.mThreadTitleTextView = (TextView) v.findViewById(R.id.main_threadinfo_title);
            this.mThreadTypeTextView = (TextView) v.findViewById(R.id.main_threadinfo_type);
            this.mThreadReadCountTextView = (TextView) v.findViewById(R.id.main_threadinfo_read);
            this.mThreadCommentCountTextView = (TextView) v.findViewById(R.id.main_threadinfo_comment);
            this.mContentView.setOnClickListener(this);
            this.mThreadAuthorUserAvatarImageView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v == this.mThreadAuthorUserAvatarImageView) {
                this.mIThreadInfoViewHolderOnClick.onClickThreadInfoUserAvatar((ImageView) v, getAdapterPosition());
            } else {
                this.mIThreadInfoViewHolderOnClick.onClickThreadInfoContent(v, getAdapterPosition());
            }
        }
    }

    public ThreadInfoAdapter(Context context, ArrayList<ThreadInfo> threadInfos, IThreadInfoViewHolderOnClick threadInfoViewHolderOnClick) {
        this.mContext = context;
        this.mThreadInfos = threadInfos;
        this.mThreadInfoViewHolderOnClick = threadInfoViewHolderOnClick;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThreadInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.threadinfo_viewholder_recyclerview_main, parent, false), this.mThreadInfoViewHolderOnClick);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ThreadInfo threadInfo = (ThreadInfo) this.mThreadInfos.get(position);
        ImageView circularImageView = ((ThreadInfoViewHolder) holder).mThreadAuthorUserAvatarImageView;
        Picasso.with(this.mContext).load(UserAvatarUtils.getAvatarHrefByID(threadInfo.getThreadAuthorID(), AvatarSize.MEDIUM)).placeholder((int) R.drawable.ic_threadinfo_account_circle).error((int) R.drawable.ic_threadinfo_account_circle).into(circularImageView);
        circularImageView.setBorderWidth(0.0f);
        ((ThreadInfoViewHolder) holder).mThreadAuthorUserNameTextView.setText(threadInfo.getThreadAuthorName());
        ((ThreadInfoViewHolder) holder).mThreadDateTimeTextView.setText(threadInfo.getThreadDateTime() + " ");
        ((ThreadInfoViewHolder) holder).mThreadDigestImageView.setVisibility(threadInfo.isThreadDigest() ? 0 : 8);
        ((ThreadInfoViewHolder) holder).mThreadRecommendedImageView.setVisibility(threadInfo.isThreadRecommended() ? 0 : 8);
        ((ThreadInfoViewHolder) holder).mThreadHotImageView.setVisibility(threadInfo.isThreadHot() ? 0 : 8);
        ((ThreadInfoViewHolder) holder).mThreadAgreedImageView.setVisibility(threadInfo.isThreadAgreed() ? 0 : 8);
        ((ThreadInfoViewHolder) holder).mThreadTitleTextView.setText(threadInfo.getThreadTitle());
        ((ThreadInfoViewHolder) holder).mThreadTypeTextView.setText(threadInfo.getThreadType());
        ((ThreadInfoViewHolder) holder).mThreadTypeTextView.setSelected(true);
        switch (threadInfo.getThreadPin()) {
            case LOCAL_PIN:
                ((ThreadInfoViewHolder) holder).mThreadPinnedImageView.setVisibility(0);
                break;
            default:
                ((ThreadInfoViewHolder) holder).mThreadPinnedImageView.setVisibility(8);
                break;
        }
        switch (threadInfo.getThreadStatus()) {
            case LOCKED:
                ((ThreadInfoViewHolder) holder).mThreadLockedImageView.setVisibility(0);
                ((ThreadInfoViewHolder) holder).mThreadNewImageView.setVisibility(8);
                break;
            case NEW:
                ((ThreadInfoViewHolder) holder).mThreadLockedImageView.setVisibility(8);
                break;
            default:
                ((ThreadInfoViewHolder) holder).mThreadLockedImageView.setVisibility(8);
                break;
        }
        ((ThreadInfoViewHolder) holder).mThreadAttachedImageView.setVisibility(threadInfo.isThreadAttached() ? 0 : 8);
        long threadReadCount = threadInfo.getThreadReadCount();
        long threadCommentCount = threadInfo.getThreadCommentCount();
        if (threadReadCount > 0) {
            ((ThreadInfoViewHolder) holder).mThreadReadCountTextView.setText(String.valueOf(threadReadCount));
        } else {
            ((ThreadInfoViewHolder) holder).mThreadReadCountTextView.setText("-");
        }
        if (threadCommentCount > 0) {
            ((ThreadInfoViewHolder) holder).mThreadCommentCountTextView.setText(String.valueOf(threadCommentCount));
        } else {
            ((ThreadInfoViewHolder) holder).mThreadCommentCountTextView.setText("-");
        }
    }

    public int getItemCount() {
        return this.mThreadInfos == null ? -1 : this.mThreadInfos.size();
    }
}

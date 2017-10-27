package naco_siren.github.a1point3acres.contents;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils.AvatarSize;

public class ThreadCommentAdapter extends Adapter {
    private final String LOG_TAG = ThreadCommentAdapter.class.getSimpleName();
    private Context mContext;
    public IThreadCommentViewHolderOnClick mThreadCommentViewHolderOnClick;
    private ArrayList<ThreadComment> mThreadComments;

    public interface IThreadCommentViewHolderOnClick {
        void onClickThreadCommentContent(View view, int i);

        void onClickThreadCommentUserAvatar(ImageView imageView, int i);
    }

    public static class ThreadCommentViewHolder extends ViewHolder implements OnClickListener {
        private final String LOG_TAG = ThreadComment.class.getSimpleName();
        public View mContentView;
        public IThreadCommentViewHolderOnClick mIThreadCommentViewHolderOnClick;
        public View mRootView;
        public CircularImageView mThreadCommentAuthorUserAvatarImageView;
        public TextView mThreadCommentAuthorUserNameTextView;
        public TextView mThreadCommentBodyTextView;
        public TextView mThreadCommentDateTimeTextView;
        public TextView mThreadCommentQuoteAuthorNameTextView;
        public TextView mThreadCommentQuoteBodyTextView;
        public LinearLayout mThreadCommentQuoteContent;
        public TextView mThreadCommentQuoteDateTimeTextView;

        public ThreadCommentViewHolder(View v, IThreadCommentViewHolderOnClick threadCommentViewHolderOnClick) {
            super(v);
            this.mRootView = v;
            this.mIThreadCommentViewHolderOnClick = threadCommentViewHolderOnClick;
            this.mContentView = v.findViewById(R.id.thread_comment_content);
            this.mThreadCommentAuthorUserAvatarImageView = (CircularImageView) v.findViewById(R.id.thread_comment_author_useravatar);
            this.mThreadCommentAuthorUserNameTextView = (TextView) v.findViewById(R.id.thread_comment_author_username);
            this.mThreadCommentDateTimeTextView = (TextView) v.findViewById(R.id.thread_comment_datetime);
            this.mThreadCommentBodyTextView = (TextView) v.findViewById(R.id.thread_comment_body);
            this.mThreadCommentQuoteContent = (LinearLayout) v.findViewById(R.id.thread_comment_quote_content);
            this.mThreadCommentQuoteAuthorNameTextView = (TextView) v.findViewById(R.id.thread_comment_quote_author_username);
            this.mThreadCommentQuoteDateTimeTextView = (TextView) v.findViewById(R.id.thread_comment_quote_datetime);
            this.mThreadCommentQuoteBodyTextView = (TextView) v.findViewById(R.id.thread_comment_quote_body);
            this.mContentView.setOnClickListener(this);
            this.mThreadCommentAuthorUserAvatarImageView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v == this.mThreadCommentAuthorUserAvatarImageView) {
                this.mIThreadCommentViewHolderOnClick.onClickThreadCommentUserAvatar((ImageView) v, getAdapterPosition());
            } else {
                this.mIThreadCommentViewHolderOnClick.onClickThreadCommentContent(v, getAdapterPosition());
            }
        }
    }

    public ThreadCommentAdapter(Context context, ArrayList<ThreadComment> threadComments, IThreadCommentViewHolderOnClick threadCommentViewHolderOnClick) {
        this.mContext = context;
        this.mThreadComments = threadComments;
        this.mThreadCommentViewHolderOnClick = threadCommentViewHolderOnClick;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThreadCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.threadcomment_viewholder_recyclerview_thread, parent, false), this.mThreadCommentViewHolderOnClick);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ThreadComment threadComment = (ThreadComment) this.mThreadComments.get(position);
        ImageView circularImageView = ((ThreadCommentViewHolder) holder).mThreadCommentAuthorUserAvatarImageView;
        Picasso.with(this.mContext).load(UserAvatarUtils.getAvatarHrefByID(threadComment.getCommentAuthorID(), AvatarSize.MEDIUM)).placeholder((int) R.drawable.ic_threadinfo_account_circle).error((int) R.drawable.ic_threadinfo_account_circle).into(circularImageView);
        circularImageView.setBorderWidth(0.0f);
        ((ThreadCommentViewHolder) holder).mThreadCommentAuthorUserNameTextView.setText(threadComment.getCommentAuthorName());
        ((ThreadCommentViewHolder) holder).mThreadCommentDateTimeTextView.setText(threadComment.getCommentDateTime());
        if (threadComment.hasQuote()) {
            ((ThreadCommentViewHolder) holder).mThreadCommentQuoteContent.setVisibility(0);
            ((ThreadCommentViewHolder) holder).mThreadCommentQuoteAuthorNameTextView.setText(threadComment.getQuoteAuthorName());
            ((ThreadCommentViewHolder) holder).mThreadCommentQuoteDateTimeTextView.setText(threadComment.getQuoteDateTime());
            ((ThreadCommentViewHolder) holder).mThreadCommentQuoteBodyTextView.setText(threadComment.getQuoteBody());
        } else {
            ((ThreadCommentViewHolder) holder).mThreadCommentQuoteContent.setVisibility(8);
        }
        ((ThreadCommentViewHolder) holder).mThreadCommentBodyTextView.setText(threadComment.getCommentBody());
    }

    public int getItemCount() {
        return this.mThreadComments == null ? -1 : this.mThreadComments.size();
    }
}

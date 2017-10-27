package naco_siren.github.a1point3acres.contents;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;

public class BlogInfoAdapter extends Adapter {
    public static final int ERROR_NOT_INITIALIZED = 1;
    private final String LOG_TAG = BlogInfoAdapter.class.getSimpleName();
    public IBlogInfoViewHolderOnClick mBlogInfoViewHolderOnClick;
    private ArrayList<BlogInfo> mBlogInfos;
    private Context mContext;

    public interface IBlogInfoViewHolderOnClick {
        void onClickBlogInfoContent(View view, int i);
    }

    public static class BlogInfoViewHolder extends ViewHolder implements OnClickListener {
        private final String LOG_TAG = BlogInfo.class.getSimpleName();
        public TextView mBlogDateDayTextView;
        public TextView mBlogDateMonthTextView;
        public TextView mBlogDateYearTextView;
        public TextView mBlogIntroTextView;
        public TextView mBlogTitleTextView;
        public View mContentView;
        public IBlogInfoViewHolderOnClick mIBlogInfoViewHolderOnClick;
        public View mRootView;

        public BlogInfoViewHolder(View v, IBlogInfoViewHolderOnClick blogInfoViewHolderOnClick) {
            super(v);
            this.mRootView = v;
            this.mIBlogInfoViewHolderOnClick = blogInfoViewHolderOnClick;
            this.mContentView = v.findViewById(R.id.blog_content);
            this.mBlogDateMonthTextView = (TextView) v.findViewById(R.id.blog_date_month);
            this.mBlogDateDayTextView = (TextView) v.findViewById(R.id.blog_date_day);
            this.mBlogDateYearTextView = (TextView) v.findViewById(R.id.blog_date_year);
            this.mBlogTitleTextView = (TextView) v.findViewById(R.id.blog_title);
            this.mBlogIntroTextView = (TextView) v.findViewById(R.id.main_bloginfo_intro);
            this.mContentView.setOnClickListener(this);
        }

        public void onClick(View v) {
            this.mIBlogInfoViewHolderOnClick.onClickBlogInfoContent(v, getAdapterPosition());
        }
    }

    public BlogInfoAdapter(Context context, ArrayList<BlogInfo> blogInfos, IBlogInfoViewHolderOnClick blogInfoViewHolderOnClick) {
        this.mContext = context;
        this.mBlogInfos = blogInfos;
        this.mBlogInfoViewHolderOnClick = blogInfoViewHolderOnClick;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlogInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bloginfo_viewholder_recyclerview_main, parent, false), this.mBlogInfoViewHolderOnClick);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        BlogInfo blogInfo = (BlogInfo) this.mBlogInfos.get(position);
        ((BlogInfoViewHolder) holder).mBlogDateMonthTextView.setText(blogInfo.getBlogDateMonth());
        ((BlogInfoViewHolder) holder).mBlogDateDayTextView.setText(blogInfo.getBlogDateDay());
        ((BlogInfoViewHolder) holder).mBlogDateYearTextView.setText(blogInfo.getBlogDateYear());
        ((BlogInfoViewHolder) holder).mBlogTitleTextView.setText(blogInfo.getBlogTitle());
        ((BlogInfoViewHolder) holder).mBlogIntroTextView.setText(blogInfo.getBlogIntro() + "……");
    }

    public int getItemCount() {
        return this.mBlogInfos == null ? -1 : this.mBlogInfos.size();
    }
}

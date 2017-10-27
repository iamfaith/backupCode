package naco_siren.github.a1point3acres.contents;

import android.util.Log;

public class ThreadComment {
    private final String LOG_TAG = ThreadComment.class.getSimpleName();
    private String mCommentAuthorID;
    private String mCommentAuthorName;
    private String mCommentBody;
    private String mCommentDateTime;
    private int mCommentID;
    private int mCommentIndex;
    private String mFastReplyHref;
    private boolean mHasQuote;
    private boolean mIsLouzhu;
    private String mQuoteAuthorName;
    private String mQuoteBody;
    private String mQuoteDateTime;
    private String mQuoteText;
    private String mThreadID;

    public ThreadComment(String threadID, int commentIndex, int commentID, String commentAuthorName, String commentAuthorID, String commentDateTime, String commentBody, String fastReplyHref, boolean hasQuote, String quoteText) {
        this.mThreadID = threadID;
        this.mCommentIndex = commentIndex;
        this.mCommentID = commentID;
        this.mCommentAuthorName = commentAuthorName;
        this.mCommentAuthorID = commentAuthorID;
        this.mCommentDateTime = commentDateTime;
        this.mCommentBody = commentBody;
        this.mHasQuote = hasQuote;
        this.mQuoteText = quoteText;
        this.mFastReplyHref = fastReplyHref;
    }

    public void setQuote(String quoteAuthorName, String quoteDateTime, String quoteBody) {
        if (this.mHasQuote) {
            this.mQuoteAuthorName = quoteAuthorName;
            this.mQuoteDateTime = quoteDateTime;
            this.mQuoteBody = quoteBody;
            return;
        }
        Log.e(this.LOG_TAG, "Cannot set quote specs to a comment without quote!");
    }

    public String getThreadID() {
        return this.mThreadID;
    }

    public int getCommentIndex() {
        return this.mCommentIndex;
    }

    public int getCommentID() {
        return this.mCommentID;
    }

    public String getCommentAuthorName() {
        return this.mCommentAuthorName;
    }

    public String getCommentAuthorID() {
        return this.mCommentAuthorID;
    }

    public String getCommentDateTime() {
        return this.mCommentDateTime;
    }

    public String getCommentBody() {
        return this.mCommentBody;
    }

    public boolean hasQuote() {
        return this.mHasQuote;
    }

    public String getQuoteText() {
        return this.mQuoteText;
    }

    public String getQuoteAuthorName() {
        if (hasQuote()) {
            return this.mQuoteAuthorName;
        }
        Log.e(this.LOG_TAG, "Cannot get quote spec: comment contains no quote!");
        return null;
    }

    public String getQuoteDateTime() {
        if (hasQuote()) {
            return this.mQuoteDateTime;
        }
        Log.e(this.LOG_TAG, "Cannot get quote spec: comment contains no quote!");
        return null;
    }

    public String getQuoteBody() {
        if (hasQuote()) {
            return this.mQuoteBody;
        }
        Log.e(this.LOG_TAG, "Cannot get quote spec: comment contains no quote!");
        return null;
    }

    public String getFastReplyHref() {
        return this.mFastReplyHref;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#" + this.mCommentIndex + ": Comment " + this.mCommentID + " @ " + this.mThreadID);
        stringBuilder.append("\n\tAuthor: " + this.mCommentAuthorName + ", " + this.mCommentAuthorID);
        stringBuilder.append("\n\tDateTime: " + this.mCommentDateTime);
        stringBuilder.append("\n\tBody: " + this.mCommentBody);
        stringBuilder.append("\n\tReplyHref: " + this.mFastReplyHref);
        if (this.mHasQuote) {
            stringBuilder.append("\nQUOTE: " + this.mQuoteText);
            stringBuilder.append("\n\tAuthor: " + this.mQuoteAuthorName);
            stringBuilder.append("\n\tDateTime: " + this.mQuoteDateTime);
            stringBuilder.append("\n\tBody: " + this.mQuoteBody);
        }
        return stringBuilder.toString();
    }
}

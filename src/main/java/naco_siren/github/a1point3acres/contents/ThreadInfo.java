package naco_siren.github.a1point3acres.contents;

public class ThreadInfo {
    private final String LOG_TAG = ThreadInfo.class.getSimpleName();
    private String mThreadAuthorID;
    private String mThreadAuthorName;
    private String mThreadAuthorTextColor;
    private long mThreadCommentCount;
    private String mThreadDateTime;
    private String mThreadHref;
    private String mThreadID;
    private int mThreadIndex;
    private boolean mThreadIsAgreed;
    private boolean mThreadIsAttached;
    private boolean mThreadIsDigest;
    private boolean mThreadIsHot;
    private boolean mThreadIsRecommended;
    private ThreadPin mThreadPin;
    private long mThreadReadCount;
    private ThreadStatus mThreadStatus;
    private String mThreadTitle;
    private String mThreadTitleTextColor;
    private boolean mThreadTitleTextIsBold;
    private boolean mThreadTitleTextIsUnderlined;
    private String mThreadType;

    public ThreadInfo(int threadIndex, String threadHref, String threadID, String threadTitle, String threadAuthorName, String threadAuthorID, String threadDateTime, ThreadPin threadPin, ThreadStatus threadStatus, String threadType, boolean threadIsAttached, long threadReadCount, long threadCommentCount, boolean threadIsDigest, boolean threadIsRecommended, boolean threadIsHot, boolean threadIsAgreed, boolean threadTitleTextIsBold, boolean threadTitleTextIsUnderlined, String threadTitleTextColor, String threadAuthorTextColor) {
        this.mThreadIndex = threadIndex;
        this.mThreadHref = threadHref;
        this.mThreadID = threadID;
        this.mThreadTitle = threadTitle;
        this.mThreadAuthorName = threadAuthorName;
        this.mThreadAuthorID = threadAuthorID;
        this.mThreadDateTime = threadDateTime;
        this.mThreadPin = threadPin;
        this.mThreadStatus = threadStatus;
        this.mThreadType = threadType;
        this.mThreadIsAttached = threadIsAttached;
        this.mThreadReadCount = threadReadCount;
        this.mThreadCommentCount = threadCommentCount;
        this.mThreadIsDigest = threadIsDigest;
        this.mThreadIsRecommended = threadIsRecommended;
        this.mThreadIsHot = threadIsHot;
        this.mThreadIsAgreed = threadIsAgreed;
        this.mThreadTitleTextIsBold = threadTitleTextIsBold;
        this.mThreadTitleTextIsUnderlined = threadTitleTextIsUnderlined;
        this.mThreadTitleTextColor = threadTitleTextColor;
        this.mThreadAuthorTextColor = threadAuthorTextColor;
    }

    public String toString() {
        String threadPin;
        String threadStatus;
        StringBuilder stringBuilder = new StringBuilder("#" + this.mThreadIndex + ": Thread " + this.mThreadID + ", HREF: " + this.mThreadHref + "\n\tTitle: " + this.mThreadTitle + "\n\tAuthor: " + this.mThreadAuthorName + ", " + this.mThreadAuthorID + "\n\tDate: " + this.mThreadDateTime);
        switch (this.mThreadPin) {
            case GLOBAL_PIN:
                threadPin = "全局置顶";
                break;
            case GROUP_PIN:
                threadPin = "分类置顶";
                break;
            case LOCAL_PIN:
                threadPin = "本版置顶";
                break;
            case NORMAL:
                threadPin = "不置顶";
                break;
            default:
                threadPin = "UNKNOWN置顶";
                break;
        }
        switch (this.mThreadStatus) {
            case LOCKED:
                threadStatus = "已关闭";
                break;
            case NEW:
                threadStatus = "新帖子";
                break;
            case COMMON:
                threadStatus = "正常";
                break;
            default:
                threadStatus = "未知状态";
                break;
        }
        stringBuilder.append("\nSTATUS\n\tPin: " + threadPin + "\n\tStatus: " + threadStatus + "\n\tType: " + this.mThreadType + "\n\tAttached: " + this.mThreadIsAttached);
        stringBuilder.append("\nTREND\n\tReadCount: " + this.mThreadReadCount + "\n\tCommentCount: " + this.mThreadCommentCount);
        stringBuilder.append("\n\tDigest: " + this.mThreadIsDigest + "\n\tRecommended: " + this.mThreadIsRecommended + "\n\tHot: " + this.mThreadIsHot + "\n\tAgreed: " + this.mThreadIsAgreed);
        stringBuilder.append("STYLE\n\tBold: " + this.mThreadTitleTextIsBold + "\n\tUnderlined: " + this.mThreadTitleTextIsUnderlined);
        stringBuilder.append("\n\tTitleColor: " + this.mThreadTitleTextColor + "\n\tAuthorColor: " + this.mThreadAuthorTextColor);
        return stringBuilder.toString();
    }

    public int getThreadIndex() {
        return this.mThreadIndex;
    }

    public String getThreadHref() {
        return this.mThreadHref;
    }

    public String getThreadID() {
        return this.mThreadID;
    }

    public String getThreadTitle() {
        return this.mThreadTitle;
    }

    public String getThreadAuthorName() {
        return this.mThreadAuthorName;
    }

    public String getThreadAuthorID() {
        return this.mThreadAuthorID;
    }

    public String getThreadDateTime() {
        return this.mThreadDateTime;
    }

    public ThreadPin getThreadPin() {
        return this.mThreadPin;
    }

    public ThreadStatus getThreadStatus() {
        return this.mThreadStatus;
    }

    public String getThreadType() {
        return this.mThreadType;
    }

    public long getThreadCommentCount() {
        return this.mThreadCommentCount;
    }

    public long getThreadReadCount() {
        return this.mThreadReadCount;
    }

    public boolean isThreadDigest() {
        return this.mThreadIsDigest;
    }

    public boolean isThreadRecommended() {
        return this.mThreadIsRecommended;
    }

    public boolean isThreadHot() {
        return this.mThreadIsHot;
    }

    public boolean isThreadAgreed() {
        return this.mThreadIsAgreed;
    }

    public boolean isThreadAttached() {
        return this.mThreadIsAttached;
    }

    public boolean isThreadTitleTextBold() {
        return this.mThreadTitleTextIsBold;
    }

    public boolean isThreadTitleTextUnderlined() {
        return this.mThreadTitleTextIsUnderlined;
    }

    public String getThreadTitleTextColor() {
        return this.mThreadTitleTextColor;
    }

    public String getThreadAuthorTextColor() {
        return this.mThreadAuthorTextColor;
    }
}

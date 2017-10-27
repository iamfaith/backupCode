package naco_siren.github.a1point3acres.contents;

public class BlogInfo {
    private final String LOG_TAG = BlogInfo.class.getSimpleName();
    private String mBlogDateDay;
    private String mBlogDateMonth;
    private String mBlogDateYear;
    private String mBlogHref;
    private int mBlogIndex;
    private String mBlogIntro;
    private String mBlogTitle;

    public BlogInfo(int blogIndex, String blogHref, String blogTitle, String blogIntro, String blogDateYear, String blogDateMonth, String blogDateDay) {
        this.mBlogIndex = blogIndex;
        this.mBlogHref = blogHref;
        this.mBlogTitle = blogTitle;
        this.mBlogIntro = blogIntro;
        this.mBlogDateYear = blogDateYear;
        this.mBlogDateMonth = blogDateMonth;
        this.mBlogDateDay = blogDateDay;
    }

    public String getDate() {
        return this.mBlogDateYear + "-" + this.mBlogDateMonth + "-" + this.mBlogDateDay;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("#" + this.mBlogIndex + ": " + this.mBlogHref);
        stringBuilder.append("\n\tTitle: " + this.mBlogTitle);
        stringBuilder.append("\n\tDate: " + getDate());
        stringBuilder.append("\n\tIntro: " + this.mBlogIntro);
        return stringBuilder.toString();
    }

    public int getBlogIndex() {
        return this.mBlogIndex;
    }

    public String getBlogHref() {
        return this.mBlogHref;
    }

    public String getBlogTitle() {
        return this.mBlogTitle;
    }

    public String getBlogIntro() {
        return this.mBlogIntro;
    }

    public String getBlogDateYear() {
        return this.mBlogDateYear;
    }

    public String getBlogDateMonth() {
        return this.mBlogDateMonth;
    }

    public String getBlogDateDay() {
        return this.mBlogDateDay;
    }
}

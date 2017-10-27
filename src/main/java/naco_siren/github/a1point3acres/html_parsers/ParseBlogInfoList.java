package naco_siren.github.a1point3acres.html_parsers;

import android.util.Log;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.contents.BlogInfo;
import naco_siren.github.http_utils.HttpErrorType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseBlogInfoList {
    private final String LOG_TAG = ParseBlogInfoList.class.getSimpleName();
    private int mBlogIndex;
    private ArrayList<BlogInfo> mBlogInfos;
    private int mBlogPageIndex;
    private Document mDoc;
    private HttpErrorType mErrorCode;
    private boolean mHasNextPage;
    private boolean mOutputBlogInfo;

    public ParseBlogInfoList(int subforumPageIndex, Document doc, int blogsCount) {
        this.mBlogPageIndex = subforumPageIndex;
        this.mDoc = doc;
        this.mBlogInfos = new ArrayList(10);
        this.mBlogIndex = blogsCount;
        this.mOutputBlogInfo = false;
        this.mHasNextPage = false;
    }

    public void setOutputBlogInfo(boolean outputBlogInfo) {
        this.mOutputBlogInfo = outputBlogInfo;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }

    public HttpErrorType execute() {
        Element postLI = null;
        try {
            Element pageContentDIV = this.mDoc.getElementsByClass("pagecontent listpage").first();
            Elements postInfoLIs = pageContentDIV.getElementsByTag("ul").first().getElementsByTag("li");
            if (pageContentDIV.getElementsByClass("next").first().children().size() != 0) {
                this.mHasNextPage = true;
            }
            Log.d(this.LOG_TAG, "=== BlogList has " + (this.mHasNextPage ? "" : "NO ") + "next page ===\n");
            int postInfoLICount = postInfoLIs.size();
            for (int i = 0; i < postInfoLICount; i++) {
                postLI = (Element) postInfoLIs.get(i);
                Element blogInfoDateSPAN = postLI.getElementsByClass("date_time").first();
                String blogDateYear = blogInfoDateSPAN.getElementsByClass("year").first().text();
                String blogDateMonth = blogInfoDateSPAN.getElementsByClass("month").first().text();
                String blogDateDay = blogInfoDateSPAN.getElementsByClass("day").first().text();
                Element blogInfoContentDIV = postLI.getElementsByClass("mybloglist").first();
                Element blogInfoTitleH2 = blogInfoContentDIV.getElementsByTag("h2").first();
                Element blogInfoTitleA = blogInfoTitleH2.getElementsByTag("a").first();
                String blogTitle = blogInfoTitleA.text();
                String blogHref = blogInfoTitleA.attr("href");
                blogInfoTitleH2.remove();
                Element subInfoPostDIV = blogInfoContentDIV.getElementsByClass("subinfopost").first();
                if (subInfoPostDIV != null) {
                    subInfoPostDIV.remove();
                }
                BlogInfo postInfo = new BlogInfo(this.mBlogIndex, blogHref, blogTitle, blogInfoContentDIV.text(), blogDateYear, blogDateMonth, blogDateDay);
                this.mBlogInfos.add(postInfo);
                this.mBlogIndex++;
                if (this.mOutputBlogInfo) {
                    Log.d(this.LOG_TAG, postInfo.toString());
                }
            }
            this.mErrorCode = HttpErrorType.SUCCESS;
            return this.mErrorCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION, ParsingBlogInfoList");
            this.mErrorCode = HttpErrorType.ERROR_BLOGINFOLIST_PARSE_FAILURE;
            System.out.println(postLI.html());
            return this.mErrorCode;
        }
    }

    public boolean hasNextPage() {
        return this.mHasNextPage;
    }

    public ArrayList<BlogInfo> getBlogInfos() {
        return this.mBlogInfos;
    }

    public static String getBlogInfoListHref(int pageIndex) {
        return "http://www.1point3acres.com/BlogPosts/page/" + pageIndex;
    }
}

package naco_siren.github.a1point3acres.html_parsers;

import android.util.Log;
import naco_siren.github.http_utils.HttpErrorType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseBlog {
    private final String LOG_TAG = ParseBlog.class.getSimpleName();
    private String mBlogContentPureText;
    private int mBlogID;
    private Document mDoc;
    private HttpErrorType mErrorCode;
    private boolean mOutputBlogContent;

    public ParseBlog(Document doc) {
        this.mDoc = doc;
        this.mOutputBlogContent = false;
    }

    public void setOutputBlogContent(boolean outputBlogContent) {
        this.mOutputBlogContent = outputBlogContent;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }

    public HttpErrorType execute() {
        int i = 0;
        Element blogListBody = null;
        try {
            Element contentLI = this.mDoc.getElementsByClass("singlecontent").first().getElementsByTag("li").first();
            contentLI.getElementsByTag("h2").remove();
            for (String className : new String[]{"meta_tags", "pagination", "subinfopost", "yarpp-related", "yarpp-related yarpp-related-none"}) {
                Elements unwantedELEs = contentLI.getElementsByClass(className);
                if (unwantedELEs != null) {
                    unwantedELEs.remove();
                }
            }
            String[] idsToRemove = new String[]{"comments"};
            int length = idsToRemove.length;
            while (i < length) {
                Element unwantedELE = contentLI.getElementById(idsToRemove[i]);
                if (unwantedELE != null) {
                    unwantedELE.remove();
                }
                i++;
            }
            this.mBlogContentPureText = contentLI.text();
            this.mErrorCode = HttpErrorType.SUCCESS;
            return this.mErrorCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION, ParsingBlogInfoList");
            this.mErrorCode = HttpErrorType.ERROR_BLOG_PARSE_FAILURE;
            System.out.println(blogListBody.html());
            return this.mErrorCode;
        }
    }

    public String getBlogContentPureText() {
        return this.mBlogContentPureText;
    }

    public static String generateBlogShareText(String blogTitle, String blogHref) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("【一亩三分地：官方博客】\n" + blogTitle);
        stringBuilder.append("\n" + blogHref);
        return stringBuilder.toString();
    }
}

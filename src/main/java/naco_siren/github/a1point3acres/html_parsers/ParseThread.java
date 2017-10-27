package naco_siren.github.a1point3acres.html_parsers;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import naco_siren.github.a1point3acres.contents.ThreadComment;
import naco_siren.github.http_utils.HttpErrorType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseThread {
    private final String LOG_TAG = ParseThread.class.getSimpleName();
    private Document mDoc;
    private HttpErrorType mErrorCode;
    private String mFormHash;
    private boolean mOutputThreadComment;
    private boolean mOutputThreadContent;
    private int mThreadCommentIndex;
    private ArrayList<ThreadComment> mThreadComments;
    private ArrayList<String> mThreadContentImgHrefs;
    private String mThreadContentPureText;
    private String mThreadID;
    private int mThreadPageCount;
    private int mThreadPageIndex;

    public ParseThread(String threadID, int threadPageIndex, Document doc, int threadCommentsCount) {
        this.mThreadID = threadID;
        this.mThreadPageIndex = threadPageIndex;
        this.mDoc = doc;
        this.mThreadComments = new ArrayList(40);
        this.mThreadCommentIndex = threadCommentsCount;
        this.mOutputThreadContent = false;
        this.mOutputThreadComment = false;
        this.mThreadPageCount = -1;
    }

    public void setOutputThreadContent(boolean outputThreadContent) {
        this.mOutputThreadContent = outputThreadContent;
    }

    public void setOutputThreadComment(boolean outputThreadComment) {
        this.mOutputThreadComment = outputThreadComment;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }

    public HttpErrorType execute() {
        Element threadListBody = null;
        if (this.mThreadPageIndex == 1) {
            Elements pagesDIVs = this.mDoc.getElementById("pgt").getElementsByClass("pg");
            if (pagesDIVs == null || pagesDIVs.size() == 0) {
                this.mThreadPageCount = 1;
            } else {
                this.mThreadPageCount = Integer.parseInt(pagesDIVs.first().getElementsByTag("span").first().attr("title").split(" ")[1]);
            }
            Log.d(this.LOG_TAG, "=== Thread has " + this.mThreadPageCount + " pages ===\n");
            Element formHashINPUT = null;
            try {
                formHashINPUT = this.mDoc.getElementById("f_pst").getElementsByAttributeValue("name", "formhash").first();
            } catch (Exception e) {
            }
            if (formHashINPUT == null) {
                try {
                    formHashINPUT = this.mDoc.getElementById("scbar_form").getElementsByAttributeValue("name", "formhash").first();
                } catch (Exception e2) {
                    Log.v(this.LOG_TAG, "Parse FormHash failure on approach #1");
                }
            }
            if (formHashINPUT == null) {
                try {
                    formHashINPUT = this.mDoc.getElementById("searhsort").getElementsByAttributeValue("name", "formhash").first();
                } catch (Exception e3) {
                    Log.v(this.LOG_TAG, "Parse FormHash failure on approach #2");
                }
            }
            if (formHashINPUT == null) {
                try {
                    formHashINPUT = this.mDoc.getElementsByAttributeValue("name", "formhash").first();
                } catch (Exception e4) {
                    Log.e(this.LOG_TAG, "Parse FormHash failure on approach #3 !!! No HashForm found at all!");
                }
            }
            if (formHashINPUT != null) {
                this.mFormHash = formHashINPUT.attr("value");
            }
        }
        Elements candidatePosts = this.mDoc.getElementById("postlist").children();
        int postsCount = 0;
        ArrayList<Element> arrayList = new ArrayList(30);
        Iterator it = candidatePosts.iterator();
        while (it.hasNext()) {
            Element candidatePost = (Element) it.next();
            if (candidatePost.attr("id").startsWith("post_")) {
                postsCount++;
                arrayList.add(candidatePost);
            }
        }
        if (this.mThreadPageIndex == 1) {
            Log.d(this.LOG_TAG, "Thread " + this.mThreadID + ": " + (postsCount - 1) + " posts in page " + this.mThreadPageIndex + " including Thread Content.");
        } else {
            Log.d(this.LOG_TAG, "Thread " + this.mThreadID + ": " + postsCount + " posts in page " + this.mThreadPageIndex);
        }
        int i = 0;
        while (i < postsCount) {
            String postDate;
            Element post = (Element) arrayList.get(i);
            int postID = Integer.parseInt(post.id().split("_")[1]);
            Element plcTD0 = post.getElementsByClass("plc").first();
            Element authiDIV = plcTD0.getElementsByClass("authi").first();
            boolean isLouzhu = authiDIV.getElementsByTag("img").first().attr("src").contains("ico_lz");
            String authorID = authiDIV.getElementsByTag("a").first().attr("href").split("uid-")[1].split(".html")[0];
            String authorName = authiDIV.getElementsByClass("xi2").first().text();
            Element em = authiDIV.getElementsByTag("em").first();
            Element span = em.getElementsByTag("span").first();
            if (span == null) {
                postDate = em.text().split("发表于 ")[1];
            } else {
                postDate = span.attr("title");
            }
            Element t_fTD = plcTD0.getElementsByClass("t_f").first();
            if (t_fTD != null) {
                Elements nopELEs = t_fTD.getElementsByClass("attach_nopermission attach_tips");
                if (nopELEs != null) {
                    nopELEs.remove();
                }
                Elements aprELEs = t_fTD.getElementsByClass("a_pr");
                if (aprELEs != null) {
                    aprELEs.remove();
                }
                Elements pstatusELEs = t_fTD.getElementsByClass("pstatus");
                if (pstatusELEs != null) {
                    pstatusELEs.remove();
                }
                Elements jammerELEs = t_fTD.getElementsByClass("jammer");
                if (jammerELEs != null) {
                    jammerELEs.remove();
                }
                Elements invisJammerELEs = t_fTD.getElementsByAttributeValue("style", "display:none");
                if (invisJammerELEs != null) {
                    invisJammerELEs.remove();
                }
                if (this.mThreadPageIndex == 1 && i == 0) {
                    this.mThreadContentPureText = t_fTD.text();
                    Elements images = t_fTD.getElementsByClass("zoom");
                    String imgHrefPref = "http://www.1point3acres.com/bbs/";
                    this.mThreadContentImgHrefs = new ArrayList();
                    it = images.iterator();
                    while (it.hasNext()) {
                        this.mThreadContentImgHrefs.add(imgHrefPref + ((Element) it.next()).attr("zoomfile"));
                    }
                    int imgCount = this.mThreadContentImgHrefs.size();
                    if (this.mOutputThreadContent) {
                        Log.d(this.LOG_TAG, "Thread PureContent: \n" + this.mThreadContentPureText);
                        Log.d(this.LOG_TAG, "Thread has " + imgCount + " images: ");
                        for (int imgIndex = 0; imgIndex < imgCount; imgIndex++) {
                            Log.d(this.LOG_TAG, "\n\tImage #" + imgIndex + ": " + ((String) this.mThreadContentImgHrefs.get(imgIndex)));
                        }
                    }
                } else {
                    Element quoteDIV = t_fTD.getElementsByClass("quote").first();
                    boolean hasQuote = false;
                    String quoteText = null;
                    String quoteAuthorName = null;
                    String quoteDate = null;
                    String quoteBody = null;
                    if (quoteDIV != null) {
                        try {
                            quoteText = quoteDIV.text();
                            Element quoteFONT = quoteDIV.getElementsByAttributeValue("color", "#999999").first();
                            String[] quoteSpecs = quoteFONT.text().split(" 发表于 ");
                            quoteAuthorName = quoteSpecs[0];
                            quoteDate = quoteSpecs[1];
                            quoteFONT.remove();
                            quoteBody = quoteDIV.text();
                            quoteDIV.remove();
                            hasQuote = true;
                        } catch (Exception e5) {
                            Log.e(this.LOG_TAG, "Exception while parsing quote: " + e5.getMessage());
                        }
                    }
                    try {
                        ThreadComment threadComment = new ThreadComment(this.mThreadID, this.mThreadCommentIndex, postID, authorName, authorID, postDate, t_fTD.text(), "http://www.1point3acres.com/bbs/" + post.getElementsByClass("fastre").first().attr("href"), hasQuote, quoteText);
                        if (hasQuote) {
                            threadComment.setQuote(quoteAuthorName, quoteDate, quoteBody);
                        }
                        this.mThreadComments.add(threadComment);
                        this.mThreadCommentIndex++;
                        if (this.mOutputThreadComment) {
                            Log.d(this.LOG_TAG, threadComment.toString());
                        }
                    } catch (Exception e52) {
                        e52.printStackTrace();
                        Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION, ParsingThreadInfoList");
                        this.mErrorCode = HttpErrorType.ERROR_THREAD_PARSE_FAILURE;
                        System.out.println(threadListBody.html());
                        return this.mErrorCode;
                    }
                }
            }
            i++;
        }
        this.mErrorCode = HttpErrorType.SUCCESS;
        return this.mErrorCode;
    }

    public String getThreadContentPureText() {
        return this.mThreadContentPureText;
    }

    public ArrayList<String> getThreadContentImgHrefs() {
        return this.mThreadContentImgHrefs;
    }

    public int getThreadPageCount() {
        return this.mThreadPageCount;
    }

    public ArrayList<ThreadComment> getThreadComments() {
        return this.mThreadComments;
    }

    public String getThreadFormHash() {
        return this.mFormHash;
    }

    public static String getThreadHref(String threadID, int pageIndex) {
        return "http://www.1point3acres.com/bbs/thread-" + threadID + "-" + pageIndex + "-1.html?mobile=no";
    }

    public static String generateThreadShareText(String subforumTitle, String threadTitle, String threadAuthorName, String threadID) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("【一亩三分地：" + subforumTitle + "】\n");
        stringBuilder.append(threadTitle + "\n（作者：" + threadAuthorName + "）");
        stringBuilder.append("\nhttp://www.1point3acres.com/bbs/thread-" + threadID + "-1-1.html");
        return stringBuilder.toString();
    }
}

package naco_siren.github.a1point3acres.html_parsers;

import android.util.Log;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.contents.ThreadInfo;
import naco_siren.github.a1point3acres.contents.ThreadPin;
import naco_siren.github.a1point3acres.contents.ThreadStatus;
import naco_siren.github.http_utils.HttpErrorType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseThreadInfoList {
    private final String LOG_TAG = ParseThreadInfoList.class.getSimpleName();
    private Document mDoc;
    private HttpErrorType mErrorCode;
    private String mFormHash;
    private boolean mOutputThreadInfo;
    private int mSubforumID;
    private int mSubforumPageCount;
    private int mSubforumPageIndex;
    private String mSubforumTitle;
    private int mThreadIndex;
    private ArrayList<ThreadInfo> mThreadInfos;

    public ParseThreadInfoList(int subforumID, String subforumTitle, int subforumPageIndex, Document doc, int threadInfosCount) {
        this.mSubforumID = subforumID;
        this.mSubforumTitle = subforumTitle;
        this.mSubforumPageIndex = subforumPageIndex;
        this.mDoc = doc;
        this.mThreadInfos = new ArrayList(40);
        this.mThreadIndex = threadInfosCount;
        this.mOutputThreadInfo = false;
        this.mSubforumPageCount = -1;
    }

    public void setOutputThreadInfo(boolean outputThreadInfo) {
        this.mOutputThreadInfo = outputThreadInfo;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }

    public HttpErrorType execute() {
        Element threadListBody = null;
        try {
            Elements threadListTableBodies = this.mDoc.getElementById("threadlisttableid").getElementsByTag("tbody");
            if (this.mSubforumPageIndex == 1) {
                Elements pagesDIVs = this.mDoc.getElementById("fd_page_top").getElementsByClass("pg");
                if (pagesDIVs == null || pagesDIVs.size() == 0) {
                    this.mSubforumPageCount = 1;
                } else {
                    this.mSubforumPageCount = Integer.parseInt(pagesDIVs.first().getElementsByTag("span").first().attr("title").split(" ")[1]);
                }
                Log.d(this.LOG_TAG, "=== Subforum has " + this.mSubforumPageCount + " pages ===\n");
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
            int threadListTableBodiesCount = threadListTableBodies.size();
            for (int i = 0; i < threadListTableBodiesCount; i++) {
                threadListBody = (Element) threadListTableBodies.get(i);
                if (threadListBody.id().contains("thread")) {
                    String threadID;
                    boolean threadIsDigest = false;
                    boolean threadIsRecommended = false;
                    boolean threadIsHot = false;
                    boolean threadIsAttached = false;
                    boolean threadTitleTextIsBold = false;
                    boolean threadTitleTextIsUnderlined = false;
                    String threadTitleTextColor = "#000000";
                    String threadAuthorTextColor = "#000000";
                    Element rootTR = threadListBody.getElementsByTag("tr").first();
                    Element elementAnchor = ((Element) rootTR.getElementsByTag("td").get(0)).getElementsByTag("a").first();
                    String threadHref = elementAnchor.attr("href");
                    if (threadHref.contains("viewthread")) {
                        threadID = threadHref.split("tid=")[1].split("&")[0];
                    } else {
                        threadID = threadHref.split("-")[1];
                    }
                    String elementAnchorTitleAttr = elementAnchor.attr("title");
                    ThreadPin threadPin;
                    if (elementAnchorTitleAttr.startsWith("全局置顶")) {
                        threadPin = ThreadPin.GLOBAL_PIN;
                    } else {
                        if (elementAnchorTitleAttr.contains("分类置顶")) {
                            threadPin = ThreadPin.GROUP_PIN;
                        } else {
                            ThreadStatus threadStatus;
                            String threadType;
                            long threadCommentCount;
                            long threadReadCount;
                            if (elementAnchorTitleAttr.contains("本版置顶")) {
                                threadPin = ThreadPin.LOCAL_PIN;
                            } else {
                                if (elementAnchorTitleAttr.contains("新窗口")) {
                                    threadPin = ThreadPin.NORMAL;
                                } else {
                                    threadPin = ThreadPin.UNKNOWN;
                                }
                            }
                            Element firstTH = (Element) rootTR.getElementsByTag("th").get(0);
                            String threadStatusString = firstTH.className();
                            if (threadStatusString.equals("lock")) {
                                threadStatus = ThreadStatus.LOCKED;
                            } else {
                                if (threadStatusString.equals("new")) {
                                    threadStatus = ThreadStatus.NEW;
                                } else {
                                    if (threadStatusString.equals("common")) {
                                        threadStatus = ThreadStatus.COMMON;
                                    } else {
                                        threadStatus = ThreadStatus.UNKNOWN;
                                    }
                                }
                            }
                            elementAnchor = firstTH.getElementsByClass("xst").last();
                            String threadTitle = elementAnchor.text();
                            String threadTitleStyleAttrText = elementAnchor.attr("style");
                            if (threadTitleStyleAttrText != null) {
                                if (threadTitleStyleAttrText.contains("font-weight: bold")) {
                                    threadTitleTextIsBold = true;
                                }
                                if (threadTitleStyleAttrText.contains("text-decoration: underline")) {
                                    threadTitleTextIsUnderlined = true;
                                }
                                String[] threadTitleStyleAttrTextColors = threadTitleStyleAttrText.replace("background-color", "").split("color: ");
                                if (threadTitleStyleAttrTextColors.length > 1) {
                                    threadTitleTextColor = threadTitleStyleAttrTextColors[1].split(";")[0];
                                }
                            }
                            Element elementEmphasize = firstTH.getElementsByTag("em").first();
                            if (elementEmphasize == null) {
                                threadType = this.mSubforumTitle;
                            } else {
                                threadType = elementEmphasize.getElementsByTag("a").first().text();
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "digest").size() > 0) {
                                threadIsDigest = true;
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "recommend").size() > 0) {
                                threadIsRecommended = true;
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "heatlevel").size() > 0) {
                                threadIsHot = true;
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "agree").size() > 0) {
                                threadIsHot = true;
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "attachment").size() > 0) {
                                threadIsAttached = true;
                            }
                            if (firstTH.getElementsByAttributeValueMatching("alt", "attach_img").size() > 0) {
                                threadIsAttached = true;
                            }
                            Element secondTD = (Element) rootTR.getElementsByTag("td").get(1);
                            elementAnchor = secondTD.getElementsByTag("cite").first().getElementsByTag("a").first();
                            String threadAuthor = elementAnchor.text();
                            String temp = elementAnchor.attr("href");
                            String threadAuthorID = elementAnchor.attr("href").split("uid-")[1].split("\\.")[0];
                            String threadAuthorTextColorText = elementAnchor.attr("style");
                            if (threadAuthorTextColorText.length() > 0) {
                                threadAuthorTextColor = threadAuthorTextColorText.split("color: ")[1].split(";")[0];
                            }
                            String threadDate = secondTD.getElementsByTag("em").first().child(0).text();
                            Element thirdTD = (Element) rootTR.getElementsByTag("td").get(2);
                            String threadCommentCountText = thirdTD.getElementsByTag("a").first().text();
                            if (threadCommentCountText.equals("-")) {
                                threadCommentCount = 0;
                            } else {
                                threadCommentCount = Long.parseLong(threadCommentCountText);
                            }
                            String threadReadCountText = thirdTD.getElementsByTag("em").first().text();
                            if (threadReadCountText.equals("-")) {
                                threadReadCount = 0;
                            } else {
                                threadReadCount = Long.parseLong(threadReadCountText);
                            }
                            ThreadInfo threadInfo = new ThreadInfo(this.mThreadIndex, threadHref, threadID, threadTitle, threadAuthor, threadAuthorID, threadDate, threadPin, threadStatus, threadType, threadIsAttached, threadReadCount, threadCommentCount, threadIsDigest, threadIsRecommended, threadIsHot, false, threadTitleTextIsBold, threadTitleTextIsUnderlined, threadTitleTextColor, threadAuthorTextColor);
                            this.mThreadInfos.add(threadInfo);
                            this.mThreadIndex++;
                            if (this.mOutputThreadInfo) {
                                Log.d(this.LOG_TAG, threadInfo.toString());
                            }
                        }
                    }
                }
            }
            this.mErrorCode = HttpErrorType.SUCCESS;
            return this.mErrorCode;
        } catch (Exception e5) {
            e5.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION, ParsingThreadInfoList");
            this.mErrorCode = HttpErrorType.ERROR_THREADINFOLIST_PARSE_FAILURE;
            System.out.println(threadListBody.html());
            return this.mErrorCode;
        }
    }

    public int getSubforumPageCount() {
        return this.mSubforumPageCount;
    }

    public ArrayList<ThreadInfo> getThreadInfos() {
        return this.mThreadInfos;
    }

    public String getFormHash() {
        return this.mFormHash;
    }

    public static String getThreadInfoListHref(int subforumID, int pageIndex) {
        return "http://www.1point3acres.com/bbs/forum-" + subforumID + "-" + pageIndex + ".html?mobile=no";
    }
}

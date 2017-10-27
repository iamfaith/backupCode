package naco_siren.github.a1point3acres.bmob.record;

import cn.bmob.v3.BmobObject;

public class BlogRecord extends BmobObject {
    private String blogTitle;
    private String userId;

    public String getUserId() {
        return this.userId;
    }

    public String getBlogTitle() {
        return this.blogTitle;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }
}

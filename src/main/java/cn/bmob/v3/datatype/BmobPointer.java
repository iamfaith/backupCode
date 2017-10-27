package cn.bmob.v3.datatype;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.BmobUser;
import java.io.Serializable;

public class BmobPointer implements Serializable {
    private static final long serialVersionUID = -2906907910428442090L;
    private String __type = "Pointer";
    private String className;
    private String objectId;

    public BmobPointer(String className, String objectId) {
        setClassName(className);
        setObjectId(objectId);
    }

    public BmobPointer(Object value) {
        if (value instanceof BmobUser) {
            BmobUser bmobUser = (BmobUser) value;
            setClassName("_User");
            setObjectId(bmobUser.getObjectId());
        } else if (value instanceof BmobRole) {
            BmobRole bmobRole = (BmobRole) value;
            setClassName(BmobRole.tableName);
            setObjectId(bmobRole.getObjectId());
        } else if (value instanceof BmobObject) {
            BmobObject bmobObject = (BmobObject) value;
            setClassName(bmobObject.getClass().getSimpleName());
            setObjectId(bmobObject.getObjectId());
        }
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

package cn.bmob.v3;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BmobACL implements Serializable {
    private static final long serialVersionUID = 3706778250022037535L;
    private Map<String, Object> Code = new HashMap();

    public Map<String, Object> getAcl() {
        return this.Code;
    }

    public void setAcl(Map<String, Object> acl) {
        this.Code = acl;
    }

    private void Code(String str, String str2, boolean z) {
        if (this.Code.containsKey(str2)) {
            Map map = (Map) this.Code.get(str2);
            map.put(str, Boolean.valueOf(z));
            this.Code.put(str2, map);
            return;
        }
        map = new HashMap();
        map.put(str, Boolean.valueOf(z));
        this.Code.put(str2, map);
    }

    public void setReadAccess(String userId, boolean allowed) {
        if (TextUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else if (allowed) {
            Code("read", userId, allowed);
        }
    }

    public void setReadAccess(BmobUser user, boolean allowed) {
        if (user == null) {
            throw new IllegalArgumentException("cannot setReadAccess for null user");
        } else if (TextUtils.isEmpty(user.getObjectId())) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else if (allowed) {
            Code("read", user.getObjectId(), allowed);
        }
    }

    public void setWriteAccess(String userId, boolean allowed) {
        if (TextUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else if (allowed) {
            Code("write", userId, allowed);
        }
    }

    public void setWriteAccess(BmobUser user, boolean allowed) {
        if (user == null) {
            throw new IllegalArgumentException("cannot setReadAccess for null user");
        } else if (TextUtils.isEmpty(user.getObjectId())) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else if (allowed) {
            Code("write", user.getObjectId(), allowed);
        }
    }

    public void setRoleReadAccess(String roleName, boolean allowed) {
        if (TextUtils.isEmpty(roleName)) {
            throw new IllegalArgumentException("cannot setReadAccess for null roleName");
        } else if (allowed) {
            Code("read", "role:" + roleName, allowed);
        }
    }

    public void setRoleReadAccess(BmobRole role, boolean allowed) {
        if (role == null) {
            throw new IllegalArgumentException("cannot setReadAccess for null role");
        } else if (TextUtils.isEmpty(role.getName())) {
            throw new IllegalArgumentException("cannot setReadAccess for null roleName");
        } else if (allowed) {
            Code("read", "role:" + role.getName(), allowed);
        }
    }

    public void setRoleWriteAccess(String roleName, boolean allowed) {
        if (TextUtils.isEmpty(roleName)) {
            throw new IllegalArgumentException("cannot setReadAccess for null roleName");
        } else if (allowed) {
            Code("write", "role:" + roleName, allowed);
        }
    }

    public void setRoleWriteAccess(BmobRole role, boolean allowed) {
        if (role == null) {
            throw new IllegalArgumentException("cannot setReadAccess for null role");
        } else if (TextUtils.isEmpty(role.getName())) {
            throw new IllegalArgumentException("cannot setReadAccess for null roleName");
        } else if (allowed) {
            Code("write", "role:" + role.getName(), allowed);
        }
    }

    public void setPublicReadAccess(boolean allowed) {
        if (allowed) {
            setReadAccess("*", allowed);
        }
    }

    public void setPublicWriteAccess(boolean allowed) {
        if (allowed) {
            setWriteAccess("*", allowed);
        }
    }
}

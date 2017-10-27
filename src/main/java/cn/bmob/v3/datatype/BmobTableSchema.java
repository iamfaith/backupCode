package cn.bmob.v3.datatype;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class BmobTableSchema implements Serializable {
    private static final long serialVersionUID = 1;
    private String Code;
    private Map<String, Map<String, Object>> V;

    public BmobTableSchema(String className, Map<String, Map<String, Object>> map) {
        this.Code = className;
        this.V = map;
    }

    public String getClassName() {
        return this.Code;
    }

    public void setClassName(String className) {
        this.Code = className;
    }

    public Map<String, Map<String, Object>> getFields() {
        return Collections.unmodifiableMap(this.V);
    }

    public void setFields(Map<String, Map<String, Object>> map) {
        this.V = map;
    }
}

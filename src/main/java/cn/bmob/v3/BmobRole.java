package cn.bmob.v3;

import cn.bmob.v3.datatype.BmobRelation;

public class BmobRole extends BmobObject {
    private static final long serialVersionUID = -4139218579795079311L;
    public static String tableName = "_Role";
    private String Code;
    private BmobRelation I = new BmobRelation();
    private BmobRelation V = new BmobRelation();

    public String getTableName() {
        return tableName;
    }

    public BmobRole(String name) {
        setName(name);
    }

    public String getName() {
        return this.Code;
    }

    public void setName(String name) {
        this.Code = name;
    }

    public BmobRelation getRoles() {
        return this.V;
    }

    public BmobRelation getUsers() {
        return this.I;
    }
}

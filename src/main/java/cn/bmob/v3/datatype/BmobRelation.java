package cn.bmob.v3.datatype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BmobRelation implements Serializable {
    private static final long serialVersionUID = 7419229244419967901L;
    private String __op = "AddRelation";
    private List<BmobPointer> objects = new ArrayList();

    public BmobRelation(Object value) {
        this.objects.add(new BmobPointer(value));
    }

    public void add(Object value) {
        this.objects.add(new BmobPointer(value));
    }

    public void remove(Object value) {
        this.__op = "RemoveRelation";
        this.objects.add(new BmobPointer(value));
    }

    @Deprecated
    public void isRemove(boolean state) {
        if (state) {
            this.__op = "RemoveRelation";
        }
    }

    public String get__op() {
        return this.__op;
    }

    public List<BmobPointer> getObjects() {
        return Collections.unmodifiableList(this.objects);
    }

    public void setObjects(List<BmobPointer> objects) {
        this.objects = objects;
    }
}

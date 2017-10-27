package cn.bmob.v3.datatype;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class BmobQueryResult<T> implements Serializable {
    private static final long serialVersionUID = 1;
    private List<T> Code;
    private int V;

    public BmobQueryResult() {
        this.Code = Collections.emptyList();
    }

    public BmobQueryResult(List<T> list, int count) {
        this.Code = list;
        this.V = count;
    }

    public List<T> getResults() {
        return Collections.unmodifiableList(this.Code);
    }

    public int getCount() {
        return this.V;
    }
}

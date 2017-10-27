package cn.bmob.v3.listener;

import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;

public abstract class SQLQueryListener<T> extends BmobCallback2<BmobQueryResult<T>, BmobException> {
    public abstract void done(BmobQueryResult<T> bmobQueryResult, BmobException bmobException);
}

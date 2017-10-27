package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;
import java.util.List;

public abstract class FindListener<T> extends BmobCallback2<List<T>, BmobException> {
    public abstract void done(List<T> list, BmobException bmobException);
}

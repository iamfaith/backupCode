package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class CountListener extends BmobCallback2<Integer, BmobException> {
    public abstract void done(Integer num, BmobException bmobException);
}

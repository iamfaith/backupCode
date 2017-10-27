package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class UpdateListener extends BmobCallback1<BmobException> {
    public abstract void done(BmobException bmobException);
}

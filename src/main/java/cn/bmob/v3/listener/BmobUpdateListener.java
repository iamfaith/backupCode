package cn.bmob.v3.listener;

import cn.bmob.v3.update.UpdateResponse;

public interface BmobUpdateListener {
    void onUpdateReturned(int i, UpdateResponse updateResponse);
}

package cn.bmob.v3.listener;

import cn.bmob.v3.datatype.BmobFile;
import java.util.List;

public interface UploadBatchListener {
    void onError(int i, String str);

    void onProgress(int i, int i2, int i3, int i4);

    void onSuccess(List<BmobFile> list, List<String> list2);
}

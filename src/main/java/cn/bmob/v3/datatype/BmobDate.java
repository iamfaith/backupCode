package cn.bmob.v3.datatype;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.protocol.HTTP;

public class BmobDate implements Serializable {
    private static final long serialVersionUID = -7739760111722811743L;
    private String __type = HTTP.DATE_HEADER;
    private String iso;

    public BmobDate(Date date) {
        this.iso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public String getDate() {
        return this.iso;
    }

    public static long getTimeStamp(String createdAt) {
        long j = 0;
        try {
            j = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createdAt).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public static BmobDate createBmobDate(String formatType, String time) {
        try {
            return new BmobDate(new SimpleDateFormat(formatType).parse(time));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

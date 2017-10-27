package cn.bmob.v3.helper;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.datatype.BmobRelation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GsonUtil {
    public static <T> List<T> toList(String value) {
        return (List) new Gson().fromJson(value, List.class);
    }

    public static <T> Object toObject(String value, Class<T> clazz) {
        return new Gson().fromJson(value, (Class) clazz);
    }

    public static <T> Object toObject(JsonElement value, Class<T> clazz) {
        return new Gson().fromJson(value, (Class) clazz);
    }

    public static String toJson(Object value) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BmobACL.class, new JsonSerializer<BmobACL>() {
            public final /* synthetic */ JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
                return new Gson().toJsonTree(((BmobACL) obj).getAcl());
            }
        });
        gsonBuilder.registerTypeAdapter(BmobRelation.class, new JsonSerializer<BmobRelation>() {
            public final /* synthetic */ JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
                BmobRelation bmobRelation = (BmobRelation) obj;
                if (bmobRelation.getObjects().size() == 0) {
                    return null;
                }
                return new Gson().toJsonTree(bmobRelation);
            }
        });
        return gsonBuilder.create().toJson(value);
    }

    public static <T> String mapToJson(Map<String, T> map) {
        return new Gson().toJson((Object) map);
    }

    public static Map<String, Object> toMap(String json) {
        return (Map) new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

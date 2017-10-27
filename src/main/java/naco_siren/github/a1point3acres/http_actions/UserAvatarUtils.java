package naco_siren.github.a1point3acres.http_actions;

public class UserAvatarUtils {

    public enum AvatarSize {
        MIDDLE,
        MEDIUM,
        SMALL
    }

    public static String getAvatarHrefByID(String userID, AvatarSize avatarSize) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://www.1point3acres.com/bbs/uc_server/avatar.php?uid=");
        stringBuilder.append(userID);
        stringBuilder.append("&size=");
        switch (avatarSize) {
            case MIDDLE:
                stringBuilder.append("middle");
                break;
            case MEDIUM:
                stringBuilder.append("middle");
                break;
            case SMALL:
                stringBuilder.append("small");
                break;
        }
        return stringBuilder.toString();
    }
}

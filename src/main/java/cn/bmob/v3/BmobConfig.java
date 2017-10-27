package cn.bmob.v3;

import android.content.Context;

public final class BmobConfig {
    public String applicationId;
    public long connectTimeout;
    public Context context;
    public long fileExpiration;
    public int uploadBlockSize;

    public static final class Builder {
        private long B;
        private Context Code;
        private long I;
        private String V;
        private int Z;

        public Builder(Context context) {
            this.Code = context;
        }

        public final Builder setApplicationId(String applicationId) {
            this.V = applicationId;
            return this;
        }

        public final Builder setConnectTimeout(long connectTimeout) {
            this.I = connectTimeout;
            return this;
        }

        public final Builder setUploadBlockSize(int blockSize) {
            this.Z = blockSize;
            return this;
        }

        public final Builder setFileExpiration(long expiration) {
            this.B = expiration;
            return this;
        }

        public final BmobConfig build() {
            return new BmobConfig();
        }
    }

    private BmobConfig(Builder builder) {
        this.context = builder.Code;
        this.applicationId = builder.V;
        this.connectTimeout = builder.I;
        this.uploadBlockSize = builder.Z;
        this.fileExpiration = builder.B;
    }
}

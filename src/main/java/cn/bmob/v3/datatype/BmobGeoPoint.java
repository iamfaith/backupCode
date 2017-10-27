package cn.bmob.v3.datatype;

import java.io.Serializable;

public class BmobGeoPoint implements Serializable {
    public static double EARTH_MEAN_RADIUS_KM = 6371.0d;
    public static double EARTH_MEAN_RADIUS_MILE = 3958.8d;
    private static final long serialVersionUID = -2527515194340586771L;
    private String __type = "GeoPoint";
    private Double latitude = Double.valueOf(0.0d);
    private Double longitude = Double.valueOf(0.0d);

    public BmobGeoPoint(double longitude, double latitude) {
        setLongitude(longitude);
        setLatitude(latitude);
        this.latitude = Double.valueOf(latitude);
        this.longitude = Double.valueOf(longitude);
    }

    public void setLatitude(double latitude) {
        if (latitude > 90.0d || latitude < -90.0d) {
            throw new IllegalArgumentException("Latitude must be within the range (-90.0, 90.0).");
        }
        this.latitude = Double.valueOf(latitude);
    }

    public double getLatitude() {
        return this.latitude.doubleValue();
    }

    public void setLongitude(double longitude) {
        if (longitude > 180.0d || longitude < -180.0d) {
            throw new IllegalArgumentException("Longitude must be within the range (-180.0, 180.0).");
        }
        this.longitude = Double.valueOf(longitude);
    }

    public double getLongitude() {
        return this.longitude.doubleValue();
    }

    public double distanceInRadiansTo(BmobGeoPoint point) {
        double doubleValue = this.latitude.doubleValue() * 0.0174532925199433d;
        double latitude = point.getLatitude() * 0.0174532925199433d;
        double doubleValue2 = (this.longitude.doubleValue() * 0.0174532925199433d) - (point.getLongitude() * 0.0174532925199433d);
        double sin = Math.sin((doubleValue - latitude) / 2.0d);
        doubleValue2 = Math.sin(doubleValue2 / 2.0d);
        return Math.asin(Math.sqrt(Math.min(1.0d, (((Math.cos(doubleValue) * Math.cos(latitude)) * doubleValue2) * doubleValue2) + (sin * sin)))) * 2.0d;
    }

    public double distanceInKilometersTo(BmobGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_KM;
    }

    public double distanceInMilesTo(BmobGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_MILE;
    }
}

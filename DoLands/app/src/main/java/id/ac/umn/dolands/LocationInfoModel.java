package id.ac.umn.dolands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationInfoModel {
    @SerializedName("xid")
    @Expose
    private String xid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dist")
    @Expose
    private Double dist;
    @SerializedName("rate")
    @Expose
    private Integer rate;
    @SerializedName("osm")
    @Expose
    private String osm;
    @SerializedName("kinds")
    @Expose
    private String kinds;
    @SerializedName("point")
    @Expose
    private Point point;

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDist() {
        return dist;
    }

    public void setDist(Double dist) {
        this.dist = dist;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getOsm() {
        return osm;
    }

    public void setOsm(String osm) {
        this.osm = osm;
    }

    public String getKinds() {
        return kinds;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}

package id.ac.umn.dolands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyDetailModel {
    @SerializedName("xid")
    @Expose
    private String xid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("wikidata")
    @Expose
    private String wikidata;
    @SerializedName("kinds")
    @Expose
    private String kinds;
    @SerializedName("sources")
    @Expose
    private Sources sources;
    @SerializedName("otm")
    @Expose
    private String otm;
    @SerializedName("wikipedia")
    @Expose
    private String wikipedia;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("preview")
    @Expose
    private Preview preview;
    @SerializedName("wikipedia_extracts")
    @Expose
    private WikipediaExtracts wikipediaExtracts;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWikidata() {
        return wikidata;
    }

    public void setWikidata(String wikidata) {
        this.wikidata = wikidata;
    }

    public String getKinds() {
        return kinds;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public Sources getSources() {
        return sources;
    }

    public void setSources(Sources sources) {
        this.sources = sources;
    }

    public String getOtm() {
        return otm;
    }

    public void setOtm(String otm) {
        this.otm = otm;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public WikipediaExtracts getWikipediaExtracts() {
        return wikipediaExtracts;
    }

    public void setWikipediaExtracts(WikipediaExtracts wikipediaExtracts) {
        this.wikipediaExtracts = wikipediaExtracts;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}

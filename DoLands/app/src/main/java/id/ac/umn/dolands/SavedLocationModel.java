package id.ac.umn.dolands;

public class SavedLocationModel {
    public String xid, name, roadName, county, country, postcode, stateDistrict, image;
    public Double lon, lat;

    public SavedLocationModel() {}

    public SavedLocationModel(String xid, String name, String roadName, String county, String country, String postcode, String stateDistrict, Double lon, Double lat, String image) {
        this.xid = xid;
        this.name = name;
        this.roadName = roadName;
        this.county = county;
        this.country = country;
        this.postcode = postcode;
        this.stateDistrict = stateDistrict;
        this.lon = lon;
        this.lat = lat;
        this.image = image;
    }

    public String getXid() {
        return xid;
    }

    public String getName() {
        return name;
    }

    public String getRoadName() {
        return roadName;
    }

    public String getCounty() {
        return county;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getStateDistrict() {
        return stateDistrict;
    }

    public String getImage() {
        return image;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }
}

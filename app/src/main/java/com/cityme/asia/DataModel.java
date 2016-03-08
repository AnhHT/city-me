package com.cityme.asia;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

/**
 * Created by AnhHoang on 3/7/2016.
 */
public class DataModel implements ClusterItem {
    private static final String EMPTY = "";
    private static final String API_IMAGE_URL = "http://media.cityme.asia/c35x35/";
    private LatLng mPosition;
    private String name;
    private String address;
    private String district;
    private String city;
    private List<String> categories;
    private Double rating;
    private String imageUrl;

    public DataModel() {

    }

    public DataModel(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public String getAddress() {
        if (address == null)
            return EMPTY;
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        if (district == null)
            return EMPTY;
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        if (city == null)
            return EMPTY;
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getName() {
        if (name == null)
            return EMPTY;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return String.format("%s, %s", getName(), getRating());
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            return EMPTY;
        }

        return String.format("%s%s.jpg", API_IMAGE_URL, imageUrl);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

package com.cityme.asia;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by AnhHoang on 3/7/2016.
 */
public class DataModel implements ClusterItem {
    private static final String EMPTY = "";
    private static final String API_IMAGE_URL = "http://media.cityme.asia/c35x35/";
    private static final String CAFE = "Café";
    private static final String SMALL_RES = "Quán ăn";
    private static final String RESTAURANT = "Nhà hàng";
    private static final String BAR_PUB = "Bar/Pub";
    private static final String BEER_PUB = "Quán bia";
    private static final String STREET_FOOD = "Ăn vặt";
    private static final String BAKERY = "Tiệm bánh";
    private static final String FAST_FOOD = "Ăn nhanh";
    private LatLng mPosition;
    private String name;
    private String address;
    private String district;
    private String city;
    private String category;
    private double rating;
    private String imageUrl;
    private double distance;

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

    public String getName() {
        if (name == null)
            return EMPTY;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        if (getRating() <= 0) {
            return String.format("%s, %s", getAddress(), formatNumber(getDistance()));
        }

        return String.format("%s, Rating: %s, %s", getAddress(), getRating(), formatNumber(getDistance()));
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public int getCategoryImage() {
        switch (getCategory()) {
            case CAFE:
                return R.drawable.cafe_24;
            case BAR_PUB:
                return R.drawable.cocktail_24;
            case SMALL_RES:
                return R.drawable.cento_24;
            case BEER_PUB:
                return R.drawable.ceer_24;
            case RESTAURANT:
                return R.drawable.cutlery_24;
            case STREET_FOOD:
                return R.drawable.bread_24;
            case BAKERY:
                return R.drawable.pizza_24;
            case FAST_FOOD:
                return R.drawable.hamburger_24;
            default:
                return R.drawable.cook_24;
        }
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

    public String getCategory() {
        if (category == null)
            return EMPTY;
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

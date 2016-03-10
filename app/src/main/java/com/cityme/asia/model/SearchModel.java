package com.cityme.asia.model;

import com.cityme.asia.AppConfig;
import com.cityme.asia.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by AnhHoang on 3/7/2016.
 */
public class SearchModel implements ClusterItem {
    private LatLng mPosition;
    private String name;
    private String address;
    private String district;
    private String city;
    private String category;
    private double rating;
    private String imageUrl;
    private double distance;

    public SearchModel() {

    }

    public SearchModel(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public String getAddress() {
        if (address == null)
            return AppConfig.EMPTY;
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        if (district == null)
            return AppConfig.EMPTY;
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        if (city == null)
            return AppConfig.EMPTY;
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        if (name == null)
            return AppConfig.EMPTY;
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
            case AppConfig.CAFE:
                return R.drawable.cafe_24;
            case AppConfig.BAR_PUB:
                return R.drawable.cocktail_24;
            case AppConfig.SMALL_RES:
                return R.drawable.cento_24;
            case AppConfig.BEER_PUB:
                return R.drawable.ceer_24;
            case AppConfig.RESTAURANT:
                return R.drawable.cutlery_24;
            case AppConfig.STREET_FOOD:
                return R.drawable.bread_24;
            case AppConfig.BAKERY:
                return R.drawable.pizza_24;
            case AppConfig.FAST_FOOD:
                return R.drawable.hamburger_24;
            default:
                return R.drawable.cook_24;
        }
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            return AppConfig.EMPTY;
        }

        return String.format("%s%s.jpg", AppConfig.API_IMAGE_URL, imageUrl);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        if (category == null)
            return AppConfig.EMPTY;
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

package com.cityme.asia.model;

import com.cityme.asia.AppConfig;

/**
 * Created by AnhHoang on 3/10/2016.
 */
public class SuggestionModel {
    private String id;
    private String name;
    private String address;
    private String district;
    private String city;
    private String imageUrl;
    private String slug;

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

    public String getImageUrl() {
        if (imageUrl == null) {
            return AppConfig.EMPTY;
        }

        return String.format("%s%s.jpg", AppConfig.API_IMAGE_URL, imageUrl);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(getAddress());
        builder.append(",");
        if (!getDistrict().isEmpty()) {
            builder.append(getDistrict());
            builder.append(",");
        }

        builder.append(getCity());
        return builder.toString();
    }

    public String getSlug() {
        if (slug == null) {
            return AppConfig.EMPTY;
        }
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}

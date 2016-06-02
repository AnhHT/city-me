package com.cityme.asia.helper;

import com.cityme.asia.model.SearchModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AnhHoang on 3/10/2016.
 */
public class Utility {
    public static List<JSONObject> getLocalBizs(JSONObject response) throws JSONException {
        final JSONObject entities = response.getJSONObject("entities");
        if (entities.length() == 0) {
            return new ArrayList<>();
        }

        final JSONObject localBizs = entities.getJSONObject("localBizs");
        final Iterator<String> keys = localBizs.keys();
        List<JSONObject> list = new ArrayList<>();
        while (keys.hasNext()) {
            final String currentKey = keys.next();
            final JSONObject currentObject = localBizs.getJSONObject(currentKey);
            list.add(currentObject);
        }

        return list;
    }

    public static SearchModel extractSearchItem(JSONObject object, LatLng from) throws JSONException {
        final JSONObject location = object.getJSONObject("location");
        final JSONArray coo = location.getJSONArray("coordinates");
        final JSONArray categories = object.getJSONArray("categories");
        SearchModel model = new SearchModel(coo.getDouble(1), coo.getDouble(0));
        model.setName(object.getString("name"));
        model.setAddress(object.getString("address"));
        model.setCity(object.getString("city"));
        if (!object.get("rating").toString().equals("null")) {
            model.setRating(object.getDouble("rating"));
        }

        if (object.has("mainPhoto")) {
            model.setImageUrl(object.getString("mainPhoto"));
        }

        if (categories.length() > 0) {
            model.setCategory(categories.getString(0));
        }

        model.setDistance(SphericalUtil.computeDistanceBetween(from, model.getPosition()));

        return model;
    }
}

package com.cityme.asia.helper;

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
}

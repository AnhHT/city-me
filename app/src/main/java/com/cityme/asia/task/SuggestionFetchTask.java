package com.cityme.asia.task;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.cityme.asia.AppConfig;
import com.cityme.asia.AppController;
import com.cityme.asia.helper.CustomContract;
import com.cityme.asia.helper.Utility;
import com.cityme.asia.model.SuggestionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by AnhHoang on 3/10/2016.
 */
public class SuggestionFetchTask {

    private static boolean isLoading = false;
    private final String TAG = SuggestionFetchTask.class.getSimpleName();
    private Context mContext;
    private ProgressBar mBar;

    public SuggestionFetchTask(Context context, ProgressBar bar) {
        this.mContext = context;
        this.mBar = bar;
    }

    public synchronized void getSuggestion(String text) throws UnsupportedEncodingException {
        if (isLoading) {
            return;
        } else {
            isLoading = true;
            String formattedUrl = String.format("%s%s", AppConfig.API_SUGGESTION,
                    URLEncoder.encode(text, "UTF-8"));
            Log.d(TAG, formattedUrl);
            StringRequest req = new StringRequest(formattedUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    VolleyLog.v("Response:%n %s", response);
                    try {
                        final JSONObject obj = new JSONObject(response);
                        final List<SuggestionModel> suggestionModels = fromJson(obj);
                        insertSuggestion(suggestionModels);
                        mBar.setVisibility(View.GONE);
                        isLoading = false;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        isLoading = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                    isLoading = false;
                }
            });

            AppController.getInstance().addToRequestQueue(req);
        }
    }

    public void clearSuggestion() {
        this.mContext.getContentResolver().delete(CustomContract.SuggestionEntry.CONTENT_URI, null, null);
    }

    private void insertSuggestion(List<SuggestionModel> messageApiModels) {
        final Vector<ContentValues> cVVector = new Vector<>(messageApiModels.size());
        ContentValues cv;
        for (final SuggestionModel model : messageApiModels) {
            cv = new ContentValues();
            cv.put(CustomContract.SuggestionEntry.KEY_UNIQUE_ID, model.getId());
            cv.put(CustomContract.SuggestionEntry.KEY_FULL_ADDRESS, model.getFullAddress());
            cv.put(CustomContract.SuggestionEntry.KEY_NAME, model.getName());
            cv.put(CustomContract.SuggestionEntry.KEY_IMAGE_URL, model.getImageUrl());
            cv.put(CustomContract.SuggestionEntry.KEY_SLUG, model.getSlug());
            cVVector.add(cv);
        }

        int inserted = 0;
        if (cVVector.size() > 0) {
            final ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = this.mContext.getContentResolver()
                    .bulkInsert(CustomContract.SuggestionEntry.CONTENT_URI, cvArray);
        }

        Log.d(TAG, "Update message Complete. " + inserted + " Inserted");
    }

    private List<SuggestionModel> fromJson(JSONObject response) throws JSONException {
        final List<SuggestionModel> data = new ArrayList<>();
        final List<JSONObject> localBizsList = Utility.getLocalBizs(response);
        for (JSONObject biz : localBizsList) {
            data.add(getItem(biz));
        }

        return data;
    }

    private SuggestionModel getItem(JSONObject object) throws JSONException {
        SuggestionModel model = new SuggestionModel();
        model.setId(object.getString("_id"));
        model.setName(object.getString("name"));
        model.setAddress(object.getString("address"));
        model.setCity(object.getString("city"));

        if (object.has("slug")) {
            model.setSlug(object.getString("slug"));
        }

        if (object.has("mainPhoto")) {
            model.setImageUrl(object.getString("mainPhoto"));
        }

        if (object.has("district")) {
            model.setDistrict(object.getString("district"));
        }

        return model;
    }

}

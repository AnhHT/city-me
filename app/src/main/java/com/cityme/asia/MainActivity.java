package com.cityme.asia;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.cityme.asia.component.CustomAutoCompleteTextView;
import com.cityme.asia.helper.CustomContract;
import com.cityme.asia.helper.Utility;
import com.cityme.asia.model.SearchModel;
import com.cityme.asia.model.SearchModelRenderer;
import com.cityme.asia.task.SuggestionAdapter;
import com.cityme.asia.task.SuggestionFetchTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ClusterManager.OnClusterClickListener<SearchModel>,
        ClusterManager.OnClusterInfoWindowClickListener<SearchModel>,
        ClusterManager.OnClusterItemClickListener<SearchModel>,
        ClusterManager.OnClusterItemInfoWindowClickListener<SearchModel>,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SUGGESTION_LOADER_ID = 0;
    private static final String[] SUGGESTION_COLUMN = {
            CustomContract.SuggestionEntry.TABLE_NAME + "." + CustomContract.SuggestionEntry._ID,
            CustomContract.SuggestionEntry.KEY_NAME,
            CustomContract.SuggestionEntry.KEY_FULL_ADDRESS,
            CustomContract.SuggestionEntry.KEY_SLUG
    };
    private static final int REQUEST_LOCATION = 0;
    private final String TAG = MainActivity.class.getSimpleName();
    private final String API_SEARCH = "https://api.cityme.vn/search?categories=&skip=0";
    private final String includeLocation = API_SEARCH + "&sort=near&location=";
    private GoogleMap mGoogleMap;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ClusterManager<SearchModel> mClusterManager;
    private Marker currentMarker;
    private LatLng currentLatLng;
    private float zoomLevel = 10f;
    private SuggestionAdapter suggestAdapter;
    private SuggestionFetchTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();
        this.initializeView();
        this.getSupportLoaderManager().initLoader(SUGGESTION_LOADER_ID, savedInstanceState, this);
        this.setUpMap();
        this.buildGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.mGoogleApiClient.isConnected()) {
            this.mGoogleApiClient.disconnect();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        if (this.mGoogleMap != null) {
            return;
        }

        this.mGoogleMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        this.mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("MyMapFragment", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public boolean onClusterClick(Cluster<SearchModel> cluster) {
        String firstName = cluster.getItems().iterator().next().getName();
        Toast.makeText(mContext, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Clicked onClusterClick");
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<SearchModel> cluster) {
        Log.d(TAG, "Clicked onClusterInfoWindowClick");
    }

    @Override
    public boolean onClusterItemClick(SearchModel searchModel) {
        Log.d(TAG, "Clicked onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(SearchModel searchModel) {
        Log.d(TAG, "Clicked onClusterItemInfoWindowClick");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String sortOrder = CustomContract.SuggestionEntry._ID + " ASC";
        final Uri uri = CustomContract.SuggestionEntry.buildOrders();
        return new CursorLoader(this.mContext, uri, SUGGESTION_COLUMN, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.suggestAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.suggestAdapter.swapCursor(null);
    }

    private void initializeView() {
        this.suggestAdapter = new SuggestionAdapter(this, null);
        final ProgressBar p = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        this.task = new SuggestionFetchTask(this.mContext, p);
        final FloatingActionButton myLocation = (FloatingActionButton) findViewById(R.id.fabLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.clear();
                currentLatLng = null;
                zoomLevel = mGoogleMap.getCameraPosition().zoom;
                getLocation();
            }
        });

        final CircleImageView btnGetSuggest = (CircleImageView) findViewById(R.id.btnGetSuggestion);
        final CustomAutoCompleteTextView viewAutoComplete = (CustomAutoCompleteTextView) findViewById(R.id.tvSuggest);
        viewAutoComplete.setThreshold(2);
        viewAutoComplete.setAutoCompleteDelay(5);
        viewAutoComplete.setAdapter(this.suggestAdapter);
        viewAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    btnGetSuggest.setVisibility(View.VISIBLE);
                }
            }
        });

        viewAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    viewAutoComplete.setText(cursor.getString(CustomContract.SuggestionEntry.COL_NAME));
                    onAutoCompleteItemTap(cursor.getString(CustomContract.SuggestionEntry.COL_SLUG));
                }

                btnGetSuggest.setVisibility(View.GONE);
            }
        });

        btnGetSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (viewAutoComplete.getText().toString().length() > 0) {
                        task.getSuggestion(viewAutoComplete.getText().toString());
                        p.setVisibility(View.VISIBLE);
                        btnGetSuggest.setVisibility(View.GONE);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @SuppressWarnings("ResourceType")
    private void getLocation() {
        this.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
        if (this.mLastLocation != null && this.mGoogleMap != null) {
            if (this.currentLatLng == null) {
                this.currentLatLng = new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude());
                this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.currentLatLng, this.zoomLevel));
                this.currentMarker = this.mGoogleMap.addMarker(new MarkerOptions().position(this.currentLatLng).title("You are here"));
                try {
                    this.searchAround();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeClusterManager() {
        this.mClusterManager = new ClusterManager<>(mContext, this.mGoogleMap);
        this.mClusterManager.setRenderer(new SearchModelRenderer(mContext, getLayoutInflater(),
                getResources(), mGoogleMap, mClusterManager));
        this.mGoogleMap.setOnCameraChangeListener(this.mClusterManager);
        this.mGoogleMap.setOnMarkerClickListener(mClusterManager);
        this.mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
        this.mClusterManager.setOnClusterClickListener(this);
        this.mClusterManager.setOnClusterInfoWindowClickListener(this);
        this.mClusterManager.setOnClusterItemClickListener(this);
        this.mClusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    private void searchAround() throws UnsupportedEncodingException {
        Log.d(TAG, "Search again");
        this.initializeClusterManager();
        final String encodedValue = URLEncoder.encode(String.format("%s,%s",
                this.currentLatLng.latitude, this.currentLatLng.longitude), "UTF-8");
        final String formattedUrl = this.includeLocation + encodedValue;
        Log.d(TAG, formattedUrl);
        StringRequest req = new StringRequest(formattedUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.v("Response:%n %s", response);
                try {
                    final JSONObject obj = new JSONObject(response);
                    final List<SearchModel> searchModels = fromJson(obj);
                    for (SearchModel item : searchModels) {
                        mClusterManager.addItem(item);
                    }

                    mClusterManager.cluster();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(req);
    }

    @SuppressWarnings("ResourceType")
    private void onAutoCompleteItemTap(String slug) {
        this.mGoogleMap.clear();
        this.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
        this.currentLatLng = new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude());
        this.currentMarker = this.mGoogleMap.addMarker(new MarkerOptions().position(this.currentLatLng).title("You are here"));
        this.initializeClusterManager();
        final String formattedUrl = String.format("%s%s", AppConfig.API_LOCAL_BIZ, slug);
        Log.d(TAG, formattedUrl);
        StringRequest req = new StringRequest(formattedUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.v("Response:%n %s", response);
                try {
                    final JSONObject obj = new JSONObject(response);
                    final List<SearchModel> searchModels = fromJson(obj);
                    final SearchModel slugModel = searchModels.get(0);
                    mClusterManager.addItem(slugModel);
                    mClusterManager.cluster();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(slugModel.getPosition(), zoomLevel));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(req);
    }

    private List<SearchModel> fromJson(JSONObject response) throws JSONException {
        final List<SearchModel> data = new ArrayList<>();
        final List<JSONObject> localBizsList = Utility.getLocalBizs(response);
        for (JSONObject biz : localBizsList) {
            data.add(getItem(biz));
        }

        return data;
    }

    private SearchModel getItem(JSONObject object) throws JSONException {
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

        model.setDistance(SphericalUtil.computeDistanceBetween(this.currentLatLng, model.getPosition()));

        return model;
    }
}

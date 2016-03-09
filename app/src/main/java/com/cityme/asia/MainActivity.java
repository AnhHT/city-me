package com.cityme.asia;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ClusterManager.OnClusterClickListener<DataModel>,
        ClusterManager.OnClusterInfoWindowClickListener<DataModel>,
        ClusterManager.OnClusterItemClickListener<DataModel>,
        ClusterManager.OnClusterItemInfoWindowClickListener<DataModel> {

    private static final int REQUEST_LOCATION = 0;
    private final String TAG = MyMapFragment.class.getSimpleName();
    private final String url = "http://api.cityme.asia/search?categories=&skip=0&limit=100&sort=rating&location=&priceRange=&serves=&fullmap=";
    private GoogleMap mGoogleMap;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ClusterManager<DataModel> mClusterManager;
    private Marker currentMarker;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
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

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
        /*if (ActivityCompat.checkSelfPermission(this.mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }*/

        getLocation();
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

    @SuppressWarnings("ResourceType")
    private void getLocation() {
        this.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
        if (this.mLastLocation != null && this.mGoogleMap != null) {
            if (this.currentLatLng == null) {
                this.currentLatLng = new LatLng(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude());
                this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 9.5f));
                this.currentMarker = this.mGoogleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                this.searchAll();
            }
        }
    }

    private void searchAll() {
        Log.d(TAG, "Search again");
        this.mClusterManager = new ClusterManager<>(mContext, this.mGoogleMap);
        this.mClusterManager.setRenderer(new DataModelRenderer());
        this.mGoogleMap.setOnCameraChangeListener(this.mClusterManager);
        this.mGoogleMap.setOnMarkerClickListener(mClusterManager);
        this.mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
        this.mClusterManager.setOnClusterClickListener(this);
        this.mClusterManager.setOnClusterInfoWindowClickListener(this);
        this.mClusterManager.setOnClusterItemClickListener(this);
        this.mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        // pass second argument as "null" for GET requests
        StringRequest req = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.v("Response:%n %s", response);
                try {
                    final JSONObject obj = new JSONObject(response);
                    List<DataModel> dataModels = fromJson(obj);
                    for (DataModel item : dataModels) {
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

    private List<DataModel> fromJson(JSONObject response) throws JSONException {
        final List<DataModel> data = new ArrayList<>();
        final JSONObject entities = response.getJSONObject("entities");
        final JSONObject localBizs = entities.getJSONObject("localBizs");
        final Iterator<String> keys = localBizs.keys();
        while (keys.hasNext()) {
            final String currentKey = keys.next();
            final JSONObject currentObject = localBizs.getJSONObject(currentKey);
            data.add(getItem(currentObject));
        }

        return data;
    }

    private DataModel getItem(JSONObject object) throws JSONException {
        final JSONObject location = object.getJSONObject("location");
        final JSONArray coo = location.getJSONArray("coordinates");
        final JSONArray categories = object.getJSONArray("categories");
        DataModel model = new DataModel(coo.getDouble(1), coo.getDouble(0));
        model.setName(object.getString("name"));
        model.setAddress(object.getString("address"));
        model.setCity(object.getString("city"));
        model.setRating(object.getDouble("rating"));
        if (object.has("mainPhoto")) {
            model.setImageUrl(object.getString("mainPhoto"));
        }

        if (categories.length() > 0) {
            model.setCategory(categories.getString(0));
        }

        model.setDistance(SphericalUtil.computeDistanceBetween(this.currentLatLng, model.getPosition()));

        return model;
    }

    @Override
    public boolean onClusterClick(Cluster<DataModel> cluster) {
        String firstName = cluster.getItems().iterator().next().getName();
        Toast.makeText(mContext, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Clicked onClusterClick");
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<DataModel> cluster) {
        Log.d(TAG, "Clicked onClusterInfoWindowClick");
    }

    @Override
    public boolean onClusterItemClick(DataModel dataModel) {
        Log.d(TAG, "Clicked onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(DataModel dataModel) {
        Log.d(TAG, "Clicked onClusterItemInfoWindowClick");
    }

    private class DataModelRenderer extends DefaultClusterRenderer<DataModel> {
        private final IconGenerator mIconGenerator = new IconGenerator(mContext);
        private final IconGenerator mClusterIconGenerator = new IconGenerator(mContext);
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;
        private final TextView mRatingTextView;

        public DataModelRenderer() {
            super(mContext, mGoogleMap, mClusterManager);

            View clusterMarker = getLayoutInflater().inflate(R.layout.cluster_custom_marker, null);
            mClusterIconGenerator.setContentView(clusterMarker);
            mClusterImageView = (ImageView) clusterMarker.findViewById(R.id.image);

            View singleMarker = getLayoutInflater().inflate(R.layout.single_custom_marker, null);
            mImageView = (ImageView) singleMarker.findViewById(R.id.image);
            mRatingTextView = (TextView) singleMarker.findViewById(R.id.txtRating);
            mDimension = (int) getResources().getDimension(R.dimen.item_image_small);
            mIconGenerator.setContentView(singleMarker);
        }

        @Override
        protected void onBeforeClusterItemRendered(DataModel model, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.

            /*mImageView.setImageResource(model.getCategoryImage());*/
            Glide.with(mContext)
                    .load(model.getImageUrl())
                    .centerCrop()
                    .placeholder(model.getCategoryImage())
                    .crossFade()
                    .into(mImageView);

            mRatingTextView.setText(model.getName());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(model.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<DataModel> cluster, MarkerOptions markerOptions) {
            List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (DataModel p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;

                Drawable drawable = ContextCompat.getDrawable(mContext, p.getCategoryImage());
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }

            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);
            mClusterImageView.setImageDrawable(multiDrawable);

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}

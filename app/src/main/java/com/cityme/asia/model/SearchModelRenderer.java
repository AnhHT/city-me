package com.cityme.asia.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cityme.asia.MultiDrawable;
import com.cityme.asia.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhHoang on 3/10/2016.
 */
public class SearchModelRenderer extends DefaultClusterRenderer<SearchModel> {
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;
    private final TextView mRatingTextView;
    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private Context mContext;

    public SearchModelRenderer(Context context, LayoutInflater inflater, Resources resources,
                               GoogleMap googleMap, ClusterManager<SearchModel> clusterManager) {
        super(context, googleMap, clusterManager);
        this.mContext = context;
        this.mIconGenerator = new IconGenerator(context);
        this.mClusterIconGenerator = new IconGenerator(context);

        View clusterMarker = inflater.inflate(R.layout.cluster_custom_marker, null);
        this.mClusterIconGenerator.setContentView(clusterMarker);
        this.mClusterImageView = (ImageView) clusterMarker.findViewById(R.id.image);

        View singleMarker = inflater.inflate(R.layout.single_custom_marker, null);
        this.mImageView = (ImageView) singleMarker.findViewById(R.id.image);
        this.mRatingTextView = (TextView) singleMarker.findViewById(R.id.txtRating);
        this.mDimension = (int) resources.getDimension(R.dimen.item_image_small);
        this.mIconGenerator.setContentView(singleMarker);
    }

    @Override
    protected void onBeforeClusterItemRendered(SearchModel model, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.

            /*mImageView.setImageResource(model.getCategoryImage());*/
        Glide.with(this.mContext)
                .load(model.getImageUrl())
                .centerCrop()
                .placeholder(model.getCategoryImage())
                .crossFade()
                .into(this.mImageView);

        this.mRatingTextView.setText(model.getName());
        Bitmap icon = this.mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(model.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<SearchModel> cluster, MarkerOptions markerOptions) {
        List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = this.mDimension;
        int height = this.mDimension;

        for (SearchModel p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;

            Drawable drawable = ContextCompat.getDrawable(this.mContext, p.getCategoryImage());
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }

        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);
        this.mClusterImageView.setImageDrawable(multiDrawable);

        Bitmap icon = this.mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}

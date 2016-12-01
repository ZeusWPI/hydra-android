package be.ugent.zeus.hydra.activities.resto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.models.resto.Resto;
import be.ugent.zeus.hydra.models.resto.RestoMeta;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.requests.resto.RestoMetaRequest;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestoLocationActivity extends HydraActivity implements OnMapReadyCallback, DataCallback<RestoMeta>, GoogleMap.OnMarkerClickListener {

    private static final LatLng DEFAULT_LOCATION = new LatLng(51.05, 3.72); //Gent
    private static final float DEFAULT_ZOOM = 12; //Between city & street zoom

    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private static final String TAG = "RestoLocationActivity";

    private GoogleMap map;
    private RestoMeta meta;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private TextView restoName;
    private TextView restoAddress;
    private ImageView restoIcon;

    private final RestoMetaRequest restoMetaRequest = new RestoMetaRequest();
    private final RequestPlugin<RestoMeta> plugin = new RequestPlugin<>(this, RequestPlugin.wrap(restoMetaRequest));

    private Map<LatLng, Resto> lookup = new HashMap<>();

    public RestoLocationActivity() {
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_location);
        restoName = $(R.id.resto_name);
        restoAddress = $(R.id.resto_address);
        restoIcon = $(R.id.resto_icon);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bottomSheetBehavior = BottomSheetBehavior.from($(R.id.bottom_sheet));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(ViewUtils.convertDpToPixelInt(80, this));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        $(R.id.bottom_sheet_title).setOnClickListener(view -> {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else { //We assume it is expanded.
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        plugin.getLoaderPlugin().startLoader();
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
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(latLng -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        if (meta != null) {
            addData();
        }
    }

    @Override
    public void receiveData(@NonNull RestoMeta data) {
        meta = data;
        if (map != null) {
            addData();
        }
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.e(TAG, "Error while getting data.", e);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> plugin.refresh())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto_location, menu);
        tintToolbarIcons(menu, R.id.resto_refresh, R.id.resto_center);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.resto_refresh:
                plugin.refresh();
                return true;
            case R.id.resto_center:
                centerDefault();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addData() {

        lookup.clear();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);
        for (Resto location : meta.locations) {
            LatLng pos = new LatLng(location.latitude, location.longitude);
            map.addMarker(
                    new MarkerOptions()
                            .position(pos)
                            .title(location.name)
                            .snippet(location.address)
            );
            lookup.put(pos, location);
        }
        centerDefault();
        plugin.getProgressBarPlugin().hideProgressBar();
    }

    private void centerDefault() {
        if(map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        }
    }

    @SuppressWarnings("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //The map should never be null, but check anyway
        if (map == null) {
            return;
        }

        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        restoName.setText(marker.getTitle());
        restoAddress.setText(marker.getSnippet());

        Resto resto = lookup.get(marker.getPosition());
        if (resto != null) {
            restoIcon.setVisibility(View.VISIBLE);
            Drawable drawable = ViewUtils.getTintedVectorDrawableInt(this, resto.getTypeIcon(), restoName.getCurrentTextColor());
            restoIcon.setImageDrawable(drawable);
        } else {
            restoIcon.setVisibility(View.GONE);
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return true;
    }
}
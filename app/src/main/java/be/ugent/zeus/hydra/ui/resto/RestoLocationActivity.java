package be.ugent.zeus.hydra.ui.resto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.Resto;
import be.ugent.zeus.hydra.data.models.resto.RestoMeta;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.MetaRequest;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.plugins.RequestPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class RestoLocationActivity extends BaseActivity implements OnMapReadyCallback {

    private static final LatLng DEFAULT_LOCATION = new LatLng(51.05, 3.72); //Gent
    private static final float DEFAULT_ZOOM = 12; //Between city & street zoom

    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private final RequestPlugin<RestoMeta> plugin = new RequestPlugin<>((context, aBoolean) -> new CachedRequest<>(context, new MetaRequest(), aBoolean));
    private GoogleMap map;
    private RestoMeta meta;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.enableProgress()
                .defaultError()
                .setSuccessCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        plugin.startLoader();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can insert markers or lines, insert listeners or move the camera. In this case,
     * we just insert a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (meta != null) {
            addData();
        }
    }

    private void receiveData(@NonNull RestoMeta data) {
        meta = data;
        if (map != null) {
            addData();
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);
        for (Resto location : meta.locations) {
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(
                    new MarkerOptions()
                            .position(pos)
                            .title(location.getName())
                            .snippet(location.getAddress())
            );
        }
        centerDefault();
        plugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::hideProgressBar);
    }

    private void centerDefault() {
        if (map != null) {
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
}
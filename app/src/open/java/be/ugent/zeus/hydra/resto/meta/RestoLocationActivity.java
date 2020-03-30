package be.ugent.zeus.hydra.resto.meta;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import com.google.android.material.snackbar.Snackbar;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class RestoLocationActivity extends BaseActivity {

    private static final String TAG = "RestoLocationActivity";

    private static final GeoPoint DEFAULT_LOCATION = new GeoPoint(51.05, 3.72); //Gent
    private static final float DEFAULT_ZOOM = 15;

    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private MapView map;
    private RestoMeta meta;
    private ProgressBar progressBar;
    private MetaViewModel viewModel;

    private MyLocationNewOverlay myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_resto_location);

        progressBar = findViewById(R.id.progress_bar);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.setCenter(DEFAULT_LOCATION);

        viewModel = new ViewModelProvider(this).get(MetaViewModel.class);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(progressBar));
        viewModel.getData().observe(this, SuccessObserver.with(this::receiveData));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
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
                viewModel.onRefresh();
                return true;
            case R.id.resto_center:
                if (this.myLocation != null) {
                    IMapController mapController = map.getController();
                    mapController.setCenter(myLocation.getMyLocation());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addData() {

        for (Resto location : meta.locations) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            Marker m = new Marker(map);
            m.setPosition(point);
            m.setTitle(location.getName());
            m.setSubDescription(location.getAddress());
            m.setDraggable(false);
            map.getOverlayManager().add(m);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //The map should never be null, but check anyway
        if (map == null) {
            return;
        }

        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            }
        }
    }

    private void enableLocation() {
        if (myLocation != null) {
            myLocation.disableMyLocation();
            map.getOverlayManager().remove(myLocation);
        }
        myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocation.enableMyLocation();
        map.getOverlayManager().add(myLocation);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}

/*
 * Copyright (c) 2018.
 * Created for MSc in Computer Science - Distributed Systems
 * All right reserved except otherwise noted
 */

package auebdreamteam.com.dssparkclient.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import auebdreamteam.com.dssparkclient.R;
import auebdreamteam.com.dssparkclient.databinding.FragmentMapBinding;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_map, container, false);

        binding.totalBuses.setText(getString(R.string.total_buses, "N/A"));

        binding.mapView.onCreate(savedInstanceState);
		binding.mapView.onResume();
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Log.e("MapFragment", "onCreateView: ", e);
        }

		binding.mapView.getMapAsync(this);
        binding.fab.setOnClickListener(view -> getMapCoordinates());
        binding.coordinatesButton.setOnClickListener(view -> openCoordinatesDialog());

		return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadMarkers();
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadMarkers() {
    }

	private void openCoordinatesDialog() {

		LayoutInflater li = getActivity().getLayoutInflater();
		View dialogView = li.inflate(R.layout.coordinates_dialog, null);

		final MaterialEditText startingLong = dialogView.findViewById(R.id.starting_long);
		final MaterialEditText startingLat = dialogView.findViewById(R.id.starting_lat);
		final MaterialEditText endingLong = dialogView.findViewById(R.id.ending_long);
		final MaterialEditText endingLat = dialogView.findViewById(R.id.ending_lat);

		new MaterialDialog.Builder(getActivity())
				.title(R.string.coordinates_starting_lat)
				.customView(R.layout.coordinates_dialog, true)
				.positiveText(R.string.ok_button)
				.negativeText(R.string.cancel_button)
				.onPositive((dialog1, which) -> getCoordinatesFromManual(startingLong, startingLat, endingLong, endingLat))
				.build().show();
	}

	private void getCoordinatesFromManual(MaterialEditText startingLong, MaterialEditText startingLat, MaterialEditText endingLong, MaterialEditText endingLat) {
    	double startingLongCoordinate = parseCoordinateEditText(startingLong);
    	double startingLatCoordinate = parseCoordinateEditText(startingLat);
    	double endingLongCoordinate = parseCoordinateEditText(endingLong);
    	double endingLatCoordinate = parseCoordinateEditText(endingLat);

		LatLng upLeft = new LatLng(startingLatCoordinate, startingLongCoordinate);
		LatLng upRight = new LatLng(startingLatCoordinate, endingLongCoordinate);
		LatLng downRight = new LatLng(endingLatCoordinate, endingLongCoordinate);
		LatLng downLeft = new LatLng(endingLatCoordinate, startingLongCoordinate);

		googleMap.clear();

		googleMap.addPolygon(new PolygonOptions()
				.add(upLeft, upRight, downRight, downLeft)
				.strokeColor(Color.RED)
				.strokeWidth(4));
	}

	private double parseCoordinateEditText(MaterialEditText editText) {
		if (editText.getText() == null || editText.getText().toString().isEmpty()) {
			return 0.0;
		} else {
			return  Double.parseDouble(editText.getText().toString());
		}
	}

	private void getMapCoordinates() {
		Projection projection = googleMap.getProjection();
		LatLng upRight = projection.getVisibleRegion().farRight;
		LatLng upLeft = projection.getVisibleRegion().farLeft;
		LatLng downRight = projection.getVisibleRegion().nearRight;
		LatLng downLeft = projection.getVisibleRegion().nearLeft;

		googleMap.clear();

		googleMap.addPolygon(new PolygonOptions()
				.add(upLeft, upRight, downRight, downLeft)
				.strokeColor(Color.RED)
				.strokeWidth(4));

	}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * Initialised over Dublin
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


        LatLng dublin = new LatLng(53.345563, -6.270346);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(dublin, 10);
        googleMap.animateCamera(cameraUpdate);
        googleMap.setTrafficEnabled(false);

    }

	@Override
	public void onResume() {
		super.onResume();
		binding.mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		binding.mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		binding.mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		binding.mapView.onLowMemory();
	}
}

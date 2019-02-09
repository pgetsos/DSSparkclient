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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import auebdreamteam.com.dssparkclient.R;
import auebdreamteam.com.dssparkclient.databinding.FragmentMapBinding;
import auebdreamteam.com.dssparkclient.entities.MapPoint;
import auebdreamteam.com.dssparkclient.entities.MapQuery;
import auebdreamteam.com.dssparkclient.helpers.DataSenderAsync;


public class MapFragment extends Fragment implements BaseFragment, OnMapReadyCallback {

	private static final String SERVER_IP = "192.168.1.2";

    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    MapQuery query = new MapQuery();

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

		query.setStartingLatCoordinate(startingLatCoordinate);
		query.setStartingLongCoordinate(startingLongCoordinate);
		query.setEndingLatCoordinate(endingLatCoordinate);
		query.setEndingLongCoordinate(endingLongCoordinate);

		openIPDialog();
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


		query.setStartingLatCoordinate(upLeft.latitude);
		query.setStartingLongCoordinate(upLeft.longitude);
		query.setEndingLatCoordinate(downRight.latitude);
		query.setEndingLongCoordinate(downRight.longitude);

		openIPDialog();
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

	private void openIPDialog() {
		new MaterialDialog.Builder(getActivity())
				.title(R.string.ip_hint)
				.content("O")
				.inputType(InputType.TYPE_CLASS_TEXT)
				.input(getActivity().getString(R.string.ip_hint), SERVER_IP, (dialog, input) -> sendToServer(input.toString()))
				.show();
	}

	private void sendToServer(String ipAddress) {
    	googleMap.clear();

		DataSenderAsync async = new DataSenderAsync(this, getActivity());
		query.setServerIP(ipAddress);
		async.execute(query);

	}

	@Override
	public void onResult(List<Object> results) {
		if (results == null) {
			return; //FIXME
		}
		int counter = 0;
		binding.totalBuses.setText(getString(R.string.total_buses, String.valueOf(results.size())));
		Collections.shuffle(results);
		for (Object result : results) {
			MapPoint point = (MapPoint) result;
			MarkerOptions markerOptions = new MarkerOptions();
			LatLng latLng = new LatLng(point.getLatCoordinate(), point.getLongCoordinate());
			markerOptions.position(latLng);
			googleMap.addMarker(markerOptions);
			counter++;
			if (counter == 100){
				break;
			}
		}
	}
}

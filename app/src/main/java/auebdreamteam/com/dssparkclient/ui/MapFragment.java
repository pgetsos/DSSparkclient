/*
 * Copyright (c) 2018.
 * Created for MSc in Computer Science - Distributed Systems
 * All right reserved except otherwise noted
 */

package auebdreamteam.com.dssparkclient.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import auebdreamteam.com.dssparkclient.R;
import auebdreamteam.com.dssparkclient.databinding.FragmentMapBinding;


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_map, container, false);

        binding.mapView.onCreate(savedInstanceState);
		binding.mapView.onResume();
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Log.e("MapFragment", "onCreateView: ", e);
        }

		binding.mapView.getMapAsync(this);

		return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadMarkers();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }

    private void loadMarkers() {
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
        googleMap.setTrafficEnabled(true);

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

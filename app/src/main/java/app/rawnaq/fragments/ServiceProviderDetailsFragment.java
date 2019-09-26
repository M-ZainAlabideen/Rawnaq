package app.rawnaq.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.SubServicesAdapter;
import app.rawnaq.adapters.ServiceProvidersAdapter;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.models.SubServicesModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceProviderDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ServiceProviderDetailsFragment fragment;
    public static SessionManager sessionManager;
    public static Map<String, String> User;
    ArrayList<SubServicesModel> subServicesList = new ArrayList<>();
    public SubServicesAdapter subServicesAdapter;
    GridLayoutManager layoutManager;
 
    @BindView(R.id.fragment_service_provider_details_rv_subServices)
    RecyclerView subServices;


    public static ServiceProviderDetailsFragment newInstance(FragmentActivity activity) {
        fragment = new ServiceProviderDetailsFragment();
        ServiceProviderDetailsFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_service_provider_details, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);

        layoutManager = new GridLayoutManager(activity,2);
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());
        subServicesList.add(new SubServicesModel());

        subServicesAdapter = new SubServicesAdapter(activity,subServicesList);
        subServices.setLayoutManager(layoutManager);
        subServices.setAdapter(subServicesAdapter);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        12.2,
                      -12), 14.0f));


                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(12.2,
                                -12)));

            }
        });
    }

}


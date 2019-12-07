package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.ServicesAdapter;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.responses.categories.Service;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServicesFragment extends Fragment {
    public static FragmentActivity activity;
    public static ServicesFragment fragment;
    public static SessionManager sessionManager;
    public ServicesAdapter servicesAdapter;
    public LinearLayoutManager layoutManager;
    public ArrayList<Service> servicesList = new ArrayList<>();

    @BindView(R.id.fragment_services_rv_services)
    RecyclerView services;

    public static ServicesFragment newInstance(FragmentActivity activity) {
        fragment = new ServicesFragment();
        ServicesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_services, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);
        MainActivity.title.setText(getString(R.string.services));

        servicesList.clear();
        servicesList.addAll((ArrayList<Service>)getArguments().getSerializable("services"));
        layoutManager = new LinearLayoutManager(activity);
        servicesAdapter = new ServicesAdapter(activity,servicesList);
        services.setLayoutManager(layoutManager);
        services.setAdapter(servicesAdapter);

    }
}

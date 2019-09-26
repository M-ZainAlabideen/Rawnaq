package app.rawnaq.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.ServiceProvidersAdapter;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.models.ServiceProvidersModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceProvidersFragment extends Fragment {
    public static FragmentActivity activity;
    public static ServiceProvidersFragment fragment;
    public static SessionManager sessionManager;
    public static Map<String, String> User;
    ArrayList<ServiceProvidersModel> serviceProvidersList = new ArrayList<>();
    public ServiceProvidersAdapter serviceProvidersAdapter;
    LinearLayoutManager layoutManager;
    public boolean searchCheck = false;

    @BindView(R.id.service_providers_cl_searchContainer)
    ConstraintLayout searchContainer;
    @BindView(R.id.fragment_service_provider_rv_serviceProviders)
    RecyclerView serviceProviders;

    @BindView(R.id.fragment_service_providers_iv_arrow)
    ImageView arrow;
    @BindView(R.id.fragment_service_providers_spin_country)
    Spinner Country;
    @BindView(R.id.fragment_service_providers_spin_city)
    Spinner City;
    @BindView(R.id.fragment_service_providers_spin_zone)
    Spinner zone;

    @BindView(R.id.fragment_service_providers_tv_highRateTxt)
    TextView highRateTxt;
    @BindView(R.id.fragment_service_providers_tv_mapTxt)
    TextView mapTxt;
    @BindView(R.id.fragment_service_providers_tv_nearestTxt)
    TextView nearestTxt;
    @BindView(R.id.fragment_service_providers_tv_offersTxt)
    TextView offersTxt;

    @BindView(R.id.fragment_service_providers_iv_highRateImg)
    ImageView highRateImg;
    @BindView(R.id.fragment_service_providers_iv_mapImg)
    ImageView mapImg;
    @BindView(R.id.fragment_service_providers_iv_nearestImg)
    ImageView nearestImg;
    @BindView(R.id.fragment_service_providers_iv_offersImg)
    ImageView offersImg;


    public static ServiceProvidersFragment newInstance(FragmentActivity activity) {
        fragment = new ServiceProvidersFragment();
        ServiceProvidersFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_service_providers, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        searchContainer.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(activity);
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());
        serviceProvidersList.add(new ServiceProvidersModel());

        serviceProvidersAdapter = new ServiceProvidersAdapter(activity,serviceProvidersList);
        serviceProviders.setLayoutManager(layoutManager);
        serviceProviders.setAdapter(serviceProvidersAdapter);
    }


    @OnClick(R.id.fragment_service_providers_v_offers)
    public void offersClick() {
        offersTxt.setTextColor(Color.parseColor("#E67315"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tune_select);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

    }

    @OnClick(R.id.fragment_service_providers_v_map)
    public void mapClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#E67315"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tune_select);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

    }

    @OnClick(R.id.fragment_service_providers_v_nearest)
    public void nearestClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#E67315"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tune_select);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);
    }

    @OnClick(R.id.fragment_service_providers_v_highRate)
    public void highRateClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#E67315"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tune_select);
    }


    @OnClick(R.id.fragment_service_providers_v_startSearch)
    public void startSearchCLick(){
        if(!searchCheck){
            arrow.setRotation(-90);
            searchContainer.setVisibility(View.VISIBLE);
            searchCheck = true;
        }
        else{
            arrow.setRotation(0);
            searchContainer.setVisibility(View.GONE);
            searchCheck = false;
        }
    }

    @OnClick(R.id.fragment_service_providers_btn_search)
    public void searchClick() {

    }
}

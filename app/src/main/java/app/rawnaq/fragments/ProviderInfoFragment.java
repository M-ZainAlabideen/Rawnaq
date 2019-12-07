package app.rawnaq.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.SubServicesAdapter;
import app.rawnaq.classes.Constants;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import app.rawnaq.webservices.responses.providers.Image;
import app.rawnaq.webservices.responses.providers.Provider;
import app.rawnaq.webservices.responses.providers.ProviderInfoResponse;
import app.rawnaq.webservices.responses.providers.ProviderService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProviderInfoFragment extends Fragment {
    public static FragmentActivity activity;
    public static ProviderInfoFragment fragment;
    public static SessionManager sessionManager;
    private ArrayList<ProviderService> subServicesList = new ArrayList<>();
    private SubServicesAdapter subServicesAdapter;
    private GridLayoutManager layoutManager;
    private Provider provider;

    @BindView(R.id.fragment_provider_info_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_provider_info_rv_subServices)
    RecyclerView subServices;
    @BindView(R.id.fragment_provider_info_tv_name)
    TextView name;
    @BindView(R.id.fragment_provider_info_sl_providerSlider)
    SliderLayout providerSlider;
    @BindView(R.id.fragment_provider_info_iv_addToFav)
    ImageView addToFav;
    @BindView(R.id.fragment_provider_info_iv_workFromHome)
    ImageView workFromHome;
    @BindView(R.id.fragment_provider_info_rb_ratingValue)
    RatingBar rating;
    @BindView(R.id.fragment_provider_info_tv_contactWith)
    TextView contactWith;
    @BindView(R.id.fragment_provider_info_tv_ratingOf)
    TextView ratingOf;
    @BindView(R.id.fragment_provider_info_tv_ratingCounter)
    TextView ratingCounter;
    @BindView(R.id.fragment_provider_info_tv_phoneNumber)
    TextView phoneNumber;
    @BindView(R.id.fragment_provider_info_tv_description)
    TextView description;
    @BindView(R.id.fragment_provider_info_tv_address)
    TextView address;
    @BindView(R.id.fragment_provider_info_tv_workFromHomeTV)
    TextView workFromHomeTV;
    @BindView(R.id.fragment_provider_info_tv_homeServicePrice)
    TextView homeServicePrice;
    @BindView(R.id.loading)
    ProgressBar loading;


    public static ProviderInfoFragment newInstance(FragmentActivity activity) {
        fragment = new ProviderInfoFragment();
        ProviderInfoFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_provider_info, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);

        container.setVisibility(View.GONE);

        layoutManager = new GridLayoutManager(activity, 2);
        subServicesAdapter = new SubServicesAdapter(activity, subServicesList, false);
        subServices.setLayoutManager(layoutManager);
        subServices.setAdapter(subServicesAdapter);

        if (provider != null) {
            loading.setVisibility(View.GONE);
            setData();
        } else {
            ProviderInfoApi();
        }


    }

    @OnClick(R.id.fragment_provider_info_iv_addToFav)
    public void addToFavClick() {
        if (!sessionManager.isProvider()) {
            if (sessionManager.isGuest()) {
                Snackbar.make(loading, getString(R.string.loginFirst), Snackbar.LENGTH_SHORT).show();
            } else {
                makeFavoriteApi();
            }
        }
    }

    @OnClick(R.id.fragment_provider_info_btn_makeOrder)
    public void makeOrderClick() {
        if (!sessionManager.isProvider()) {
            if (sessionManager.isGuest()) {
                Snackbar.make(loading, getString(R.string.loginFirst), Snackbar.LENGTH_SHORT).show();
            } else {
                MakeOrderFragment fragment = MakeOrderFragment.newInstance(activity);
                Bundle b = new Bundle();
                b.putSerializable("provider", provider);
                fragment.setArguments(b);
                Navigator.loadFragment(activity, fragment, R.id.main_fl_container, true);
            }
        }
    }

    @OnClick(R.id.fragment_provider_info_iv_call)
    public void callClick() {
        if (!sessionManager.isProvider()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("tel:" + provider.providerShop.tel));
            activity.startActivity(intent);
        }
    }

    public void createSliderViews(String sliderViewUrl) {
        DefaultSliderView sliderView = new DefaultSliderView(activity);
        sliderView.image(Constants.IMAGE_BASE_URL + sliderViewUrl).setRequestOption(new RequestOptions().centerCrop());
        providerSlider.addSlider(sliderView);
        //add slider duration for every View(Advertisement)
        providerSlider.setDuration(5000);

        sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                //Navigator.loadFragment(activity, ImageGestureFragment.newInstance(activity,sliderList,sliderLayout.getCurrentPosition()), R.id.main_ContentLayout, true);

            }
        });

    }

    public void getLocation(final double lat, final double lng) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_provider_info_mFrag_map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng)));

            }
        });
    }

    public void setData() {
        container.setVisibility(View.VISIBLE);
        String shopName = provider.providerShop.nameShop;
        MainActivity.title.setText(shopName);

        ArrayList<Image> images = provider.providerShop.images;
        String city = provider.providerShop.city.name;
        String zone = provider.providerShop.zone.name;
        String street = provider.providerShop.street;
        String phone = provider.providerShop.tel;
        double ratingValue = provider.providerShop.rate;
        int isWorkFromHome = provider.providerShop.workFromHome;
        String descriptionValue = provider.providerShop.description;
        boolean favorite = provider.providerShop.fav;
        int countRate = provider.providerShop.countRate;
        String homePrice = String.valueOf(provider.providerShop.home_price);
        Double lat = provider.providerShop.latitude;
        Double lng = provider.providerShop.longitude;
        subServicesList.clear();
        subServicesList.addAll(provider.providerShop.providerServices);
        subServicesAdapter.notifyDataSetChanged();

        for (Image value : images) {
            createSliderViews(value.image);
        }
        name.setText(shopName);
        address.setText(city + "| " + zone + "| " + street);
        description.setText(descriptionValue);
        contactWith.setText(getString(R.string.contact_with) + " " + shopName);
        ratingOf.setText(getString(R.string.rating_of) + " " + shopName);
        if (isWorkFromHome != 1) {
            workFromHome.setVisibility(View.INVISIBLE);
            workFromHomeTV.setVisibility(View.INVISIBLE);
        }
        phoneNumber.setText(phone);
        if (favorite) {
            addToFav.setImageResource(R.drawable.ic_fav);
        }
        rating.setRating((float) ratingValue);
        ratingCounter.setText(countRate + "");
        homeServicePrice.setText(getString(R.string.home_cost).replace(getString(R.string.fifty), homePrice));
        if (lat != null && lng != null) {
            getLocation(lat, lng);
        } else {

        }
    }

    private void ProviderInfoApi() {
        int providerId = 0;
        if (sessionManager.isProvider()) {
            providerId = sessionManager.getProviderId();
        } else {
            providerId = getArguments().getInt("providerId");
        }

        RawnaqApiConfig.getCallingAPIInterface().ProviderInfo(
                providerId,
                new Callback<ProviderInfoResponse>() {
                    @Override
                    public void success(ProviderInfoResponse providerInfoResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = providerInfoResponse.status;
                        if (status == 200) {
                            if (providerInfoResponse != null) {
                                provider = providerInfoResponse.provider;
                                setData();
                            }


                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeFavoriteApi() {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().makeFavorite(
                sessionManager.getUserToken(), sessionManager.getProviderId(),
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = generalResponse.status;
                        if (status == 200) {
                            if (addToFav.getDrawable().getConstantState() ==
                                    getResources().getDrawable(R.mipmap.ic_fav).getConstantState()) {
                                addToFav.setImageResource(R.mipmap.ic_un_fav);
                            } else {
                                addToFav.setImageResource(R.mipmap.ic_fav);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }
}


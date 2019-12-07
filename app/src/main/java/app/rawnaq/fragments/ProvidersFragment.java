package app.rawnaq.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.ProvidersAdapter;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.countries.CountriesResponse;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import app.rawnaq.webservices.responses.providers.City;
import app.rawnaq.webservices.responses.providers.Country;
import app.rawnaq.webservices.responses.providers.Provider;
import app.rawnaq.webservices.responses.providers.ProviderShop;
import app.rawnaq.webservices.responses.providers.ProvidersResponse;
import app.rawnaq.webservices.responses.providers.Zone;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProvidersFragment extends Fragment {
    public static FragmentActivity activity;
    public static ProvidersFragment fragment;
    public static SessionManager sessionManager;
    private ArrayList<Provider> providersList = new ArrayList<>();

    private List<Country> countriesList = new ArrayList<>();
    private List<City> citiesList = new ArrayList<>();
    private List<Zone> zonesList = new ArrayList<>();

    private List<String> countriesNamesList = new ArrayList<>();
    private List<String> citiesNamesList = new ArrayList<>();
    private List<String> zonesNamesList = new ArrayList<>();

    private Map<Double, Double> locationsMap = new HashMap<>();
    private int countryPosition = 0;
    private int cityPosition = 0;
    private int zonePosition = 0;

    private ProvidersAdapter providersAdapter;
    private ArrayAdapter<String> countriesAdapter;
    private ArrayAdapter<String> citiesAdapter;
    private ArrayAdapter<String> zonesAdapter;
    private LinearLayoutManager layoutManager;
    private boolean searchCheck = false;
    private int categoryId;
    public String categoryName;


    @BindView(R.id.fragment_providers_cl_searchContainer)
    ConstraintLayout searchContainer;
    @BindView(R.id.fragment_providers_rv_providers)
    RecyclerView providers;

    @BindView(R.id.fragment_providers_iv_arrow)
    ImageView arrow;
    @BindView(R.id.fragment_providers_spin_country)
    Spinner countrySpin;
    @BindView(R.id.fragment_providers_spin_city)
    Spinner citySpin;
    @BindView(R.id.fragment_providers_spin_zone)
    Spinner zoneSpin;

    @BindView(R.id.fragment_providers_tv_highRateTxt)
    TextView highRateTxt;
    @BindView(R.id.fragment_providers_tv_mapTxt)
    TextView mapTxt;
    @BindView(R.id.fragment_providers_tv_nearestTxt)
    TextView nearestTxt;
    @BindView(R.id.fragment_providers_tv_offersTxt)
    TextView offersTxt;

    @BindView(R.id.fragment_providers_iv_highRateImg)
    ImageView highRateImg;
    @BindView(R.id.fragment_providers_iv_mapImg)
    ImageView mapImg;
    @BindView(R.id.fragment_providers_iv_nearestImg)
    ImageView nearestImg;
    @BindView(R.id.fragment_providers_iv_offersImg)
    ImageView offersImg;

    @BindView(R.id.loading)
    ProgressBar loading;


    public static ProvidersFragment newInstance(FragmentActivity activity) {
        fragment = new ProvidersFragment();
        ProvidersFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_providers, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);

        searchContainer.setVisibility(View.GONE);
        categoryId = getArguments().getInt("categoryId");
        categoryName = getArguments().getString("categoryName");
        MainActivity.title.setText(categoryName);

        layoutManager = new LinearLayoutManager(activity);
        providersAdapter = new ProvidersAdapter(activity, providersList, new ProvidersAdapter.OnItemClickListener() {
            @Override
            public void favoriteClick(int position, ImageView addToFav) {
                if (sessionManager.isGuest()) {
                    Snackbar.make(loading, getString(R.string.loginFirst), Snackbar.LENGTH_SHORT).show();
                } else {
                    makeFavoriteApi(providersList.get(position).id, addToFav);
                }
            }
        });
        providers.setLayoutManager(layoutManager);
        providers.setAdapter(providersAdapter);

        if (providersList.size() > 0) {
            loading.setVisibility(View.GONE);
        } else {
            providersApi(null, null, null, null, null);
        }

        countriesAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, countriesNamesList);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpin.setAdapter(countriesAdapter);

        citiesAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, citiesNamesList);
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpin.setAdapter(citiesAdapter);

        zonesAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, zonesNamesList);
        zonesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zoneSpin.setAdapter(zonesAdapter);

        if (countriesList.size() == 0) {
            getCountries();
        }
        countrySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryPosition = position;
                citiesNamesList.clear();
                for (City value : countriesList.get(position).cities) {
                    citiesNamesList.add(value.name);
                }
                citiesAdapter.notifyDataSetChanged();
                citiesList.addAll(countriesList.get(position).cities);

                zonesNamesList.clear();
                for (Zone value : citiesList.get(position).zones) {
                    zonesNamesList.add(value.name);
                }
                zonesAdapter.notifyDataSetChanged();
                zonesList.addAll(citiesList.get(position).zones);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityPosition = position;

                zonesNamesList.clear();
                for (Zone value : citiesList.get(position).zones) {
                    zonesNamesList.add(value.name);
                }
                zonesAdapter.notifyDataSetChanged();
                zonesList.addAll(citiesList.get(position).zones);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        zoneSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zonePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnClick(R.id.fragment_providers_v_offers)
    public void offersClick() {
        offersTxt.setTextColor(Color.parseColor("#E67315"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tune_select);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

        if (searchCheck) {
            closeSearch();
        }
        notifyData();
        providersApi(null, null, 1, null, null);

    }

    @OnClick(R.id.fragment_providers_v_map)
    public void mapClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#E67315"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tune_select);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

        if (searchCheck) {
            closeSearch();
        }
        LocationsFragment fragment = LocationsFragment.newInstance(activity);
        Bundle b = new Bundle();
        for (Provider value : providersList) {
            if (value.providerShop.latitude != null && value.providerShop.longitude != null) {
                locationsMap.put(value.providerShop.latitude, value.providerShop.longitude);
            }
        }
        if (locationsMap.size() > 0) {
            b.putSerializable("locations", (Serializable) locationsMap);
            fragment.setArguments(b);
            Navigator.loadFragment(activity, fragment, R.id.main_fl_container, true);

        }
    }

    @OnClick(R.id.fragment_providers_v_nearest)
    public void nearestClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#E67315"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tune_select);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

        if (searchCheck) {
            closeSearch();
        }
        notifyData();
        providersApi(null, 1, null, null, null);

    }

    @OnClick(R.id.fragment_providers_v_highRate)
    public void highRateClick() {
        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#E67315"));

        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tune_select);

        if (searchCheck) {
            closeSearch();
        }
        notifyData();
        providersApi(1, null, null, null, null);

    }


    @OnClick(R.id.fragment_providers_v_startSearch)
    public void startSearchCLick() {
        if (!searchCheck) {
            openSearch();
        } else {
            closeSearch();
        }
    }

    @OnClick(R.id.fragment_providers_btn_search)
    public void searchClick() {
        offersImg.setImageResource(R.mipmap.ic_tun_unselect);
        mapImg.setImageResource(R.mipmap.ic_tun_unselect);
        nearestImg.setImageResource(R.mipmap.ic_tun_unselect);
        highRateImg.setImageResource(R.mipmap.ic_tun_unselect);

        offersTxt.setTextColor(Color.parseColor("#095B98"));
        mapTxt.setTextColor(Color.parseColor("#095B98"));
        nearestTxt.setTextColor(Color.parseColor("#095B98"));
        highRateTxt.setTextColor(Color.parseColor("#095B98"));
        notifyData();
        providersSearchApi(countriesList.get(countryPosition).id, citiesList.get(cityPosition).id, zonesList.get(zonePosition).id);
    }

    public void notifyData() {
        providersList.clear();
        providersAdapter.notifyDataSetChanged();
    }

    public void openSearch() {
        arrow.setRotation(-90);
        searchContainer.setVisibility(View.VISIBLE);
        searchCheck = true;
    }

    public void closeSearch() {
        arrow.setRotation(0);
        searchContainer.setVisibility(View.GONE);
        searchCheck = false;
    }
    private void providersApi(Integer rate, Integer nearBy, Integer discount, Double longitude, Double latitude) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().provider(
                sessionManager.getUserToken(), categoryId,
                rate, nearBy, discount, longitude, latitude, new Callback<ProvidersResponse>() {
                    @Override
                    public void success(ProvidersResponse providersResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = providersResponse.status;
                        if (status == 200) {
                            if (providersResponse.providers != null) {
                                if (providersResponse.providers.size() > 0) {
                                    providersList.clear();
                                    for (Provider value : providersResponse.providers) {
                                        if (value.providerShop != null) {
                                            providersList.add(value);
                                        }
                                    }
                                    providersAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Snackbar.make(loading, getString(R.string.noProviders), Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public void providersSearchApi(int country, int city, int zone) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().providerSearch(country, city, zone,
                new Callback<ProvidersResponse>() {
                    @Override
                    public void success(ProvidersResponse providersResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = providersResponse.status;
                        if (status == 200) {
                            if (providersResponse.providers != null) {
                                if (providersResponse.providers.size() > 0) {
                                    providersList.clear();
                                    providersList.addAll(providersResponse.providers);
                                    providersAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            providersList.clear();
                            Snackbar.make(loading, getString(R.string.noProviders), Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void makeFavoriteApi(int providerId, final ImageView addToFav) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().makeFavorite(
                sessionManager.getUserToken(), providerId,
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
    public void getCountries() {
        RawnaqApiConfig.getCallingAPIInterface().getCountries(new Callback<CountriesResponse>() {
            @Override
            public void success(CountriesResponse countriesResponse, Response response) {
                int status = countriesResponse.status;
                if (status == 200) {
                    if (countriesResponse.country != null) {
                        if (countriesResponse.country.size() > 0) {
                            for (Country value : countriesResponse.country) {
                                countriesNamesList.add(value.name);
                            }
                            countriesList.clear();
                            countriesList.addAll(countriesResponse.country);
                            countriesAdapter.notifyDataSetChanged();
                        }
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
}

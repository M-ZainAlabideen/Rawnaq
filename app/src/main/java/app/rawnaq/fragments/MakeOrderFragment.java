package app.rawnaq.fragments;

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
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.SubServicesAdapter;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.GpsTracker;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import app.rawnaq.webservices.responses.providers.Provider;
import app.rawnaq.webservices.responses.providers.ProviderService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MakeOrderFragment extends Fragment {
    public static FragmentActivity activity;
    public static MakeOrderFragment fragment;
    public static SessionManager sessionManager;
    private ArrayList<ProviderService> subServicesList = new ArrayList<>();
    private SubServicesAdapter subServicesAdapter;
    private GridLayoutManager layoutManager;
    private Provider provider;
    int day, month, year;
    String monthStr, dayStr, yearStr;
    String fullDate;
    private double currentLat;
    private double currentLng;

    @BindView(R.id.fragment_make_order_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_make_order_rv_subServices)
    RecyclerView subServices;
    @BindView(R.id.fragment_make_order_tv_name)
    TextView name;
    @BindView(R.id.fragment_make_order_tv_address)
    TextView address;
    @BindView(R.id.fragment_make_order_iv_workFromHome)
    ImageView workFromHome;
    @BindView(R.id.fragment_make_order_tv_workFromHomeTV)
    TextView workFromHomeTV;
    @BindView(R.id.fragment_make_order_rb_rating)
    RatingBar rating;
    @BindView(R.id.fragment_make_order_iv_addToFav)
    ImageView addToFav;
    @BindView(R.id.fragment_make_order_cb_homeRequest)
    CheckBox homeRequest;
    @BindView(R.id.fragment_make_order_tv_homeServicePrice)
    TextView homeServicePrice;
    @BindView(R.id.fragment_make_order_cv_date)
    CalendarView date;
    @BindView(R.id.fragment_make_order_et_notes)
    EditText notes;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static MakeOrderFragment newInstance(FragmentActivity activity) {
        fragment = new MakeOrderFragment();
        MakeOrderFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_make_order, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);
        FixControl.setupUI(container,activity);

        provider = (Provider) getArguments().getSerializable("provider");
        MainActivity.title.setText(provider.providerShop.nameShop);

        layoutManager = new GridLayoutManager(activity, 2);
        subServicesAdapter = new SubServicesAdapter(activity, subServicesList, true);
        subServices.setLayoutManager(layoutManager);
        subServices.setAdapter(subServicesAdapter);
        setData();

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        format();

        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, int year, int month,
                                            int date) {
                day = date;
                fragment.year = year;
                fragment.month = month + 1;
                format();

            }
        });
    }

    @OnClick(R.id.fragment_make_order_btn_sendOrder)
    public void sendOrderClick() {
        boolean workFromHome = homeRequest.isChecked();
        if (workFromHome) {
            getLocation();
        }
        String idsStr = null;
        for (int j = 0; j < SubServicesAdapter.subServicesIds.size(); j++) {
            if (idsStr == null) {
                idsStr = String.valueOf(SubServicesAdapter.subServicesIds.get(j));
            } else {
                idsStr = idsStr + "," + SubServicesAdapter.subServicesIds.get(j);
            }
        }

        String noteStr = notes.getText().toString();

        if (idsStr == null || idsStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.selectService), Snackbar.LENGTH_SHORT).show();
        } else if (noteStr == null || noteStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.addYouAddress), Snackbar.LENGTH_SHORT).show();
        } else {
            makeOrderApi(idsStr,workFromHome, noteStr);
        }
    }

    @OnClick(R.id.fragment_make_order_iv_addToFav)
    public void addToFavClick() {
        makeFavoriteApi();
    }

    private void setData() {
        String shopName = provider.providerShop.nameShop;
        String city = provider.providerShop.city.name;
        String zone = provider.providerShop.zone.name;
        String street = provider.providerShop.street;
        double ratingValue = provider.providerShop.rate;
        int isWorkFromHome = provider.providerShop.workFromHome;
        boolean favorite = provider.providerShop.fav;
        String homePrice = String.valueOf(provider.providerShop.home_price);
        subServicesList.addAll(provider.providerShop.providerServices);
        subServicesAdapter.notifyDataSetChanged();

        name.setText(shopName);
        address.setText(city + "| " + zone + "| " + street);
        if (isWorkFromHome != 1) {
            workFromHome.setVisibility(View.GONE);
            workFromHomeTV.setVisibility(View.GONE);
        }
        if (favorite) {
            addToFav.setImageResource(R.drawable.ic_fav);
        }
        rating.setRating((float) ratingValue);
        homeServicePrice.setText(getString(R.string.homeCost).replace(getString(R.string.fifty), homePrice));
    }

    private void getLocation() {
        GpsTracker gps = new GpsTracker(activity);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            currentLat = gps.getLatitude();
            currentLng = gps.getLongitude();
        }
    }

    private void format() {
        monthStr = (month < 10 ? "0" : "") + month;
        dayStr = (day < 10 ? "0" : "") + day;
        yearStr = year + "";
        fullDate = yearStr + "-" + monthStr + "-" + dayStr;
    }

    private void makeFavoriteApi() {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().makeFavorite(
                sessionManager.getUserToken(), provider.id,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = generalResponse.status;
                        if (status == 200) {
                            if (addToFav.getDrawable().getConstantState() ==
                                    getResources().getDrawable(R.drawable.ic_fav).getConstantState()) {
                                addToFav.setImageResource(R.mipmap.ic_un_fav);
                                provider.providerShop.fav = false;
                            } else {
                                addToFav.setImageResource(R.drawable.ic_fav);
                                provider.providerShop.fav = true;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }

    public void makeOrderApi(String idsStr,boolean workFromHome, String noteStr) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().makeOrder(
                sessionManager.getUserToken(),
                provider.id,
                idsStr,
                workFromHome,
                fullDate,
                currentLat,
                currentLng,
                noteStr,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = generalResponse.status;
                        if (status == 200) {
                            Navigator.loadFragment(activity, OrdersFragment.newInstance(activity), R.id.main_fl_container, false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.setVisibility(View.GONE);
                    }
                }
        );

    }
}

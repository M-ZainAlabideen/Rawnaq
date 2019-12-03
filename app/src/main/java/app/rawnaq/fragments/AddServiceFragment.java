package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.categories.CategoriesResponse;
import app.rawnaq.webservices.responses.categories.Category;
import app.rawnaq.webservices.responses.categories.Service;
import app.rawnaq.webservices.responses.providers.ProviderService;
import app.rawnaq.webservices.responses.providers.ProviderServiceResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddServiceFragment extends Fragment {
    public static FragmentActivity activity;
    public static AddServiceFragment fragment;
    private SessionManager sessionManager;
    public ArrayList<Category> categoriesList = new ArrayList<>();
    public ArrayList<String> categoriesNamesList = new ArrayList<>();
    public ArrayList<String> servicesNamesList = new ArrayList<>();

    private ArrayAdapter<String> categoriesAdapter;
    private ArrayAdapter<String> servicesNamesAdapter;
    private int categoryPosition;
    private int serviceNamePosition;
    private ProviderService serviceData;

    @BindView(R.id.fragment_add_service_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_add_service_spin_mainCategory)
    Spinner mainCategory;
    @BindView(R.id.fragment_add_service_spin_serviceName)
    Spinner serviceName;
    @BindView(R.id.fragment_add_service_et_serviceCost)
    EditText serviceCost;
    @BindView(R.id.fragment_add_service_rb_forMen)
    RadioButton forMen;
    @BindView(R.id.fragment_add_service_rb_forWomen)
    RadioButton forWomen;
    @BindView(R.id.fragment_add_service_rb_forAll)
    RadioButton forAll;
    @BindView(R.id.fragment_add_service_cb_specialOffer)
    CheckBox specialOffer;
    @BindView(R.id.fragment_add_service_et_serviceCostWithOffer)
    EditText serviceCostWithOffer;
    @BindView(R.id.loading)
    ProgressBar loading;


    public static AddServiceFragment newInstance(FragmentActivity activity, ProviderService serviceData) {
        fragment = new AddServiceFragment();
        AddServiceFragment.activity = activity;
        Bundle b = new Bundle();
        b.putSerializable("serviceData", serviceData);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_add_service, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        serviceData = (ProviderService) getArguments().getSerializable("serviceData");

        if(serviceData == null) {
            MainActivity.title.setText(getString(R.string.addService));
        }
        else{
            MainActivity.title.setText(getString(R.string.editService));
        }
        container.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);

        categoriesAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, categoriesNamesList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainCategory.setAdapter(categoriesAdapter);

        servicesNamesAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, servicesNamesList);
        servicesNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceName.setAdapter(servicesNamesAdapter);

        mainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryPosition = position;
                servicesNamesList.clear();
                for (Service value : categoriesList.get(position).services) {
                    servicesNamesList.add(value.name);
                }
                servicesNamesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        serviceName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serviceNamePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categoriesApi();
    }

    private void setData(ProviderService serviceData) {
        for (byte i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).id == serviceData.category.categoryId) {
                mainCategory.setSelection(i);
            }
            for (byte j = 0; j < servicesNamesList.size(); j++) {
                if (servicesNamesList.get(i).equals(serviceData.category.name)) {
                    serviceName.setSelection(i);
                }
            }
            if (serviceData.gender.equals("f")) {
                forWomen.setChecked(true);
            } else if (serviceData.gender.equals("m")) {
                forMen.setChecked(true);
            } else if (serviceData.gender.equals("all")) {
                forAll.setChecked(true);
            }
            serviceCost.setText(String.valueOf(serviceData.price));
            if (serviceData.is_discount == 1) {
                specialOffer.setChecked(true);
                serviceCostWithOffer.setText(String.valueOf(serviceData.discount));
            }

        }
    }

    @OnClick(R.id.fragment_add_service_btn_save)
    public void saveClick() {
        String serviceCostStr = serviceCost.getText().toString();
        String serviceSegment = null;
        String serviceCostWithOfferStr = null;
        if (forMen.isChecked()) {
            serviceSegment = "m";
        } else if (forWomen.isChecked()) {
            serviceSegment = "f";
        } else if (forAll.isChecked()) {
            serviceSegment = "all";
        }
        int serviceId = categoriesList.get(categoryPosition).services.get(serviceNamePosition).id;

        if (serviceSegment == null || serviceSegment.isEmpty()) {
            Snackbar.make(loading, getString(R.string.selectServiceSegment), Snackbar.LENGTH_SHORT).show();
        } else if (serviceCostStr == null || serviceCostStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.enterServiceCost), Snackbar.LENGTH_SHORT).show();
        } else if (specialOffer.isChecked()) {
            serviceCostWithOfferStr = serviceCostWithOffer.getText().toString();
            if (serviceCostWithOfferStr == null || serviceCostWithOfferStr.isEmpty()) {
                Snackbar.make(loading, getString(R.string.enterServiceCostWithOffer), Snackbar.LENGTH_SHORT).show();
            } if(serviceData != null){
                editServiceApi(serviceId, serviceSegment, Integer.parseInt(serviceCostStr),
                        1, Integer.parseInt(serviceCostWithOfferStr));
            }
            else {
                addServiceApi(serviceId, serviceSegment, Integer.parseInt(serviceCostStr),
                        1, Integer.parseInt(serviceCostWithOfferStr));
            }

        } else {
            if (serviceData != null){
                editServiceApi(serviceId, serviceSegment, Integer.parseInt(serviceCostStr),
                        0, 0);
            }
            else {
                addServiceApi(serviceId, serviceSegment, Integer.parseInt(serviceCostStr),
                        0, 0);
            }
        }
    }


    private void categoriesApi() {
        RawnaqApiConfig.getCallingAPIInterface().Categories(new Callback<CategoriesResponse>() {
            @Override
            public void success(CategoriesResponse categoriesResponse, Response response) {
                loading.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                int status = categoriesResponse.status;
                if (status == 200) {
                    if (categoriesResponse.categories != null) {
                        if (categoriesResponse.categories.size() > 0) {
                            categoriesList.clear();
                            categoriesList.addAll(categoriesResponse.categories);
                            for (Category value : categoriesList) {
                                categoriesNamesList.add(value.name);
                                for (Service innerValue : value.services) {
                                    servicesNamesList.add(innerValue.name);
                                }
                            }
                            categoriesAdapter.notifyDataSetChanged();
                            servicesNamesAdapter.notifyDataSetChanged();
                            if (serviceData != null) {
                                setData(serviceData);
                            }
                        }
                    }
                } else if (status == 204) {
                    Snackbar.make(loading, getString(R.string.noCategories), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                loading.setVisibility(View.GONE);
                Snackbar.make(loading, getString(R.string.error), Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    private void addServiceApi(int serviceId, String type, int price, int isDiscount, int discount) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().addService(
                sessionManager.getUserToken(), serviceId, type,
                price, isDiscount, discount, new Callback<ProviderServiceResponse>() {
                    @Override
                    public void success(ProviderServiceResponse providerServiceResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = providerServiceResponse.status;
                        if (status == 200) {
                            Snackbar.make(loading, getString(R.string.serviceAdded), Snackbar.LENGTH_SHORT).show();
                            Navigator.loadFragment(activity, ProviderServicesFragment.newInstance(activity), R.id.main_fl_container, false);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }

        );
    }
    private void editServiceApi(int serviceId,String type, int price, int isDiscount, int discount){
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().providerEditService(sessionManager.getUserToken(),
                sessionManager.getProviderId(), serviceId, type, price, isDiscount, discount,
                new Callback<ProviderServiceResponse>() {
                    @Override
                    public void success(ProviderServiceResponse providerServiceResponse, Response response) {
                        int status = providerServiceResponse.status;
                        //fix it
                       // if(status == 200){
                            loading.setVisibility(View.GONE);
                            Snackbar.make(loading, getString(R.string.serviceUpdated), Snackbar.LENGTH_SHORT).show();
                            Navigator.loadFragment(activity, ProviderServicesFragment.newInstance(activity), R.id.main_fl_container, false);
                      //  }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

}


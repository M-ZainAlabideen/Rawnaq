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
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgetPass1Fragment extends Fragment {
    public static FragmentActivity activity;
    public static ForgetPass1Fragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.fragment_forget_pass1_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_forget_pass1_et_phone)
    EditText phone;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ForgetPass1Fragment newInstance(FragmentActivity activity) {
        fragment = new ForgetPass1Fragment();
        ForgetPass1Fragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_forget_pass1, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        FixControl.setupUI(container,activity);

    }

    @OnClick(R.id.fragment_forget_pass1_btn_send)
    public void sendClick(){
        String phoneStr = phone.getText().toString();
        if(phoneStr == null || phoneStr.matches("")){
            Snackbar.make(loading,getString(R.string.enterPhone),Snackbar.LENGTH_SHORT).show();
        }
        else{
            forgetPasswordApi(phoneStr);
        }
    }

    private void forgetPasswordApi(String phone){
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().forgetPassword(phone,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = generalResponse.status;
                        if(status == 200){
                            Navigator.loadFragment(activity, ForgetPass2Fragment.newInstance(activity), R.id.main_fl_container, false);
                        }
                        else if(status == 412){
                            Snackbar.make(loading,getString(R.string.wrongPhone),Snackbar.LENGTH_SHORT).show();
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



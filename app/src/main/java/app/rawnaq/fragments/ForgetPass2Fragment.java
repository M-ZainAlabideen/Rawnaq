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
import app.rawnaq.webservices.responses.user.UserResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgetPass2Fragment extends Fragment {
    public static FragmentActivity activity;
    public static ForgetPass2Fragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.fragment_forget_pass2_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_forget_pass2_et_code)
    EditText code;
    @BindView(R.id.fragment_forget_pass2_et_password)
    EditText password;
    @BindView(R.id.fragment_forget_pass2_et_confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ForgetPass2Fragment newInstance(FragmentActivity activity) {
        fragment = new ForgetPass2Fragment();
        ForgetPass2Fragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_forget_pass2, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        FixControl.setupUI(container,activity);
    }

    @OnClick(R.id.fragment_forget_pass2_btn_send)
    public void sendClick(){
        String codeStr = code.getText().toString();
        String passwordStr = password.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();
        if(codeStr == null || codeStr.matches("")){
            Snackbar.make(loading, getString(R.string.enterCode), Snackbar.LENGTH_SHORT).show();
        }
        else if(passwordStr == null || passwordStr.matches("")){
            Snackbar.make(loading, getString(R.string.enterPassword), Snackbar.LENGTH_SHORT).show();
        }
        else if(confirmPasswordStr == null || confirmPasswordStr.matches("")){
            Snackbar.make(loading, getString(R.string.enterConfirmPassword), Snackbar.LENGTH_SHORT).show();
        }
        else if(!passwordStr.equals(confirmPasswordStr)){
            Snackbar.make(loading, getString(R.string.passwordNotMatched), Snackbar.LENGTH_SHORT).show();
        }
        else if (passwordStr.length() < 6) {
            Snackbar.make(loading, getString(R.string.passwordMinLength), Snackbar.LENGTH_SHORT).show();
        }
        else{
            changePasswordApi(codeStr,passwordStr,confirmPasswordStr);
        }
    }

    private void changePasswordApi(String code,String phone,String password){
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().changePassword(
                code, phone, password, new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = generalResponse.status;
                        if(status == 200){
                            Snackbar.make(loading,getString(R.string.passwordChanged),Snackbar.LENGTH_SHORT).show();
                            Navigator.loadFragment(activity, LoginFragment.newInstance(activity), R.id.main_fl_container, false);
                        }
                        else if(status == 410){
                            Snackbar.make(loading,getString(R.string.wrongCode),Snackbar.LENGTH_SHORT).show();
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
}

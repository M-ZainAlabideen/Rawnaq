package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.general.GeneralResponse;
import app.rawnaq.webservices.responses.user.Provider;
import app.rawnaq.webservices.responses.user.User;
import app.rawnaq.webservices.responses.user.UserResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginFragment extends Fragment {
    public static FragmentActivity activity;
    public static LoginFragment fragment;
    public static SessionManager sessionManager;
    private String regId = "";

    @BindView(R.id.fragment_login_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_login_et_phone)
    EditText phone;
    @BindView(R.id.fragment_login_et_password)
    EditText password;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static LoginFragment newInstance(FragmentActivity activity) {
        fragment = new LoginFragment();
        LoginFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);
        FixControl.setupUI(container,activity);
    }

    @OnClick(R.id.fragment_login_btn_login)
    public void loginClick() {
        String phoneStr = phone.getText().toString();
        String passwordStr = password.getText().toString();


        if (phoneStr == null || phoneStr.matches(""))
            Snackbar.make(loading, getString(R.string.enterPhone), Snackbar.LENGTH_SHORT).show();

        else if (passwordStr == null || passwordStr.matches(""))
            Snackbar.make(loading, getString(R.string.enterPassword), Snackbar.LENGTH_SHORT).show();

        else if (passwordStr.length() < 6)
            Snackbar.make(loading, getString(R.string.passwordMinLength), Snackbar.LENGTH_SHORT).show();

        else {
            loading.setVisibility(View.VISIBLE);
            RawnaqApiConfig.getCallingAPIInterface().Login(
                    phoneStr, passwordStr, new Callback<UserResponse>() {
                        @Override
                        public void success(UserResponse userResponse, Response response) {
                            loading.setVisibility(View.GONE);
                            int status = userResponse.status;
                            if (status == 200) {
                                clearStack();

                                if (userResponse.user != null) {
                                    User myUser = userResponse.user;
                                    sessionManager.setUserToken(myUser.apiToken);
                                    sessionManager.setUserMail(myUser.email);
                                    sessionManager.setUserName(myUser.name);
                                    sessionManager.setUserPhone(myUser.phone);

                                     if (sessionManager.isValidation()) {
                                    Navigator.loadFragment(activity, CategoriesFragment.newInstance(activity), R.id.main_fl_container, false);
                                    } else {
                                        Navigator.loadFragment(activity, ValidationCodeFragment.newInstance(activity,false), R.id.main_fl_container, false);
                                    }
                                } else if (userResponse.provider != null) {
                                    Provider myProvider = userResponse.provider;
                                    sessionManager.setProviderId(myProvider.id);
                                    sessionManager.setUserToken(myProvider.apiToken);
                                    sessionManager.setUserMail(myProvider.email);
                                    sessionManager.setUserName(myProvider.name);
                                    sessionManager.setUserPhone(myProvider.phone);
                                    sessionManager.setProvider();

                                    if (myProvider.providerShop != null) {
                                        sessionManager.setShop();
                                    }
                                      if (sessionManager.isValidation()) {
                                    if (sessionManager.hasShop()) {
                                        Navigator.loadFragment(activity, CurrentOrdersFragment.newInstance(activity, "waiting"), R.id.main_fl_container, false);
                                    } else {
                                        Navigator.loadFragment(activity, HomeFragment.newInstance(activity), R.id.main_fl_container, false);
                                    }
                                    } else {
                                        Navigator.loadFragment(activity, ValidationCodeFragment.newInstance(activity,false), R.id.main_fl_container, false);
                                    }
                                }
                                String firstName = sessionManager.getUserName().split(" ")[0];
                                if (firstName.length() > 10)
                                    MainActivity.userName.setText(getString(R.string.welcomeUser) + ":  " + firstName.substring(0, 10));
                                else
                                    MainActivity.userName.setText(getString(R.string.welcomeUser) + ":  " + firstName);

                                if (sessionManager.isGuest()) {
                                    sessionManager.guestLogout();
                                }
                                sessionManager.LoginSession();

                                if (sessionManager.isProvider()) {
                                    MainActivity.providerContainer.setVisibility(View.VISIBLE);
                                    MainActivity.userContainer.setVisibility(View.GONE);
                                } else {
                                    MainActivity.providerContainer.setVisibility(View.GONE);
                                    MainActivity.userContainer.setVisibility(View.VISIBLE);
                                }
                                MainActivity.accountOrLoginTxt.setText(getString(R.string.myAccount));
                                MainActivity.providerAccountOrLoginTxt.setText(getString(R.string.myAccount));

                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.w("login", "getInstanceId failed", task.getException());
                                                    return;
                                                }

                                                // Get new Instance ID token
                                                regId = task.getResult().getToken();

                                                Log.e("registerationid Splash ", "regid -> "+regId);

                                                registerFirebaseTokenApi();


                                            }
                                        });
                            } else if (status == 415) {
                                Snackbar.make(loading, getString(R.string.unRegisterPhone), Snackbar.LENGTH_SHORT).show();
                            } else if (status == 410) {
                                Snackbar.make(loading, getString(R.string.inCorrectPassword), Snackbar.LENGTH_SHORT).show();
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

    @OnClick(R.id.fragment_login_tv_signUp)
    public void signUpClick() {
        Navigator.loadFragment(activity, SignUpFragment.newInstance(activity, "signUp"), R.id.main_fl_container, true);
    }

    @OnClick(R.id.fragment_login_tv_forgetPass)
    public void forgetPassClick() {
        Navigator.loadFragment(activity, ForgetPass1Fragment.newInstance(activity), R.id.main_fl_container, true);
    }

    @OnClick(R.id.fragment_login_tv_guest)
    public void guestClick() {
        sessionManager.ContinueAsGuest();
        Navigator.loadFragment(activity, CategoriesFragment.newInstance(activity), R.id.main_fl_container, false);
    }


    private void registerFirebaseTokenApi() {

        RawnaqApiConfig.getCallingAPIInterface().registerFirebaseToken(sessionManager.getUserToken(), regId,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {
                        if (generalResponse.status == 200) {
                            sessionManager.setRegId(regId);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }

                });

    }

    private void clearStack() {
        FragmentManager fm = activity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}

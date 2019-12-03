package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.user.Provider;
import app.rawnaq.webservices.responses.user.UserResponse;
import app.rawnaq.webservices.responses.user.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpFragment extends Fragment {
    public static FragmentActivity activity;
    public static SignUpFragment fragment;
    public static SessionManager sessionManager;

    @BindView(R.id.fragment_sign_up_et_name)
    EditText name;
    @BindView(R.id.fragment_sign_up_et_email)
    EditText email;
    @BindView(R.id.fragment_sign_up_et_phone)
    EditText phone;
    @BindView(R.id.fragment_sign_up_et_password)
    EditText password;
    @BindView(R.id.fragment_sign_up_et_confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.fragment_sign_up_rb_serviceProvider)
    RadioButton serviceProvider;
    @BindView(R.id.fragment_sign_up_rb_user)
    RadioButton user;
    @BindView(R.id.fragment_sign_up_cb_agree)
    CheckBox agree;
    @BindView(R.id.fragment_sign_up_tv_title)
    TextView title;
    @BindView(R.id.fragment_sign_up_btn_signUp)
    Button signUp;
    @BindView(R.id.fragment_sign_up_tv_guest)
    TextView guest;
    @BindView(R.id.fragment_sign_up_tv_login)
    TextView login;
    @BindView(R.id.fragment_sign_up_iv_bottomBg)
    ImageView bottomBg;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static SignUpFragment newInstance(FragmentActivity activity, String comingFrom) {
        fragment = new SignUpFragment();
        SignUpFragment.activity = activity;
        Bundle b = new Bundle();
        b.putString("comingFrom", comingFrom);
        fragment.setArguments(b);
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.GONE);

        if (getArguments().getString("comingFrom").equals("update")) {
            serviceProvider.setVisibility(View.INVISIBLE);
            user.setVisibility(View.INVISIBLE);
            guest.setVisibility(View.INVISIBLE);
            login.setVisibility(View.INVISIBLE);
            agree.setVisibility(View.INVISIBLE);
            password.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);
            bottomBg.setVisibility(View.GONE);
            signUp.setText(getString(R.string.save));
            title.setText(getString(R.string.updateProfile));
            name.setText(sessionManager.getUserName());
            phone.setText(sessionManager.getUserPhone());
            email.setText(sessionManager.getUserMail());

        }
        serviceProvider.setOnCheckedChangeListener(radioCheckListener);
        user.setOnCheckedChangeListener(radioCheckListener);
    }

    CompoundButton.OnCheckedChangeListener radioCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                buttonView.setChecked(true);
                switch (buttonView.getId()) {
                    case R.id.fragment_sign_up_rb_serviceProvider:
                        user.setChecked(false);
                        break;
                    case R.id.fragment_sign_up_rb_user:
                        serviceProvider.setChecked(false);
                        break;
                }
            }
        }
    };

    @OnClick(R.id.fragment_sign_up_btn_signUp)
    public void signUpClick() {
        String nameStr = name.getText().toString();
        final String phoneStr = phone.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String confirmPassStr = confirmPassword.getText().toString();
        if (getArguments().getString("comingFrom").equals("update")) {
            if (nameStr == null || nameStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterName), Snackbar.LENGTH_SHORT).show();

            else if (phoneStr == null || phoneStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterPhone), Snackbar.LENGTH_SHORT).show();

            else if (emailStr == null || emailStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterEmail), Snackbar.LENGTH_SHORT).show();
            else {
                loading.setVisibility(View.VISIBLE);
                RawnaqApiConfig.getCallingAPIInterface().updateProfile(
                        sessionManager.getUserToken(), nameStr, phoneStr, emailStr, null,
                        new Callback<UserResponse>() {
                            @Override
                            public void success(UserResponse userResponse, Response response) {
                                loading.setVisibility(View.GONE);
                                if (!sessionManager.getUserPhone().equals(phoneStr)) {
                                    Navigator.loadFragment(activity, ValidationCodeFragment.newInstance(activity,true), R.id.main_fl_container, false);
                                } else {
                                    Snackbar.make(loading, getString(R.string.updatedSuccessfully), Snackbar.LENGTH_SHORT).show();
                                    activity.onBackPressed();
                                }
                                if (userResponse.user != null) {
                                    User myUser = userResponse.user;
                                    sessionManager.setUserToken(myUser.apiToken);
                                    sessionManager.setUserMail(myUser.email);
                                    sessionManager.setUserName(myUser.name);
                                    sessionManager.setUserPhone(myUser.phone);

                                } else if (userResponse.provider != null) {
                                    Provider myProvider = userResponse.provider;
                                    sessionManager.setUserToken(myProvider.apiToken);
                                    sessionManager.setUserMail(myProvider.email);
                                    sessionManager.setUserName(myProvider.name);
                                    sessionManager.setUserPhone(myProvider.phone);
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        }
                );
            }
        } else {
            int accountTypeValue = -1;
            if (user.isChecked())
                accountTypeValue = 0;
            else if (serviceProvider.isChecked())
                accountTypeValue = 1;

            if (nameStr == null || nameStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterName), Snackbar.LENGTH_SHORT).show();

            else if (phoneStr == null || phoneStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterPhone), Snackbar.LENGTH_SHORT).show();

            else if (emailStr == null || emailStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterEmail), Snackbar.LENGTH_SHORT).show();

            else if (passwordStr == null || passwordStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterPassword), Snackbar.LENGTH_SHORT).show();

            else if (confirmPassStr == null || confirmPassStr.matches(""))
                Snackbar.make(loading, getString(R.string.enterPasswordAgain), Snackbar.LENGTH_SHORT).show();

            else if (!passwordStr.equals(confirmPassStr))
                Snackbar.make(loading, getString(R.string.passwordNotMatched), Snackbar.LENGTH_SHORT).show();

            else if (accountTypeValue == -1)
                Snackbar.make(loading, getString(R.string.selectType), Snackbar.LENGTH_SHORT).show();

            else if (!agree.isChecked())
                Snackbar.make(loading, getString(R.string.agreeForTerms), Snackbar.LENGTH_SHORT).show();

            else if (nameStr.length() < 3)
                Snackbar.make(loading, getString(R.string.nameMinLength), Snackbar.LENGTH_SHORT).show();

            else if (emailStr.length() < 5 || !FixControl.isValidEmail(emailStr))
                Snackbar.make(loading, getString(R.string.unValidMail), Snackbar.LENGTH_SHORT).show();

            else if (passwordStr.length() < 6)
                Snackbar.make(loading, getString(R.string.passwordMinLength), Snackbar.LENGTH_SHORT).show();


            else {
                loading.setVisibility(View.VISIBLE);
                RawnaqApiConfig.getCallingAPIInterface().Register(
                        phoneStr, nameStr, emailStr, passwordStr, accountTypeValue,
                        new Callback<UserResponse>() {
                            @Override
                            public void success(UserResponse userResponse, Response response) {
                                loading.setVisibility(View.GONE);
                                int status = userResponse.status;
                                if (status == 200) {
                                    if (userResponse.user != null) {
                                        User myUser = userResponse.user;
                                        sessionManager.setUserToken(myUser.apiToken);
                                        sessionManager.setUserMail(myUser.email);
                                        sessionManager.setUserName(myUser.name);
                                        sessionManager.setUserPhone(myUser.phone);
                                    } else if (userResponse.provider != null) {
                                        Provider myProvider = userResponse.provider;
                                        sessionManager.setProviderId(myProvider.id);
                                        sessionManager.setUserToken(myProvider.apiToken);
                                        sessionManager.setUserMail(myProvider.email);
                                        sessionManager.setUserName(myProvider.name);
                                        sessionManager.setUserPhone(myProvider.phone);
                                        sessionManager.setProvider();
                                    }

                                    if (sessionManager.isGuest()) {
                                        sessionManager.guestLogout();
                                    }
                                    sessionManager.LoginSession();
                                    Navigator.loadFragment(activity, ValidationCodeFragment.newInstance(activity,false), R.id.main_fl_container, false);

                                } else if (status == 409) {
                                    Snackbar.make(loading, getString(R.string.emailExisted), Snackbar.LENGTH_SHORT).show();
                                } else if (status == 410) {
                                    Snackbar.make(loading, getString(R.string.phoneExisted), Snackbar.LENGTH_SHORT).show();
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
    }

    @OnClick(R.id.fragment_sign_up_tv_guest)
    public void guestClick() {
        sessionManager.ContinueAsGuest();
        Navigator.loadFragment(activity, CategoriesFragment.newInstance(activity), R.id.main_fl_container, false);
    }

    @OnClick(R.id.fragment_sign_up_tv_login)
    public void loginClick() {
        activity.onBackPressed();
    }


}

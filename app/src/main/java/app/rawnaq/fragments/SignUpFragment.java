package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {
    public static FragmentActivity activity;
    public static SignUpFragment fragment;
    public static SessionManager sessionManager;
    public static Map<String, String> User;

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


    public static SignUpFragment newInstance(FragmentActivity activity) {
        fragment = new SignUpFragment();
        SignUpFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
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
    }

    @OnClick(R.id.fragment_sign_up_btn_signUp)
    public void signUpClick() {
        Navigator.loadFragment(activity, ServicesFragment.newInstance(activity), R.id.main_fl_container, false);
    }

    @OnClick(R.id.fragment_sign_up_tv_guest)
    public void guestClick() {
        Navigator.loadFragment(activity, ServicesFragment.newInstance(activity), R.id.main_fl_container, false);
    }

    @OnClick(R.id.fragment_sign_up_tv_login)
    public void loginClick() {
        activity.onBackPressed();
    }


}

package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {
    public static FragmentActivity activity;
    public static LoginFragment fragment;

    public static SessionManager sessionManager;
    public static Map<String, String> User;

    public static LoginFragment newInstance(FragmentActivity activity) {
        fragment = new LoginFragment();
        LoginFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
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
    }

    @OnClick(R.id.fragment_login_btn_login)
    public void loginClick(){
        Navigator.loadFragment(activity, ServicesFragment.newInstance(activity), R.id.main_fl_container, false);
    }

    @OnClick(R.id.fragment_login_tv_signUp)
    public void signUpClick(){
        Navigator.loadFragment(activity, SignUpFragment.newInstance(activity), R.id.main_fl_container, true);
    }

    @OnClick(R.id.fragment_login_tv_forgetPass)
    public void forgetPassClick(){
        Navigator.loadFragment(activity, ForgetPass1Fragment.newInstance(activity), R.id.main_fl_container, false);
    }

    @OnClick(R.id.fragment_login_tv_guest)
    public void guestClick(){
        Navigator.loadFragment(activity, ServicesFragment.newInstance(activity), R.id.main_fl_container, false);
    }


}

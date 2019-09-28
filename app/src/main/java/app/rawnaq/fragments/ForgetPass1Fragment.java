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

public class ForgetPass1Fragment extends Fragment {
    public static FragmentActivity activity;
    public static ForgetPass1Fragment fragment;

    public static SessionManager sessionManager;
    public static Map<String, String> User;

    public static ForgetPass1Fragment newInstance(FragmentActivity activity) {
        fragment = new ForgetPass1Fragment();
        ForgetPass1Fragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
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
    }

    @OnClick(R.id.fragment_forget_pass1_btn_send)
    public void sendClick(){
        Navigator.loadFragment(activity, ForgetPass2Fragment.newInstance(activity), R.id.main_fl_container, false);

    }
}



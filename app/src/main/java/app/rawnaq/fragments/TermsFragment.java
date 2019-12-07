package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.GlobalFunctions;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsFragment extends Fragment {
    public static FragmentActivity activity;
    public static TermsFragment fragment;

    @BindView(R.id.fragment_terms_tv_policyUser)
    TextView policyUser;
    @BindView(R.id.fragment_terms_tv_policyProvider)
    TextView policyProvider;

    public static TermsFragment newInstance(FragmentActivity activity) {
        fragment = new TermsFragment();
        TermsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);
        MainActivity.title.setText(getString(R.string.terms));

        policyUser.setText(GlobalFunctions.policy_user);
        policyProvider.setText(GlobalFunctions.policy_provider);
    }
    


}

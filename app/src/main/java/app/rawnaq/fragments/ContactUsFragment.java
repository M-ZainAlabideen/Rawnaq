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
import android.widget.TextView;

import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.GlobalFunctions;
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

public class ContactUsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ContactUsFragment fragment;
    private SessionManager sessionManager;

    @BindView(R.id.fragment_contact_us_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_contact_us_tv_phoneContact)
    TextView phoneContact;
    @BindView(R.id.fragment_contact_us_tv_emailContact)
    TextView emailContact;
    @BindView(R.id.fragment_contact_us_et_name)
    EditText name;
    @BindView(R.id.fragment_contact_us_et_email)
    EditText email;
    @BindView(R.id.fragment_contact_us_et_phone)
    EditText phone;
    @BindView(R.id.fragment_contact_us_et_message)
    EditText message;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ContactUsFragment newInstance(FragmentActivity activity) {
        fragment = new ContactUsFragment();
        ContactUsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.back.setVisibility(View.VISIBLE);
        FixControl.setupUI(container,activity);
        MainActivity.title.setText(getString(R.string.contactUs));

        sessionManager = new SessionManager(activity);
        phoneContact.setText(GlobalFunctions.appPhone);

        name.setText(sessionManager.getUserName());
        email.setText(sessionManager.getUserMail());
        phone.setText(sessionManager.getUserPhone());
    }

    @OnClick(R.id.fragment_contact_us_iv_whatsApp)
    public void whatsClick() {
        Navigator.loadFragment(activity,UrlsFragment.newInstance(activity,GlobalFunctions.whatsLink),R.id.main_fl_container,true);
    }

    @OnClick(R.id.fragment_contact_us_iv_facebook)
    public void facebookClick() {
        Navigator.loadFragment(activity,UrlsFragment.newInstance(activity,GlobalFunctions.faceLink),R.id.main_fl_container,true);
    }

    @OnClick(R.id.fragment_contact_us_iv_twitter)
    public void twitterClick() {
        Navigator.loadFragment(activity,UrlsFragment.newInstance(activity,GlobalFunctions.twitterLink),R.id.main_fl_container,true);
    }

    @OnClick(R.id.fragment_contact_us_btn_send)
    public void sendClick() {
        String nameStr = name.getText().toString();
        String phoneStr = phone.getText().toString();
        String emailStr = email.getText().toString();
        String messageStr = message.getText().toString();
        if (nameStr == null || nameStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.enterName), Snackbar.LENGTH_SHORT).show();
        } else if (phoneStr == null || phoneStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.enterPhone), Snackbar.LENGTH_SHORT).show();
        } else if (emailStr == null || emailStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.enterEmail), Snackbar.LENGTH_SHORT).show();
        } else if (messageStr == null || messageStr.isEmpty()) {
            Snackbar.make(loading, getString(R.string.enterMessage), Snackbar.LENGTH_SHORT).show();
        }
        else {
            contactApi(nameStr,phoneStr,emailStr,messageStr);
        }

    }

    private void initialize(){
        name.setText("");
        phone.setText("");
        email.setText("");
        message.setText("");
    }

    private void contactApi(final String name, String phone, String mail, String message) {
        loading.setVisibility(View.VISIBLE);
        RawnaqApiConfig.getCallingAPIInterface().Contact(name, phone, mail, message, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, Response response) {
                loading.setVisibility(View.GONE);
                int status = generalResponse.status;
                if (status == 200) {
                    initialize();
                    Snackbar.make(loading, getString(R.string.thanks), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}

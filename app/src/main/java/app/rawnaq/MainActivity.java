package app.rawnaq;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import app.rawnaq.classes.LocaleHelper;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.fragments.LoginFragment;
import app.rawnaq.fragments.TermsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigationView)
    NavigationView navigationView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_fl_container)
    FrameLayout Container;
    @BindView(R.id.main_iv_back)
    ImageView back;


    /*butterKnife don't work with static or private
     if there is needing of static or private >> use the normal way
    * */
    public static AppBarLayout appbar;
    public static TextView title;
    public static TextView accountOrLogin;
    public static ImageView menu;


    //this variable is used to check the current selected language in the app
    public static boolean isEnglish;
    private String language;
    SessionManager sessionManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupStatusBar();
        //this line must be added to use butterKnife in the activity
        ButterKnife.bind(this);
        setupAppBar();

        sessionManager = new SessionManager(this);
        language = sessionManager.getUserLanguage();
        if (language.equals(""))
            language = "ar";
        LocaleHelper.setLocale(this, language);
        sessionManager.setUserLanguage(language);

        appbar = (AppBarLayout) findViewById(R.id.main_appbar);
        title = (TextView) findViewById(R.id.main_tv_title);
        accountOrLogin = (TextView) findViewById(R.id.main_tv_accountOrLogin);
        menu = (ImageView) findViewById(R.id.main_iv_menu);

        Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_fl_container, false);
    }

    @OnClick(R.id.main_iv_menu)
    public void menuClick() {
        //open and close the sideMenu when the navigationIcon clicked
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    //the back button action of all the app
    @OnClick(R.id.main_iv_back)
    public void back() {
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawers();
            } else {
                onBackPressed();
            }
        }
        return true;
    }


    //SideMenu items Clicks

    //myAccount click
    @OnClick(R.id.main_ll_accountOrLogin)
    public void accountClick() {
        drawerLayout.closeDrawers();
    }

    //contactUs Click
    @OnClick(R.id.main_ll_myOrders)
    public void myOrdersClick() {
        drawerLayout.closeDrawers();
    }

    //aboutUs Click
    @OnClick(R.id.main_ll_aboutUs)
    public void aboutUsClick() {
        drawerLayout.closeDrawers();
    }

    //aboutUs Click
    @OnClick(R.id.main_ll_contactUs)
    public void contactUsClick() {
        drawerLayout.closeDrawers();
    }

    //aboutUs Click
    @OnClick(R.id.main_ll_favorites)
    public void favoritesClick() {
        drawerLayout.closeDrawers();
    }

    //aboutUs Click
    @OnClick(R.id.main_ll_terms)
    public void termsClick() {
        Navigator.loadFragment(this, TermsFragment.newInstance(this),R.id.main_fl_container,false);
        drawerLayout.closeDrawers();
    }


    //language Click
    @OnClick(R.id.main_ll_language)
    public void languageClick() {
        changeLanguage();
    }

    @OnClick(R.id.main_ll_logout)
    public void logoutClick(){
        clearStack();
        Navigator.loadFragment(this,LoginFragment.newInstance(this),R.id.main_fl_container,false);
        drawerLayout.closeDrawers();
    }


    public void setupStatusBar() {
        //change the color of status Bar and make it light >> but it work from lollipop version and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    public void setupAppBar() {
        //set the actionBar to the activity
        setSupportActionBar(toolbar);
        //the content of toolbar Leaves a space on left or right hand so this function remove the spaces
        toolbar.setContentInsetsAbsolute(0, 0);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //adding animation when the content of activity change (fragment navigation)
                float moveFactor = navigationView.getWidth() * slideOffset;
                Container.setTranslationX(-moveFactor);
            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(null);
    }

    public void changeLanguage() {
        /*for changing the language of App
        1- check the value of language in sharedPreference and Reflects the language
         2- set the new value of language in local and change the value of sharedPreference to new value
         3- restart the mainActivity with noAnimation
        * */

        if (language.equals("ar")) {
            language = "en";
            isEnglish = true;
        } else if (language.equals("en")) {
            language = "ar";
            isEnglish = false;
        }

        LocaleHelper.setLocale(this, language);
        sessionManager.setUserLanguage(language);

        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void clearStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}

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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.rawnaq.classes.GlobalFunctions;
import app.rawnaq.classes.LocaleHelper;
import app.rawnaq.classes.Navigator;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.fragments.AboutUsFragment;
import app.rawnaq.fragments.CategoriesFragment;
import app.rawnaq.fragments.ContactUsFragment;
import app.rawnaq.fragments.CurrentOrdersFragment;
import app.rawnaq.fragments.FavoritesFragment;
import app.rawnaq.fragments.HomeFragment;
import app.rawnaq.fragments.LoginFragment;
import app.rawnaq.fragments.OrdersFragment;
import app.rawnaq.fragments.ProviderInfoFragment;
import app.rawnaq.fragments.ProviderServicesFragment;
import app.rawnaq.fragments.SignUpFragment;
import app.rawnaq.fragments.TermsFragment;
import app.rawnaq.fragments.ValidationCodeFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigationView)
    NavigationView navigationView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_fl_container)
    FrameLayout Container;


    /*butterKnife don't work with static or private
     if there is needing of static or private >> use the normal way
    * */
    public static AppBarLayout appbar;
    public static TextView title;
    public static TextView accountOrLoginTxt;
    public static TextView providerAccountOrLoginTxt;
    public static ImageView menu;
    public static LinearLayout userContainer;
    public static LinearLayout providerContainer;
    public static ImageView back;
    public static LinearLayout favorites;
    public static LinearLayout myOrders;
    public static LinearLayout logout;

    private String language;
    SessionManager sessionManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(LocaleHelper.onAttach(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupStatusBar();
        //this line must be added to use butterKnife in the activity
        ButterKnife.bind(this);
        setupAppBar();
        GlobalFunctions.setDefaultLanguage(this);
        GlobalFunctions.setUpFont(this);

        sessionManager = new SessionManager(this);


        language = sessionManager.getUserLanguage();
        if (!language.equals("en"))
            language = "ar";
        LocaleHelper.setLocale(this, language);
        sessionManager.setUserLanguage(language);

        appbar = (AppBarLayout) findViewById(R.id.main_appbar);
        title = (TextView) findViewById(R.id.main_tv_title);
        accountOrLoginTxt = (TextView) findViewById(R.id.main_tv_accountOrLoginTxt);
        providerAccountOrLoginTxt = (TextView) findViewById(R.id.main_tv_providerAccountOrLoginTxt);
        menu = (ImageView) findViewById(R.id.main_iv_menu);
        userContainer = (LinearLayout) findViewById(R.id.main_ll_userContainer);
        providerContainer = (LinearLayout) findViewById(R.id.main_ll_providerContainer);
        back = (ImageView) findViewById(R.id.main_iv_back);
        favorites = (LinearLayout) findViewById(R.id.main_ll_favorites);
        myOrders = (LinearLayout) findViewById(R.id.main_ll_myOrders);
        logout = (LinearLayout) findViewById(R.id.main_ll_logout);

        GlobalFunctions.appInfoApi();

        if (sessionManager.isProvider()) {
            providerContainer.setVisibility(View.VISIBLE);
            userContainer.setVisibility(View.GONE);
        } else {
            providerContainer.setVisibility(View.GONE);
            userContainer.setVisibility(View.VISIBLE);
            if (sessionManager.isGuest()) {
                favorites.setVisibility(View.GONE);
                myOrders.setVisibility(View.GONE);
                logout.setVisibility(View.GONE);
            } else {
                favorites.setVisibility(View.VISIBLE);
                myOrders.setVisibility(View.VISIBLE);
                logout.setVisibility(View.VISIBLE);
            }
        }
        if (sessionManager.isLoggedIn()) {
            accountOrLoginTxt.setText(getString(R.string.myAccount));
            providerAccountOrLoginTxt.setText(getString(R.string.myAccount));
        } else {
            accountOrLoginTxt.setText(getString(R.string.login));
            providerAccountOrLoginTxt.setText(getString(R.string.login));
        }


        if (sessionManager.isLoggedIn()) {
            if (!sessionManager.isValidation()) {
                Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_fl_container, false);
            } else {
                if (sessionManager.isProvider()) {
                    if (sessionManager.hasShop()) {
                        Navigator.loadFragment(this, CurrentOrdersFragment.newInstance(this, "providerWait"), R.id.main_fl_container, false);
                    } else {
                        Navigator.loadFragment(this, HomeFragment.newInstance(this), R.id.main_fl_container, false);
                    }
                } else {
                    Navigator.loadFragment(this, CategoriesFragment.newInstance(this), R.id.main_fl_container, false);
                }
            }
        } else if (sessionManager.isGuest()) {
            Navigator.loadFragment(this, CategoriesFragment.newInstance(this), R.id.main_fl_container, false);
        } else {
            Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_fl_container, false);
        }
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


    @OnClick(R.id.main_ll_accountOrLogin)
    public void accountClick() {
        if (accountOrLoginTxt.getText().toString().equals(getString(R.string.login))) {
            Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_fl_container, false);
        } else {
            Navigator.loadFragment(this, SignUpFragment.newInstance(this, "update"), R.id.main_fl_container, true);
        }
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_home)
    public void homeClick() {
        Navigator.loadFragment(this, CategoriesFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_providerAccountOrLogin)
    public void providerAccountClick() {
        Navigator.loadFragment(this, SignUpFragment.newInstance(this, "update"), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_favorites)
    public void favoritesClick() {
        Navigator.loadFragment(this, FavoritesFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_myOrders)
    public void myOrdersClick() {
        Navigator.loadFragment(this, OrdersFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_myServices)
    public void myServicesClick() {
        Navigator.loadFragment(this, ProviderServicesFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_providerCurrentOrders)
    public void providerCurrentOrdersClick() {
        Navigator.loadFragment(this, CurrentOrdersFragment.newInstance(this, "providerWait"), R.id.main_fl_container, false);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_providerOrders)
    public void providerOrdersClick() {
        Navigator.loadFragment(this, OrdersFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_providerPage)
    public void providerPageClick() {
        if (sessionManager.hasShop()) {
            Navigator.loadFragment(this, ProviderInfoFragment.newInstance(this), R.id.main_fl_container, true);
        } else {
            Navigator.loadFragment(this, HomeFragment.newInstance(this), R.id.main_fl_container, true);
        }
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_aboutUs)
    public void aboutUsClick() {
        Navigator.loadFragment(this, AboutUsFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.main_ll_contactUs)
    public void contactUsClick() {
        Navigator.loadFragment(this, ContactUsFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }


    @OnClick(R.id.main_ll_terms)
    public void termsClick() {
        Navigator.loadFragment(this, TermsFragment.newInstance(this), R.id.main_fl_container, true);
        drawerLayout.closeDrawers();
    }


    @OnClick(R.id.main_ll_language)
    public void languageClick() {
        changeLanguage();
    }

    @OnClick(R.id.main_ll_logout)
    public void logoutClick() {
        sessionManager.logout();
        clearStack();
        Navigator.loadFragment(this, LoginFragment.newInstance(this), R.id.main_fl_container, false);
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
        } else if (language.equals("en")) {
            language = "ar";
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

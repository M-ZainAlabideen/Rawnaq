package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.FavoritesAdapter;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.models.FavoritesModel;
import app.rawnaq.webservices.RawnaqApiConfig;
import app.rawnaq.webservices.responses.providers.FavoritesResponse;
import app.rawnaq.webservices.responses.providers.Provider;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FavoritesFragment extends Fragment {
    public static FragmentActivity activity;
    public static FavoritesFragment fragment;

    public static SessionManager sessionManager;

    LinearLayoutManager layoutManager;
    FavoritesAdapter favoritesAdapter;
    ArrayList<Provider> favoritesList = new ArrayList<>();

    @BindView(R.id.fragment_favorites_rv_favorites)
    RecyclerView favorites;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static FavoritesFragment newInstance(FragmentActivity activity) {
        fragment = new FavoritesFragment();
        FavoritesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.title.setText(getString(R.string.favorites));


        layoutManager = new LinearLayoutManager(activity);
        favoritesAdapter = new FavoritesAdapter(activity, favoritesList);
        favorites.setLayoutManager(layoutManager);
        favorites.setAdapter(favoritesAdapter);
        getFavoritesApi();
    }

    public void getFavoritesApi() {
        RawnaqApiConfig.getCallingAPIInterface().getFavorites(
                sessionManager.getUserToken(),
                new Callback<FavoritesResponse>() {
                    @Override
                    public void success(FavoritesResponse favoritesResponse, Response response) {
                        loading.setVisibility(View.GONE);
                        int status = favoritesResponse.status;
                        if (status == 200) {
                            if (favoritesResponse.provider != null && favoritesResponse.provider.size() > 0) {
                                favoritesList.clear();
                                favoritesList.addAll(favoritesResponse.provider);
                                favoritesAdapter.notifyDataSetChanged();
                            }
                        } else if (status == 204) {
                            Snackbar.make(loading, getString(R.string.noFavorites), Snackbar.LENGTH_SHORT).show();
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


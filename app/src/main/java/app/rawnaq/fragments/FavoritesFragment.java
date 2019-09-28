package app.rawnaq.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import app.rawnaq.MainActivity;
import app.rawnaq.R;
import app.rawnaq.adapters.FavoritesAdapter;
import app.rawnaq.classes.SessionManager;
import app.rawnaq.models.FavoritesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment {
    public static FragmentActivity activity;
    public static FavoritesFragment fragment;

    public static SessionManager sessionManager;
    public static Map<String, String> User;

    LinearLayoutManager layoutManager;
    FavoritesAdapter favoritesAdapter;
    ArrayList<FavoritesModel> favoritesList = new ArrayList<>();

    @BindView(R.id.fragment_favorites_rv_favorites)
    RecyclerView favorites;

    public static FavoritesFragment newInstance(FragmentActivity activity) {
        fragment = new FavoritesFragment();
        FavoritesFragment.activity = activity;
        sessionManager = new SessionManager(activity);
        User = sessionManager.getUser();
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

        layoutManager = new LinearLayoutManager(activity);
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());
        favoritesList.add(new FavoritesModel());

        favoritesAdapter = new FavoritesAdapter(activity,favoritesList);
        favorites.setLayoutManager(layoutManager);
        favorites.setAdapter(favoritesAdapter);
    }
}


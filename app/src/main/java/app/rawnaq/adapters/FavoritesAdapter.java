package app.rawnaq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.webservices.responses.providers.Provider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.viewHolder> {

    private Context context;
    private ArrayList<Provider> favoritesList;

    public FavoritesAdapter(Context context, ArrayList<Provider> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_favorite_tv_name)
        TextView name;
        @BindView(R.id.item_favorite_tv_address)
        TextView address;
        @BindView(R.id.item_favorite_iv_addToFav)
        ImageView addToFav;
        @BindView(R.id.item_favorite_iv_availableInHome)
        ImageView availableInHome;
        @BindView(R.id.item_favorite_tv_availableInHomeTV)
        TextView availableInHomeTV;
        @BindView(R.id.item_favorite_rb_rating)
        RatingBar rating;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public FavoritesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_favorite, viewGroup, false);
        return new FavoritesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.viewHolder viewHolder, final int position) {
        if (favoritesList.get(position) != null) {
            String shopName = favoritesList.get(position).providerShop.nameShop;
            String city = favoritesList.get(position).providerShop.city.name;
            String zone = favoritesList.get(position).providerShop.zone.name;
            String street = favoritesList.get(position).providerShop.street;
            int workFromHome = favoritesList.get(position).providerShop.workFromHome;
            boolean fav = favoritesList.get(position).providerShop.fav;
            double rate = favoritesList.get(position).providerShop.rate;
            viewHolder.name.setText(shopName);

        if (city != null && !city.isEmpty() && zone != null && !zone.isEmpty() && street != null && !street.isEmpty()) {
            viewHolder.address.setText(city + "| " + zone + "| " + street);
        }

        if (workFromHome == 0) {
            viewHolder.availableInHome.setVisibility(View.GONE);
            viewHolder.availableInHomeTV.setVisibility(View.GONE);
        }
        if (fav) {
            viewHolder.addToFav.setImageResource(R.drawable.ic_fav);
        }
        viewHolder.rating.setRating((float) rate);
    }

}

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

}


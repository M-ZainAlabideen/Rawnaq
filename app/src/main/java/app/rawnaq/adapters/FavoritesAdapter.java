package app.rawnaq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.fragments.ServiceProvidersFragment;
import app.rawnaq.models.FavoritesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.viewHolder> {

    Context context;
    ArrayList<FavoritesModel> favoritesList;

    public FavoritesAdapter(Context context, ArrayList<FavoritesModel> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

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
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

}


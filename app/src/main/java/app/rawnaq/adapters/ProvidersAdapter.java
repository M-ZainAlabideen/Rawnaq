package app.rawnaq.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.classes.Constants;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.fragments.ProviderInfoFragment;
import app.rawnaq.webservices.responses.providers.Image;
import app.rawnaq.webservices.responses.providers.Provider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProvidersAdapter extends RecyclerView.Adapter<ProvidersAdapter.viewHolder> {

    Context context;
    ArrayList<Provider> serviceProvidersList;
    OnItemClickListener listener;

    public ProvidersAdapter(Context context, ArrayList<Provider> serviceProvidersList,OnItemClickListener listener) {
        this.context = context;
        this.serviceProvidersList = serviceProvidersList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_provider_iv_image)
        ImageView image;
        @BindView(R.id.item_provider_iv_addToFav)
        ImageView addToFav;
        @BindView(R.id.item_provider_iv_workFromHome)
        ImageView workFromHome;
        @BindView(R.id.item_provider_rb_rating)
        RatingBar rating;
        @BindView(R.id.item_provider_tv_address)
        TextView address;
        @BindView(R.id.item_provider_tv_name)
        TextView name;
        @BindView(R.id.item_provider_iv_workFromHomeTV)
        TextView workFromHomeTV;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProvidersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_provider, viewGroup, false);
        return new ProvidersAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProvidersAdapter.viewHolder viewHolder, final int position) {

        if(serviceProvidersList.get(position).providerShop != null) {
            String shopName = serviceProvidersList.get(position).providerShop.nameShop;
            ArrayList<Image> images = serviceProvidersList.get(position).providerShop.images;
            String city = serviceProvidersList.get(position).providerShop.city.name;
            String zone = serviceProvidersList.get(position).providerShop.zone.name;
            String street = serviceProvidersList.get(position).providerShop.street;
            double ratingValue = serviceProvidersList.get(position).providerShop.rate;
            int workFromHome = serviceProvidersList.get(position).providerShop.workFromHome;
            boolean favorite = serviceProvidersList.get(position).providerShop.fav;

            int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_service_provider);
            int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_service_provider);
            viewHolder.image.getLayoutParams().height = Height;
            viewHolder.image.getLayoutParams().width = Width;

            if (images != null && images.size() > 0) {
                if (images.get(0).image != null
                        && !images.get(0).image.matches("")) {

                    Glide.with(context).load(Constants.IMAGE_BASE_URL+images.get(0).image)
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.placeholder_service_provider)
                                    .error(R.mipmap.placeholder_service_provider))
                            .into(viewHolder.image);
                }
            }
            if (shopName != null && !shopName.isEmpty()) {
                if (shopName.length() >= 25) {
                    viewHolder.name.setText(shopName.substring(0, 24));
                } else {
                    viewHolder.name.setText(shopName);
                }
            }
            if (city != null && !city.isEmpty() && zone != null && !zone.isEmpty() && street != null && !street.isEmpty()) {
                viewHolder.address.setText(city + "| " + zone + "| " + street);
            }

            viewHolder.rating.setRating((float) ratingValue);
            if (workFromHome != 1) {
                viewHolder.workFromHome.setVisibility(View.GONE);
                viewHolder.workFromHomeTV.setVisibility(View.GONE);
            }

            if (favorite) {
                viewHolder.addToFav.setImageResource(R.mipmap.ic_fav);
            }
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProviderInfoFragment fragment = ProviderInfoFragment.newInstance((FragmentActivity) context);
                    Bundle b = new Bundle();
                    b.putInt("providerId", serviceProvidersList.get(position).id);
                    fragment.setArguments(b);
                    Navigator.loadFragment((FragmentActivity) context, fragment, R.id.main_fl_container, true);
                }
            });

            viewHolder.addToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.favoriteClick(position, viewHolder.addToFav);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return serviceProvidersList.size();
    }

    public interface OnItemClickListener{
        void favoriteClick(int position,ImageView addToFav);
    }
}


package app.rawnaq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.fragments.ServiceProviderDetailsFragment;
import app.rawnaq.fragments.ServiceProvidersFragment;
import app.rawnaq.models.ServiceProvidersModel;
import app.rawnaq.models.ServicesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceProvidersAdapter extends RecyclerView.Adapter<ServiceProvidersAdapter.viewHolder> {

    Context context;
    ArrayList<ServiceProvidersModel> serviceProvidersList;

    public ServiceProvidersAdapter(Context context, ArrayList<ServiceProvidersModel> serviceProvidersList) {
        this.context = context;
        this.serviceProvidersList = serviceProvidersList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_service_provider_iv_image)
        ImageView image;
        @BindView(R.id.item_service_provider_iv_addToFav)
        ImageView addToFav;
        @BindView(R.id.item_service_provider_iv_availableInHome)
        ImageView availableInHome;
        @BindView(R.id.item_service_provider_iv_location)
        ImageView location;
        @BindView(R.id.item_service_provider_rb_rating)
        RatingBar rating;
        @BindView(R.id.item_service_provider_tv_address)
        TextView address;
        @BindView(R.id.item_service_provider_tv_name)
        TextView name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ServiceProvidersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_service_provider, viewGroup, false);
        return new ServiceProvidersAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProvidersAdapter.viewHolder viewHolder, final int position) {
        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_service_provider);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_service_provider);
        viewHolder.image.getLayoutParams().height = Height;
        viewHolder.image.getLayoutParams().width = Width;

        if (serviceProvidersList.get(position).image != null
                && !serviceProvidersList.get(position).image.matches("")
                && !serviceProvidersList.get(position).image.isEmpty()) {

            Glide.with(context).load(serviceProvidersList.get(position).image)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_service_provider)
                            .error(R.mipmap.placeholder_service_provider))
                    .into(viewHolder.image);
        }
        if (serviceProvidersList.get(position).name != null && !serviceProvidersList.get(position).name.matches("")) {
            if (serviceProvidersList.get(position).name.length() >= 10) {
                viewHolder.name.setText(serviceProvidersList.get(position).name.substring(0, 9));
            } else {
                viewHolder.name.setText(serviceProvidersList.get(position).name);
            }
        }
        if (serviceProvidersList.get(position).address != null && !serviceProvidersList.get(position).address.matches("")) {
            if (serviceProvidersList.get(position).address.length() >= 10) {
                viewHolder.address.setText(serviceProvidersList.get(position).address.substring(0, 9));
            } else {
                viewHolder.address.setText(serviceProvidersList.get(position).address);
            }
        }

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment((FragmentActivity) context,
                        ServiceProviderDetailsFragment.newInstance((FragmentActivity) context),
                        R.id.main_fl_container, true);
            }
        });

        viewHolder.addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

       viewHolder.location.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });
    }

    @Override
    public int getItemCount() {
        return serviceProvidersList.size();
    }

}


package app.rawnaq.adapters;

import android.content.Context;
import android.os.Bundle;
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
import app.rawnaq.classes.Constants;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.fragments.ProvidersFragment;
import app.rawnaq.webservices.responses.categories.Service;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.viewHolder> {

    Context context;
    ArrayList<Service> servicesList;

    public ServicesAdapter(Context context, ArrayList<Service> servicesList) {
        this.context = context;
        this.servicesList = servicesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_service_iv_image)
        ImageView image;
        @BindView(R.id.item_service_tv_title)
        TextView title;
        @BindView(R.id.item_service_tv_description)
        TextView description;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ServicesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_service, viewGroup, false);
        return new ServicesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesAdapter.viewHolder viewHolder, final int position) {
        int Width = FixControl.getImageWidth(context, R.mipmap.placeholder_services);
        int Height = FixControl.getImageHeight(context, R.mipmap.placeholder_services);
        viewHolder.image.getLayoutParams().height = Height;
        viewHolder.image.getLayoutParams().width = Width;

        if (servicesList.get(position).image != null
                && !servicesList.get(position).image.matches("")
                && !servicesList.get(position).image.isEmpty()) {

            Glide.with(context).load(Constants.IMAGE_BASE_URL+servicesList.get(position).image)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_services)
                            .error(R.mipmap.placeholder_services))
                    .into(viewHolder.image);
        }
        if (servicesList.get(position).name != null && !servicesList.get(position).name.matches("")) {
            if (servicesList.get(position).name.length() >= 30) {
                viewHolder.title.setText(servicesList.get(position).name.substring(0, 29));
            } else {
                viewHolder.title.setText(servicesList.get(position).name);
            }
        } else {
            viewHolder.title.setText("");
        }
        if (servicesList.get(position).description != null && !servicesList.get(position).description.matches("")) {
            if (servicesList.get(position).description.length() >= 100) {
                viewHolder.description.setText(servicesList.get(position).description.substring(0, 99));
            } else {
                viewHolder.description.setText(servicesList.get(position).description);
            }
        } else {
            viewHolder.description.setText("");
        }

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProvidersFragment fragment = ProvidersFragment.newInstance((FragmentActivity) context);
                Bundle b = new Bundle();
                b.putInt("serviceId", servicesList.get(position).id);
                b.putString("serviceName",servicesList.get(position).name);
                fragment.setArguments(b);
                Navigator.loadFragment((FragmentActivity) context,
                        fragment,
                        R.id.main_fl_container, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

}


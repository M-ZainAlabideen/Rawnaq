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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.classes.FixControl;
import app.rawnaq.classes.Navigator;
import app.rawnaq.fragments.ServiceProvidersFragment;
import app.rawnaq.models.ServicesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.viewHolder> {

    Context context;
    ArrayList<ServicesModel> servicesList;

    public ServicesAdapter(Context context, ArrayList<ServicesModel> servicesList) {
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

            Glide.with(context).load(servicesList.get(position).image)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.placeholder_services)
                            .error(R.mipmap.placeholder_services))
                    .into(viewHolder.image);
        }
        if (servicesList.get(position).title != null && !servicesList.get(position).title.matches("")) {
            if (servicesList.get(position).title.length() >= 10) {
                viewHolder.title.setText(servicesList.get(position).title.substring(0, 9));
            } else {
                viewHolder.title.setText(servicesList.get(position).title);
            }
        }
            if (servicesList.get(position).description != null && !servicesList.get(position).description.matches("")) {
                if (servicesList.get(position).description.length() >= 10) {
                    viewHolder.description.setText(servicesList.get(position).description.substring(0, 9));
                } else {
                    viewHolder.description.setText(servicesList.get(position).description);
                }
            }

            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.loadFragment((FragmentActivity) context,
                            ServiceProvidersFragment.newInstance((FragmentActivity) context),
                            R.id.main_fl_container, true);
                }
            });
        }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

}


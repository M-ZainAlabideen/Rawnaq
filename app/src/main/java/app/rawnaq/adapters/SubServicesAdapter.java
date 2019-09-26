package app.rawnaq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.rawnaq.R;
import app.rawnaq.models.SubServicesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubServicesAdapter extends RecyclerView.Adapter<SubServicesAdapter.viewHolder> {

        Context context;
        ArrayList<SubServicesModel> subServicesList;

public SubServicesAdapter(Context context, ArrayList<SubServicesModel> subServicesList) {
        this.context = context;
        this.subServicesList = subServicesList;
        }

public class viewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_sub_service_name)
    TextView name;
    @BindView(R.id.item_sub_service_price)
    TextView price;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

    @NonNull
    @Override
    public SubServicesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_sub_service, viewGroup, false);
        return new SubServicesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubServicesAdapter.viewHolder viewHolder, final int position) {
               // viewHolder.name.setText(subServicesList.get(position).name);
               // viewHolder.price.setText(subServicesList.get(position).price);
    }

    @Override
    public int getItemCount() {
        return subServicesList.size();
    }

}



package app.rawnaq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.rawnaq.R;
import app.rawnaq.webservices.responses.providers.ProviderService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubServicesAdapter extends RecyclerView.Adapter<SubServicesAdapter.viewHolder> {

    Context context;
    ArrayList<ProviderService> subServicesList;
    boolean isOrder;
    public static ArrayList<Integer> subServicesIds = new ArrayList<>();

    public SubServicesAdapter(Context context, ArrayList<ProviderService> subServicesList, boolean isOrder) {
        this.context = context;
        this.subServicesList = subServicesList;
        this.isOrder = isOrder;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_sub_service_cb_name)
        CheckBox name;
        @BindView(R.id.item_sub_service_tv_price)
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
        if (isOrder) {
            viewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        subServicesIds.add(subServicesList.get(position).id);
                    } else {
                        subServicesIds.remove(subServicesList.get(position).id);
                    }
                }
            });
        } else {
            viewHolder.name.setButtonDrawable(null);
            viewHolder.name.setClickable(false);
        }

        viewHolder.name.setText(subServicesList.get(position).service.name);
        viewHolder.price.setText(String.valueOf(subServicesList.get(position).price + " " + context.getString(R.string.currency)));


    }

    @Override
    public int getItemCount() {
        return subServicesList.size();
    }

}



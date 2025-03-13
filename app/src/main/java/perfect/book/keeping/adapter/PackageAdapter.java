package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.model.PackageItem;
import perfect.book.keeping.model.PackageModel;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private List<PackageModel> packageModelList;
    Context context;

    List<PackageItem> packageItems;

    PackageItemAdapter packageItemAdapter;

    int tapHolderPos = 0;

    private SparseArray<Integer> selectedPositions;

    public PackageAdapter(List<PackageModel> packageModelList, Context context) {
        this.packageModelList = packageModelList;
        this.context = context;
        this.selectedPositions = new SparseArray<>();
    }

    @NonNull
    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.packageName.setText(packageModelList.get(position).getPackage_name());
        holder.expandable_layout.setVisibility(packageModelList.get(position).isExpendable() ? View.VISIBLE : View.GONE);
        holder.child_rv.setVisibility(packageModelList.get(position).isExpendable() ? View.VISIBLE : View.GONE);

        if(packageModelList.get(position).isExpendable()) {
            holder.expand.setImageResource(R.drawable.arrow_drop_up);
            packageItems = packageModelList.get(position).getPackageItemList();
            packageItemAdapter = new PackageItemAdapter(packageItems, context, position, packageModelList.get(position).isExpendable());
            holder.child_rv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
//            holder.child_rv.setHasFixedSize(true);
            holder.child_rv.setAdapter(packageItemAdapter);
        } else {
            holder.expand.setImageResource(R.drawable.down_arrow);
        }
        holder.linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapHolderPos = holder.getAdapterPosition();
                for(int i = 0; i< packageModelList.size(); i++) {
                    if(tapHolderPos == i) {
                        packageModelList.get(i).setExpendable(!packageModelList.get(i).isExpendable());
                        if(packageModelList.get(i).isExpendable()) {
                            holder.expandable_layout.setVisibility(View.VISIBLE);
                            holder.child_rv.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.expandable_layout.setVisibility(View.GONE);
                            holder.child_rv.setVisibility(View.GONE);
                        }
                    } else {
                        packageModelList.get(i).setExpendable(false);
                        notifyDataSetChanged();
                    }
                }

                packageItems = packageModelList.get(position).getPackageItemList();
                packageItemAdapter = new PackageItemAdapter(packageItems, context, position, packageModelList.get(position).isExpendable());
                holder.child_rv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
//                holder.child_rv.setHasFixedSize(true);
                holder.child_rv.setAdapter(packageItemAdapter);
            }

        });


    }

    @Override
    public int getItemCount() {
        return packageModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linear_layout;
        RelativeLayout expandable_layout;
        TextView packageName;
        ImageView expand;
        RecyclerView child_rv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear_layout = (itemView).findViewById(R.id.linear_layout);
            expandable_layout = (itemView).findViewById(R.id.expandable_layout);
            packageName = (itemView).findViewById(R.id.packageName);
            expand = (itemView).findViewById(R.id.expand);
            child_rv = (itemView).findViewById(R.id.child_rv);

            linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    public void updatePackage() {

    }
}

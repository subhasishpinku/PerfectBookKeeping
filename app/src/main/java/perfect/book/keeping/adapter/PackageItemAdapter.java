package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.model.PackageItem;

public class PackageItemAdapter extends RecyclerView.Adapter<PackageItemAdapter.ViewHolder> {

    private List<PackageItem> packageItems;
    Context context;

    int counter = 0, tapHolderPos;

    int parentPos, clickParentPos;
    boolean expendable;

    int selectedItem = RecyclerView.NO_POSITION, selectedID = -1;

    public PackageItemAdapter(List<PackageItem> packageItems, Context context, int parentPos, boolean expendable) {
        this.packageItems = packageItems;
        this.context = context;
        this.parentPos = parentPos;
        this.expendable = expendable;
    }

    @NonNull
    @Override
    public PackageItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_each_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.package_item_name.setText(packageItems.get(position).getPackage_item_name());
        holder.package_item_description.setText(packageItems.get(position).getPackage_item_description());
        holder.package_item_price.setText("$" + packageItems.get(position).getPackage_item_price() + "/ month");
        holder.shield.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.silver_sild));
        holder.mainView.setBackgroundColor(Color.parseColor(packageItems.get(position).getColor_code()));
        //Selected Section
        holder.package_item_name_selected.setText(packageItems.get(position).getPackage_item_name());
        holder.package_item_description_selected.setText(packageItems.get(position).getPackage_item_description());
        holder.package_item_price_selected.setText("$" + packageItems.get(position).getPackage_item_price() + "/ month");
        holder.selected_mainView.setBackgroundColor(Color.parseColor(packageItems.get(position).getColor_code()));
        if(selectedItem == position) {
            holder.selected_layout.setVisibility(View.VISIBLE);
            holder.main_layout.setVisibility(View.GONE);
            if(packageItems.get(position).getPackage_item_id() == 1) {
                holder.package_item_name_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price_selected.setTextColor(context.getResources().getColor(R.color.black));
            } else if (packageItems.get(position).getPackage_item_id() == 2) {
                holder.package_item_name_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price_selected.setTextColor(context.getResources().getColor(R.color.black));
            } else if (packageItems.get(position).getPackage_item_id() == 3) {
                holder.package_item_name_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description_selected.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price_selected.setTextColor(context.getResources().getColor(R.color.black));
            } else if (packageItems.get(position).getPackage_item_id() == 4) {
                holder.package_item_name_selected.setTextColor(context.getResources().getColor(R.color.white));
                holder.package_item_description_selected.setTextColor(context.getResources().getColor(R.color.white));
                holder.package_item_price_selected.setTextColor(context.getResources().getColor(R.color.white));
            }

        } else {
            holder.selected_layout.setVisibility(View.GONE);
            holder.main_layout.setVisibility(View.VISIBLE);
            if(packageItems.get(position).getPackage_item_id() == 1) {
                holder.package_item_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price.setTextColor(context.getResources().getColor(R.color.black));
            } else if(packageItems.get(position).getPackage_item_id() == 2) {
                holder.package_item_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price.setTextColor(context.getResources().getColor(R.color.black));
            } else if(packageItems.get(position).getPackage_item_id() == 3) {
                holder.package_item_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_description.setTextColor(context.getResources().getColor(R.color.black));
                holder.package_item_price.setTextColor(context.getResources().getColor(R.color.black));
            } else if (packageItems.get(position).getPackage_item_id() == 4) {
                holder.package_item_name.setTextColor(context.getResources().getColor(R.color.white));
                holder.package_item_description.setTextColor(context.getResources().getColor(R.color.white));
                holder.package_item_price.setTextColor(context.getResources().getColor(R.color.white));
            }

        }
    }

    @Override
    public int getItemCount() {
        return packageItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView package_item_name, package_item_description, package_item_price, package_item_name_selected, package_item_price_selected, package_item_description_selected;
        LinearLayout package_selection, show_details, package_selected;
        RelativeLayout mainView, selected_layout, main_layout, selected_mainView;
        ImageView shield, selected, shield_selected;
        Button package_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            package_item_name = (itemView).findViewById(R.id.package_item_name);
            package_item_description = (itemView).findViewById(R.id.package_item_description);
            package_item_price = (itemView).findViewById(R.id.package_item_price);
            package_selection = (itemView).findViewById(R.id.package_selection);
            shield = (itemView).findViewById(R.id.shield);
            mainView = (itemView).findViewById(R.id.mainView);
            selected = (itemView).findViewById(R.id.selected);
            main_layout = (itemView).findViewById(R.id.main_layout);
            //Selected Section
            shield_selected = (itemView).findViewById(R.id.shield_selected);
            selected_layout = (itemView).findViewById(R.id.selected_layout);
            selected_mainView = (itemView).findViewById(R.id.selected_mainView);
            package_selected = (itemView).findViewById(R.id.package_selected);
            package_item_name_selected = (itemView).findViewById(R.id.package_item_name_selected);
            package_item_price_selected = (itemView).findViewById(R.id.package_item_price_selected);
            package_item_description_selected = (itemView).findViewById(R.id.package_item_description_selected);


            package_selection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousSelected = selectedItem;
                    selectedItem = getAdapterPosition();
                    selectedID = packageItems.get(getAdapterPosition()).getPackage_item_id();
                    notifyItemChanged(previousSelected);
                    notifyItemChanged(selectedItem);
                    ((Companies)context).updatePackageId(
                            packageItems.get(getAdapterPosition()).getPackage_item_id(),
                            packageItems.get(getAdapterPosition()).getPackage_item_name(),
                            packageItems.get(getAdapterPosition()).getPackage_item_description(),
                            packageItems.get(getAdapterPosition()).getPackage_item_price(),
                            packageItems.get(getAdapterPosition()).getColor_code()
                    );
                }
            });
        }
    }
}

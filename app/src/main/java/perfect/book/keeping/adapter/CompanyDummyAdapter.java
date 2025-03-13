package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.model.BusinessDashboardModel;
import perfect.book.keeping.model.CompanyModelDummy;

public class CompanyDummyAdapter extends RecyclerView.Adapter<CompanyDummyAdapter.ViewHolder> {

    List<CompanyModelDummy> companyModelDummies;
    Context context;

    public CompanyDummyAdapter(List<CompanyModelDummy> companyModelDummies, Context context) {
        this.companyModelDummies = companyModelDummies;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyDummyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyDummyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(companyModelDummies.get(position).getCompanyName().length() > 10) {
            holder.companyName.setText(companyModelDummies.get(position).getCompanyName().substring(0, Math.min(companyModelDummies.get(position).getCompanyName().length(), 12)) + "...");
        } else {
            holder.companyName.setText(companyModelDummies.get(position).getCompanyName());
        }
        if(companyModelDummies.get(position).getLogo() != null) {
            Glide.with(holder.companyLogo.getContext())
                    .load(R.drawable.company)
                    .into(holder.companyLogo);
        }
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent businessDashboard = new Intent(context, BusinessDashboards.class);
                businessDashboard.putExtra("companyId",companyModelDummies.get(position).getCompanyId());
                businessDashboard.putExtra("companyPermission", companyModelDummies.get(position).getPermission());
                context.startActivity(businessDashboard);
            }
        });
    }

    @Override
    public int getItemCount() {
        return companyModelDummies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName;
        LinearLayout layoutRec;

        ImageView companyLogo;
        CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = (itemView).findViewById(R.id.companyName);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
            card = (itemView).findViewById(R.id.card);
            companyLogo = (itemView).findViewById(R.id.companyLogo);
        }
    }
}

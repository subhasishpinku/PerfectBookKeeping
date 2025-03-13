package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.model.BusinessDashboardModel;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.DateFormat;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.CompanyHolder> implements Filterable {

    List<CompaniesModel> companies = new ArrayList<>();
    Context context;
    private boolean isFirstImageLoaded = false; // Keep track of the first image load
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler for delayed loading
    private int imagesPerBatch = 3; // Number of images to load in a batch
    private int currentBatchIndex = 0; // Index to track the current batch of images
    private List<CompaniesModel> filterCompany;
    boolean first_input;

    List<BusinessDashboardModel> businessDashboardList;
    public CompaniesAdapter(List<CompaniesModel> companies, Context context, List<BusinessDashboardModel> businessDashboardList, boolean first_input) {
        this.companies = companies;
        this.context = context;
        this.businessDashboardList = businessDashboardList;
        this.first_input=first_input;
        this.filterCompany = companies;
    }

    @NonNull
    @Override
    public CompaniesAdapter.CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_row, parent, false);
        return new CompanyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompaniesAdapter.CompanyHolder holder, @SuppressLint("RecyclerView") int position) {
        if(companies.get(position).getCompanyName().length() > 10) {
            holder.companyName.setText(companies.get(position).getCompanyName().substring(0, Math.min(companies.get(position).getCompanyName().length(), 12)) + "...");
        } else {
            holder.companyName.setText(companies.get(position).getCompanyName());
        }
        Log.e("COMP IMG",""+companies.get(position).getLogo());
        if(companies.get(position).getLogo() != null) {
            if (first_input){
                if(companies.get(position).getLogo() != null) {
                    Glide.with(holder.companyLogo.getContext())
                            .load(companies.get(position).getLogo())
                            .placeholder(R.drawable.company)
                            .error(R.drawable.company)
                            .into(holder.companyLogo);
                } else {
                    Glide.with(holder.companyLogo.getContext())
                            .load(R.drawable.company)
                            .placeholder(R.drawable.company)
                            .error(R.drawable.company)
                            .into(holder.companyLogo);
                }
            } else {
                if(companies.get(position).getLogo() != null) {
                    Glide.with(holder.companyLogo.getContext())
                            .load(new File(companies.get(position).getLogo()))
                            .placeholder(R.drawable.company)
                            .error(R.drawable.company)
                            .into(holder.companyLogo);
                } else {
                    Glide.with(holder.companyLogo.getContext())
                            .load(R.drawable.company)
                            .placeholder(R.drawable.company)
                            .error(R.drawable.company)
                            .into(holder.companyLogo);
                }
            }

        }
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(companies.get(position).getCardId(),
                        companies.get(position).getPermission(),
                        companies.get(position).getCompanyId(),
                        companies.get(position).getCompanyName());
            }
        });
    }

    private void redirect(int payment_status, int permission, int companyId, String company_name) {
        if(payment_status == 0) {
            if(permission == 3 || permission == 4) {
                openAlert("owner", permission, companyId, company_name, payment_status);
            } else {
               openAlert("user", permission, companyId, company_name, payment_status);
            }
        } else {
            Intent businessDashboard = new Intent(context, BusinessDashboards.class);
            businessDashboard.putExtra("companyId", companyId);
            businessDashboard.putExtra("companyPermission", permission);
            context.startActivity(businessDashboard);
        }
    }

    private void openAlert(String mode, int permission, int companyId, String company_name, int payment_status) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert));
        if(mode.equals("owner")) {
            builder.setMessage(context.getResources().getString(R.string.your_payment_method_declined_please_edit_or_add_a_new_payment_method_to_continue));
        } else {
            builder.setMessage(context.getResources().getString(R.string.there_is_a_billing_issue_with_this_account_once_it_is_resolved_by_the_company_owner_access_will_be_granted));
        }
        builder.setPositiveButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finishAffinity();
                if(mode.equals("owner")) {
                    Intent editCompany = new Intent(context, PaymentMethod.class);
                    editCompany.putExtra("companyId", companyId);
                    editCompany.putExtra("companyName", company_name);
                    editCompany.putExtra("permission", permission);
                    editCompany.putExtra("payment_status", payment_status);
                    editCompany.putExtra("mode", "noCard");
                    context.startActivity(editCompany);
                }

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterCompany;
                    filterResults.count = filterCompany.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<CompaniesModel> cList = new ArrayList<>();
                    for(CompaniesModel cFilterList: filterCompany) {
                        if(cFilterList.getCompanyName().toLowerCase().contains(search)) {
                            cList.add(cFilterList);
                        }
                    }
                    filterResults.values = cList;
                    filterResults.count = cList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                companies = (List<CompaniesModel>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class CompanyHolder extends RecyclerView.ViewHolder {
        TextView companyName;
        LinearLayout layoutRec;

        ImageView companyLogo;
        CardView card;
        public CompanyHolder(@NonNull View itemView) {
            super(itemView);
            companyName = (itemView).findViewById(R.id.companyName);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
            card = (itemView).findViewById(R.id.card);
            companyLogo = (itemView).findViewById(R.id.companyLogo);
        }
    }
}

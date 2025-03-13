package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.model.CompaniesModel;

public class CompanyListAllAdapter extends RecyclerView.Adapter<CompanyListAllAdapter.ViewHolder> implements Filterable {

    List<CompaniesModel> companies;
    Context context;
    List<String> selectedComp;

    BottomSheetDialog sheetDialog;

    SwitchMaterial viewPermission, viewEditPermission;

    LinearLayout save;

    TextView changeMode;

    int view = 1;

    List<CompaniesModel> filterCompany;

    public CompanyListAllAdapter(List<CompaniesModel> companies, Context context, List<String> selectedComp) {
        this.companies = companies;
        this.context = context;
        this.selectedComp = selectedComp;
        this.filterCompany = companies;
    }

    @NonNull
    @Override
    public CompanyListAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_suggestion, parent, false);
        return new ViewHolder(v);
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
                    for(CompaniesModel cLists: filterCompany) {
                        if(cLists.getCompanyName().toLowerCase().contains(search)) {
                            cList.add(cLists);
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

    @Override
    public void onBindViewHolder(@NonNull CompanyListAllAdapter.ViewHolder holder, int position) {
        String companyName = companies.get(position).getCompanyName();
        int companyId = companies.get(position).getCompanyId();
        holder.companyName.setText(companies.get(position).getCompanyName());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(context, R.style.BottomSheetStyle);
                View permission = ((AppCompatActivity) context).getLayoutInflater().inflate(R.layout.permission_company, null);

                save = permission.findViewById(R.id.save);
                changeMode = permission.findViewById(R.id.changeMode);

                changeMode.setText("Permission of "+companyName);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Company Id",""+companyId);
                        Log.e("Company Name",""+companyName);
                        Log.e("Company Permission",""+view);
                        if(selectedComp.contains(companyName)) {
                            Toast.makeText(context, context.getResources().getString(R.string.company_already_added), Toast.LENGTH_SHORT).show();
                        } else {
                           // ((SubUserModify)context).selectedCompany(companyId, view, companyName);
                        }
                        sheetDialog.dismiss();
                    }
                });
                sheetDialog.setContentView(permission);
                sheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView companyName;
        LinearLayout layoutRec;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            companyName = (itemView).findViewById(R.id.companyName);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
        }
    }
}

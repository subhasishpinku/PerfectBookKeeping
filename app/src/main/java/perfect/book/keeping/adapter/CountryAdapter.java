package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.activity.company.UpdateCompany;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.model.Country;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder>implements Filterable {

    private List<Country> countryList;
    Context context;
    private List<Country> filterCountryList;
    String mode;
    public CountryAdapter(List<Country> countryList, Context context, String mode) {
        this.countryList = countryList;
        this.context = context;
        this.filterCountryList = countryList;
        this.mode = mode;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterCountryList;
                    filterResults.count = filterCountryList.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<Country> cList = new ArrayList<>();
                    for(Country countryLst: filterCountryList) {
                        if(countryLst.getCountryName().toLowerCase().contains(search)) {
                            cList.add(countryLst);
                        }
                    }
                    filterResults.values = cList;
                    filterResults.count = cList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countryList = (List<Country>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.countryName.setText(countryList.get(position).getCountryName());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("editComp")) {
                    ((UpdateCompany)context).setCountry(countryList.get(position).getCountryName());
                } else if (mode.equals("editCompBilling")) {
                    ((UpdateCompany)context).setCountryBilling(countryList.get(position).getCountryName());
                } else if(mode.equals("createComp")) {
                    ((AddCompany)context).setCountry(countryList.get(position).getCountryName());
                } else if (mode.equals("createCompBilling")) {
                    ((AddCompany)context).setCountryBilling(countryList.get(position).getCountryName());
                } else if (mode.equals("PaymentMethod")){
                    ((PaymentMethod)context).setCountryBilling(countryList.get(position).getCountryName());
                } else {

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView countryName;
        LinearLayout layoutRec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.countryName);
            layoutRec = itemView.findViewById(R.id.layoutRec);
        }
    }
}

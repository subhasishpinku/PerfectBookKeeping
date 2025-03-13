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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.activity.company.UpdateCompany;
import perfect.book.keeping.global.CurrencyData;
import perfect.book.keeping.global.PermissionData;
import perfect.book.keeping.model.Currency;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> implements Filterable {

    List<Currency> currencies;
    List<Currency> filterCurrency;
    Context context;

    CompanyListAdapter companyListAdapter;
    String mode;

    public CurrencyAdapter(List<Currency> currencies, Context context, String mode) {
        this.currencies = currencies;
        this.context = context;
        this.filterCurrency = currencies;
        this.mode = mode;
    }

    @NonNull
    @Override
    public CurrencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.currency.setText(currencies.get(position).getCurrency_name());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("createCompany")) {
                    ((AddCompany)context).selectedCurrency(currencies.get(position).getCurrency_name());
                } else {
                    ((UpdateCompany)context).selectedCurrency(currencies.get(position).getCurrency_name());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterCurrency;
                    filterResults.count = filterCurrency.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<Currency> cList = new ArrayList<>();
                    for(Currency dateLst: filterCurrency) {
                        if(dateLst.getCurrency_name().toLowerCase().contains(search)) {
                            cList.add(dateLst);
                        }
                    }
                    filterResults.values = cList;
                    filterResults.count = cList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                currencies = (List<Currency>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView currency;
        LinearLayout layoutRec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
            currency = (itemView).findViewById(R.id.currency);
        }
    }

}

package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.receipt.PaymentReceipt;
import perfect.book.keeping.model.Year;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> implements Filterable {

    List<Year> years;
    Context context;
    private List<Year> filteryearList;

    String auth;

    SharedPreferences shared;
    String mode;


    public YearAdapter(List<Year> years, Context context, String mode) {
        this.years = years;
        this.context=context;
        this.filteryearList=years;
        this.mode = mode;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filteryearList;
                    filterResults.count = filteryearList.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<Year> cList = new ArrayList<>();
                    for(Year yearLst: filteryearList) {
                        if(yearLst.getYear().toLowerCase().contains(search)) {
                            cList.add(yearLst);
                        }
                    }
                    filterResults.values = cList;
                    filterResults.count = cList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                years = (List<Year>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.countryName.setText(years.get(position).getYear());
        shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("inv")) {
                    ((PaymentReceipt) context).getReceipt(auth, years.get(position).getYear(), 0);
                    ((PaymentReceipt) context).dismissal(years.get(position).getYear());
                } else {
                    ((ProfitLoss)context).getPnL("Bearer "+auth, years.get(position).getYear());
                    ((ProfitLoss) context).dismissal(years.get(position).getYear());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return years.size();
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

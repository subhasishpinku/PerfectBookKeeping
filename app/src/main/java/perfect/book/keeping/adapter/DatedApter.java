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
import perfect.book.keeping.model.DateFormat;

public class DatedApter extends RecyclerView.Adapter<DatedApter.ViewHolder> implements Filterable {

    List<DateFormat> dateFormat;
    Context context;

    private List<DateFormat> filterDate;

    BusinessDashboardAdapter businessDashboardAdapter;
    public DatedApter(Context context, List<DateFormat> dateFormat, BusinessDashboardAdapter businessDashboardAdapter) {
        this.dateFormat = dateFormat;
        this.context = context;
        this.filterDate = dateFormat;
        this.businessDashboardAdapter = businessDashboardAdapter;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterDate;
                    filterResults.count = filterDate.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<DateFormat> cList = new ArrayList<>();
                    for(DateFormat dateLst: filterDate) {
                        if(dateLst.getDateFormat().toLowerCase().contains(search)) {
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
                dateFormat = (List<DateFormat>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    @NonNull
    @Override
    public DatedApter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatedApter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemText.setText(dateFormat.get(position).getDateFormat());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessDashboardAdapter.selectedFormat(dateFormat.get(position).getDateFormat());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateFormat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        LinearLayout layoutRec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.dateFormat);
            layoutRec = itemView.findViewById(R.id.layoutRec);
        }
    }


}

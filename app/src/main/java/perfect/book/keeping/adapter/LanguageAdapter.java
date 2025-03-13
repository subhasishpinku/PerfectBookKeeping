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
import perfect.book.keeping.activity.company.UpdateCompany;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.LanguageList;
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> implements Filterable {
    List<LanguageList> languageList;
    List<LanguageList> filterLanguage;
    Context context;
    CompanyListAdapter companyListAdapter;
    public LanguageAdapter(List<LanguageList> languageList, Context context) {
        this.languageList = languageList;
        this.context = context;
        this.filterLanguage = languageList;

    }
    @NonNull
    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_suggestion, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LanguageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.language.setText(languageList.get(position).getLanguageName());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Profile)context).selectedLanguage(languageList.get(position).getLanguageCode(), languageList.get(position).getLanguageName());
            }
        });
    }
    @Override
    public int getItemCount() {
        return languageList.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterLanguage;
                    filterResults.count = filterLanguage.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<LanguageList> cList = new ArrayList<>();
                    for(LanguageList dateLst: filterLanguage) {
                        if(dateLst.getLanguageName().toLowerCase().contains(search)) {
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
                languageList = (List<LanguageList>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView language;
        LinearLayout layoutRec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            language = (itemView).findViewById(R.id.language);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
        }
    }

}

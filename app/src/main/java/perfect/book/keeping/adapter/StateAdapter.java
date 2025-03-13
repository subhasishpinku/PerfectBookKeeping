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
import perfect.book.keeping.model.States;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> implements Filterable {

    List<States> states;
    Context context;

    private List<States> filterStateList;
    String mode;

    public StateAdapter(List<States> states, Context context, String mode) {
        this.states= states;
        this.context = context;
        this.filterStateList = states;
        this.mode = mode;

    }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterStateList;
                    filterResults.count = filterStateList.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<States> sList = new ArrayList<>();
                    for(States stateList: filterStateList) {
                        if(stateList.getStates_name().toLowerCase().contains(search)) {
                            sList.add(stateList);
                        }
                    }
                    filterResults.values = sList;
                    filterResults.count = sList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                states = (List<States>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    @NonNull
    @Override
    public StateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_suggestion_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StateAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.stateName.setText(states.get(position).getStates_name());
        holder.layoutRecState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("editComp")) {
                    ((UpdateCompany)context).setState(states.get(position).getStates_name());
                } else if (mode.equals("editCompBilling")) {
                    ((UpdateCompany)context).setStateBilling(states.get(position).getStates_name());
                } else if(mode.equals("createComp")) {
                    ((AddCompany)context).setState(states.get(position).getStates_name());
                } else if (mode.equals("createCompBilling")) {
                    ((AddCompany)context).setStateBilling(states.get(position).getStates_name());
                }  else if (mode.equals("PaymentMethod")) {
                    ((PaymentMethod)context).setStateBilling(states.get(position).getStates_name());
                }
                else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stateName;
        LinearLayout layoutRecState;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecState = itemView.findViewById(R.id.layoutRecState);
            stateName = itemView.findViewById(R.id.stateName);
        }
    }
}

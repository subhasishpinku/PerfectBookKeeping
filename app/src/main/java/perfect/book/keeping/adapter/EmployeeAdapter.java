package perfect.book.keeping.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.Employee;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> implements Filterable {

    List<Employee> employeeList;
    Context context;
    List<Employee> filterEmployeeList;
    public EmployeeAdapter(List<Employee> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
        this.filterEmployeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {
        holder.employeeName.setText(employeeList.get(position).getEmployeeName());
        holder.employeeEmail.setText(employeeList.get(position).getEmployeeEmail());
        holder.employeePhone.setText(employeeList.get(position).getPhone_no());

        if(employeeList.get(position).getImage() != null) {
            Glide.with(context)
                    .load(employeeList.get(position).getImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.img_loader)
                            .error(R.drawable.user_prof))
                    .into(holder.employeeImage);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterEmployeeList;
                    filterResults.count = filterEmployeeList.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<Employee> iAList = new ArrayList<>();
                    for(Employee interAuditorLst: filterEmployeeList) {
                        if(interAuditorLst.getEmployeeName().toLowerCase().contains(search) || interAuditorLst.getEmployeeEmail().toLowerCase().contains(search)) {
                            iAList.add(interAuditorLst);
                        }
                    }
                    filterResults.values = iAList;
                    filterResults.count = iAList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                employeeList = (List<Employee>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView employeeImage;

        TextView employeeName, employeeEmail, employeePhone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            employeeName = (itemView).findViewById(R.id.employeeName);
            employeeImage = (itemView).findViewById(R.id.employeeImage);
            employeeEmail = (itemView).findViewById(R.id.employeeEmail);
            employeePhone = (itemView).findViewById(R.id.employeePhone);
        }
    }
}

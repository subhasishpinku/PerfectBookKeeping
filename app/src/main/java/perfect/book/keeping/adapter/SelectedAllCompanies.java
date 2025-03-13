package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.subUser.SubUserModify;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.model.CompPermissions;
import perfect.book.keeping.model.CompanyPermission;
import perfect.book.keeping.response.SubUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedAllCompanies extends RecyclerView.Adapter<SelectedAllCompanies.ViewHolder> {

    List<CompanyPermission> companyPermissions;

    List<CompPermissions> compPermissions;
    Context context;

    BottomSheetDialog sheetDialog;

    SwitchMaterial viewPermission, viewEditPermission;

    LinearLayout save;

    TextView changeMode;

    int subUser;

    int view = 1;
    public SelectedAllCompanies(List<CompanyPermission> companyPermissions, Context context,  List<CompPermissions> compPermissions, int subUser) {
        this.companyPermissions = companyPermissions;
        this.context = context;
        this.compPermissions = compPermissions;
        this.subUser = subUser;
    }

    @NonNull
    @Override
    public SelectedAllCompanies.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_copany_with_permission, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedAllCompanies.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String companyName = companyPermissions.get(position).getCompanyName();
        int companyId = companyPermissions.get(position).getCompanyId();
        holder.companyName.setText(companyPermissions.get(position).getCompanyName());
        if(companyPermissions.get(position).getPermission() == 2) {
            holder.permission.setText("View & Edit");
        } else {
            holder.permission.setText("View");
        }

        holder.edite_test.setOnClickListener(new View.OnClickListener() {
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
                        //companyPermissions.set(position, new CompanyPermission(companyId, view, companyName));
                        compPermissions.set(position, new CompPermissions(companyId, view, 0 , ""));
                        notifyDataSetChanged();
                        /// subUserFragment.selectedCompany(companyId, view, companyName);
                        sheetDialog.dismiss();
                        Log.e("Company per",""+compPermissions.toString());
                    }
                });
                sheetDialog.setContentView(permission);
                sheetDialog.show();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SubUserModify)context).remove(companyName);
                companyPermissions.remove(position);
                compPermissions.remove(position);
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return companyPermissions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName, permission;
        ImageView edite_test,remove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = (itemView).findViewById(R.id.companyName);
            permission = (itemView).findViewById(R.id.permission);
            remove = (itemView).findViewById(R.id.remove);
            edite_test = (itemView).findViewById(R.id.edite_test);
        }
    }
}

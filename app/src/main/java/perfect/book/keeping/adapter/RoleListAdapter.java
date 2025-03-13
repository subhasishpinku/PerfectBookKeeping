package perfect.book.keeping.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.PermissionData;
import perfect.book.keeping.model.RoleList;

public class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder> {
    List<RoleList> roles;
    Context context;
    int selectedPosition = -1;

    private ValueChangeListener valueChangeListener;

    int permission;

    Global global = new Global();

    CompanyListAdapter companyListAdapter;

    public RoleListAdapter(List<RoleList> roles, Context context, int permission) {
        this.roles = roles;
        this.context = context;
        this.permission = permission;
    }

    public interface ValueChangeListener {
        void onValueChange(String permission_name, int id);
    }


    @NonNull
    @Override
    public RoleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleListAdapter.ViewHolder holder, int position) {
        holder.roleName.setText(roles.get(position).getName());

        Log.e("PERMISSION GIVEN",""+permission);

        if(position == selectedPosition) {
            holder.permission.setChecked(true);
        } else if (permission == roles.get(position).getId()) {
            Log.e("PERMISSIONS GIVEN",""+permission);
            holder.permission.setChecked(true);
        } else {
            holder.permission.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return roles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        SwitchMaterial permission;
        TextView roleName;
        LinearLayout permission_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            permission = (itemView).findViewById(R.id.permission);
            roleName = (itemView).findViewById(R.id.roleName);
            permission_layout = (itemView).findViewById(R.id.permission_layout);

            permission_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemSelect(getAdapterPosition());
                }
            });

            permission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemSelect(getAdapterPosition());

                }
            });
        }
    }

    private void setItemSelect(int adapterPosition) {
        if(adapterPosition == RecyclerView.NO_POSITION) return;
        selectedPosition = adapterPosition;
        notifyDataSetChanged();
        permission = 0;
        PermissionData.getInstance().setPermission(roles.get(adapterPosition).getId());

    }
}

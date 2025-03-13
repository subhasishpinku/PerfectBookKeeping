package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.subUser.SubUsersStore;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.PermissionData;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.CompanyPermission;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.RoleList;
import perfect.book.keeping.response.CurrencyResponse;
import perfect.book.keeping.response.Role;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedCompanies extends RecyclerView.Adapter<SelectedCompanies.ViewHolder> {


    List<CompanyPermission> companyPermissions;
    Context context;

    BottomSheetDialog sheetDialog;

    SwitchMaterial viewPermission, viewEditPermission;

    LinearLayout save;

    TextView changeMode;

    int view = 1;

    Global global = new Global();

    SharedPreferences shared;

    RecyclerView roleList;

    List<RoleList> roles = new ArrayList<>();

    RoleListAdapter roleListAdapter;

    String auth;

    EditText amount, currency, searchBy;

    List<Currency> currencies = new ArrayList<>();
    AlertDialog alertDialog;
    RecyclerView currency_list;

    CurrencyAdapter currencyAdapter;

    LinearLayout amount_layout;

    String currencyName, currencySymbol;


    public SelectedCompanies(List<CompanyPermission> companyPermissions, Context context) {
        this.companyPermissions = companyPermissions;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectedCompanies.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_copany_with_permission, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedCompanies.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        Log.e("DARK MODE",""+global.checkUi(context));

        String companyName = companyPermissions.get(position).getCompanyName();
        int companyId = companyPermissions.get(position).getCompanyId();
        holder.companyName.setText(companyPermissions.get(position).getCompanyName() + " " + companyPermissions.get(position).getAmount() + " (" + companyPermissions.get(position).getCurrency() + ")");
        if(companyPermissions.get(position).getPermission() == 4) {
            holder.permission.setText("Co-Owner");
        } else if(companyPermissions.get(position).getPermission() == 5) {
            holder.permission.setText("Editor");
        } else if(companyPermissions.get(position).getPermission() == 7) {
            holder.permission.setText("Book Keeper");
        } else {
            holder.permission.setText("Viewer");
        }

        holder.edite_test.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(context, R.style.BottomSheetStyle);
                View permission = ((AppCompatActivity) context).getLayoutInflater().inflate(R.layout.permission_company, null);
                roleList = permission.findViewById(R.id.roleList);
                save = permission.findViewById(R.id.save);
                changeMode = permission.findViewById(R.id.changeMode);
                amount = permission.findViewById(R.id.amount);
                currency = permission.findViewById(R.id.currency);
                amount_layout = permission.findViewById(R.id.amount_layout);
                currency.setFocusable(false);
                //currency.setEnabled(false);
                currency.setClickable(true);

                amount.setText(String.valueOf(companyPermissions.get(position).getAmount()));

                currency.setText(companyPermissions.get(position).getCurrency());

                changeMode.setText("Permission of "+companyName);

                currency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadCurrency(v);
                        //Toast.makeText(context, "Ready to Listen", Toast.LENGTH_SHORT).show();
                    }
                });

                roles.clear();
                Call<Role> getRole = ApiClient.getInstance().getBookKeepingApi().getRole("Bearer "+auth, companyId);
                getRole.enqueue(new Callback<Role>() {
                    @Override
                    public void onResponse(Call<Role> call, Response<Role> response) {
                        if(response.code() == 200) {
                            for(int i = 0; i < response.body().getData().size(); i++) {
                                roles.add(new RoleList(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(), response.body().getData().get(i).getDescription()));
                            }

                            if(roles.size() > 0) {
                                roleListAdapter = new RoleListAdapter(roles, context, companyPermissions.get(position).getPermission());
                                roleList.setAdapter(roleListAdapter);
                                roleList.setLayoutManager(new LinearLayoutManager(context));
                                roleListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            try {
                                JSONObject jObjError = new  JSONObject(response.errorBody().string());
                                Toast.makeText(context, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Role> call, Throwable t) {

                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Company Id",""+companyId);
                        Log.e("Company Name",""+companyName);
                        Log.e("Company Permission",""+PermissionData.getInstance().getPermission());
                        if(PermissionData.getInstance().getPermission() == 4 || PermissionData.getInstance().getPermission() == 5) {
                            //Amount and Id will be Save
                            companyPermissions.set(position, new CompanyPermission(companyId, PermissionData.getInstance().getPermission(), companyName, Integer.parseInt(amount.getText().toString()), currencySymbol, currencyName));
                        } else {
                            //Amount and Id Will be 0
                            companyPermissions.set(position, new CompanyPermission(companyId, PermissionData.getInstance().getPermission(), companyName, 0, "", ""));
                        }

                        notifyDataSetChanged();
                        sheetDialog.dismiss();
                    }
                });
                sheetDialog.setContentView(permission);
                sheetDialog.show();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SubUsersStore)context).remove(companyName);
                companyPermissions.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    private void loadCurrency(View v) {
        currencies.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.my_dialog);
        View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_company_spinner, null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        alertDialog.show();
        searchBy = dialogView.findViewById(R.id.searchBy);
        currency_list = dialogView.findViewById(R.id.company_list);

        Manager currency = new Manager(context);
        Cursor cursor = currency.fetchCurrency();
        while (cursor.moveToNext()) {
            currencies.add(new Currency(cursor.getString(1), cursor.getString(2)));
        }

        if(currencies.size() > 0) {
            currencyAdapter = new CurrencyAdapter(currencies, context);
            currency_list.setAdapter(currencyAdapter );
            currency_list.setLayoutManager(new LinearLayoutManager(context));
            currencyAdapter.notifyDataSetChanged();
        }
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

    public void updateCurrency(String currency_symbol, String currency_name) {
        currency.setText(currency_name);
        alertDialog.dismiss();
        currencySymbol = currency_symbol;
        currencyName = currency_name;
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

        List<Currency> currencies;
        Context context;

        CompanyListAdapter companyListAdapter;

        public CurrencyAdapter(List<Currency> currencies, Context context) {
            this.currencies = currencies;
            this.context = context;
            this.companyListAdapter = companyListAdapter;
        }

        @NonNull
        @Override
        public CurrencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_suggestion, parent, false);
            return new CurrencyAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CurrencyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.currency.setText(currencies.get(position).getCurrency_name());
            holder.layoutRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCurrency(currencies.get(position).getSymbol(), currencies.get(position).getCurrency_name());
                }
            });
        }

        @Override
        public int getItemCount() {
            return currencies.size();
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

    public class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder> {
        List<RoleList> roles;
        Context context;
        int selectedPosition = -1;

        private RoleListAdapter.ValueChangeListener valueChangeListener;

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
            return new RoleListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RoleListAdapter.ViewHolder holder, int position) {
            holder.roleName.setText(roles.get(position).getName());

            Log.e("PERMISSION GIVEN",""+permission);

            if(position == selectedPosition) {
                holder.permission.setChecked(true);
            } else if (permission == roles.get(position).getId()) {
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
            TextView roleName,tvDescriptionId;
            LinearLayout permission_layout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                permission = (itemView).findViewById(R.id.permission);
                roleName = (itemView).findViewById(R.id.roleName);
                permission_layout = (itemView).findViewById(R.id.permission_layout);
                tvDescriptionId = (itemView).findViewById(R.id.tvDescriptionId);

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
            if(roles.get(adapterPosition).getId() == 3 || roles.get(adapterPosition).getId() == 4 || roles.get(adapterPosition).getId() == 5) {
                amount_layout.setVisibility(View.VISIBLE);
            } else {
                amount_layout.setVisibility(View.GONE);
                currencySymbol = "";
                currencyName = "";
            }
            PermissionData.getInstance().setPermission(roles.get(adapterPosition).getId());

        }
    }
}

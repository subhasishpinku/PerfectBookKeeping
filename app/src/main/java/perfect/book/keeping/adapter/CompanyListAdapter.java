package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.PermissionData;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.RoleList;
import perfect.book.keeping.response.Role;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> implements Filterable {

    List<CompaniesModel> companies;
    Context context;

    BottomSheetDialog sheetDialog;

    SwitchMaterial viewPermission, viewEditPermission;

    LinearLayout save;

    TextView changeMode;

    int view = 1;

    List<String> selectedComp;

    List<CompaniesModel> filterCompany;

    String auth, permission_name;
    SharedPreferences shared;

    RecyclerView roleList;

    List<RoleList> roles = new ArrayList<>();

    RoleListAdapter roleListAdapter;

    CompanyListAdapter companyListAdapter;

    Global global = new Global();

    EditText amount, currency, searchBy;

    List<Currency> currencies;
    AlertDialog alertDialog;
    RecyclerView currency_list;

    CurrencyAdapter currencyAdapter;

    LinearLayout amount_layout;


    String currencyName, currencySymbol;

    Dialog progressDialog;


    public CompanyListAdapter(List<CompaniesModel> companies, Context context, List<String> selectedComp) {
        this.companies = companies;
        this.context = context;
        this.selectedComp = selectedComp;
        this.filterCompany = companies;
        this.companyListAdapter = companyListAdapter;
        this.currencies = new ArrayList<>();
    }

    @NonNull
    @Override
    public CompanyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_suggestion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");

        progressDialog = new Dialog(context);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        String companyName = companies.get(position).getCompanyName();
        int companyId = companies.get(position).getCompanyId();
        holder.companyName.setText(companies.get(position).getCompanyName());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
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

                changeMode.setText(context.getResources().getString(R.string.Permission_of)+companyName);

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
                                roles.add(new RoleList(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(),response.body().getData().get(i).getDescription()));
                            }

                            if(roles.size() > 0) {
                                roleListAdapter = new RoleListAdapter(roles, context, 1);
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
                Log.e("Permissions",""+PermissionData.getInstance().getPermission());

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Company Id",""+companyId);
                        Log.e("Company Name",""+companyName);
                        Log.e("Company Permission",""+ PermissionData.getInstance().getPermission());
                        Log.e("Company Amount",""+ amount.getText().toString());
                        Log.e("Company Currency ID",""+ currencySymbol);
                        Log.e("Company Currency NAME",""+ currencyName);
                        if(selectedComp.contains(companyName)) {
                            Toast.makeText(context, context.getResources().getString(R.string.company_already_added), Toast.LENGTH_SHORT).show();

                        } else {

                        }
                        sheetDialog.dismiss();
                    }
                });
                sheetDialog.setContentView(permission);
                sheetDialog.show();
            }
        });
    }

    private void loadCurrency(View v) {
        currencies.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.my_dialog);
        View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_currency_spinner, null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        alertDialog.show();
        searchBy = dialogView.findViewById(R.id.searchBy);
        currency_list = dialogView.findViewById(R.id.currency_list );

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
        return companies.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterCompany;
                    filterResults.count = filterCompany.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<CompaniesModel> cList = new ArrayList<>();
                    for(CompaniesModel cLists: filterCompany) {
                        if(cLists.getCompanyName().toLowerCase().contains(search)) {
                            cList.add(cLists);
                        }
                    }
                    filterResults.values = cList;
                    filterResults.count = cList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                companies = (List<CompaniesModel>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView companyName;
        LinearLayout layoutRec;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            companyName = (itemView).findViewById(R.id.companyName);
            layoutRec = (itemView).findViewById(R.id.layoutRec);
        }
    }

    public void updateCurrency(String symbol, String currency_name) {
        currency.setText(currency_name);
        alertDialog.dismiss();
        currencySymbol = symbol;
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
            Log.e("Currency Name",""+currencies.get(position).getCurrency_name());
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
            if (roles.get(position).getDescriptionId() !=null){
                holder.tvDescriptionId.setVisibility(View.VISIBLE);
                holder.tvDescriptionId.setText(roles.get(position).getDescriptionId());

            }else {
                holder.tvDescriptionId.setVisibility(View.GONE);
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

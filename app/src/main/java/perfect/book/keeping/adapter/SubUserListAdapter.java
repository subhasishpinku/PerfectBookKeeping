package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.subUser.SubUserModify;
import perfect.book.keeping.activity.company.subUser.SubUsersList;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.DateFormat;
import perfect.book.keeping.model.SubUser;
import perfect.book.keeping.request.AmountUpdateRequest;
import perfect.book.keeping.request.RemoveSubUser;
import perfect.book.keeping.response.AmountUpdateResponse;
import perfect.book.keeping.response.SendInvite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubUserListAdapter extends RecyclerView.Adapter<SubUserListAdapter.CompanyHolder> implements Filterable {
    List<SubUser> subUsers;
    Context context;
    BottomSheetDialog sheetDialog;
    TextView changeMode, amount_text;
    LinearLayout save, amount_layout;
    EditText amount, searchBy;

    TextView set_amount, symbol, enter_amount, equal_symbol, new_calculate_amount, currency;
    List<Currency> currencies = new ArrayList<>();
    AlertDialog alertDialog;
    RecyclerView currency_list;
    ImageView close;
    CurrencyAdapter currencyAdapter;
    CardView remove_balance, add_balance;
    boolean type;
    Dialog progressDialog;
    double new_Amount;
    SharedPreferences shared;
    String auth;

    int MAX_DECIMAL_DIGITS = 2;
    Global global;
    Manager db;
    List<SubUser> filterSubUsers;

    public SubUserListAdapter(List<SubUser> subUsers, Context context) {
        this.subUsers = subUsers;
        this.context = context;
        this.filterSubUsers = subUsers;
        global = new Global();
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subuser, parent, false);
        return new CompanyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyHolder holder, @SuppressLint("RecyclerView") int position) {
        progressDialog = new Dialog(context);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (subUsers.get(position).getSub_user_name() == null || subUsers.get(position).getSub_user_name().equals("")) {
            holder.name.setText(subUsers.get(position).getName());
        } else {
            holder.name.setText(subUsers.get(position).getSub_user_name());
        }
        holder.amount_currency.setText(subUsers.get(position).getAmount() + " " + subUsers.get(position).getCompany_currency());
        if(subUsers.get(position).getAmount() >= 0) {
            holder.amount_currency.setTextColor(context.getResources().getColor(R.color.green_color));
        } else {
            holder.amount_currency.setTextColor(context.getResources().getColor(R.color.red ));
        }
        if (subUsers.get(position).getAddedBy()==0){
            if(subUsers.get(position).getUser_role_id() == 7 || subUsers.get(position).getUser_rolName().equals("Internal Auditor")) {
                holder.role_name.setText(subUsers.get(position).getUser_rolName() + " " + "(PBK Bookkeeper)");
            } else {
                holder.role_name.setText(subUsers.get(position).getUser_rolName());
            }

        }else {
            holder.role_name.setText(subUsers.get(position).getUser_rolName());
        }

        if (subUsers.get(position).getUser_role_id() != 5 || !subUsers.get(position).getUser_rolName().equals("Editor")){
            holder.add_balance.setVisibility(View.GONE);
            holder.amount_currency.setVisibility(View.GONE);
            holder.or_symbol.setVisibility(View.GONE);
        }else {
            holder.add_balance.setVisibility(View.VISIBLE);
            holder.amount_currency.setVisibility(View.VISIBLE);
            holder.or_symbol.setVisibility(View.VISIBLE);
        }

        if(subUsers.get(position).getPermission() == subUsers.get(position).getUser_role_id()) {
            holder.removePermission.setVisibility(View.GONE);
        } else {
            holder.removePermission.setVisibility(View.VISIBLE);
        }
        if (subUsers.get(position).getIsLogin()==0){
            holder.flag.setVisibility(View.VISIBLE);
        }
        else {
            holder.flag.setVisibility(View.GONE);
        }
        holder.sub_user_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(context)) {
                    Intent subUserModify = new Intent(context, SubUserModify.class);
                    subUserModify.putExtra("companyId", subUsers.get(position).getCompanyId());
                    subUserModify.putExtra("companyName", subUsers.get(position).getCompanyName());
                    subUserModify.putExtra("permission", subUsers.get(position).getPermission());
                    subUserModify.putExtra("subUser", subUsers.get(position).getId());
                    context.startActivity(subUserModify);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.editPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(context)) {
                    Intent subUserModify = new Intent(context, SubUserModify.class);
                    subUserModify.putExtra("companyId", subUsers.get(position).getCompanyId());
                    subUserModify.putExtra("companyName", subUsers.get(position).getCompanyName());
                    subUserModify.putExtra("permission", subUsers.get(position).getPermission());
                    subUserModify.putExtra("subUser", subUsers.get(position).getId());
                    context.startActivity(subUserModify);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.send_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_invitation(subUsers.get(position).getCompanyId(), subUsers.get(position).getId());
            }
        });
        holder.add_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(context)) {
                    addBalance(subUsers.get(position).getAmount(), subUsers.get(position).getCompany_currency(), subUsers.get(position).getCompanyId(), subUsers.get(position).getId());
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();

                }
            }
        });
        holder.removePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(context)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_remove_the_sub_user));
                    builder.setPositiveButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // Do nothing, but close the dialog
                        }
                    });
                    builder.setNegativeButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeSubUser(subUsers.get(position).getId(), subUsers.get(position).getCompanyId(), subUsers.get(position).getCompanyName(), subUsers.get(position).getPermission());
                        }
                    });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void send_invitation(int companyId, int id) {
        progressDialog.show();
        shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        Call<SendInvite> sendInviteCall = ApiClient.getInstance().getBookKeepingApi().sendInvitation("Bearer "+auth, String.valueOf(companyId), String.valueOf(id));
        sendInviteCall.enqueue(new Callback<SendInvite>() {
            @Override
            public void onResponse(Call<SendInvite> call, Response<SendInvite> response) {
                Log.e("RESPONSE INVITE CODE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
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
            public void onFailure(Call<SendInvite> call, Throwable t) {

            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void addBalance(Double setAmount, String setCurrency, int companyId, int sub_user_id) {
        shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        sheetDialog = new BottomSheetDialog(context, R.style.BottomSheetStyle);
        View permission = ((AppCompatActivity) context).getLayoutInflater().inflate(R.layout.amount_sub_user, null);
        save = permission.findViewById(R.id.save);
        changeMode = permission.findViewById(R.id.changeMode);
        amount_text = permission.findViewById(R.id.amount_text);
        amount = permission.findViewById(R.id.amount);
        //amount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        set_amount = permission.findViewById(R.id.set_amount);
        symbol = permission.findViewById(R.id.symbol);
        enter_amount = permission.findViewById(R.id.enter_amount);
        equal_symbol = permission.findViewById(R.id.equal_symbol);
        new_calculate_amount = permission.findViewById(R.id.new_calculate_amount);
        currency = permission.findViewById(R.id.currency);

        currency.setText(setCurrency);
        set_amount.setText(String.valueOf(setAmount));
        new_calculate_amount.setText(String.valueOf(setAmount));
        enter_amount.setText("0");

        amount_layout = permission.findViewById(R.id.amount_layout);
        add_balance = permission.findViewById(R.id.add_balance);
        remove_balance = permission.findViewById(R.id.remove_balance);

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                int pointIndex = input.indexOf(".");
                if (pointIndex != -1 && input.length() - pointIndex > MAX_DECIMAL_DIGITS + 1) {
                    // Remove Last Element of the EditText when more than two digits are entered after the point
                    amount.getText().delete(input.length() - 1, input.length());
                }
            }
        });


        add_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_balance.getBackground().setTint(Color.parseColor("#51B04C"));
                remove_balance.getBackground().setTint(Color.parseColor("#FFFFFFFF"));
                amount_layout.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                amount_text.setText("Add Amount");
                type = true;
                if(symbol.getVisibility() == View.GONE) {
                    symbol.setVisibility(View.VISIBLE);
                    enter_amount.setVisibility(View.VISIBLE);
                    equal_symbol.setVisibility(View.VISIBLE);
                    new_calculate_amount.setVisibility(View.VISIBLE);
                }

                calculate(amount.getText().toString(), type, setAmount);
            }
        });

        remove_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_balance.getBackground().setTint(Color.parseColor("#FFFFFFFF"));
                remove_balance.getBackground().setTint(Color.parseColor("#D72A34"));
                amount_layout.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                amount_text.setText("Remove Amount");
                type = false;
                if(symbol.getVisibility() == View.GONE) {
                    symbol.setVisibility(View.VISIBLE);
                    enter_amount.setVisibility(View.VISIBLE);
                    equal_symbol.setVisibility(View.VISIBLE);
                    new_calculate_amount.setVisibility(View.VISIBLE);
                }

                calculate(amount.getText().toString(), type, setAmount);

            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate(s.toString(), type, setAmount);
            }
        });

//        currency.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadCurrency(v);
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().toString().equals("")) {
                    amount.setError(context.getResources().getString(R.string.please_enter_amount));
                } else {
                    updateAmount(companyId, sub_user_id, amount.getText().toString(), setCurrency, type);
                }
            }
        });

        sheetDialog.setContentView(permission);
        sheetDialog.show();
    }

    private void updateAmount(int companyId, int sub_user_id, String amount, String currency, boolean type) {
        progressDialog.show();
        AmountUpdateRequest updateRequest = new AmountUpdateRequest(companyId, sub_user_id, Double.parseDouble(amount), type);
        Call<AmountUpdateResponse> amountUpdateResponse = ApiClient.getInstance().getBookKeepingApi().updateAmount("Bearer "+auth,updateRequest);
        amountUpdateResponse.enqueue(new Callback<AmountUpdateResponse>() {
            @Override
            public void onResponse(Call<AmountUpdateResponse> call, Response<AmountUpdateResponse> response) {
                if(response.code() == 200) {
                    ((SubUsersList)context).callSubUserSave("Bearer "+auth);
                    progressDialog.dismiss();
                    sheetDialog.dismiss();
                    //((SubUsersList)context).retrieveData();

                }
                Log.e("AMOUNT UPDATE RESPONSE",""+response.code());
            }

            @Override
            public void onFailure(Call<AmountUpdateResponse> call, Throwable t) {

            }
        });
    }

    private void calculate(String amount, boolean type, Double setAmount) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#######");
        if(amount.equals("")) {
            new_Amount = setAmount;
            enter_amount.setText("0");
        } else if (amount.equals(".")) {
            new_Amount = setAmount;
        } else {
            enter_amount.setText(amount);
            if (type) {
                new_Amount = setAmount + Double.parseDouble(amount);
                symbol.setText("+");
            } else {
                new_Amount = setAmount - Double.parseDouble(amount);
                symbol.setText("-");
            }
        }
        new_calculate_amount.setText(String.valueOf(decimalFormat.format(new_Amount)));
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
        close = dialogView.findViewById(R.id.close);
        searchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(currencies.size() > 0) {
                    currencyAdapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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

    private void removeSubUser(int sub_user_id, int company_id, String cName, int permission) {
        progressDialog.show();
        db = new Manager(context);
        SharedPreferences shared = context.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        String auth = shared.getString("access_token", "");
        RemoveSubUser removeSubUsers = new RemoveSubUser(company_id, sub_user_id);
        Call<perfect.book.keeping.response.SubUser> subUserCall = ApiClient.getInstance().getBookKeepingApi().removeSubUser("Bearer "+auth, removeSubUsers);
        subUserCall.enqueue(new Callback<perfect.book.keeping.response.SubUser>() {
            @Override
            public void onResponse(Call<perfect.book.keeping.response.SubUser> call, Response<perfect.book.keeping.response.SubUser> response) {
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    db.removeSingleUser(company_id, sub_user_id);
                    Intent subUsers = new Intent(context, SubUsersList.class);
                    subUsers.putExtra("companyId",company_id);
                    subUsers.putExtra("companyName",cName);
                    subUsers.putExtra("permission",permission);
                    context.startActivity(subUsers);
                } else {
                    progressDialog.dismiss();
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
            public void onFailure(Call<perfect.book.keeping.response.SubUser> call, Throwable t) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return subUsers.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    filterResults.values = filterSubUsers;
                    filterResults.count = filterSubUsers.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<SubUser> sList = new ArrayList<>();
                    for(SubUser sFilterList: filterSubUsers) {
                        if(sFilterList.getSub_user_name().toLowerCase().contains(search) || sFilterList.getSub_user_email().toLowerCase().contains(search) || sFilterList.getName().toLowerCase().contains(search)) {
                            sList.add(sFilterList);
                        }
                    }
                    filterResults.values = sList;
                    filterResults.count = sList.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                subUsers = (List<SubUser>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class CompanyHolder extends RecyclerView.ViewHolder {
        TextView name, amount_currency, role_name, or_symbol;

        LinearLayout sub_user_layout;

        CardView add_balance, editPermission, removePermission, send_invitation;
        ImageView flag;

        public CompanyHolder(@NonNull View itemView) {
            super(itemView);
            name = (itemView).findViewById(R.id.name);
            role_name = (itemView).findViewById(R.id.role_name);
            or_symbol = (itemView).findViewById(R.id.or_symbol);
            editPermission = (itemView).findViewById(R.id.editPermission);
            removePermission = (itemView).findViewById(R.id.removePermission);
            sub_user_layout = (itemView).findViewById(R.id.sub_user_layout);
            add_balance = (itemView).findViewById(R.id.add_balance);
            send_invitation = (itemView).findViewById(R.id.send_invitation);
            amount_currency = (itemView).findViewById(R.id.amount_currency);
            flag = (itemView).findViewById(R.id.flag);
        }
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> implements Filterable {

        List<Currency> currencies;
        Context context;

        CompanyListAdapter companyListAdapter;
        private List<Currency> filterCurrency;

        public CurrencyAdapter(List<Currency> currencies, Context context) {
            this.currencies = currencies;
            this.context = context;
            this.companyListAdapter = companyListAdapter;
            this.filterCurrency=currencies;
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
    private void updateCurrency(String symbol, String currency_name) {
        currency.setText(currency_name);
        alertDialog.dismiss();
    }
}
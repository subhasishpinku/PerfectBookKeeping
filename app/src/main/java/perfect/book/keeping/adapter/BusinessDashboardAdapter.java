package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.BookKeepers;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.activity.company.UpdateCompany;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.activity.company.gallery.RejectGallery;
import perfect.book.keeping.activity.company.subUser.SubUsersList;
import perfect.book.keeping.activity.company.TakeSnapDate;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.receipt.PaymentReceipt;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.BusinessDashboardModel;
import perfect.book.keeping.model.DateFormat;

public class BusinessDashboardAdapter extends RecyclerView.Adapter<BusinessDashboardAdapter.ViewHolder> {

    List<BusinessDashboardModel> businessDashboardList;
    Context context;

    EditText company_name,watermarkText,waterMarkDateFormat;

    Dialog dialog;

    RecyclerView dateList;

    DatedApter datedApter;

    EditText searchBy;

    List<DateFormat> dateFormat = new ArrayList<>();

    LinearLayout save;
    BottomSheetDialog sheetDialog;
    Manager db;
    int type = 0;
    String compName = "", newCompName = "";

    Global global = new Global();


    public BusinessDashboardAdapter(List<BusinessDashboardModel> businessDashboardList, Context context) {
        this.businessDashboardList = businessDashboardList;
        this.context = context;
    }

    @NonNull
    @Override
    public BusinessDashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_element, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessDashboardAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.names.setText(businessDashboardList.get(position).getName());
        holder.icons.setImageResource(businessDashboardList.get(position).getImage());
        Cursor gallery = null;
        if(businessDashboardList.get(position).getMode().equals("pReceipt")) {
            gallery = db.getGalleryAllPending(businessDashboardList.get(position).getCompanyId(), "DESC");
            holder.count_pending.setVisibility(View.VISIBLE);
            holder.count_pending.setText(String.valueOf(gallery.getCount()));
        } else if(businessDashboardList.get(position).getMode().equals("rReceipt")) {
            gallery = db.checkGalleryImage(businessDashboardList.get(position).getCompanyId(), 3);
            holder.count_pending.setVisibility(View.VISIBLE);
            holder.count_pending.setText(String.valueOf(gallery.getCount()));
        } else {
            holder.count_pending.setVisibility(View.GONE);
        }
        holder.element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectCall(businessDashboardList.get(position).getMode(),
                        businessDashboardList.get(position).getCompanyId(),
                        businessDashboardList.get(position).getCompanyName(),
                        businessDashboardList.get(position).getPermission(),
                        businessDashboardList.get(position).getUserType(),
                        businessDashboardList.get(position).getPackage_id());
            }
        });

    }

    @SuppressLint("MissingInflatedId")
    private void redirectCall(String mode, int companyId, String companyName, int permission, int userType, int package_id) {
        if(mode.equals("camera")) {
            db.removeAllSnap();
            Intent takeSnapDate = new Intent(context, TakeSnapDate.class);
            takeSnapDate.putExtra("companyId",companyId);
            if(type == 0) {
                takeSnapDate.putExtra("companyName", companyName);
            } else {
                takeSnapDate.putExtra("companyName", compName);
            }
            takeSnapDate.putExtra("replacePos", 0);
            takeSnapDate.putExtra("redirectPos",0);
            takeSnapDate.putExtra("permission",permission);
            context.startActivity(takeSnapDate);
        }
        if(mode.equals("receipt")) {
            Intent viewReceipt = new Intent(context, ReceiptGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            if (type == 0) {
                viewReceipt.putExtra("companyName", companyName);
            } else {
                viewReceipt.putExtra("companyName", compName);
            }
            viewReceipt.putExtra("permission",permission);
            context.startActivity(viewReceipt);
        }
        if (mode.equals("pReceipt")){
            Intent viewReceipt = new Intent(context, PendingGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            if (type == 0) {
                viewReceipt.putExtra("companyName", companyName);
            } else {
                viewReceipt.putExtra("companyName", compName);
            }
            viewReceipt.putExtra("permission",permission);
            context.startActivity(viewReceipt);
        }
        if (mode.equals("rReceipt")){
            Intent viewReceipt = new Intent(context, RejectGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            if (type == 0) {
                viewReceipt.putExtra("companyName", companyName);
            } else {
                viewReceipt.putExtra("companyName", compName);
            }
            viewReceipt.putExtra("permission",permission);
            context.startActivity(viewReceipt);
        }
        if(mode.equals("pr")) {
            if(global.isNetworkAvailable(context)) {
                Intent pr = new Intent(context, PaymentReceipt.class);
                pr.putExtra("companyId", companyId);
                pr.putExtra("companyName", companyName);
                pr.putExtra("permission", permission);
                context.startActivity(pr);
            }
            else {
                Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            }
        }
        if(mode.equals("pnl")) {
            if(global.isNetworkAvailable(context)) {
                if (package_id == 2) {
                    Intent pnl = new Intent(context, ProfitLoss.class);
                    pnl.putExtra("companyId", companyId);
                    pnl.putExtra("companyName", companyName);
                    pnl.putExtra("permission", permission);
                    context.startActivity(pnl);
                } else if (package_id == 1) {
                    openAlert("P/L");
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.not_allowed_No_Package_Id_Available), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            }
        }
        if(mode.equals("bk")) {
            if(global.isNetworkAvailable(context)) {
                if (package_id == 2) {
                    Intent bookKeepers = new Intent(context, BookKeepers.class);
                    bookKeepers.putExtra("companyId", companyId);
                    bookKeepers.putExtra("companyName", companyName);
                    bookKeepers.putExtra("permission", permission);
                    context.startActivity(bookKeepers);
                } else if (package_id == 1) {
                    openAlert("Internal Auditor");
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.not_allowed_No_Package_Id_Available), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            }
        }
        if(mode.equals("subUsers")) {
            Intent subUserComp = new Intent(context, SubUsersList.class);
            subUserComp.putExtra("companyId",companyId);
            subUserComp.putExtra("companyName", companyName);
            subUserComp.putExtra("permission",permission);
            context.startActivity(subUserComp);
        }
        if(mode.equals("Company Info")) {
            if(global.isNetworkAvailable(context)) {
                Intent editCompany = new Intent(context, UpdateCompany.class);
                editCompany.putExtra("companyId", companyId);
                editCompany.putExtra("companyName", companyName);
                editCompany.putExtra("permission", permission);
                editCompany.putExtra("mode", "yesCard");
                context.startActivity(editCompany);
            }
            else {
                Toast.makeText(context, context.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            }
        }
        if (mode.equals("Payment Method")){
            Intent editCompany = new Intent(context, PaymentMethod.class);
            editCompany.putExtra("companyId", companyId);
            editCompany.putExtra("companyName", companyName);
            editCompany.putExtra("permission", permission);
            editCompany.putExtra("mode", "yesCard");
            context.startActivity(editCompany);
        }
    }

    private void openAlert(String mode) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert));
        if(mode.equals("Internal Auditor")) {
            builder.setMessage(context.getResources().getString(R.string.your_subscription_for_this_company_is_Receipt_App_ONLY_Please_upgrade_your_package_to_use)+mode + context.getResources().getString(R.string.feature));
        } else {
            builder.setMessage(context.getResources().getString(R.string.your_subscription_for_this_company_is_Receipt_App_ONLY_Please_upgrade_your_package_to_use) +mode + context.getResources().getString(R.string.feature));
        }
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//            }
//        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectedFormat(String dateFormat) {
        waterMarkDateFormat.setText(dateFormat);
        dialog.dismiss();
    }

    @Override
    public int getItemCount() {
        return businessDashboardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icons;
        TextView names, count_pending;

        LinearLayout element;

        LinearLayout card_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            names = itemView.findViewById(R.id.names);
            icons = itemView.findViewById(R.id.icons);
            count_pending = itemView.findViewById(R.id.count_pending);
            element = itemView.findViewById(R.id.element);
            db = new Manager(context);
        }
    }
}

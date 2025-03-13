package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.receipt.PaymentReceiptView;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.FileDownloadUtil;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.Receipt;
import perfect.book.keeping.response.ReceiptResponse;

public class PaymentReceiptAdapter extends RecyclerView.Adapter<PaymentReceiptAdapter.CompanyHolder> implements Filterable {
    List<Receipt> companies;
    Context context;

    Dialog dialog;

    WebView receipt;

    private ProgressBar loader;
    FileDownloadUtil fileDownloadUtil;

    List<Receipt> filterInvoice;

    public PaymentReceiptAdapter( Context context,ArrayList<Receipt> companies) {
        this.companies = companies;
        this.context = context;
        this.fileDownloadUtil = new FileDownloadUtil(context);
        this.filterInvoice = companies;
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
        return new CompanyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyHolder holder, @SuppressLint("RecyclerView") int position) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd'th' MMMM", Locale.US);
        Date inputDate;
        try {
            inputDate = inputFormat.parse(companies.get(position).getCreatedAt());
            Log.e("DATE",""+inputDate);
            Log.e("RETURN DATE",""+outputFormat.format(inputDate));
            holder.transactiondate.setText(outputFormat.format(inputDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(Double.valueOf(companies.get(position).getAmount()) % 1 == 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            //decimalFormat.format(companies.get(position).getAmount());
            holder.amount.setText("$"+decimalFormat.format(Integer.parseInt(companies.get(position).getAmount())));
        } else {
            holder.amount.setText("$"+companies.get(position).getAmount());
        }
        String url = ApiClient.INVOICE_BASE_URL  + companies.get(position).getId();
        holder.transactionid.setText(companies.get(position).getTransaction_id());
        String[] parts = outputFormat.format(inputDate).split(" ");
        String part1 = parts[0];
        String part2 = parts[1];
        String rename_date = part1 + "_" + part2;
          holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //showPopup(companies.get(position).getId(), v);
                // fileDownloadUtil.downloadFile(url, companies.get(position).getTransaction_id()+"_"+rename_date+".pdf");
                 System.out.println("Dowen_Load"+url);
                 Intent intent = new Intent(context, PaymentReceiptView.class);
                 intent.putExtra("companyId", companies.get(position).getCompId());
                 intent.putExtra("permission", companies.get(position).getPermission());
                 intent.putExtra("companyName", companies.get(position).getCompName());
                 intent.putExtra("url", url);
                 intent.putExtra("file_name", companies.get(position).getTransaction_id()+"_"+rename_date+".pdf");
                 context.startActivity(intent);
             }
         });
    }

    private void showPopup(int id, View v) {

        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse(ApiClient.INVOICE_BASE_URL+id));
        Log.e("INVOICE URL",""+ApiClient.INVOICE_BASE_URL+id);

        context.startActivity(httpIntent);

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
                    filterResults.values = filterInvoice;
                    filterResults.count = filterInvoice.size();
                } else {
                    String search = charSequence.toString().toLowerCase();
                    List<Receipt> iList = new ArrayList<>();
                    for(Receipt iFilterList: filterInvoice) {
                        if(iFilterList.getTransaction_id().toLowerCase().contains(search) || iFilterList.getAmount().toLowerCase().contains(search)) {
                            iList.add(iFilterList);
                        }
                    }
                    filterResults.values = iList;
                    filterResults.count = iList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                companies = (List<Receipt>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class CompanyHolder extends RecyclerView.ViewHolder {
        TextView transactiondate, transactionid, amount;
        LinearLayout layoutRec;
        public CompanyHolder(@NonNull View itemView) {
            super(itemView);
            transactiondate = (itemView).findViewById(R.id.transactiondate);
            transactionid = (itemView).findViewById(R.id.transactionid);
            amount = itemView.findViewById(R.id.amount);

        }
    }

}

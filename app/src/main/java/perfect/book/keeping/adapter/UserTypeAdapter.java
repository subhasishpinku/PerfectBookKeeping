package perfect.book.keeping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.model.UserType;

public class UserTypeAdapter extends RecyclerView.Adapter<UserTypeAdapter.ViewHolder> {

    List<UserType> userTypeList;
    Context context;

    LoginScreen loginScreen;

    public UserTypeAdapter(List<UserType> userTypeList, Context context, LoginScreen loginScreen) {
        this.userTypeList = userTypeList;
        this.context = context;
        this.loginScreen = loginScreen;
    }
    @NonNull
    @Override
    public UserTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_type_suggestion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTypeAdapter.ViewHolder holder, int position) {
        String name = userTypeList.get(position).getUserTypeName();
        int id = userTypeList.get(position).getUser_type();
        holder.userType.setText(userTypeList.get(position).getUserTypeName());
        holder.layoutRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.updateType(name, id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userType;
        LinearLayout layoutRec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutRec = (itemView).findViewById(R.id.layoutRec);
            userType = (itemView).findViewById(R.id.userType);
        }
    }
}

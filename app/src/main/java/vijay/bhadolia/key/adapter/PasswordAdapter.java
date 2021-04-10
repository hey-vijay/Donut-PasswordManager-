package vijay.bhadolia.key.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vijay.bhadolia.key.Interfaces.PasswordClickListener;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.model.Password;


public class PasswordAdapter extends ListAdapter<Password, PasswordAdapter.NewPasswordViewHolder> {

    private static final String TAG = PasswordAdapter.class.getName();

    private final PasswordClickListener mListener;
    private final Context mContext;

    public PasswordAdapter(Context context, PasswordClickListener listener) {
        super(DIFF_UTIL);
        mListener = listener;
        mContext = context;
    }

    public List<Password> getPasswordList() {
        return getCurrentList();
    }

    public Password getItem(int position) {
        return getCurrentList().get(position);
    }

    private static final DiffUtil.ItemCallback<Password> DIFF_UTIL = new DiffUtil.ItemCallback<Password>() {
        @Override
        public boolean areItemsTheSame(@NonNull Password oldItem, @NonNull Password newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Password oldItem, @NonNull Password newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getAccountName().equals(newItem.getAccountName())
                    && oldItem.getPassword().equals(newItem.getPassword())
                    && oldItem.getResIconName().equals(newItem.getResIconName())
                    && oldItem.getDeleted() == newItem.getDeleted()
                    && oldItem.getColor() == newItem.getColor()
                    && oldItem.getFrequency() == newItem.getFrequency();
        }
    };

    @NonNull
    @Override
    public NewPasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password, parent, false);
        return new NewPasswordViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPasswordViewHolder holder, int position) {
        Password password = getItem(position);
        holder.title.setText(password.getTitle());

        int resId = mContext.getResources().getIdentifier(password.getResIconName(), "drawable", mContext.getPackageName());

        Glide.with(mContext)
                .load(resId)
                .error(R.drawable.ic_choose_image)
                .into(holder.accountIcon);
    }

    static class NewPasswordViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout parentLayout;
        ImageView accountIcon;
        ImageButton showPassword;
        TextView title, password;
        //ConstraintLayout.LayoutParams params_invisible;

        @SuppressLint("ClickableViewAccessibility")
        public NewPasswordViewHolder(@NonNull View itemView, final PasswordClickListener listener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.cl_itemParentLayout);
            accountIcon = itemView.findViewById(R.id.accountIcon);
            title = itemView.findViewById(R.id.tvAccountTitle);
            password = itemView.findViewById(R.id.tvAccountPassword);
            showPassword = itemView.findViewById(R.id.imgShowPassword);
            //params_invisible = new ConstraintLayout.LayoutParams(0, 0);

            parentLayout.setOnClickListener((view -> listener.onClick(getAdapterPosition())));
        }
    }
}

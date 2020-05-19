package vijay.bhadolia.key.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import vijay.bhadolia.key.DataBase.ItemPassword;
import vijay.bhadolia.key.R;

public class passwordAdapter extends ListAdapter<ItemPassword,passwordAdapter.passwordViewHolder> {

    private onItemClickListener mListener;

    public passwordAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ItemPassword> DIFF_CALLBACK = new DiffUtil.ItemCallback<ItemPassword>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemPassword oldItem, @NonNull ItemPassword newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemPassword oldItem, @NonNull ItemPassword newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getAccountName().equals(newItem.getAccountName())
                    && oldItem.getPassword().equals(newItem.getPassword());
        }
    };

    public interface onItemClickListener {
        void onTouchListener(View v, int position);
        void onTouchRemoveListener(View v, int position);
        void onLongItemClick(View v, int position);
    }

    public void setOnItemClickListener(onItemClickListener listner) {
        mListener = listner;
    }

    static class passwordViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "KeyLog";
        TextView mAccName;
        TextView mAccPassword;
        ImageButton mUnlock;

        @SuppressLint("ClickableViewAccessibility")
        passwordViewHolder(@NonNull final View itemView, final onItemClickListener listener) {
            super(itemView);
            mAccName = itemView.findViewById(R.id.item_tv_label);
            mAccPassword = itemView.findViewById(R.id.item_tv_password);
            mUnlock = itemView.findViewById(R.id.item_imgbtn_lock);

            mUnlock.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int position = getAdapterPosition();
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        listener.onTouchListener(v,position);
                    }
                    if((event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)){
                        listener.onTouchRemoveListener(v,position);
                    }
                    return true;
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onLongItemClick(v, position);
                            Log.e(TAG, "Position "+position );
                        }
                    }
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public passwordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password, parent, false);
        return new passwordViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull passwordViewHolder holder, int position) {
        ItemPassword currentInfo = getItem(position);
        holder.mAccName.setText(currentInfo.getTitle());
        holder.mAccPassword.setText("************");
    }

}

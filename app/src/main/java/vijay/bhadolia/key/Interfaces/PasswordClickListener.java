package vijay.bhadolia.key.Interfaces;

import android.view.View;

public interface PasswordClickListener {
    void onClick(int position);

    void onLongClick(View view, int position);

    void onViewItemPressed(int position);

    void onViewItemUnpressed(int position);
}

package vijay.bhadolia.key.ui.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import vijay.bhadolia.key.R;

public class IconBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = IconBottomSheet.class.getName();

    private BottomSheetClickListener mListener;
    private Context mContext;
    List<ImageView> imageViewList;

    public IconBottomSheet(Context context,BottomSheetClickListener listener) {
        mListener = listener;
        mContext = context;
    }

    int[] resourceIds = {
            R.drawable.ic_dropbox,
            R.drawable.ic_google_plus,
            R.drawable.ic_linkedin,
            R.drawable.ic_youtube,
            R.drawable.ic_tik_tok,
            R.drawable.ic_twitter,
            R.drawable.ic_spotify,
            R.drawable.ic_snapchat,
            R.drawable.ic_skype,
            R.drawable.ic_reddit,
            R.drawable.vimeo,
            R.drawable.ic_link,
            R.drawable.man,
            R.drawable.ic_key,
            R.drawable.binary,
            R.drawable.quora,
            R.drawable.pinterest,
            R.drawable.notion,
            R.drawable.instagram,
            R.drawable.leaves,
            R.drawable.google_drive,
            R.drawable.gmail,
            R.drawable.ic_shield,
            R.drawable.woman,
            R.drawable.ic_boy
    };

    int[] imageViewIds = {
            R.id.imageView,
            R.id.imageView2,
            R.id.imageView3,
            R.id.imageView4,
            R.id.imageView5,
            R.id.imageView6,
            R.id.imageView7,
            R.id.imageView8,
            R.id.imageView9,
            R.id.imageView10,
            R.id.imageView11,
            R.id.imageView12,
            R.id.imageView13,
            R.id.imageView14,
            R.id.imageView15,
            R.id.imageView16,
            R.id.imageView17,
            R.id.imageView18,
            R.id.imageView19,
            R.id.imageView20,
            R.id.imageView21,
            R.id.imageView22,
            R.id.imageView23,
            R.id.imageView24,
            R.id.imageView25
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_icon_picker, container, false);


        setUpView(v);
        for(int imageID: imageViewIds) {
            v.findViewById(imageID).setOnClickListener(this);
        }

        printIds();

        return v;
    }

    private void printIds() {
        Log.e(TAG, "printIds: ");
        for(int i : resourceIds){
            Log.d(TAG, "printIds: " + i);
        }
    }

    private void setUpView(View v) {
        imageViewList = new ArrayList<>();
        int i = 0;
        for(int imgId : imageViewIds) {
            ImageView imageView = v.findViewById(imgId);
            Drawable drawable = ContextCompat.getDrawable(mContext, resourceIds[i]);
            imageView.setImageDrawable(drawable);
            i++;
            imageViewList.add(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        ImageView imageView = (ImageView) view;
        Drawable drawable = imageView.getDrawable();
        int idx = 0;
        for(ImageView imgView : imageViewList) {
            if(imgView.getDrawable().getConstantState() == drawable.getConstantState()) {
                mListener.onIconSelected(drawable, resourceIds[idx]);
                break;
            }
            idx++;
        }
        dismiss();
    }


    public interface BottomSheetClickListener {
        void onIconSelected(Drawable iconDrawable, int resId);
    }
}

package vijay.bhadolia.key.util;

public class Constants {


    public static final String IS_UNLOCK = "isUnlock";
    public static final String TempPassword = "############";


    /*prefs constant keys*/
    public static final String APP_SHARED_PREFS_NAME = "vijay.bhadolia.PasswordManager";
    public static final String FINGERPRINT_ENABLE = "vijay.bhadolia.FingerPrint.enable";        //boolean
    public static final String FAKE_PASSWORD_ENABLE = "vijay.bhadolia.fakePassword.enable";     //boolean
    public static final String MASTER_PASSWORD = "vijay.bhadolia.app.masterPassword.enable";
    public static final String FIRST_TIME = "vijay.bhadolia.usersFirstTime.enable";     //boolean
    public static final String COPY_ENABLE = "vijay.bhadolia.AllowCopyEnable.enable";   //boolean
    public static final String DISABLE_BLUR_BACKGROUND = "vijay.bhadolia.disableBlurBackground";      //boolean
    public static final String SHOW_LAST_ACTIVE_STATUS = "vijay.bhadolia.lastAppOpened";      //boolean
    public static final String LAST_ACTIVE_TIME = "vijay.bhadolia.lastActiveTime";      //String

    /*-------------------------------------------------------------------------------*/

    public enum SHOW_PASSWORD_TYPE{
        CENTER_DIALOG(1),
        TOP_DIALOG(2),
        INSIDE_CARD(3);

        private int value;

        SHOW_PASSWORD_TYPE(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static final String DATE_FORMAT = "dd, MMMM yyyy hh:mm:ss a";
}


/*
*   // Open the add password dialog with circular animation.
    // unexpected behavior
    private void revealShow(final View dialogView, boolean b, final Dialog dialog) {
        final View view = dialogView.findViewById(R.id.add_password_layout);
        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab.getX() + (fab.getWidth()/2));
        int cy = (int) (fab.getY() + ((fab.getHeight()))+76);

        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(400);
            revealAnimator.start();
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialogView.findViewById(R.id.tv_layout_1).requestFocus();
                }
            });
        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(400);
            anim.start();
        }
    }
* */
package vijay.bhadolia.key.Utility;

import android.util.Log;

/*
*   Utility class for helper functions
*   * Responsible for generating random string password of different types and length
* */

public class UtilityFunctions {
    private static boolean set1, set2, set3;
    public static int length;

    public static String generateRandomText(int length, boolean specialCharacter) {
        String A = "ABCDEFGHJKLMNOPQRSTUVWXYZ"
                + "abcdefghijkmnopqrstuvwxyz"
                + "0123456789";
        if (specialCharacter) {
            A = A + "!#@$#_&/*";
        }

        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < length; i++) {
            int index = (int) (A.length() * Math.random());
            sb.append(A.charAt(index));
        }
        return sb.toString();
    }

    public static String getRandomText() {
        String A;
        if(set1){
            A = "ABCDEFGHJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijkmnopqrstuvwxyz";
            set2 = true;
            set1 = false;
        }else if(set2){
            A = "ABCDEFGHJKLMNOPQRSTUVWXYZ"
                    + "5684291"
                    + "abcdefghijkmnopqrstuvwxyz" + "0123456789";
            set2 = false;
            set3 = true;
        }else if(set3){
            A = "0123456789";
            set3 = false;
        }else{
            set1 = true;
            A = "ABCDEFGHJKLMNOPQRSTUVWXYZ"
                    + "!@#$&*_."
                    + "abcdefghijkmnopqrstuvwxyz" + "0123456789"
                    + "!@#$&*_.";
            length += 1;
        }
        Log.d("Length", "generateRandomText: "+ length);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (A.length() * Math.random());
            sb.append(A.charAt(index));
        }
        return sb.toString();
    }
}

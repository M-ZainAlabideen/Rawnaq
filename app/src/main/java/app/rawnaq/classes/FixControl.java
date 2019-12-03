package app.rawnaq.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.regex.Pattern;

public class FixControl {
    public static int getImageHeight(Context context, int resId){

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), resId, dimensions);
        return dimensions.outHeight;

    }

    public static int getImageWidth(Context context, int resId){

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), resId, dimensions);
        return dimensions.outWidth;

    }

    public final static  boolean isValidEmail(String email){
        if(email==null){
            return false;
        }
        else {
            return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
        }
    }
}

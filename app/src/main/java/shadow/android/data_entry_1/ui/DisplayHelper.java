package shadow.android.data_entry_1.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;


public abstract class DisplayHelper {

    public static Offset centerOnParent(Activity activity, View view){
        DisplayMetrics metrics=new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthP=metrics.widthPixels;
        int heightP=metrics.heightPixels;
        int x = 0,y=0;
        view.measure(x,y);
        return new Offset((widthP-view.getMeasuredWidth())/2,(heightP-view.getMeasuredHeight())/2);
    }
    public static void dimPopupParent(View popupParent,boolean b) {
        WindowManager wm = (WindowManager) popupParent.getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) popupParent.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (b)
            p.dimAmount = 0.4f;
        else p.dimAmount = 1f;
        if (wm != null) {
            wm.updateViewLayout(popupParent, p);
        }
    }
}

package magosoftware.petprojetos;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

public class CustomDrawer extends DrawerLayout {

    public CustomDrawer(Context context) {
        super(context);
    }

    public CustomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View drawer = getChildAt(1);

        if(isDrawerVisible(Gravity.LEFT)) {
            return super.onInterceptTouchEvent(ev);
        }
        else if (ev.getRawX() < drawer.getWidth()) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

}
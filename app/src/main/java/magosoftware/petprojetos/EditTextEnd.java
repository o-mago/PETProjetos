package magosoftware.petprojetos;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by root on 22/02/18.
 */

public class EditTextEnd extends android.support.v7.widget.AppCompatEditText {

    public EditTextEnd(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextEnd(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextEnd(Context context) {
        super(context);

    }

    @Override
    public void onSelectionChanged(int start, int end) {

        CharSequence text = getText();
        if (text != null) {
            if (start != text.length() || end != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }
        super.onSelectionChanged(start, end);
    }
}

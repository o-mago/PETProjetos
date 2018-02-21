package magosoftware.petprojetos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 19/02/18.
 */

public class LineHolder extends RecyclerView.ViewHolder {

    public TextView title;

    public LineHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.linha);
    }
}

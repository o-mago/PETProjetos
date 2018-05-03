package magosoftware.petprojetos;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PagerAdapterTarefas extends PagerAdapter {

    private Context mContext;

    public PagerAdapterTarefas(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        if(position == 0) {
            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_fazer, collection, false);
        }
        else if(position == 1){
            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_page, collection, false);
        }
        else {
            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_feitas, collection, false);
        }
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
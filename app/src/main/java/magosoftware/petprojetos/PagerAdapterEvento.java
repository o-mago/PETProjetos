package magosoftware.petprojetos;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapterEvento extends PagerAdapter {

    private Context mContext;
    public List<ViewGroup> listaViews;

    public PagerAdapterEvento(Context context) {
        mContext = context;
        listaViews = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        layout = (ViewGroup) inflater.inflate(R.layout.mes_evento, collection, false);
//        if(position == 0) {
//
//        }
//        else if(position == 1){
//            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_page, collection, false);
//        }
//        else {
//            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_feitas, collection, false);
//        }
        collection.addView(layout);
        listaViews.add(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        listaViews.remove(position);
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "JAN";
            case 1:
                return "FEV";
            case 2:
                return "MAR";
            case 3:
                return "ABR";
            case 4:
                return "MAI";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AGO";
            case 8:
                return "SET";
            case 9:
                return "OUT";
            case 10:
                return "NOV";
            case 11:
                return "DEZ";
            default:
                return null;
        }
    }
}

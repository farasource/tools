package ghasemi.abbas.abzaar.db.my.phone;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import java.text.DecimalFormat;

/**
 * on 04/20/2018.
 */

public class Lcd extends Fragment {
    View v ;
    String[] detail_lcd = new String[4];
    View view ;
    String[] detail_name = {"ابعاد صفحه نمایش:", "اینچ:", "قطر صفحه نمایش:", "تراکم صفحه نمایش:"};
    RecyclerView list_detail ;
     Adapter_Detail adapter_detail ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);

        get();

        adapter_detail = new Adapter_Detail(Main.activity);
        list_detail = (RecyclerView)v.findViewById(R.id.list_main);
        list_detail.setLayoutManager(new LinearLayoutManager(Main.activity,LinearLayoutManager.VERTICAL,false));
        list_detail.hasFixedSize();
        list_detail.setAdapter(adapter_detail);

        return v;
    }

    class Adapter_Detail extends RecyclerView.Adapter<Adapter_Detail.contentViewHolder> {
        Context context;
        public Adapter_Detail(Context c) {
            context = c;
        }

        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info_phone, parent, false);

            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, final int position) {

            holder.text_detail_name.setText(detail_name[position]);
            holder.text_detail_value.setText(detail_lcd[position]);
        }

        @Override
        public int getItemCount() {
            return detail_name.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            TextView text_detail_name,text_detail_value;

            public contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = (TextView) itemView.findViewById(R.id.text_detail_name);
                text_detail_value = (TextView) itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = (CardView) itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }

    public  void  get(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Main.activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        double x = Math.pow(((double) width) / ((double) getResources().getDisplayMetrics().xdpi), 2.0d);
        double hi = ((double) getResources().getDisplayMetrics().heightPixels) / ((double) getResources().getDisplayMetrics().ydpi);
        double y = Math.pow(hi, 2.0d);
        double screenInches = Math.sqrt(x + y);
        detail_lcd[0] = width + " * " + height;
        detail_lcd[1] = new StringBuilder(String.valueOf(String.valueOf(new DecimalFormat("#.##").format(Math.sqrt(x))))).append(" * ").append(String.valueOf(new DecimalFormat("#.##").format(Math.sqrt(y)))).toString();
        detail_lcd[2] = String.valueOf(new DecimalFormat("#.##").format(screenInches));
        detail_lcd[3] = new StringBuilder(String.valueOf(String.valueOf(getResources().getDisplayMetrics().densityDpi))).append(" dpi").toString();
    }
}

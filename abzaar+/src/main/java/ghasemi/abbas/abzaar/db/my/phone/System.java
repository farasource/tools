package ghasemi.abbas.abzaar.db.my.phone;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import java.io.File;

/**
 * on 04/20/2018.
 */

public class System extends Fragment {
    View v ;
    View view ;
    String[] detail_name = {"ویرایش اندروید:", "شماره ویرایش اندروید:", "نام ویرایش اندروید:", "دسترسی روت:"};
    String[] detail_system = new String[4];
    RecyclerView list_detail ;
     Adapter_Detail adapter_detail ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);

        get();

        adapter_detail = new Adapter_Detail(Main.activity);
        list_detail = v.findViewById(R.id.list_main);
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
        public Adapter_Detail.contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info_phone, parent, false);

            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter_Detail.contentViewHolder holder, final int position) {

            holder.text_detail_name.setText(detail_name[position]);
            holder.text_detail_value.setText(detail_system[position]);
        }

        @Override
        public int getItemCount() {
            return detail_name.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            TextView text_detail_name,text_detail_value;

            public contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = itemView.findViewById(R.id.text_detail_name);
                text_detail_value = itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }


    public void get(){
        detail_system[0] = String.valueOf(Build.VERSION.RELEASE);
        detail_system[1] = String.valueOf(Build.VERSION.SDK_INT);
        detail_system[2] = getNameAndroid();
        detail_system[3] = "روت نیست";
        if(isRooted()) detail_system[3] = "دارد" ;
    }
    private String getNameAndroid() {
        switch (Build.VERSION.SDK_INT) {
            case 14:
                return "Ice Cream Sandvich (4.0)";
            case 15:
                return "Ice Cream Sandvich MR1 (4.0.3)";
            case 16:
                return "Jelly Bean (4.1)";
            case 17:
                return "Jelly Bean MR1 (4.2)";
            case 18:
                return "Jelly Bean MR2 (4.3)";
            case 19:
                return "Kitkat (4.4)";
            case 20:
                return "Kitkat Watch (4.4)";
            case 21:
                return "Lollipop (5.0)";
            case 22:
                return "Lollipop MR1 (5.1";
            case 23:
                return "Marshmallow (6.0)";
            case 24:
                return "Nougat (7.0)";
            case 25:
                return "Nougat MR1 (7.1.1)";
            case 26:
                return "O (8.0)";
            case 27:
                return "O MR1 (8.1)";
            case 28:
                return "Pie";
            default:
                return "" + Build.VERSION.SDK_INT;
        }
    }

    public static boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }
        return false;
        // try executing commands
        // return canExecuteCommand("/system/xbin/which su")
        //   || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }
}
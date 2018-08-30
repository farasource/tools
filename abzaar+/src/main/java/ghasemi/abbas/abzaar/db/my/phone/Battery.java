package ghasemi.abbas.abzaar.db.my.phone;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
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

/**
 * on 04/20/2018.
 */

public class Battery extends Fragment {
    View v;

    View view;
    String[] detail_name = {"وضعیت:", "درجه حرارت:", "ولتاژ:", "درصد شارژ:", "مدل باتری:", "سلامتی:"};
    RecyclerView list_detail_battery;
    String[] detail_value = new String[6];
    Adapter_Detail_Battery adapter_detail_battery;
    BatteryInfoReceiver batteryInfoReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);
        Main.activity.registerReceiver(batteryInfoReceiver = new BatteryInfoReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        adapter_detail_battery = new Adapter_Detail_Battery(Main.activity);
        list_detail_battery = (RecyclerView) v.findViewById(R.id.list_main);
        list_detail_battery.setLayoutManager(new LinearLayoutManager(Main.activity, LinearLayoutManager.VERTICAL, false));
        list_detail_battery.hasFixedSize();
        list_detail_battery.setAdapter(adapter_detail_battery);
        return v;
    }

    @Override
    public void onDestroy() {
        Main.activity.unregisterReceiver(batteryInfoReceiver);
        super.onDestroy();
    }

    class Adapter_Detail_Battery extends RecyclerView.Adapter<Adapter_Detail_Battery.contentViewHolder> {
        Context context;

        public Adapter_Detail_Battery(Context c) {
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
            holder.text_detail_value.setText(detail_value[position]);
        }

        @Override
        public int getItemCount() {
            return detail_name.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            TextView text_detail_name, text_detail_value;

            public contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = (TextView) itemView.findViewById(R.id.text_detail_name);
                text_detail_value = (TextView) itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = (CardView) itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }


    class BatteryInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String EXTRA_TEMPERATURE = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 + Character.toString((char) 176);
            String EXTRA_VOLTAGE = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / (float) 1000 + Character.toString((char) 176);
            String EXTRA_LEVEL = Integer.toString(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
            String EXTRA_TECHNOLOGY = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int EXTRA_STATUS = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            boolean ACTION_CHARGING = EXTRA_STATUS == BatteryManager.BATTERY_STATUS_CHARGING;
            detail_value[0] = ACTION_CHARGING ? "درحال شارژ شدن" : "مصرف باتری";
            detail_value[1] = EXTRA_TEMPERATURE + " C";
            detail_value[2] = EXTRA_VOLTAGE + " V";
            detail_value[3] = EXTRA_LEVEL;
            detail_value[4] = EXTRA_TECHNOLOGY;
            if (detail_value[4].equalsIgnoreCase("")) {
                detail_value[4] = "-";
            }

            int battery_helth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

            switch (battery_helth) {
                case 1:
                    detail_value[5] = "UNKNOWN";
                    break;
                case 2:
                    detail_value[5] = "GOOD";
                    break;
                case 3:
                    detail_value[5] = "OVERHEAT";
                    break;
                case 4:
                    detail_value[5] = "DEAD";
                    break;
                case 5:
                    detail_value[5] = "OVER_VOLTAGE";
                    break;
                case 6:
                    detail_value[5] = "UNSPECIFIED_FAILURE";
                    break;
                case 7:
                    detail_value[5] = "COLD";
                    break;
            }
            adapter_detail_battery.notifyDataSetChanged();
        }
    }
}

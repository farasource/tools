package ghasemi.abbas.abzaar.db.my.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;

public class Sim extends Fragment {
    private View v;
    private View view;
    private String[] detail_name = {"دانلود لحظه ای:", "آپلود لحظه ای:","مجموع دانلود(ماهانه):","مجموع آپلود(ماهانه):", "اپراتور:", "آی پی آدرس:", "مک آدرس:"};
    private String[] detail_system = new String[7];
    private RecyclerView list_detail;
    private Adapter_Detail adapter_detail;
    private final Handler mHandler = new Handler();
    private long mStartRX = TrafficStats.getTotalRxBytes();
    private long mStartTX = TrafficStats.getTotalTxBytes();
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            mStartRX = TrafficStats.getTotalRxBytes();
            detail_system[0] = getFormatDatausing(rxBytes);
            detail_system[2] = getFormatDatausing(mStartRX);
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            mStartTX = TrafficStats.getTotalTxBytes();
            detail_system[1] = getFormatDatausing(txBytes);
            detail_system[3] = getFormatDatausing(mStartTX);
            adapter_detail.notifyDataSetChanged();
            mHandler.postDelayed(mRunnable, 1000);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);
        adapter_detail = new Adapter_Detail(Main.activity);
        get();
        list_detail = v.findViewById(R.id.list_main);
        list_detail.setLayoutManager(new LinearLayoutManager(Main.activity, LinearLayoutManager.VERTICAL, false));
        list_detail.setAdapter(adapter_detail);
        return v;
    }

    @SuppressLint("HardwareIds")
    public void get() {
        mRunnable.run();
        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        detail_system[4] = manager.getSimOperatorName();
        WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        detail_system[5] = getLocalIpAddress();
        detail_system[6] = wm.getConnectionInfo().getMacAddress();
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (Exception ex) {
            //
        }
        return "0.0.0.0";
    }

    private String getFormatDatausing(long data) {
        if (data >= 1024 * 1024 * 1024) {
            return data / (1024 * 1024 * 1024) + " GBs";
        } else if (data >= 1024 * 1024) {
            return data / (1024 * 1024) + " MBs";
        } else if (data >= 1024) {
            return data / 1024 + " KBs";
        }else {
            return data + " bytes";
        }
    }

    class Adapter_Detail extends RecyclerView.Adapter<Adapter_Detail.contentViewHolder> {
        Context context;

        Adapter_Detail(Context c) {
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

            TextView text_detail_name, text_detail_value;

            contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = itemView.findViewById(R.id.text_detail_name);
                text_detail_value = itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }
}

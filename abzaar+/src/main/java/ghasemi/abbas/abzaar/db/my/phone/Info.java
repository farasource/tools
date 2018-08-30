package ghasemi.abbas.abzaar.db.my.phone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * on 04/20/2018.
 */

public class Info extends Fragment {
    View v;
    View view;
    String[] detail_name;
    RecyclerView list_detail;
    String[] detail_info;
    Adapter_Detail adapter_detail;

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) return capitalize(model);
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) return str;
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) capitalizeNext = true;
            phrase.append(c);
        }
        return phrase.toString();
    }

    private int readSystemFileAsInt(String type) throws Exception {
        InputStream in;
        try {
            final Process process = new ProcessBuilder("/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/" + type).start();
            in = process.getInputStream();
            final String content = readFully(in);
            return Integer.parseInt(content);
        } catch (final Exception e) {
            throw new Exception(e);
        }
    }

    public String readFully(final InputStream pInputStream) {
        final StringBuilder sb = new StringBuilder();
        final Scanner sc = new Scanner(pInputStream);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            detail_name = new String[]{"سازنده:", "برند:", "مدل دستگاه:", "شماره ساخت:", "برد:", "سریال:", "معماری:", "معماری هسته:", "تعداد هسته:", "سرعت پردازنده:", "بالاترین فرکانس:", "پائین ترین فرکانس:"};
            detail_info = new String[]{
                    android.os.Build.MANUFACTURER,
                    android.os.Build.BRAND,
                    getDeviceName(),
                    Build.ID + "." + Build.VERSION.INCREMENTAL,
                    Build.BOARD,
                    getSerialName(),
                    Build.SUPPORTED_ABIS[0],
                    java.lang.System.getProperty("os.arch"),
                    String.valueOf(getNamberCores()),
                    ((double) getMaxCpu()) / 1000.0d + " گیگاهرتز",
                    getCPUFrequencyMax("cpuinfo_max_freq"),
                    getCPUFrequencyMax("cpuinfo_min_freq")
            };
        } else {
            detail_info = new String[]{
                    android.os.Build.MANUFACTURER,
                    android.os.Build.BRAND,
                    getDeviceName(),
                    Build.ID + "." + Build.VERSION.INCREMENTAL,
                    Build.BOARD,
                    Build.SERIAL,
                    getCpuName(),
                    Build.CPU_ABI,
                    java.lang.System.getProperty("os.arch"),
                    String.valueOf(getNamberCores()),
                    ((double) getMaxCpu()) / 1000.0d + " گیگاهرتز",
                    readUsage(),
                    getCPUFrequencyMax("cpuinfo_max_freq"),
                    getCPUFrequencyMax("cpuinfo_min_freq")
            };
            detail_name = new String[]{"سازنده:", "برند:", "مدل دستگاه:", "شماره ساخت:", "برد:", "سریال:", "مدل پردازنده:", "معماری:", "معماری هسته:", "تعداد هسته:", "سرعت پردازنده:", "درصد استفاده از پردازنده:", "بالاترین فرکانس:", "پائین ترین فرکانس:"};
        }

        adapter_detail = new Adapter_Detail(Main.activity);
        list_detail = v.findViewById(R.id.list_main);
        list_detail.setLayoutManager(new LinearLayoutManager(Main.activity, LinearLayoutManager.VERTICAL, false));
        list_detail.hasFixedSize();
        list_detail.setAdapter(adapter_detail);

        return v;
    }

    private String getSerialName() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(Main.activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "دسترسی داده نشده است";
                }
                return Build.getSerial();
            }
            return Build.SERIAL;
        } catch (Exception e) {
            return "دسترسی داده نشده است";
        }
    }

    private String readUsage() {
        try {

            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String[] toks = reader.readLine().split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = ((((Long.parseLong(toks[2]) + Long.parseLong(toks[3])) + Long.parseLong(toks[4])) + Long.parseLong(toks[6])) + Long.parseLong(toks[7])) + Long.parseLong(toks[8]);
            Thread.sleep(360);
            reader.seek(0);
            String load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long cpu2 = ((((Long.parseLong(toks[2]) + Long.parseLong(toks[3])) + Long.parseLong(toks[4])) + Long.parseLong(toks[6])) + Long.parseLong(toks[7])) + Long.parseLong(toks[8]);
            float x = ((float) (cpu2 - cpu1)) / ((float) ((cpu2 + Long.parseLong(toks[5])) - (cpu1 + idle1)));
            return new DecimalFormat("#.##").format((double) (x * 100.0f)) + " درصد";
        } catch (Exception ex) {
            return "";
        }
    }

    public String getCpuName() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String text = br.readLine();
            br.close();
            String[] array = text.split(":\\s+", 2);
            if (array.length >= 2) return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public int getNamberCores() {
        if (Build.VERSION.SDK_INT >= 17) return Runtime.getRuntime().availableProcessors();
        return getNamberCores2();
    }

    public int getNamberCores2() {
        try {
            return new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
                public boolean accept(File p) {
                    return Pattern.matches("cpu[0-9]+", p.getName());
                }
            }).length;
        } catch (Exception e) {
            return 1;
        }
    }

    public int getMaxCpu() {
        final boolean assertionsDisabled = (!AboutPhone.class.desiredAssertionStatus());
        int maxFreq = -1;
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state", "r");
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                String[] splits = line.split("\\s+");
                if (!assertionsDisabled && splits.length != 2) throw new AssertionError();
                else if (Long.parseLong(splits[1]) > 0) {
                    int freq = (int) (Long.parseLong(splits[0]) / 1000);
                    if (freq > maxFreq) maxFreq = freq;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return maxFreq;
    }

    public String getCPUFrequencyMax(String string) {
        try {
            return (readSystemFileAsInt(string) / 1000) + " MHz";
        } catch (Exception e) {
            //
        }
        return "UNKNOWN";
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
            holder.text_detail_value.setText(detail_info[position]);
        }

        @Override
        public int getItemCount() {
            return detail_name.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            TextView text_detail_name, text_detail_value;

            public contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = itemView.findViewById(R.id.text_detail_name);
                text_detail_value = itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }
}

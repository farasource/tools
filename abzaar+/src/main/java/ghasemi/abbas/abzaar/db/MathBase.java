package ghasemi.abbas.abzaar.db;


import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import java.text.NumberFormat;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/26/2018.
 */

public class MathBase extends AppCompatActivity {
    Button done;
    EditText getMath;
    Animation show;
    TextView setMath;
    Spinner sp1, sp2, sp3;
    String[] first = {"دما", "طول", "وزن", "سرعت", "حجم", "مساحت", "زمان"};
    String[] dama = {"C", "F", "K", "R"};
    String[] tool = {"mm", "cm", "m", "km", "ft", "mi", "in", "yd", "nm"};
    String[] vazn = {"kg", "lb", "mg", "ounce", "g","t"};
    String[] sorat = {"m/s", "mph", "kmph", "kn", "ft/s"};
    String[] hajm = {"ml", "l", "mm\u00B3", "cm\u00B3", "m\u00B3", "ft\u00B3", "gal (UK)", "qt (UK)", "pt (UK)", "fl.oz (UK)", "gal (US)", "qt (US)", "pt (US)", "fl.oz (US)", "cup", "tbsp", "tsp"};
    String[] masahat = {"mm\u00B2", "m\u00B2", "cm\u00B2", "km\u00B2", "ft\u00B2", "yd\u00B2", "mil\u00B2", "ha", "acre"};
    String[] zaman = {"ns", "µs", "ms", "s", "min", "h", "day", "week", "month", "year"};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        done = findViewById(R.id.Done);
        sp1 = findViewById(R.id.spinner1);
        sp2 = findViewById(R.id.spinner2);
        sp3 = findViewById(R.id.spinner3);
        getMath = findViewById(R.id.getMath);
        setMath = findViewById(R.id.setMath);
        setMath.setTextIsSelectable(true);
        show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, first);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);

        final ArrayAdapter<String> Dama = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, dama);
        Dama.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Tool = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, tool);
        Tool.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Vazn = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, vazn);
        Vazn.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Sorat = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, sorat);
        Sorat.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Hajm = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, hajm);
        Hajm.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Masahat = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, masahat);
        Masahat.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> Zaman = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, zaman);
        Zaman.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == 0) {
                    sp2.setAdapter(Dama);
                    sp3.setAdapter(Dama);
                } else if (position == 1) {
                    sp2.setAdapter(Tool);
                    sp3.setAdapter(Tool);
                } else if (position == 2) {
                    sp2.setAdapter(Vazn);
                    sp3.setAdapter(Vazn);
                } else if (position == 3) {
                    sp2.setAdapter(Sorat);
                    sp3.setAdapter(Sorat);
                } else if (position == 4) {
                    sp2.setAdapter(Hajm);
                    sp3.setAdapter(Hajm);
                } else if (position == 5) {
                    sp2.setAdapter(Masahat);
                    sp3.setAdapter(Masahat);
                } else if (position == 6) {
                    sp2.setAdapter(Zaman);
                    sp3.setAdapter(Zaman);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getMath.getText().toString().equals("")) {
                    return;
                }
                if (sp1.getSelectedItemPosition() == 0) {
                    getDama(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 1) {
                    getTool(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 2) {
                    getVazn(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 3) {
                    getSorat(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 4) {
                    getHajm(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 5) {
                    getMasahat(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                } else if (sp1.getSelectedItemPosition() == 6) {
                    getZaman(sp2.getSelectedItemPosition(), sp3.getSelectedItemPosition(), Double.parseDouble(getMath.getText().toString()));
                }
            }
        });
    }


    private void getZaman(int from, int to, double get) {
        double[] a = {0.001,1.0E-6, 1.0E-9, 1.66666667E-11,2.77777778E-13,1.1574074E-14,1.6534392E-15,3.805175E-16,3.1709792E-17};
        double[] b = {1000, 1, 0.001, 1.0E-6, 1.66666667E-8,2.77777778E-10,1.1574074E-11,1.6534392E-12,3.805175E-13,3.1709792E-14};
        double[] c = {1000000, 1000, 1, 0.001, 1.66666667E-5,2.77777778E-8,1.15740741E-8,1.6534392E-9,3.805175E-10,3.1709792E-11};
        double[] d = {1E9, 1000000, 1000, 1, 1.016666667E-7,2.77777778E-4,1.15740741E-5 ,1.6534392E-6,3.805175E-7,3.1709792E-8};
        double[] e = {6E10, 6E7, 60000, 60,1,0.01666666667,0.9444444E-4,9.92063492E-5,2.28310502E-5,1.90258752E-6};
        double[] f = {3.6E12,3.6E9,3600000,3600,60,1,0.04166666667,0.00595238095,0.00136986301,1.1415525E-4};
        double[] g = {8.64E13,8.64E10,8.64E7, 86400, 1440, 24,1,0.14285714286,0.03287671233,0.00273972603};
        double[] h = {6.048E14, 6.048E11, 6.048E8, 6.048E5, 10080,168,7,1,0.2301369863,0.01917808219};
        double[] i = {2.628E15, 2.628E12, 2.628E9, 2.628E6, 43800,730,30.4155555557,4.34523809524,1,0.0833333333};
        double[] j = {3.1536E16, 3.1536E13, 3.1536E10, 3.1536E7, 525600,8760,365,52.1428571429,12};
        if (zaman[from].equals(zaman[to])) {
            setM(get);
        } else if (zaman[from].equals("ns")) {
            setM(get * a[to - 1]);
        } else if (zaman[from].equals("µs")) {
            setM(get * b[to]);
        } else if (zaman[from].equals("ms")) {
            setM(get * c[to]);
        } else if (zaman[from].equals("s")) {
            setM(get * d[to]);
        } else if (zaman[from].equals("min")) {
            setM(get * e[to]);
        } else if (zaman[from].equals("h")) {
            setM(get * f[to]);
        } else if (zaman[from].equals("day")) {
            setM(get * g[to]);
        } else if (zaman[from].equals("week")) {
            setM(get * h[to]);
        } else if (zaman[from].equals("month")) {
            setM(get * i[to]);
        }else if (zaman[from].equals("year")) {
            setM(get * j[to]);
        }

    }


    private void getMasahat(int from, int to, double get) {
        double[] a = {1.0E-6,0.01,1.0E-12,1.07639104E-5,1.19599005E-6,3.8610061E-13,1.0E-10,2.4710538E-10};
        double[] b = {1000000,1,10000,1.0E-6,10.7639104167,1.1959900463,3.86100614E-7,1.0E-4,2.4710538E-4};
        double[] c = {100,1.0E-4,1,1.0E-10,0.00107639104,1.19599E-4,3.8610061E-11,1.0E-8,2.47105381E-8};
        double[] d = {1.0E12, 1000000,1.0E10,1,1.07639104167E7,1195990.0463,0.38610061414,100,274.105381467};
        double[] e = {92903.04,0.09290304,929.0304,9.290304E-8,1,0.11111111,3.58699208E-8,9.290304E-6,2.29568411E-5};
        double[] f = {836127.36,0.83612736,8361.2736,8.3612736E-7,9,1,3.22829287E-7,8.3612736E-5,2.0661157E-4};
        double[] g = {2.52999847032E12,2589998.47032,2.52999847032E10,2.58999847032,2.78785115139E7,3097612.39044,1,258.999847032,640.002560008};
        double[] h = {1.0E10,10000,1.0E8,0.01,107639.104167,11959.900463,0.00386100614,1,2.47105381467};
        double[] i = {4.0468564224E9,4046.8564224,4.0468564224E7,0.00404685642,43560,4840,0.00156249375,0.40468564224};
        if (masahat[from].equals(masahat[to])) {
            setM(get);
        } else if (masahat[from].equals("mm\u00B2")) {
            setM(get * a[to - 1]);
        } else if (masahat[from].equals("m\u00B2")) {
            setM(get * b[to]);
        } else if (masahat[from].equals("cm\u00B2")) {
            setM(get * c[to]);
        } else if (masahat[from].equals("km\u00B2")) {
            setM(get * d[to]);
        } else if (masahat[from].equals("ft\u00B2")) {
            setM(get * e[to]);
        } else if (masahat[from].equals("yd\u00B2")) {
            setM(get * f[to]);
        } else if (masahat[from].equals("mil\u00B2")) {
            setM(get * g[to]);
        } else if (masahat[from].equals("ha")) {
            setM(get * h[to]);
        } else if (masahat[from].equals("acre")) {
            setM(get * i[to]);
        }
    }

    private void getHajm(int from, int to, double get) {
            double[] ml = {0.001, 1000,1,1.0E-6,3.53146625E-5,2.1996925E-4,8.7987893E-4,0.00175975476,0.03519503328,2.6417205E-4,0.00105668814,0.00211337853,0.0338140565,0.00422675706,0.06762696964,0.20288502506};
            double[] l = {1000,1,1000000,100,0.001,0.3531466247,0.2199692483,0.87987892866,1.75975476058,35.1950332769,0.26417205236,1.05668814914,2.11337853146,33.8140565033,4.22675706291,67.6269696355,202.885025056};
            double[] mm3 = {0.001,1.0E-6,1,0.001,1.0E-9,3.53146625E-8,2.19969248E-7,8.79878929E-7,1.75975476E-6,3.51950333E-5,2.64172052E-7,1.05668815E-6,2.11337853E-6,3.38140565E-5,4.22675706E-6,6.7626969E-5,2.0288503E-4};
            double[] cm3 = {1,0.001,1000,1,1.0E-6,3.53146625E-5,2.1996925E-4,8.7987893E-4,0.00175975476,0.03519503328,2.6417205E-4,0.00105668815,0.00211337853,0.0338140565,0.00422675706,0.06762696964,0.20288502506};
            double[] m3 = {1000000,1000,1.0E9,1000000,1,35.3146624713,219.969248299,879.878928659,1759.75476058,35195.0332769,264.172052358,1056.68814914,2113.37853146,33814.0565033,4226.75706291,67626.9696366,202885.025056};
            double[] h1 = {28316.85,28.31685,2.831685E7,28316.85,0.02831685,1,6.2288362087,24.915399641,49.830711592,996.612478047,7.48052038082,29.9220798159,59.8442228684,957.507565895,119.68445737,1914.9825512,5745.06482177};
            double[] h2 = {4546.09, 4.54609,4546090,4546.09,0.00454609,0.16054363391,1,4.00000879879,8.00000351951,159.99978883,1.2009499255,4.80379942791,9.60760900806,153.721744129,19.2152180161,307.43829039,922.333583558};
            double[] h3 = {1136.52,1.13652,1136520,1136.52,0.00113652,0.04013582019,0.24999945008,1,1.99999648049,39.9998592199,0.30023682095,1.20094721526,2.40189696857,38.4303514971,4.80379393714,76.8594035301,230.582888677};
            double[] h4 = {568.261,0.568261,568261,568.261,5.68261E-4,0.02006794541,0.12499994501,0.50000087988,1,19.999964805,0.15011867465,0.60047466432,1.20095059766,19.2152095626,2.40190119533,38.429769392,115.291647224};
            double[] h5 = {28.4131,0.0284131,28413.1,28.4131,2.84131E-5,0.00100339904,0.00625000825,0.02500008799,0.05000008799,1,0.00750594694,0.3002378605,0.6004763555,0.96076216883,0.1200952711,1.92149185095,5.76459250543};
            double[] h10 = {3785.411784,3.785411784,3785411.784,3785.411784,0.003785411778,0.1336805947,0.83267418463,3.33070406504,6.66139640764,133.227693705,1,3.99999977176,8.000000799702,128.000127952,16.000015994,255.99527774,768.003364645};
            double[] h20 = {946.353,0.946353,946353,946.353,9.46353E-4,0.03342013677,0.20816855804,0.83267606377,1.66534919694,33.3069253267,0.25000001427,1,2.00000211338,32.0000338141,4.00000422676,63.9989855955,192.000852117};
            double[] h30 = {473.176,0.473176,473176,473.176,4.73176E-4,0.01671005073,0.10408416903,0.41633759195,0.83267371859,16.6534450658,0.12499987505,0.49999947166,1,16,2,31.9994589845,96.000324616};
            double[] h40 = {29.5735,0.0295735,29573.5,29.5735,2.95735E-5,0.00104437817,0.00650526056,0.0260210995,0.0524210741,1.04084031661,0.00781249219,0.03124996698,0.0625,1,0.125,0.99996618652,6.0000202885};
            double[] h50 = {236.588,0.236588,236588,236.588,2.36588E-4,0.00835502536,0.05204208452,0.20816879597,0.4163368593,8.32672253292,0.06249993752,0.24999973583,0.5,8,1,15.9997294921,48.0001622308};
            double[] h400 = {14.787,0.014787,14787,14.787,1.4787E-5,5.2219791E-4,0.00325268527,0.01301076972,0.02602149364,0.52042895707,0.00390631214,0.01562524766,0.03125052834,0.50000845351,0.06250105669,1,3.00006086551};
            double[] h500 = {4.9289,0.0049289,4928.9,4.9289,4.9289E-6,1.7406244E-4,0.00108420643,0.00433683525,0.00867365524,0.17347279952,0.00130207763,0.00520831022,0.01041663144,0.1666661031,0.2083326289,0.33332657064};
            if (hajm[from].equals(hajm[to])) {
                setM(get);
            } else if (hajm[from].equals("ml")) {
                setM(get * ml[to - 1]);
            } else if (hajm[from].equals("l")) {
                setM(get * l[to]);
            } else if (hajm[from].equals("mm\u00B3")) {
                setM(get * mm3[to]);
            } else if (hajm[from].equals("cm\u00B3")) {
                setM(get * cm3[to]);
            } else if (hajm[from].equals("m\u00B3")) {
                setM(get * m3[to]);
            } else if (hajm[from].equals("ft\u00B3")) {
                setM(get * h1[to]);
            } else if (hajm[from].equals("gal (UK)")) {
                setM(get * h2[to]);
            } else if (hajm[from].equals("qt (UK)")) {
                setM(get * h3[to]);
            } else if (hajm[from].equals("pt (UK)")) {
                setM(get * h4[to]);
            } else if (hajm[from].equals("fl.oz (UK)")) {
                setM(get * h5[to]);
            } else if (hajm[from].equals("gal (US)")) {
                setM(get * h10[to]);
            } else if (hajm[from].equals("qt (US)")) {
                setM(get * h20[to]);
            } else if (hajm[from].equals("pt (US)")) {
                setM(get * h30[to]);
            } else if (hajm[from].equals("fl.oz (US)")) {
                setM(get * h40[to]);
            } else if (hajm[from].equals("cup")) {
                setM(get * h50[to]);
            } else if (hajm[from].equals("tbsp")) {
                setM(get * h400[to]);
            } else if (hajm[from].equals("tsp")) {
                setM(get * h500[to]);
        }
    }

    private void getSorat(int from, int to, double get) {
        double[] MS = {2.23693629205, 3.59999712, 1.94384617179, 3.28083989501};
        double[] MPH = {0.44704, 1, 1.60934271253, 0.86897699264, 1.4666666667};
        double[] KMPH = {0.277778, 0.62137168933, 1, 0.53995770191, 0.91134514436};
        double[] KN = {0.514444, 1.15277845383, 1.8519969184, 1, 1.6870839895};
        double[] FTS = {0.3048, 0.68181818182, 1.09727912218, 0.59248431316};
        if (sorat[from].equals(sorat[to])) {
            setM(get);
        } else if (sorat[from].equals("m/s")) {
            setM(get * MS[to - 1]);
        } else if (sorat[from].equals("mph")) {
            setM(get * MPH[to]);
        } else if (sorat[from].equals("kmph")) {
            setM(get * KMPH[to]);
        } else if (sorat[from].equals("kn")) {
            setM(get * KN[to]);
        } else if (sorat[from].equals("ft/s")) {
            setM(get * FTS[to]);
        }
    }

    private void getVazn(int from, int to, double get) {;
        double[] KG = {2.204622621849, 1000000, 0.45359237, 1000,0.001};
        double[] LB = {0.45359237, 1, 453592.37, 16, 453.59237,0.00045359236996889};
        double[] MG = {1.0E-6, 2.204622621849E-6, 1, 3.527396194988E-5, 0.001,1E-9};
        double[] OUNCE = {0.028349523125, 0.0625, 28349.523125, 1, 28.349523125,0.000028349523124663};
        double[] G = {0.001, 0.002204622621849, 1000, 0.03527396194958,1,0.000001};
        double[] T = {1000,2204.62262185,1000000000,32150.74656863,1000000};
        if (vazn[from].equals(vazn[to])) {
            setM(get);
        } else if (vazn[from].equals("kg")) {
            setM(get * KG[to - 1]);
        } else if (vazn[from].equals("lb")) {
            setM(get * LB[to]);
        } else if (vazn[from].equals("mg")) {
            setM(get * MG[to]);
        } else if (vazn[from].equals("ounce")) {
            setM(get * OUNCE[to]);
        } else if (vazn[from].equals("g")) {
            setM(get * G[to]);
        }else if (vazn[from].equals("t")) {
            setM(get * T[to]);
        }
    }

    private void getTool(int from, int to, double get) {
        double[] MM = {0.1, 0.001, 1.0E-6, 0.0032808399, 6.21371192E-7, 0.03937007874, 0.0010936133, 1000000};
        double[] CM = {10, 1, 0.01, 1.0E-5, 0.03280839895, 6.21371192E-6, 0.3937007874, 0.01093613298, 1.0E7};
        double[] M = {1000, 100, 1, 0.001, 3.28083989501, 6.21237119E-4, 39.3700787402, 1.09361329834, 1.0E9};
        double[] KM = {1000000, 100000, 1000, 1, 3280.83989501, 0.62137119224, 39370.0787402, 1093.6132834, 1.0E12};
        double[] FT = {304.8, 30.48, 0.3048, 3.048E-4, 1, 1.8939394E-4, 12, 0.33333333333, 3.048E8};
        double[] MI = {1609344, 160934.4, 1609.344, 1.609344, 5280, 1, 63360, 1760, 1.609344E12};
        double[] IN = {25.4, 2.54, 0.0254, 2.54E-5, 0.08333333333, 1.057828283E-5, 1, 0.02777777778, 2.54E7};
        double[] YD = {914.4, 91.44,0.9144, 0.9144E-4, 3, 5.6818182E-4, 36, 1, 9.144E8};
        double[] NM = {1.0E-6, 1.0E-7, 1.0E-9, 1.0E-12, 3.2808399E-9, 6.2137119E-13, 3.93700787E-8, 1.0936133E-9};
        if (tool[from].equals(tool[to])) {
            setM(get);
        } else if (tool[from].equals("mm")) {
            setM(get * MM[to - 1]);
        } else if (tool[from].equals("cm")) {
            setM(get * CM[to]);
        } else if (tool[from].equals("m")) {
            setM(get * M[to]);
        } else if (tool[from].equals("km")) {
            setM(get * KM[to]);
        } else if (tool[from].equals("ft")) {
            setM(get * FT[to]);
        } else if (tool[from].equals("mi")) {
            setM(get * MI[to]);
        } else if (tool[from].equals("in")) {
            setM(get * IN[to]);
        } else if (tool[from].equals("yd")) {
            setM(get * YD[to]);
        } else if (tool[from].equals("nm")) {
            setM(get * NM[to]);
        }
    }

    private void getDama(int from, int to, double get) {
        double[] C = {33.8, 274.15, 493.47};
        double[] F = {-17.222222222222, 1, 255.92777777777775, 460.67};
        double[] K = {-272.15, -457.87, 1, 1.8};
        double[] R = {-272.59444444444443, -458.67, 0.55555555555556};
        if (dama[from].equals(dama[to])) {
            setM(get);
        } else if (dama[from].equals("C")) {
            setM(get * C[to - 1]);
        } else if (dama[from].equals("F")) {
            setM(get * F[to]);
        } else if (dama[from].equals("K")) {
            setM(get * K[to]);
        } else if (dama[from].equals("R")) {
            setM(get * R[to]);
        }
    }

    private void setM(double math) {
        Log.d("Abc", "math");
        String M = String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(math));
        if(M.equals("0")){
            M = String.valueOf(math);
        }
        setMath.setText(M);
        setMath.startAnimation(show);
    }


    ///////////////////////////////////////////////////////////////
    public double feetInchToCmConverter(double feet, double inch) {
        return (feet * 30.48) + (inch * 2.54);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page,menu);
        menu.findItem(R.id.item_help).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), MathBase.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

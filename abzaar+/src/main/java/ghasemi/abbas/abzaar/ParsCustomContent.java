package ghasemi.abbas.abzaar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;
import com.pushpole.sdk.NotificationButtonData;
import com.pushpole.sdk.NotificationData;
import com.pushpole.sdk.PushPole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ParsCustomContent {

    static {
        PushPole.setNotificationListener(new PushPole.NotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull NotificationData notificationData) {
                // receive
            }

            @Override
            public void onNotificationClicked(@NonNull NotificationData notificationData) {
                // click
            }

            @Override
            public void onNotificationButtonClicked(@NonNull NotificationData notificationData, @NonNull NotificationButtonData clickedButton) {
                // button click
            }

            @Override
            public void onCustomContentReceived(@NonNull JSONObject customContent) {
                // custom content (JSON) received
                ParsCustomContent.initialize(customContent);
            }

            @Override
            public void onNotificationDismissed(@NonNull NotificationData notificationData) {
                // dismissed
            }
        });
    }

    private Activity activity;
    @SuppressLint("StaticFieldLeak")
    private static ParsCustomContent parsCustomContent;

    private static ParsCustomContent getParsCustomContent() {
        if (parsCustomContent == null) {
            parsCustomContent = new ParsCustomContent();
        }
        return parsCustomContent;
    }

    private ParsCustomContent() {

    }

    private static void initialize(JSONObject jsonObject) {
        try {
            getParsCustomContent().parsJson(jsonObject);
        } catch (JSONException e) {
            // error pars json
        } catch (Exception e) {
            //
        }
    }

    public static void initialize(Activity activity) {
        getParsCustomContent().activity = activity;
        try {
            getParsCustomContent().run();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() throws JSONException, Exception {
        String json_array_dialog_ad = new TinyDB(Main.activity).getString("json_array_dialog_ad");
        if (!json_array_dialog_ad.isEmpty()) {
            JSONArray jsonArray = new JSONArray(json_array_dialog_ad);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    getParsCustomContent().createDialog(jsonObject.getString("title"),
                            jsonObject.getString("content"),
                            jsonObject.getString("button_name"),
                            jsonObject.getString("button_action"));
                }
                new TinyDB(Main.activity).putString("json_array_dialog_ad", "");
            }
        }
    }

    private void createDialog(String title, String content, String button_name, final String button_action) {
        new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(content)
                .setNegativeButton("بستن", null)
                .setPositiveButton(button_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String button;
                        switch (BuildVars.ADAD_MARKET) {
                            case 1:
                                button = "https://cafebazaar.ir/app/" + button_action;
                                break;
                            case 2:
                                button = "https://myket.ir/app/" + button_action;
                                break;
                            default:
                                button = "http://iranapps.ir/app/" + button_action;
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(button));
                        try {
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            ToastActivity.Toast(Main.activity,"error:: sorry!",0);
                        }
                    }
                }).show();
    }

    private void parsJson(JSONObject jsonObject) throws JSONException, Exception {
        if (jsonObject.has("is_dialog_ad") && jsonObject.getBoolean("is_dialog_ad")) {
            JSONArray jsonArray = new JSONArray();
            String json_array_dialog_ad =  new TinyDB(Main.activity).getString("json_array_dialog_ad");
            if (!json_array_dialog_ad.isEmpty()) {
                jsonArray = new JSONArray(json_array_dialog_ad);
            }
            jsonArray.put(jsonObject);
            new TinyDB(Main.activity).putString("json_array_dialog_ad", jsonArray.toString());
        }
    }

}

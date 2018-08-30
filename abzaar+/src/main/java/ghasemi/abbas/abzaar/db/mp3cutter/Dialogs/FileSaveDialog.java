/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ghasemi.abbas.abzaar.db.mp3cutter.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ghasemi.abbas.abzaar.R;

import java.util.ArrayList;

public class FileSaveDialog extends Dialog {

    // File kinds - these should correspond to the order in which
    // they're presented in the spinner control
    public static final int FILE_KIND_MUSIC = 0;
    public static final int FILE_KIND_ALARM = 1;
    public static final int FILE_KIND_NOTIFICATION = 2;
    public static final int FILE_KIND_RINGTONE = 3;

    private Spinner mTypeSpinner;
    private EditText mFilename;
    private Message mResponse;
    private String mOriginalName;
    private ArrayList<String> mTypeArray;
    private int mPreviousSelection;



    public FileSaveDialog(Context context, Resources resources, String originalName, Message response) {
        super(context,R.style.Theme_Dialog_Alert);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.arm_music_save);
        getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        setTitle("عنوان");

        mTypeArray = new ArrayList<>();
        mTypeArray.add("موسیقی");
        mTypeArray.add("هشدار");
        mTypeArray.add("اعلان");
        mTypeArray.add("آهنگ زنگ");

        mFilename = (EditText) findViewById(R.id.fileName);
        mOriginalName = originalName;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, mTypeArray);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mTypeSpinner = (Spinner) findViewById(R.id.ringtone_type);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(FILE_KIND_RINGTONE);
        mPreviousSelection = FILE_KIND_RINGTONE;

        setFilenameEditBoxFromName(false);

        mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View v,
                                       int position,
                                       long id) {
                setFilenameEditBoxFromName(true);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button save = (Button) findViewById(R.id.msave);
        save.setOnClickListener(saveListener);
        Button cancel = (Button) findViewById(R.id.mcancel);
        cancel.setOnClickListener(cancelListener);
        mResponse = response;
    }

    private void setFilenameEditBoxFromName(boolean onlyIfNotEdited) {
        if (onlyIfNotEdited) {
            CharSequence currentText = mFilename.getText();
            String expectedText = mOriginalName + " " +
                    mTypeArray.get(mPreviousSelection);

            if (!expectedText.contentEquals(currentText)) {
                return;
            }
        }

        int newSelection = mTypeSpinner.getSelectedItemPosition();
        String newSuffix = mTypeArray.get(newSelection);
        mFilename.setText(mOriginalName + " " + newSuffix);
        mPreviousSelection = mTypeSpinner.getSelectedItemPosition();
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View view) {
            mResponse.obj = mFilename.getText();
            mResponse.arg1 = mTypeSpinner.getSelectedItemPosition();
            mResponse.sendToTarget();
            dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View view) {
            dismiss();
        }
    };
}
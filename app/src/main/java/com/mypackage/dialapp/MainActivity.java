package com.mypackage.dialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView main_num_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_constraint_layout); // Instead of activity_main

        // Get permission from user
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        else {
            // Permission has been approved
            init();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check permission has been granted
        boolean granted = true;
        for (int res: grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        if (granted) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    void init() {
        if (!getElements() || !addListeners()) {
           System.out.println("Issues when adding app elements or adding listeners to elements");
        }
    }
    boolean getElements() {
        try {
            main_num_textview = findViewById(R.id.main_num_textview);
            return true;
        }
        catch (Exception e) {
            Toast.makeText(this, "Could not find top text view", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    void deleteLastDigit() {
        try {
            if (main_num_textview.getText().toString().equals("")) {
                return; // There is no text to delete
            }
            // Add text to current text displayed in main text box
            String current = main_num_textview.getText().toString();

            main_num_textview.setText(current.substring(0, current.length()-1));
        }
        catch (Exception e) {
            Toast.makeText(this, "Could not delete digit", Toast.LENGTH_SHORT).show();
        }
    }
    void makeCall() {
        if (main_num_textview.getText().equals("")) {
            Toast.makeText(this, "Enter a number first", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Make call
                String callNum = "tel:" + main_num_textview.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(callNum));
                startActivity(callIntent);
            }
            catch (Exception e) {
                Toast.makeText(getBaseContext(), "Could not process call", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void addText(View view) {
        // Add text to current text displayed in main text box
        String current = main_num_textview.getText().toString();
        String newDigit = ((Button) view).getText().toString();
        String result = current+newDigit;
        main_num_textview.setText(result);
    }
    boolean addListeners() {
        // Add listeners to digit buttons

        int orientation = this.getResources().getConfiguration().orientation;
        try {

            ConstraintLayout t = findViewById(R.id.main_layout);
            ArrayList<LinearLayout> rows = new ArrayList<>();

            // Add listeners (this is orientation dependent, due to the layout of elements)
            switch (orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    for (int i = 1; i < t.getChildCount(); i++) { // Note the difference in iteration scope
                        rows.add((LinearLayout) t.getChildAt(i));
                    }
                    for (LinearLayout tr : rows) {
                        for (int i = 0; i < tr.getChildCount(); i++) {
                            tr.getChildAt(i).setOnClickListener(this::addText);
                        }
                    }
                    break;
                default: // Assume portrait orientation
                    for (int i = 1; i < t.getChildCount()-2; i++) {
                        // Get all DIGIT rows (ignore other elements)
                        rows.add((LinearLayout) t.getChildAt(i));
                    }
                    // Iterate through all items in each row; overkill perhaps
                    for (LinearLayout tr : rows) {
                        for (int i = 0; i < tr.getChildCount(); i++) {
                            tr.getChildAt(i).setOnClickListener(this::addText);
                        }
                    }
                    break;
            }

        }
        catch (Exception e) {
            Toast.makeText(this, "Could not add listeners to digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add listeners to (I/O) buttons
        try {
            FloatingActionButton callBtn = (FloatingActionButton) findViewById(R.id.btn_call);
            Button deleteBtn = (Button) findViewById(R.id.btn_delete);
            callBtn.setOnClickListener( view -> makeCall());
            deleteBtn.setOnClickListener(view -> deleteLastDigit());
            deleteBtn.setOnLongClickListener(view -> {
                    main_num_textview.setText("");
                    return false;
            });
        }
        catch (Exception e) {
            Toast.makeText(this, "Could not add listeners to I/O buttons", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
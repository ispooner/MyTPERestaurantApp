package com.example.mytperestaurantapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytperestaurantapp.R;
import com.example.mytperestaurantapp.presenter.Contract;
import com.example.mytperestaurantapp.presenter.Executor;
import com.example.mytperestaurantapp.tasks.Customer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Contract.View {

    Contract.Presenter presenter;

    private static final int PERMISSION_REQUEST_CODE = 707;

    ProgressBar[] bars = new ProgressBar[4];
    TextView[] names = new TextView[4];
    TextView[] progs = new TextView[4];
    Boolean[] finished = new Boolean[4];

    Button custButton;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new Executor(this, handler);

        custButton = findViewById(R.id.customer_button);

        bars[0] = findViewById(R.id.progressBar0);
        names[0] = findViewById(R.id.textView);
        progs[0] = findViewById(R.id.PercentView0);
        bars[1] = findViewById(R.id.progressBar1);
        names[1] = findViewById(R.id.textView2);
        progs[1] = findViewById(R.id.PercentView1);
        bars[2] = findViewById(R.id.progressBar2);
        names[2] = findViewById(R.id.textView3);
        progs[2] = findViewById(R.id.PercentView2);
        bars[3] = findViewById(R.id.progressBar3);
        names[3] = findViewById(R.id.textView4);
        progs[3] = findViewById(R.id.PercentView3);

        for(int i = 0; i < 4; i++) {
            bars[i].setVisibility(View.INVISIBLE);
            names[i].setVisibility(View.INVISIBLE);
            progs[i].setVisibility(View.INVISIBLE);
            finished[i] = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(permissions[0].equals(Manifest.permission.CAMERA)) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "CAMERA PERMISSION RECEIVED", Toast.LENGTH_SHORT);
                }
                else {
                    Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public void onClick(View view) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }

        custButton.setVisibility(View.GONE);
        presenter.generateNewCustomers();
    }

    @Override
    public void receiveNewCustomers(List<Customer> customers) {
        for(int i = 0; i < customers.size(); i++) {
            Customer cust = customers.get(i);
            bars[i].setVisibility(View.VISIBLE);
            bars[i].setProgress(0);
            names[i].setVisibility(View.VISIBLE);
            names[i].setText(cust.name);
            progs[i].setVisibility(View.VISIBLE);
            progs[i].setText("0/" + (int)cust.time);
            finished[i] = false;
        }

        presenter.runCustomers(customers);
    }

    @Override
    public void setProgress(int index, float amount, float total) {
        bars[index].setProgress((int)(amount/total * 100));
        progs[index].setText((int) amount + "/" + (int)total);
    }

    @Override
    public void setFinished(int index) {
        finished[index] = true;

        bars[index].setVisibility(View.INVISIBLE);
        names[index].setVisibility(View.INVISIBLE);
        progs[index].setVisibility(View.INVISIBLE);

        for(int i = 0; i < 4; i++) {
            if(!finished[i]) {
                return;
            }
        }
        custButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void success(Boolean successful) {
        Log.d("TAG_S", "success: Started the threadpool");
    }
}

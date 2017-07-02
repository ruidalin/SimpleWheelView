package com.dalin.simplewheelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WheelView mWheelView;
    private List<String> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWheelView = (WheelView) findViewById(R.id.wheelView);
        for (int i = 0; i < 20; i++) {
            mItems.add("item " + i);
        }
        mWheelView.setItems(mItems);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn1: {
                Toast.makeText(this,
                        mWheelView.getSelectedIndex() + "=" + mWheelView.getSelected(),
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_btn2: {
                mWheelView.setSelected(mWheelView.getSelectedIndex() - 1);
                break;
            }
            case R.id.main_btn3: {
                mWheelView.setSelected(mWheelView.getSelectedIndex() + 1);

                break;
            }
        }

    }
}

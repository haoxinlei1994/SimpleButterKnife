package com.mrh.simplebutterknife;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mrh.knife_annotation.BindView;
import com.mrh.knife_api.SimpleButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_hello)
    TextView mHelloTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleButterKnife.bind(this, this);
        mHelloTv.setText("hello world!");
    }
}

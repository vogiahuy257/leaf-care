package com.example.green;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private TextView option1, option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);

        option1.setOnClickListener(v ->
                Toast.makeText(this, "Mở cài đặt ngôn ngữ", Toast.LENGTH_SHORT).show()
        );

        option2.setOnClickListener(v ->
                Toast.makeText(this, "Chuyển sang chế độ tối", Toast.LENGTH_SHORT).show()
        );
    }
}

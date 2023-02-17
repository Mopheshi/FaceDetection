package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import morpheus.softwares.facedetection.R;

public class MainActivity extends AppCompatActivity {
    private Button load, capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load = findViewById(R.id.importFromDevice);
        capture = findViewById(R.id.captureFromCamera);

        load.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ImportActivity.class)));
        capture.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                CaptureActivity.class)));
    }
}
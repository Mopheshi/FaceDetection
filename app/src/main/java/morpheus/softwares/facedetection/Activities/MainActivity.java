package morpheus.softwares.facedetection.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import morpheus.softwares.facedetection.R;

public class MainActivity extends AppCompatActivity {
    Button load, capture, video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load = findViewById(R.id.importFromDevice);
        capture = findViewById(R.id.captureFromCamera);
        video = findViewById(R.id.videoFromCamera);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            Snackbar.make(findViewById(R.id.main), "Please grant app permissions", Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 5);
        }

        load.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ImportActivity.class)));
        capture.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CaptureActivity.class)));
        video.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, VideoActivity.class)));
    }
}
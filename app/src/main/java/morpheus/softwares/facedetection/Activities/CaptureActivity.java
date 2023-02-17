package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import morpheus.softwares.facedetection.R;

public class CaptureActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 500;
    private ImageView imageView;
    private Button capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imageView = findViewById(R.id.imageView);
        capture = findViewById(R.id.capture);

        capture.setOnClickListener(v -> {
            Intent intent = new Intent()
        });
    }
}
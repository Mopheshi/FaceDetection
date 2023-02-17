package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import morpheus.softwares.facedetection.R;

public class CaptureActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 10;
    private ImageView imageView;
    private Button capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imageView = findViewById(R.id.imageView);
        capture = findViewById(R.id.capture);

        capture.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, VIDEO_CAPTURE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
package morpheus.softwares.facedetection.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import morpheus.softwares.facedetection.R;

public class RealTimeCaptureActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 500;
    private VideoView videoView;
    private Button capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_capture);

        videoView = findViewById(R.id.videoView);
        capture = findViewById(R.id.capture);


    }
}
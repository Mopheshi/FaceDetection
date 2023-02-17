package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

import morpheus.softwares.facedetection.R;

public class CaptureActivity extends AppCompatActivity {
    private static final int SCALING_FACTOR = 10;   // Make detecting images smaller thereby faster
    BitmapDrawable bitmapDrawable;
    private FaceDetector detector;
    private static final int VIDEO_CAPTURE = 10;
    private ImageView imageView, detected;
    private Button detect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL).build();

        detector = FaceDetection.getClient(faceDetectorOptions);

        imageView = findViewById(R.id.imageView);
        detected = findViewById(R.id.detected);
        detect = findViewById(R.id.detect);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, VIDEO_CAPTURE);

        bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        detect.setOnClickListener(v -> analyzeImage(bitmapDrawable.getBitmap()));
    }

    private void analyzeImage(Bitmap bitmap) {
        // Reduce bitmap to SCALING_FACTOR for faster processing
        Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / SCALING_FACTOR,
                bitmap.getHeight() / SCALING_FACTOR, false);

        InputImage inputImage = InputImage.fromBitmap(smallBitmap, 0);

        detector.process(inputImage).addOnSuccessListener(faces -> {
            for (Face face : faces) {
                Rect rect = face.getBoundingBox();  // Get detected faces as rectangles
                rect.set(rect.left * SCALING_FACTOR, rect.top * (SCALING_FACTOR - 1),
                        rect.right * SCALING_FACTOR,
                        rect.bottom * (SCALING_FACTOR + 90));
            }

            cropDetectedFaces(bitmap, faces);
            Toast.makeText(getApplicationContext(), String.format("Detected %s faces...",
                    faces.size()), Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                String.format("Detection failed due to: %s.." +
                        ".", e.getMessage()), Toast.LENGTH_LONG).show());
    }

    private void cropDetectedFaces(Bitmap bitmap, @NonNull List<Face> faces) {
        // Crop the first detected face | Multiple faces can be managed with a for loop...
        Rect rect = faces.get(0).getBoundingBox();

        int x = Math.max(rect.left, 0);
        int y = Math.max(rect.top, 0);
        int width = rect.width();
        int height = rect.height();
        Bitmap cropped = Bitmap.createBitmap(bitmap, x, y,
                (x + width > bitmap.getWidth()) ? bitmap.getWidth() - x : width,
                (y + height > bitmap.getHeight()) ? bitmap.getHeight() - y : height);

        detected.setImageBitmap(cropped);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
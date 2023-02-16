package com.example.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int SCALING_FACTOR = 10;   // Make detecting images smaller thereby faster
    Button detectFace;
    private ImageView originalImage, detectedImage;
    private FaceDetector detector;
    private Bitmap bitmap;
    //    private Uri imageUri;
    private ArrayList<Integer> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL).build();

        detector = FaceDetection.getClient(faceDetectorOptions);

        originalImage = findViewById(R.id.originalImage);
        detectedImage = findViewById(R.id.detectedFace);
        detectFace = findViewById(R.id.detectFace);

        images = new ArrayList<>();
        images.add(R.drawable.one);
        images.add(R.drawable.two);
        images.add(R.drawable.three);
        images.add(R.drawable.four);
        images.add(R.drawable.five);

        bitmap = BitmapFactory.decodeResource(getResources(),
                images.get(new Random().nextInt(images.size())));
        originalImage.setImageBitmap(bitmap);

        originalImage.setOnClickListener(v -> {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    images.get(new Random().nextInt(images.size())));
            originalImage.setImageBitmap(bitmap);
        });

        detectFace.setOnClickListener(v -> analyzeImage(bitmap));
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

        detectedImage.setImageBitmap(cropped);
    }

            /*
            // Bitmap from drawable folder
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.five);

            // Bitmap from device folder
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Bitmap from ImageView/Web/
            BitmapDrawable bitmapDrawable = (BitmapDrawable) originalImage.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();URL
            */
}
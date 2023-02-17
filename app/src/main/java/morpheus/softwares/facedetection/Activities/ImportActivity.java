package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import morpheus.softwares.facedetection.R;

public class ImportActivity extends AppCompatActivity {
    private static final int SCALING_FACTOR = 10;   // Make detecting images smaller thereby faster
    Button detectFace;
    private ImageView originalImage, detectedImage;
    private FaceDetector detector;
    BitmapDrawable bitmapDrawable;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL).build();

        detector = FaceDetection.getClient(faceDetectorOptions);

        originalImage = findViewById(R.id.originalImage);
        detectedImage = findViewById(R.id.detectedFace);
        detectFace = findViewById(R.id.detectFace);

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.one);
        images.add(R.drawable.two);
        images.add(R.drawable.three);
        images.add(R.drawable.four);
        images.add(R.drawable.five);

//        Randomly load Bitmap from drawable folder
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images.get(new Random().nextInt(images.size())));
        originalImage.setImageBitmap(bitmap);

        originalImage.setOnClickListener(v -> {
//            Randomly load Bitmap from drawable folder
//            bitmap = BitmapFactory.decodeResource(getResources(),
//                    images.get(new Random().nextInt(images.size())));
//            originalImage.setImageBitmap(bitmap);

//            Load Bitmap from device folder

            Intent intent = new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*");
            startActivityForResult(intent, 1);
        });

        bitmapDrawable = (BitmapDrawable) originalImage.getDrawable();
        detectFace.setOnClickListener(v -> analyzeImage(bitmapDrawable.getBitmap()));
//        try {
//            SharedPreferences sharedPreferences = getSharedPreferences("Uri", MODE_PRIVATE);
//            Uri uri = Uri.parse(sharedPreferences.getString("uri", String.valueOf(imageUri)));
//
//            Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//            detectFace.setOnClickListener(v -> analyzeImage(b));
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                imageUri = data.getData();
                originalImage.setImageURI(imageUri);

                SharedPreferences sharedPreferences = getSharedPreferences("Uri", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("uri", String.valueOf(imageUri));
                myEdit.apply();
            }
        }
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
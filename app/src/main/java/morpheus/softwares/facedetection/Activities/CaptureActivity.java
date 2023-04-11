package morpheus.softwares.facedetection.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
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
    Button detect;
    private static final int VIDEO_CAPTURE = 10;
    private ImageView imageView, detected;
    //    BitmapDrawable bitmapDrawable;
    private FaceDetector detector;
    //    private String currentPath;
    Bitmap bitmap;

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

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, VIDEO_CAPTURE);

//        bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
//        detect.setOnClickListener(v -> analyzeImage(bitmapDrawable.getBitmap()));

        detect.setOnClickListener(v -> {
//            String fileName = "photo";
//            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            File directory = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            try {
//                File file = File.createTempFile(fileName, ".jpg", directory);
//                currentPath = file.getAbsolutePath();
//
//                Uri imageUri = FileProvider.getUriForFile(CaptureActivity.this, "morpheus" +
//                        ".softwares.facedetection.fileprovider", file);
//
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, VIDEO_CAPTURE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            analyzeImage(bitmap);
        });
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
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(),
                    String.format("Detection failed due to: %s.." +
                            ".", e.getMessage()), Toast.LENGTH_LONG).show();
            System.out.println(e.getMessage());
        });
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

            // Create a copy of the resultant image data with a better quality...
//            Bitmap bitmap = BitmapFactory.decodeFile(currentPath);
//            Matrix matrix = new Matrix();
            // Image rotation by 90 degrees...
//            matrix.postRotate(90);
//            Bitmap photo = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//                    matrix, true);

            // Get the resultant 'data' with compressed poor quality image...
            bitmap = (Bitmap) data.getExtras().get("data");

//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
            imageView.setImageBitmap(getResizedBitmap(bitmap));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Resizes a bitmap in by creating a new bitmap with dimensions less than or equal to 32 pixels
     * {@code @BingChat}
     */
    public Bitmap getResizedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > 32 || height > 32) {
            float scaleWidth = ((float) 32) / width;
            float scaleHeight = ((float) 32) / height;

            // Create a Matrix for the manipulation
            Matrix matrix = new Matrix();

            // Resize the Bitmap
            matrix.postScale(scaleWidth, scaleHeight);

            // Recreate the new Bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            bitmap.recycle();
            return resizedBitmap;
        } else {
            return bitmap;
        }
    }
}
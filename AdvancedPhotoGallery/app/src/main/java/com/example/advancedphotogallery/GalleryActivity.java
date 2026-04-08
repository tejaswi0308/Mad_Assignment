package com.example.advancedphotogallery;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.core.content.FileProvider;

public class GalleryActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int FOLDER_PICK_REQUEST = 103;

    private File currentPhotoFile;
    private static final String PREFS = "gallery_prefs";
    private static final String KEY_FOLDER = "last_folder";
    private static final String SAVE_FOLDER_NAME = "PhotoGalleryApp";

    private TextView tvCurrentFolder;
    private File saveFolder;
    private File viewFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        saveFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), SAVE_FOLDER_NAME);
        if (!saveFolder.exists()) saveFolder.mkdirs();

        String saved = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_FOLDER, null);
        viewFolder = (saved != null) ? new File(saved) : saveFolder;

        tvCurrentFolder = findViewById(R.id.tvCurrentFolder);
        updateLabel();

        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        findViewById(R.id.btnChooseFolder).setOnClickListener(v -> pickFolder());

        findViewById(R.id.btnViewGallery).setOnClickListener(v -> {
            Intent intent = new Intent(this, GridActivity.class);
            intent.putExtra("folder_path", viewFolder.getAbsolutePath());
            startActivity(intent);
        });
    }

    private void openCamera() {
        try {
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            File file = new File(saveFolder, "IMG_" + time + ".jpg");

            Uri photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            currentPhotoFile = file;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pickFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, FOLDER_PICK_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            findViewById(android.R.id.content).postDelayed(() -> {
                if (currentPhotoFile != null
                        && currentPhotoFile.exists()
                        && currentPhotoFile.length() > 0) {
                    Toast.makeText(this, "✅ Full quality photo saved!",
                            Toast.LENGTH_SHORT).show();
                    viewFolder = saveFolder;
                    updateLabel();
                } else {
                    Toast.makeText(this, "Photo not saved", Toast.LENGTH_SHORT).show();
                }
            }, 500);
        }

        if (requestCode == FOLDER_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            viewFolder = resolveUriToFile(treeUri);
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit().putString(KEY_FOLDER, viewFolder.getAbsolutePath()).apply();
            updateLabel();
            Toast.makeText(this, "View folder: " + viewFolder.getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private File resolveUriToFile(Uri treeUri) {
        try {
            String path = treeUri.getPath();
            if (path != null && path.contains(":")) {
                String relative = path.split(":")[1];
                File folder = new File(Environment.getExternalStorageDirectory(), relative);
                if (!folder.exists()) folder.mkdirs();
                return folder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveFolder;
    }   

    private void updateLabel() {
        tvCurrentFolder.setText("Saving to: Pictures/" + SAVE_FOLDER_NAME
                + "\nViewing: " + viewFolder.getAbsolutePath());
    }
}
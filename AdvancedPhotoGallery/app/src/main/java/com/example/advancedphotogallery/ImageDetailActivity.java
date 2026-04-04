package com.example.advancedphotogallery;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.Date;

public class ImageDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ✅ Fixed: was "path", must match what GalleryActivity sends: "image_path"
        String path = getIntent().getStringExtra("image_path");

        if (path == null) {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File file = new File(path);

        ImageView iv = findViewById(R.id.detailImage);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPath = findViewById(R.id.tvPath);
        TextView tvSize = findViewById(R.id.tvSize);
        TextView tvDate = findViewById(R.id.tvDate);

        iv.setImageBitmap(BitmapFactory.decodeFile(path));
        tvName.setText("Name: " + file.getName());
        tvPath.setText("Path: " + file.getAbsolutePath());
        tvSize.setText("Size: " + (file.length() / 1024) + " KB");
        tvDate.setText("Date: " + new Date(file.lastModified()).toString());

        findViewById(R.id.btnDelete).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Image")
                        .setMessage("Are you sure you want to permanently delete this image?")
                        .setPositiveButton("Delete", (d, w) -> {
                            if (file.delete()) {
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                finish(); // ✅ Goes back to gallery, onResume refreshes grid
                            } else {
                                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );
    }
}
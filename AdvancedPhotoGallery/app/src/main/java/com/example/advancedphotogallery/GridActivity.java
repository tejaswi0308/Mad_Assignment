package com.example.advancedphotogallery;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GridActivity extends AppCompatActivity {

    private GridView gridView;
    private String folderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        folderPath = getIntent().getStringExtra("folder_path");
        gridView = findViewById(R.id.gridViewDisplay);
        TextView tvFolderPath = findViewById(R.id.tvFolderPath);

        if (folderPath == null) {
            finish();
            return;
        }

        tvFolderPath.setText(folderPath);
        loadImages();
    }

    private void loadImages() {
        File folder = new File(folderPath);
        File[] files = folder.listFiles(f -> {
            String name = f.getName().toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
        });

        ArrayList<File> imageList = new ArrayList<>();
        if (files != null) {
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            imageList.addAll(Arrays.asList(files));
        }

        gridView.setAdapter(new ImageAdapter(this, imageList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages(); // ✅ Just reload images, no recreate()
    }
}
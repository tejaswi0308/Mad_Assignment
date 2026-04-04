package com.example.advancedphotogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<File> images;

    public ImageAdapter(Context context, ArrayList<File> images) {
        this.context = context;
        this.images = images;
    }

    @Override public int getCount() { return images.size(); }
    @Override public Object getItem(int position) { return images.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        String filePath = images.get(position).getAbsolutePath();

        // ✅ Load scaled-down thumbnail to avoid OOM crashes
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageDetailActivity.class);
            intent.putExtra("image_path", filePath); // ✅ Fixed key to "image_path"
            context.startActivity(intent);
        });

        return imageView;
    }
}
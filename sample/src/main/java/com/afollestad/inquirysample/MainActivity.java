package com.afollestad.inquirysample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.inquiry.Inquiry;
import com.afollestad.inquiry.callbacks.GetCallback;
import com.afollestad.inquirysample.reference.ReferenceActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity {

    private MainAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Inquiry.init(this, "test_db", 1);
        setContentView(R.layout.activity_main);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        if (list != null){
            mAdapter = new MainAdapter();
            list.setLayoutManager(new LinearLayoutManager(this));
            list.setAdapter(mAdapter);
        }

        reload();
    }

    private void reload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 69);
            return;
        }

        Inquiry.get().selectFrom(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Photo.class)
                .sort(String.format("%s DESC", MediaStore.Images.Media.DATE_MODIFIED))
                .all(new GetCallback<Photo>() {
                    @Override
                    public void result(@Nullable Photo[] result) {
                        mAdapter.setPhotos(result);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            Inquiry.deinit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            reload();
        else
            Toast.makeText(this, "Permission is needed in order for the sample to work.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_references){
            startActivity(new Intent(this, ReferenceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
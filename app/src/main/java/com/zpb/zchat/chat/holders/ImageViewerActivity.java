package com.zpb.zchat.chat.holders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zpb.zchat.R;

public class ImageViewerActivity extends Activity
{
    private ImageView imageView, back;
    private String currUser,othUser;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        Intent intentReceived = getIntent();
        Bundle data = intentReceived.getExtras();
        if (data != null) {
            currUser = data.getString("curUser");
            othUser = data.getString("othUser");
        }

        imageView = findViewById(R.id.image_viewer);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        imageUrl = getIntent().getStringExtra("url");

        Picasso.get().load(imageUrl).into(imageView);
    }
}

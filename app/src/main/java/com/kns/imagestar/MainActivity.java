package com.kns.imagestar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Button button,save;
    Bitmap bmCombine,bmp1,bmpFinal;
    ImageView imageView,tshirt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        button = (Button)findViewById(R.id.button);
        save = (Button)findViewById(R.id.save);
        imageView = (ImageView) findViewById(R.id.result);
        tshirt = (ImageView) findViewById(R.id.tshirt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StickerImageView iv_sticker = new StickerImageView(MainActivity.this);
                iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.c10));
                relativeLayout.addView(iv_sticker);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.destroyDrawingCache();

                relativeLayout.buildDrawingCache();
                bmCombine = relativeLayout.getDrawingCache();
                tshirt.buildDrawingCache();
                bmp1 = tshirt.getDrawingCache();
                new BackgroundTask().execute();

            }
        });
    }
    private Bitmap findDifference(Bitmap bmp1, Bitmap bmCombine) {
        Bitmap bmp = bmCombine.copy(bmCombine.getConfig(), true);

        if (bmp1.getWidth() != bmCombine.getWidth()
                || bmp1.getHeight() != bmCombine.getHeight()) {
            return null;
        }

        for (int i = 0; i < bmp1.getWidth(); i++) {
            for (int j = 0; j < bmp1.getHeight(); j++) {

                int firstImagePixel = bmp1.getPixel(i,j);

                if (firstImagePixel == Color.TRANSPARENT){
                    bmp.setPixel(i, j, Color.TRANSPARENT);
                }
            }
        }


        return bmp;
    }

    private class BackgroundTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Processing Image, please wait.");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);

                }
            });

            dialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            while ( running) {
                try {
                    //Thread.sleep(5000);
                    if (isCancelled()) {
                        return null;
                    }

                    bmpFinal = findDifference(bmp1,bmCombine);
                    break;
                    //findDifference();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            running  = false;
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (bmpFinal != null){
                imageView.setImageBitmap(bmpFinal);
            }
            else {
                Toast.makeText(getApplicationContext(),"No Image Return ",Toast.LENGTH_SHORT).show();
            }

        }


    }

}

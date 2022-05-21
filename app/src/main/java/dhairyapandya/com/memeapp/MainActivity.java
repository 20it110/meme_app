package dhairyapandya.com.memeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.ReferenceQueue;

import dhairyapandya.com.memeapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
Button Share,Next;
ImageView memes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff00FFED));
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.white)));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#815700")));
        getmeme();


        Share=findViewById(R.id.button);
        Next=findViewById(R.id.button2);
        memes=findViewById(R.id.meme);


        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getmeme();
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharememe();
            }
        });
    }

    private void getmeme() {
        String url = "https://meme-api.herokuapp.com/gimme";

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
progressBar.setVisibility(View.VISIBLE);
//        memes.setVisibility(View.GONE);


        RequestQueue que = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imgurl = response.getString("url");
                            Glide.with(getApplicationContext()).load(imgurl).into(binding.meme);
                            memes.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
que.add(jsonObjectRequest);
    }

    private void sharememe() {
Bitmap image = getBitmapFromView(binding.meme);
shareImageAndText(image);
    }

    private void shareImageAndText(Bitmap image) {
        Uri uri = getImageToShare(image);
        Intent intent = new Intent(Intent.ACTION_SEND);

        //putting the uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM,uri);

//        //Add the message of happy birthday
//        intent.putExtra(Intent.EXTRA_TEXT,msg );

        //setting type of image
        intent.setType("image/png");

        //calling startActivity to share
        startActivity(Intent.createChooser(intent, "Share Image Via:"));
    }

    private Uri getImageToShare(Bitmap image) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try{

            imageFolder.mkdirs();
            File file = new File(imageFolder, "meme.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this,"dhairyapandya.com.memeapp.fileProvider",file);

        }catch (Exception e){
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with same height and width
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the background view of layout
        Drawable background = view.getBackground();
        if(background != null){
            background.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }
        //draw the view on canvas
        view.draw(canvas);

        return returnedBitmap;
    }
}
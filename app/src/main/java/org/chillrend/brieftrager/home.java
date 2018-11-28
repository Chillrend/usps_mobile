package org.chillrend.brieftrager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String drawerUname, drawerEmail, homeName, uerel, file, pathz;
    private TextView emailView, unameView, nameView;
    private DrawerLayout mDrawerLayout;
    private SliderLayout mSlider;
    private Bitmap userpp;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolber);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);



        mSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String,String> urlMaps = new HashMap<String, String>();
        urlMaps.put("Boxes Fee in USPS","http://192.168.43.14/brieftrager/boxes.png");
        urlMaps.put("Florence Hurricane Announcement","http://192.168.43.14/brieftrager/florence.jpg");
        urlMaps.put("Rates UP in 2019","http://192.168.43.14/brieftrager/ratesup.jpg");
        urlMaps.put("Services Continue Despite of Wildfire","http://192.168.43.14/brieftrager/reachesyou.jpg");

        for(String name : urlMaps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);

            textSliderView.description(name).image(urlMaps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);

            mSlider.addSlider(textSliderView);
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        }

//        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
//        TextView abTitle = (TextView) findViewById(titleId);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        abTitle.setGravity(Gravity.CENTER_HORIZONTAL);
//        abTitle.setWidth(displayMetrics.widthPixels);
//        getActionBar().setTitle("i'm center now");

        setNavigationViewListener();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        nameView = (TextView) findViewById(R.id.viewUsername);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navbar);
        View vi = navigationView.getHeaderView(0);

        emailView = (TextView) vi.findViewById(R.id.drawerUemail);
        unameView = (TextView) vi.findViewById(R.id.drawerUname);

        uerel = sharedPreferences.getString("userpicture", "http://192.168.43.14/post_pp/def.png");

        GetBitmapFromURLAsync getBitmapFromURLAsync = new GetBitmapFromURLAsync();
        getBitmapFromURLAsync.execute(uerel);

        if(!sharedPreferences.contains("username")){
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        }else{
            drawerUname = sharedPreferences.getString("username","No User");
            drawerEmail = sharedPreferences.getString("email","No User");
            homeName = sharedPreferences.getString("name","No User");
            nameView.setText(homeName);
            emailView.setText(drawerEmail);
            unameView.setText(drawerUname);
        }
    }

    private void loadImageFromStorageToView(String path, String filename)
    {

        try {
            File f=new File(path, filename);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            NavigationView navigationView = (NavigationView) findViewById(R.id.navbar);
            View vi = navigationView.getHeaderView(0);

            ImageView img=(ImageView) vi.findViewById(R.id.drawerUserpp);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navbar);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_power: {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intention = new Intent(home.this, login.class);
                startActivity(intention);
                //do somthing
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop(){
        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params){
            return getBitmapFromURL(params[0]);
        }

        protected Bitmap getBitmapFromURL(String src){
            try{
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(is);
                return myBitmap;
            }catch(IOException io){
                io.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap image){
            userpp = image;

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String path = saveToInternalStorage(image);

            editor.putString("filename", uerel.substring(uerel.lastIndexOf("/") + 1));
            editor.putString("path", path);

            file = uerel.substring(uerel.lastIndexOf("/") + 1);
            pathz = path;

            editor.commit();

            loadImageFromStorageToView(path, file);

        }

    }

    private String saveToInternalStorage(Bitmap image){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String fileName = uerel.substring(uerel.lastIndexOf("/") + 1);

        File myPath = new File(directory, fileName);

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                fos.close();
            }catch (IOException io){
                io.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}

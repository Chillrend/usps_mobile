package org.chillrend.brieftrager;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.HashMap;

public class home extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        urlMaps.put("demo","http://192.168.43.14/brieftrager/header.jpg");
        urlMaps.put("demo","http://192.168.43.14/brieftrager/header.jpg");
        urlMaps.put("demo","http://192.168.43.14/brieftrager/header.jpg");
        urlMaps.put("demo","http://192.168.43.14/brieftrager/header.jpg");

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


        NavigationView navigationView = findViewById(R.id.navbar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);
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
}

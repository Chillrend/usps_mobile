package org.chillrend.brieftrager;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.chillrend.brieftrager.sapot.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class searchZip extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String zip;
    JSONParser jsonParser = new JSONParser();
    JSONArray zipArray = null;
    String URL = "http://192.168.43.14/brieftrager/rest/api/search_zip.php";
    String retZip, retCity, retCounty, retState, retStateAbbr;
    Double retLatitude, retLongitude;
    EditText zipSearch;
    TextView cityView, countyView, stateView, zipView;
    ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_zip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        zipSearch = (EditText) findViewById(R.id.searchForm);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        cityView = (TextView) findViewById(R.id.cityView);
        countyView = (TextView) findViewById(R.id.countyView);
        stateView = (TextView) findViewById(R.id.stateView);
        zipView = (TextView) findViewById(R.id.zipView);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new searchFromRESTAsync().execute();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(40.8154, -73.0451);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in America"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private class searchFromRESTAsync extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            zip = zipSearch.getText().toString();

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("zip", zip));

            JSONObject json = jsonParser.makeHttpRequest(URL,"GET", param);

            try{
                int success = json.getInt("success");

                if(success == 1){
                    zipArray = json.getJSONArray("zipData");
                    for(int i=0; i<zipArray.length(); i++){
                        JSONObject c = zipArray.getJSONObject(i);

                        retZip = c.getString("zip");
                        retCity = c.getString("city");
                        retState = c.getString("state");
                        retStateAbbr = c.getString("state_abbr");
                        retCounty = c.getString("county");
                        retLatitude = c.getDouble("latitude");
                        retLongitude = c.getDouble("longitude");
                    }
                }else{
                    stateView.setText("-");
                    zipView.setText("-");
                    countyView.setText("-");
                    cityView.setText("-");
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String wew){
            cityView.setText(retCity);
            String stateConcat = retState + ", " + retStateAbbr;
            stateView.setText(stateConcat);
            zipView.setText(retZip);
            countyView.setText(retCounty);

            LatLng marker = new LatLng(retLatitude, retLongitude);
            mMap.addMarker(new MarkerOptions().position(marker).title(retZip));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        }
    }
}

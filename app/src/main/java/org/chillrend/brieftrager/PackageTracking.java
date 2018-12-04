package org.chillrend.brieftrager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.Result;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.chillrend.brieftrager.sapot.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PackageTracking extends AppCompatActivity {

    public static TextView tvResult;
    private String awb;
    JSONParser jsonParser = new JSONParser();
    JSONArray shipmentArray = null;
    String URL = "http://192.168.1.7:1945/brieftrager/rest/api/search_shipment.php";
    String retAwb, sender, recipient, senderTelp, recipientTelp, fromAddress, toAddress,
            fromCity, fromState, fromAbbr, toCity, toState, toAbbr, nowCity, nowState, nowAbbr, status;
    int nowZip, weight, toZip, fromZip;

    EditText zipSearch;
    TextView cityView, countyView, stateView, zipView;
    ImageButton btnSearch;
    Button btnDeatail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_tracking);

        tvResult = findViewById(R.id.tvResult);

        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Scanner.class);
                startActivity(i);
            }
        });
        tvResult.setText(Scanner.txtResult);

        btnDeatail = findViewById(R.id.btndeatil);

        btnDeatail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new searchFromRESTAsync().execute();
            }
        });
    }

    private class searchFromRESTAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            awb = tvResult.getText().toString();

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("awb", awb));

            JSONObject json = jsonParser.makeHttpRequest(URL,"GET", param);

            try{
                int success = json.getInt("success");

                if(success == 1){
                    shipmentArray = json.getJSONArray("trackingData");
                    for(int i=0; i<shipmentArray.length(); i++){
                        JSONObject c = shipmentArray.getJSONObject(i);

                        retAwb= c.getString("awb");
                        fromZip = c.getInt("from_zip");
                        toZip = c.getInt("to_zip");
                        sender = c.getString("sender_name");
                        recipient = c.getString("recipient_name");
                        senderTelp = c.getString("sender_telp");
                        recipientTelp = c.getString("recipient_telp");
                        fromAddress= c.getString("from_address");
                        toAddress = c.getString("to_address");
                        weight = c.getInt("weight");
                        fromCity = c.getString("from_city");
                        fromAbbr = c.getString("from_state_abbr");
                        toCity = c.getString("to_city");
                        toState = c.getString("to_state");
                        toAbbr = c.getString("to_state_abbr");
                        nowZip = c.getInt("now_at_zip");
                        nowCity = c.getString("now_at_city");
                        nowState = c.getString("now_at_state");
                        nowAbbr = c.getString("now_at_state_abbr");
                        status = c.getString("status");
                    }
                }else{
                    Toast.makeText(PackageTracking.this, "ada yang salah bro", Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String wew){
            //tvResult.setText(retAwb+"\n"+fromZip+toZip+sender+recipient+senderTelp+recipientTelp+fromAddress);
            tvResult.setText(shipmentArray.toString());
            Toast.makeText(PackageTracking.this, shipmentArray.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

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
    String URL = "http://192.168.43.14/brieftrager/rest/api/search_shipment.php";
    String retAwb, sender, recipient, senderTelp, recipientTelp, fromAddress, toAddress,
            fromCity, fromState, fromAbbr, toCity, toState, toAbbr, nowCity, nowState, nowAbbr, status;
    int nowZip, weight, toZip, fromZip;
    TextView mesaje,trackingAwb,trackingSenderNameView,trackingSenderCityStateZipView, trackingSenderAddressView,trackingAmericaText,
            trackingRecipientNameView,trackingRecipientAddressView,trackingRecipientCityStateZipView,
            trackingAmericaaas,shipmentAt, recipientDetails, senderDetails;

    EditText zipSearch;
    TextView cityView, countyView, stateView, zipView;
    ImageButton btnSearch;
    ImageButton btnDeatail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_tracking);

        zipSearch = findViewById(R.id.zippySearch);

        ImageButton btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Scanner.class);
                startActivity(i);
            }
        });

        zipSearch.setText(Scanner.txtResult);

        mesaje = findViewById(R.id.mesaje);
        trackingAwb = findViewById(R.id.trackingAwb);
        trackingSenderNameView = findViewById(R.id.trackingSenderNameView);
        trackingSenderCityStateZipView = findViewById(R.id.trackingSenderCityStateZipView);
        trackingAmericaText = findViewById(R.id.trackingAmericaText);
        trackingRecipientNameView = findViewById(R.id.trackingRecipientNameView);
        trackingRecipientCityStateZipView = findViewById(R.id.trackingRecipientCityStateZipView);
        trackingAmericaaas = findViewById(R.id.trackingAmericaaas);
        shipmentAt = findViewById(R.id.shipmentAt);
        trackingRecipientAddressView = findViewById(R.id.trackingRecipientAddressView);
        trackingSenderAddressView = findViewById(R.id.trackingSenderAddressView);
        recipientDetails = findViewById(R.id.recipientDetails);
        senderDetails = findViewById(R.id.senderDetails);


        btnDeatail = findViewById(R.id.btnZippySearch);

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
            awb = zipSearch.getText().toString();

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
                        fromState = c.getString("from_state");
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
            mesaje.setText("Shipment Found!");
            trackingAwb.setText(retAwb);
            trackingAmericaaas.setText("United States");
            trackingAmericaText.setText("United States");
            String fromConcat = fromCity + ", " + fromState + ", " + fromAbbr;
            trackingSenderCityStateZipView.setText(fromConcat);
            trackingSenderNameView.setText(sender);
            trackingSenderAddressView.setText(fromAddress);
            trackingRecipientAddressView.setText(toAddress);
            trackingRecipientNameView.setText(recipient);
            String toConcat = toCity + ", " + toState + ", " + toAbbr;
            trackingRecipientCityStateZipView.setText(toConcat);
            String atConcat = "Shipment are " + status + " at " + nowCity + ", " + nowState + ", " + nowAbbr;
            shipmentAt.setText(atConcat);
            senderDetails.setText("Sender Details");
            recipientDetails.setText("Recipient Details");


        }
    }
}

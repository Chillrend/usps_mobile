package org.chillrend.brieftrager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.chillrend.brieftrager.sapot.JSONParser;
import org.chillrend.brieftrager.sapot.KeyValPair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.Attributes;

public class scheduledBook extends AppCompatActivity {

    List<KeyValPair> ShipmentMethod = new ArrayList<KeyValPair>();
    JSONParser jsonParser = new JSONParser();
    Calendar myCalendar = Calendar.getInstance();
    EditText detPicker,recipientName, recipientAddress, recipientZip, recipientTel, packWeight;
    String awb, from_zip, to_zip, sender_name, recipient_name, sender_telp, recipient_telp, from_address,
            to_address, shipment_type, weight, expected_date, shipment_type_name;
    String fromCity, fromState, fromStateAbbr, fromCounty, toCity, toState, toStateAbbr, toCounty;
    private String submitUrl = "http://192.168.43.14/brieftrager/rest/api/new_scheduled.php";
    Spinner method;
    Button sambidBtn;
    JSONArray shipmentArray = null;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    String url = "http://192.168.43.14/brieftrager/rest/api/scheduledspinner.php";

    private void updateLabel(){
        String dateFormats = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormats, Locale.US);

        detPicker.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_book);

        detPicker = (EditText) findViewById(R.id.whenPickup);
        recipientName = (EditText) findViewById(R.id.recipientNameBox);
        recipientAddress = (EditText) findViewById(R.id.recipientAddressBox);
        recipientTel = (EditText) findViewById(R.id.recipientTelBox);
        recipientZip = (EditText) findViewById(R.id.recipientZipBox);
        method = (Spinner) findViewById(R.id.shipmentMethod);
        sambidBtn = (Button) findViewById(R.id.btnSambid);

        method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValPair k = (KeyValPair) parent.getItemAtPosition(position);
                shipment_type = String.valueOf(k.key);
                shipment_type_name = k.valuez;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sambidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);

                from_zip = sharedPreferences.getString("zip", "No zip!");
                to_zip = recipientZip.getText().toString();
                sender_name = sharedPreferences.getString("name", "No Name!");
                recipient_name = recipientName.getText().toString();
                sender_telp = sharedPreferences.getString("phone", "No Phone!");
                recipient_telp = recipientTel.getText().toString();
                from_address = sharedPreferences.getString("address", "no address!");
                to_address = recipientAddress.getText().toString();
                weight = packWeight.getText().toString();
                expected_date = detPicker.getText().toString();
                awb = UUID.randomUUID().toString();
            }
        });



        detPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(scheduledBook.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        new spinnerPopulate().execute();
    }

    private class submitScheduled extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params){
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("from_zip", from_zip));
            param.add(new BasicNameValuePair("to_zip", to_zip));
            param.add(new BasicNameValuePair("sender_name", sender_name));
            param.add(new BasicNameValuePair("recipient_name", recipient_name));
            param.add(new BasicNameValuePair("sender_telp", sender_telp));
            param.add(new BasicNameValuePair("recipient_telp", recipient_telp));
            param.add(new BasicNameValuePair("from_address", from_address));
            param.add(new BasicNameValuePair("to_address", to_address));
            param.add(new BasicNameValuePair("weight", weight));
            param.add(new BasicNameValuePair("expected_date", expected_date));
            param.add(new BasicNameValuePair("awb", awb));
            param.add(new BasicNameValuePair("shipment_type", shipment_type));

            JSONObject json = jsonParser.makeHttpRequest(submitUrl, "POST", param);



            try{
                shipmentArray = json.getJSONArray("shipmentData");

                for(int iter=0; iter<shipmentArray.length(); iter++){
                    JSONObject c = shipmentArray.getJSONObject(iter);

                    fromCity = c.getString("fromCity");
                    fromState = c.getString("fromState");
                    fromStateAbbr = c.getString("fromStateAbbr");
                    fromCounty = c.getString("fromCounty");
                    toCity = c.getString("toCity");
                    toState = c.getString("toState");
                    toStateAbbr = c.getString("toStateAbbr");
                    toCounty = c.getString("toCounty");
                }
            }catch(JSONException jsone){
                jsone.printStackTrace();
            }

            Intent intent = new Intent(scheduledBook.this, showScheduledResult.class);

            intent.putExtra("from_zip", from_zip);
            intent.putExtra("to_zip", to_zip);
            intent.putExtra("sender_name", sender_name);
            intent.putExtra("recipient_name", recipient_name);
            intent.putExtra("sender_telp", sender_telp);
            intent.putExtra("recipient_telp", recipient_telp);
            intent.putExtra("from_address", from_address);
            intent.putExtra("to_address", to_address);
            intent.putExtra("weight", weight);
            intent.putExtra("expected_date", expected_date);
            intent.putExtra("awb", awb);
            intent.putExtra("fromCity", fromCity);
            intent.putExtra("fromState", fromState);
            intent.putExtra("fromStateAbbr", fromStateAbbr);
            intent.putExtra("fromCounty", fromCounty);
            intent.putExtra("toCity", toCity);
            intent.putExtra("toState", toState);
            intent.putExtra("toStateAbbr", toStateAbbr);
            intent.putExtra("toCounty", toCounty);

            startActivity(intent);
            return null;
        }
    }

    private class spinnerPopulate extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            JSONObject json = jsonParser.makeHttpRequest(url, "NO_PARAMS", null);
            try{
                JSONArray spinnerArray = json.getJSONArray("method");
                for(int i=0; i<spinnerArray.length(); i++){
                    JSONObject iterator = spinnerArray.getJSONObject(i);
                    String name = iterator.getString("name");
                    int id = iterator.getInt("id");
                    ShipmentMethod.add(new KeyValPair(name,id));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String well){
            ArrayAdapter<KeyValPair> spinnerAdapter = new ArrayAdapter<KeyValPair>(scheduledBook.this, android.R.layout.simple_spinner_item, ShipmentMethod);

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.shipmentMethod);
            spinner.setAdapter(spinnerAdapter);
        }
    }
}

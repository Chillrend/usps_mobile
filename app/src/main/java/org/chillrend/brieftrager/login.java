package org.chillrend.brieftrager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.chillrend.brieftrager.sapot.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {

    private String username, password, sessionUsername, sessionName, sessionEmail, sessionPhone, sessionAddr,
            sessionImageUrl, sessionZip, sessionGender;
    private String getUrl = "http://192.168.43.14/brieftrager/rest/api/login.php";
    private ProgressDialog pDialog;
    private EditText userbox, pwdbox;
    private TextView errorText;
    private Button btnLogin;
    private Bitmap userpp;
    JSONParser jsonParser = new JSONParser();
    JSONArray userArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userbox = (EditText) findViewById(R.id.userbox);
        pwdbox = (EditText) findViewById(R.id.pwdbox);
        errorText = (TextView) findViewById(R.id.errorText);
        errorText.setVisibility(View.GONE);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyUser().execute();


            }
        });
    }

    private class VerifyUser extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //progress toast
            pDialog = new ProgressDialog(login.this);
            pDialog.setMessage("Verifying");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){
            username = userbox.getText().toString();
            password = pwdbox.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            JSONObject json = jsonParser.makeHttpRequest(getUrl,"GET", params);

            //reading success tag

            try{
                int success = json.getInt("success");

                if(success == 1){
                    userArray = json.getJSONArray("userdata");
                    for(int i=0; i<userArray.length(); i++){
                        JSONObject c = userArray.getJSONObject(i);

                        //retrieving username and name
                        sessionUsername = c.getString("username");
                        sessionName =  c.getString("name");
                        sessionAddr = c.getString("address");
                        sessionEmail = c.getString("email");
                        sessionImageUrl = c.getString("pp_image");
                        sessionZip = c.getString("zip");
                        sessionPhone = c.getString("phone");
                        sessionGender = c.getString("gender");
                    }

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file),
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", sessionUsername);
                    editor.putString("name", sessionName);
                    editor.putString("email", sessionEmail);
                    editor.putString("userpicture", sessionImageUrl);
                    editor.commit();

                    if(sharedPreferences.contains("userpictures")){
                        String uerel = sharedPreferences.getString("userpp", "http://192.168.43.14/post_pp/def.png");

                        GetBitmapFromURLAsync getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                        getBitmapFromURLAsync.execute(uerel);

                        String path = saveToInternalStorage(userpp);

                        editor.putString("filename", sessionImageUrl.substring(sessionImageUrl.lastIndexOf("/") + 1));
                        editor.putString("path", path);

                        editor.apply();
                    }

                    Intent intent = new Intent(login.this,home.class);
                    startActivity(intent);
                }else{
//                    pDialog.dismiss();
//                    errorText.setVisibility(View.VISIBLE);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
        }

    }

    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap>{
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
        }

    }

    private String saveToInternalStorage(Bitmap image){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String fileName = sessionImageUrl.substring(sessionImageUrl.lastIndexOf("/") + 1);

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

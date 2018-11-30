package org.chillrend.brieftrager;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.chillrend.brieftrager.sapot.qrGenerate;

public class showScheduledResult extends AppCompatActivity {
    String awb, from_zip, to_zip, sender_name, recipient_name, sender_telp, recipient_telp, from_address,
            to_address, shipment_type, weight, expected_date, shipment_type_name;
    String fromCity, fromState, fromStateAbbr, fromCounty, toCity, toState, toStateAbbr, toCounty;
    TextView senderNameView, senderAddressView, senderCityStateZipView, recipientNameView, recipientAddressView,
            recipientCityStateZipView, awbView;
    ImageView QRView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scheduled_result);

        from_zip = getIntent().getStringExtra("from_zip");
        to_zip = getIntent().getStringExtra("to_zip");
        sender_name = getIntent().getStringExtra("sender_name");
        recipient_name = getIntent().getStringExtra("recipient_name");
        sender_telp = getIntent().getStringExtra("sender_telp");
        recipient_telp = getIntent().getStringExtra("recipient_telp");
        from_address = getIntent().getStringExtra("from_address");
        to_address = getIntent().getStringExtra("to_address");
        weight = getIntent().getStringExtra("weight");
        expected_date = getIntent().getStringExtra("expected_date");
        awb = getIntent().getStringExtra("awb");
        fromCity = getIntent().getStringExtra("fromCity");
        fromState = getIntent().getStringExtra("fromState");
        fromStateAbbr = getIntent().getStringExtra("fromStateAbbr");
        fromCounty = getIntent().getStringExtra("fromCounty");
        toCity = getIntent().getStringExtra("toCity");
        toState = getIntent().getStringExtra("toState");
        toStateAbbr = getIntent().getStringExtra("toStateAbbr");
        toCounty = getIntent().getStringExtra("toCounty");
        shipment_type_name = getIntent().getStringExtra("shipment_type_name");

        qrGenerate qrGenerate = new qrGenerate();
        Bitmap qr = qrGenerate.getQr(awb,160,160);

        senderNameView = (TextView) findViewById(R.id.senderNameView);
        senderAddressView = (TextView) findViewById(R.id.senderAddressView);
        senderCityStateZipView = (TextView) findViewById(R.id.senderCityStateZipView);
        recipientNameView = (TextView) findViewById(R.id.recipientNameView);
        recipientAddressView = (TextView) findViewById(R.id.recipientAddressView);
        recipientCityStateZipView = (TextView) findViewById(R.id.recipientCityStateZipView);
        awbView = (TextView) findViewById(R.id.awbView);
        QRView = (ImageView) findViewById(R.id.qrView);

        senderNameView.setText(sender_name);
        senderAddressView.setText(from_address);
        String concatCityState = fromCity + ", " + fromState + " " + fromStateAbbr + ", " + from_zip;
        senderCityStateZipView.setText(concatCityState);

        recipientNameView.setText(recipient_name);
        recipientAddressView.setText(to_address);
        String concatCityStateTo = toCity + ", " + toState + " " + toStateAbbr + ", " + to_zip;
        recipientCityStateZipView.setText(concatCityStateTo);

        String concatAwb = "airwaybill : " + awb;
        awbView.setText(concatAwb);

        QRView.setImageBitmap(qr);

    }
}

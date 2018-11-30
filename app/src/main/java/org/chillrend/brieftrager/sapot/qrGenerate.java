package org.chillrend.brieftrager.sapot;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class qrGenerate {
    public Bitmap getQr(String suuid, int width, int height){
        MultiFormatWriter mfw = new MultiFormatWriter();
        try{
            BitMatrix bm = mfw.encode(suuid, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder be = new BarcodeEncoder();
            Bitmap qrCode = be.createBitmap(bm);
            return qrCode;
        }catch(WriterException e){
            e.printStackTrace();
            return null;
        }
    }
}

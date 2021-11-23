package com.example.zhanbozhang.test.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Utils {

    static {
        Log.i("elifli", "Utils static");
    }

    static public int number = 39;

    static public int getNumber() {
        return number;
    }


    /**
     * view 2 bitmap
     */
    public static Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    /**
     * Bitmap 2 file
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
    }

    //Bitmap.CompressFormat.PNG
    /**
     * drawable 2 file
     * @param drawable
     * @param filePath
     */
    public void drawableToFile(Drawable drawable, String filePath) {
        if (drawable == null)
            return;

        try {
            File file = new File(filePath);

            if (file.exists())
                file.delete();

            if (!file.exists())
                file.createNewFile();

            FileOutputStream out = null;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Point getRealScreenSize() {
        Point point = new Point();
        return point;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String exec(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(command);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                String data = "";
                BufferedReader ie = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String error;
                while ((error = ie.readLine()) != null
                        && !error.equals("null")) {
                    data += error + "\n";
                }

                String line;
                while ((line = in.readLine()) != null
                        && !line.equals("null")) {
                    data += line + "\n";
                }

                Log.i("elifli", "exusecmd: \n" + data);
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static Location getLocation(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        List<String> providers = locationManager.getAllProviders();
        for (int i = 0; i < providers.size(); ++i) {
            String provider = providers.get(i);
            location = (provider != null) ? locationManager.getLastKnownLocation(provider) : null;
            if (location != null)
                break;
        }

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2*1000, 2.0f, listener);
        return location;
    }
}

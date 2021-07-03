package com.example.rf;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallAPI extends AsyncTask<String, String, String> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String data = params[1]; //data to post
//        String data = "{\"lac\":1}";
        OutputStream out = null;

        try {

//            URL url = new URL(urlString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoOutput(true);
//            urlConnection.setDoInput(true);
//            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
//            urlConnection.setRequestProperty("Accept", "application/json");
            URL serverAddress = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) serverAddress.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            out = new BufferedOutputStream(urlConnection.getOutputStream());
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
//            JSONObject json = new JSONObject(data);
            System.out.println(data);
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
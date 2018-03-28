package com.example.tpaj0.leaguerandom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    int randomChamp = 0;
    String[] champs;
    String[] champName;
    Bitmap splashImage;
    ImageView imageView;
    TextView nameTextView;
    TextView titleTextView;
    public class DownloadTask extends AsyncTask<String,Void,String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                String[] champUrl = new String[0];
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String champions = jsonObject.getString("champions");

                    Log.i("info", champions);

                    JSONArray arr = new JSONArray(champions);

                    champUrl = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        String champId = jsonPart.getString("id");
                        champUrl[i] = "https://na1.api.riotgames.com/lol/static-data/v3/champions/" + champId + "?locale=en_US&api_key=RGAPI-d27944eb-caa2-4d59-8daf-68463e4e5537";
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                return champUrl;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
        }
    }

    public class champDownload extends AsyncTask <String, Void,String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                String champName = null;
                String champTitle = null;
                String[] champInfo = new String[2];
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    champName = jsonObject.getString("name");
                    champTitle = jsonObject.getString("title");

                    champInfo[0] = champName;
                    champInfo[1] = champTitle;
                    Log.i("info", champName);

                } catch (Exception e) {
                    e.printStackTrace();

                }

                return champInfo;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public void randomChampion () throws ExecutionException, InterruptedException {
        champDownload champTask = new champDownload();
        ImageDownloader splashTask = new ImageDownloader();
        Random rand= new Random();
        randomChamp = rand.nextInt(champs.length);
        champName = champTask.execute(champs[randomChamp]).get();
        splashImage = splashTask.execute("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/"+champName[0]+"_0.jpg").get();
        imageView.setImageBitmap(splashImage);
        nameTextView.setText(champName[0]);
        titleTextView.setText(champName[1]);
    }

    public void rerollChamp (View view){
        try {
            randomChampion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        nameTextView = findViewById(R.id.nameTextView);
        titleTextView =findViewById(R.id.titleTextView);

        DownloadTask task = new DownloadTask();
        try {
            champs = task.execute("https://na1.api.riotgames.com/lol/platform/v3/champions?freeToPlay=false&api_key=RGAPI-d27944eb-caa2-4d59-8daf-68463e4e5537").get();
            randomChampion();
            Log.i("name", champName[1]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}

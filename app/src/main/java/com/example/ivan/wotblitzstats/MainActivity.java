package com.example.ivan.wotblitzstats;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ivan.http.FetchHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String APPLICATION_ID = "5118575d12418d55c67effeeb5915fe1";
    private static final String URL = "https://api.wotblitz.ru/wotb/";
    private static final String PERSONAL_DATA = "account/info/";
    private static final String PLAYERS = "account/list/";

    private String nickname;

    private Button btnGo;
    private TextView txtBattles;
    private EditText editNickname;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        btnGo = (Button) findViewById(R.id.btnGo);
        txtBattles = (TextView) findViewById(R.id.txtBattles);
        editNickname = (EditText) findViewById(R.id.editNickname);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = editNickname.getText().toString();
                new FetchItemsTask().execute();
            }
        });

    }

    private class FetchItemsTask extends AsyncTask<Void, String, Void> {

        private int battles;

        @Override
        protected void onPreExecute() {
            txtBattles.setText("Loading.");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            txtBattles.setText("Battles : " + battles);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            txtBattles.setText("Loading." + values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject jsonID, jsonData;
                publishProgress(".");
                jsonID = new JSONObject(new FetchHttp().getUrl(URL + PLAYERS
                        + "?application_id=" + APPLICATION_ID + "&search=" + nickname));
                int account_id = (int) jsonID.getJSONArray("data").getJSONObject(0).get("account_id");
                publishProgress("..");
                String data = new FetchHttp().getUrl(URL + PERSONAL_DATA + "?application_id="
                        + APPLICATION_ID + "&account_id=" + account_id);
                jsonData = new JSONObject(data);
                battles = (int) jsonData.getJSONObject("data").getJSONObject(Integer.toString(account_id))
                        .getJSONObject("statistics").getJSONObject("all").get("battles");
            } catch (IOException ioe) {
                Log.e(TAG, "Failed!", ioe);
            } catch (JSONException e) {
                Log.e(TAG, "Failed!", e);
            }
            return null;
        }
    }

}

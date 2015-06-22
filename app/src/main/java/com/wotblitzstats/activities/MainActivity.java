package com.wotblitzstats.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import com.wotblitzstats.http.FetchHttp;
import com.wotblitzstats.ivan.activities.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String APPLICATION_ID = "5118575d12418d55c67effeeb5915fe1";
    private static final String URL = "https://api.wotblitz.ru/wotb/";
    private static final String PERSONAL_DATA = "account/info/";
    private static final String PLAYERS = "account/list/";

    private static final boolean LOADING_STATUS = false;

    private String nickname;
    private ArrayList<Integer> usersID = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    private Button btnGo;
    private TextView txtBattles;
    private EditText editNickname;

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
                new LoadStatsTask().execute();
            }
        });

    }

    private class LoadStatsTask extends AsyncTask<Void, String, Void> {

        private int battles;
        private int wins;
        private int account_id;

        @Override
        protected void onPreExecute() {
            txtBattles.setText("Loading.");
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (battles != -1) {
                Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                intent.putExtra("battles", battles);
                intent.putExtra("wins", wins);
                startActivity(intent);
                txtBattles.setText("");
            } else {
                txtBattles.setText("Please, enter correct nickname");
            }

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
                int k = 0;
                String searchNickname;
                for (int i = 0; i < jsonID.getJSONObject("meta").getInt("count"); i++) {
                    searchNickname = jsonID.getJSONArray("data").getJSONObject(i)
                            .get("nickname").toString();
                    if (nickname.equalsIgnoreCase(searchNickname)) {
                        Log.i("enter", "enter");
                        account_id = (int) jsonID.getJSONArray("data").getJSONObject(0).get("account_id");
                        break;
                    } else {
                        k++;
                    }
                }
                if (k == jsonID.getJSONObject("meta").getInt("count")) {
                    account_id = -1;
                    battles = -1;
                }
                Log.i("account", String.valueOf(account_id));
                Log.i("account", Integer.toString(jsonID.getJSONObject("meta").getInt("count")));
                publishProgress("..");
                String data = getUserName(account_id);
                jsonData = new JSONObject(data);
                battles = (int) jsonData.getJSONObject("data").getJSONObject(Integer.toString(account_id))
                        .getJSONObject("statistics").getJSONObject("all").get("battles");
                wins = (int) jsonData.getJSONObject("data").getJSONObject(Integer.toString(account_id))
                        .getJSONObject("statistics").getJSONObject("all").get("wins");
            } catch (IOException ioe) {
                Log.e(TAG, "Failed!", ioe);
            } catch (JSONException e) {
                Log.e(TAG, "Failed!", e);
                battles = -1;
            }
            return null;
        }

        public String getUserName(int account_id) throws IOException {
            return new FetchHttp().getUrl(URL + PERSONAL_DATA + "?application_id="
                    + APPLICATION_ID + "&account_id=" + account_id);
        }

    }

}

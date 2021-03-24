package com.example.vkinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.example.vkinfo.utils.NetworkUtils.generateURL;
import static com.example.vkinfo.utils.NetworkUtils.getResponseFromURL;

public class MainActivity extends AppCompatActivity {
    private EditText searchField;
    private Button searchBtn;
    private TextView result;
    private TextView errorMessage;
    private ProgressBar progressLoading;

    private void showResultTextView(){
        result.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorTextView(){
        result.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }
    class VKQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute(){
            progressLoading.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            } catch (IOException e){
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response)
        {
            String firsName = null;
            String lastname = null;
            String id = null;

            if(response!=null&&!response.equals("")) {

                try {
                    String resultString = "";
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    for(int i = 0; i<jsonArray.length();i++) {
                        JSONObject userInfo = jsonArray.getJSONObject(i);

                        firsName = userInfo.getString("first_name");
                        lastname = userInfo.getString("last_name");
                        id = userInfo.getString("id");
                        resultString += "Id: " + id + "\n" + "Name: " + firsName + "\n"
                                + "Sername: : " + lastname + "\n\n";

                    }
                    result.setText(resultString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                showResultTextView();
            }else {
                showErrorTextView();
            }
            progressLoading.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = (EditText) findViewById(R.id.set_search_field);
        searchBtn = (Button) findViewById(R.id.btn_search_vk);
        result = (TextView) findViewById(R.id.tv_result);
        errorMessage = (TextView) findViewById(R.id.tv_error_message);
        progressLoading = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL generatedURL = generateURL(searchField.getText().toString());
                new VKQueryTask().execute(generatedURL);

            }
        };
        searchBtn.setOnClickListener(onClickListener);
    }
}
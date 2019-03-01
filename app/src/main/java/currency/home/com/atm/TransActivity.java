package currency.home.com.atm;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransActivity extends AppCompatActivity {

    private static final String TAG = TransActivity.class.getSimpleName();
    private List<Transcation> transcations;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
//        new TransTask().execute("http://atm201605.appspot.com/h");
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://atm201605.appspot.com/h")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                Log.d(TAG, "onResponse: "+json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseGSON(json);
                      /*  parseJSON(json);*/
                    }
                });

            }
        });


    }

    private void parseGSON(String json) {
        Gson gson = new Gson();
        transcations = gson.fromJson(json, new TypeToken<ArrayList<Transcation>>(){}.getType());
        TransAdater transAdater = new TransAdater();
        recyclerView.setAdapter(transAdater);
    }

    private void parseJSON(String string) {

        transcations = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(string);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transcations.add(new Transcation(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TransAdater transAdater = new TransAdater();
        recyclerView.setAdapter(transAdater);
    }


    public class TransAdater extends RecyclerView.Adapter<TransAdater.TransHolder> {
        @NonNull
        @Override
        public TransHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.items_transcation,viewGroup,false);
            return new TransHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransHolder transHolder, int i) {
            Transcation tran = transcations.get(i);
            transHolder.bindTo(tran);



        }

        @Override
        public int getItemCount() {
            return transcations.size();
        }

        public  class TransHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView amount;
            TextView type;

            public TransHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.item_date);
                amount = itemView.findViewById(R.id.item_amount);
                type = itemView.findViewById(R.id.item_type);
            }

            public void bindTo(Transcation tran) {
                date.setText(tran.getDate());
                amount.setText(String.valueOf(tran.getAmount()));
                type.setText(String.valueOf(tran.getType()));
            }
        }
    }

    public class TransTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(strings[0]);
                InputStream is = url.openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                Log.d(TAG, "Result = " + sb.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return sb.toString();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: "+s);
        }
    }
}

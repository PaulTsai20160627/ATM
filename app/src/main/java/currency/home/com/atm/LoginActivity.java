package currency.home.com.atm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CAMERA = 5;
    //private static final String TAG = LoginActivity.class.getSimpleName();
    EditText edUserId ;
    EditText edPasswd ;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Camera();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragementTransaction = fragmentManager.beginTransaction();
        fragementTransaction.add(R.id.news_container,NewsFragment.getInstance());
        fragementTransaction.commit();

        Intent helloIntent = new Intent(this,HelloService.class);
        helloIntent.putExtra("NAME","T1");
        startService(helloIntent);

        String userID = Settings();
        findViews(userID);
    new TestTask().execute("http://tw.yahoo.com");



    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: "+intent.getAction());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HelloService.ACTION_HELLO_DONE);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public class TestTask extends AsyncTask<String,Void,Integer>{

        private int data;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            Toast.makeText(LoginActivity.this,"onPreExecute",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d(TAG, "onPostExecute: ");
            Toast.makeText(LoginActivity.this,"onPostExecuted"+integer,Toast.LENGTH_LONG).show();

        }

        @Override
        protected Integer doInBackground(String... strings) {

            URL url = null;
            try {
                url = new URL(strings[0]);
                data = url.openStream().read();
                Log.d(TAG,"read = "+ data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    private void findViews(String userID) {
        edUserId = findViewById(R.id.userid);
        edPasswd = findViewById(R.id.passwd);
        checkBox = findViewById(R.id.cb_remeber_id);
        checkBox.setChecked(getSharedPreferences("atm",MODE_PRIVATE).getBoolean("REMEMBER_USERID",false));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("atm",0)
                        .edit()
                        .putBoolean("REMEMBER_USERID",isChecked)
                        .apply();
            }
        });
        edUserId.setText(userID);
    }

    private String Settings() {
    /*  getSharedPreferences("atm",MODE_PRIVATE)
              .edit()
              .putInt("LEVEL",3)
              .putString("USERID","")
              .commit();*/
        int level = getSharedPreferences("atm",MODE_PRIVATE)
                .getInt("LEVEL",0);
        Log.d(TAG, "onCreate: "+level);
        return getSharedPreferences("atm",MODE_PRIVATE)
                .getString("USERID","");
    }

    private void Camera() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permission == PackageManager.PERMISSION_GRANTED){
            takePhoto();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    public void login(View view){
        final String userid = edUserId.getText().toString();
        Log.d(TAG,userid);
        FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(userid)
                .child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pw = dataSnapshot.getValue().toString();
                        Log.d(TAG, "onDataChange: "+pw);
                        if(pw.equals(edPasswd.getText().toString())){
                            boolean remember = getSharedPreferences("atm",MODE_PRIVATE)
                                    .getBoolean("REMEMBER_USERID",false);
                            if(remember){
                                getSharedPreferences("atm",MODE_PRIVATE)
                                        .edit()
                                        .putString("USERID",userid)
                                        .apply();
                            }
                           setResult(RESULT_OK);
                         finish();
                        }else{
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("登入資訊")
                                    .setMessage("登入失敗")
                                    .setPositiveButton("OK",null)
                                    .show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//        FirebaseDatabase.getInstance().getReference("users").child(userid).child("password")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String pw = dataSnapshot.getValue().toString();
//                    if(pw.equals(edPasswd.toString())){
//                           setResult(RESULT_OK);
//                    }      finish();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        if(edUserId.getText().toString().equals("jack") && edPasswd.getText().toString().equals("1234")){
//            setResult(RESULT_OK);
//            finish();
//        }
    }
    public void quit(View view){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePhoto();
        }else{

        }
    }
}

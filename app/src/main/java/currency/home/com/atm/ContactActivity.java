package currency.home.com.atm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS =80 ;
    private static final String TAG = ContactActivity.class.getSimpleName();
    private List<ContactInfo> contactInfoList;
    private ContactInfo contactInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if(permission == PackageManager.PERMISSION_GRANTED){
            readContacts();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CONTACTS);
        }
    }

    private void readContacts() {
        contactInfoList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            contactInfo = null;
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d(TAG, "readContacts: "+name);
            contactInfo = new ContactInfo(name ,id);
            int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if(hasPhone == 1){
                Cursor c2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ "=?" ,new String[]{String.valueOf(id)},null);
                while(c2.moveToNext()){
                    String phone = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    Log.d(TAG, "readContacts: "+phone);
                contactInfo.getPhones().add(phone);
                }
            }
            contactInfoList.add(contactInfo);
        }
        ContactAdapter contactAdapter = new ContactAdapter(contactInfoList);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);
  }


    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder>{

        private List<ContactInfo> contactInfoList;
        public ContactAdapter(List<ContactInfo> contactInfoList){
            this.contactInfoList = contactInfoList;

        }
        @NonNull
        @Override
        public ContactHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,viewGroup,false);
            return new ContactHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactHolder contactHolder, int i) {
            StringBuilder sb = new StringBuilder();
            ContactInfo contactInfo = contactInfoList.get(i);
            contactHolder.name.setText(contactInfo.getName());
            for (String phone : contactInfo.getPhones()) {
                sb.append(phone);
                sb.append("/");
            }
            contactHolder.phones.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return contactInfoList.size();
        }

        public class ContactHolder extends RecyclerView.ViewHolder{

            private final TextView phones;
            private final TextView name;

            public ContactHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(android.R.id.text1);
                phones = itemView.findViewById(android.R.id.text2);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CONTACTS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                readContacts();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.menu_contact,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_upload){
            Log.d(TAG, "onOptionsItemSelected: ");

            String userID = getSharedPreferences("atm",MODE_PRIVATE)
                    .getString("USERID","");
            FirebaseDatabase.getInstance().getReference("users").child(userID).child("contacts").setValue(contactInfoList);
        }
        return super.onOptionsItemSelected(item);
    }
}

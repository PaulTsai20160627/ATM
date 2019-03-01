package currency.home.com.atm;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOGIN = 100;
    private static final String TAG = MainActivity.class.getSimpleName();
    boolean logon = false;
    private RecyclerView recyclerView;
    String[] functions = null;
    private List<Function> functionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!logon){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivityForResult(intent,RESULT_LOGIN);
           // startActivity(intent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        functions = getResources().getStringArray(R.array.functions);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        setFunctionsList();
     /*   recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
      /*  FunctionAdapter adapter = new FunctionAdapter(this);*/
        IconAdapter adapter = new IconAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setFunctionsList() {
        functionList = new ArrayList<>();
        functionList.add(new Function(functions[0],R.drawable.transaction));
        functionList.add(new Function(functions[1],R.drawable.balance));
        functionList.add(new Function(functions[2],R.drawable.invest));
        functionList.add(new Function(functions[3],R.drawable.contacts_info));
        functionList.add(new Function(functions[4],R.drawable.exit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_LOGIN && resultCode != RESULT_OK){
            finish();
        }
    }

    public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

        public IconAdapter() {

        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_view,null);
            return new IconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconViewHolder iconViewHolder, int i) {
            final Function function = functionList.get(i);
            iconViewHolder.imageView.setImageResource(function.getImageViewID());
            iconViewHolder.name.setText(function.getName());
            iconViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(function);
                }
            });

        }

        @Override
        public int getItemCount() {
            return functionList.size();
        }

        public class IconViewHolder extends RecyclerView.ViewHolder{

             ImageView imageView;
             TextView name;

            public IconViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_txt);
                imageView = itemView.findViewById(R.id.item_img);
            }
        }

    }

    private void itemClicked(Function function) {
        Log.d(TAG, "itemClicked: "+function.getName());
        switch (function.getImageViewID()){
            case R.drawable.transaction:
                startActivity(new Intent(this,TransActivity.class));
                break;
            case R.drawable.balance:
                break;
            case R.drawable.invest:
                Intent invest = new Intent(this,FinanceActivity.class);
                startActivity(invest);
                break;
            case R.drawable.contacts_info:
                Intent contacts = new Intent(this,ContactActivity.class);
                startActivity(contacts);
                break;
            case R.drawable.exit:
                finish();
                break;
        }
    }
}

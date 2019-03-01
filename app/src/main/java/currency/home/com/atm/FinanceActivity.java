package currency.home.com.atm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FinanceActivity extends AppCompatActivity {

    private ExpenseAdapter expenseAdapter;
    private RecyclerView recyclerView;
    private ExpenseHelper expenseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinanceActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseHelper = new ExpenseHelper(this);
        Cursor cursor = expenseHelper.getReadableDatabase().query("expense",null,null,null,null,null,null);

        expenseAdapter = new ExpenseAdapter(cursor);
        recyclerView.setAdapter(expenseAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = expenseHelper.getReadableDatabase().query("expense",null,null,null,null,null,null);
        expenseAdapter = new ExpenseAdapter(cursor);
        recyclerView.setAdapter(expenseAdapter);
    }

    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>{

        Cursor cursor;
        public ExpenseAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @NonNull
        @Override
        public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.expense_item,viewGroup,false);
            return new ExpenseHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseHolder expenseHolder, int i) {
            cursor.moveToPosition(i);
            String date = cursor.getString(cursor.getColumnIndex("cdate"));
            String info = cursor.getString(cursor.getColumnIndex("info"));
            int amount = cursor.getInt(cursor.getColumnIndex("amount"));
            expenseHolder.date.setText(date);
            expenseHolder.info.setText(info);
            expenseHolder.amount.setText(String.valueOf(amount));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ExpenseHolder extends RecyclerView.ViewHolder{
            TextView date;
            TextView info;
            TextView amount;
            public ExpenseHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.item_date);
                info = itemView.findViewById(R.id.item_info);
                amount = itemView.findViewById(R.id.item_amount);
            }
        }
    }

}

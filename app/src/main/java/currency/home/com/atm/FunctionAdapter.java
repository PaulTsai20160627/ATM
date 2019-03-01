package currency.home.com.atm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder>{
    Context mContext;
    String[] functions;

    public FunctionAdapter(Context mContext) {
        this.mContext = mContext;
        functions = mContext.getResources().getStringArray(R.array.functions);
    }

    @NonNull
    @Override
    public FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        return new FunctionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionViewHolder functionViewHolder, int i) {
        FunctionViewHolder.textView.setText(functions[i]);
    }

    @Override
    public int getItemCount() {
        return functions.length;
    }

    public static class FunctionViewHolder extends RecyclerView.ViewHolder {

         static TextView textView;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

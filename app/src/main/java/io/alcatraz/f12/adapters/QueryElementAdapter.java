package io.alcatraz.f12.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.f12.R;
import io.alcatraz.f12.beans.QueryElement;

public class QueryElementAdapter extends RecyclerView.Adapter<QueryElementContainer> {
    private List<QueryElement> data;
    private Context ctx;
    private LayoutInflater lf;

    public QueryElementAdapter(Context c, List<QueryElement> data) {
        ctx = c;
        this.data = data;
        lf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public QueryElementContainer onCreateViewHolder(@NonNull ViewGroup p1, int p2) {
        View v = lf.inflate(R.layout.item_opensource_holder, p1, false);
        return new QueryElementContainer(v);
    }

    @Override
    public void onBindViewHolder(QueryElementContainer p1, int p2) {
        View root = p1.getView();
        final QueryElement cur = data.get(p2);
        CardView cv = root.findViewById(R.id.listcardCardView1);
        if (cur.getUrl() != null) {
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent("android.intent.action.VIEW");
                    localIntent.setData(Uri.parse(cur.getUrl()));
                    ctx.startActivity(localIntent);
                }
            });

        }
        TextView name = root.findViewById(R.id.opensourceholderTextView1);
        TextView author = root.findViewById(R.id.opensourceholderTextView2);
        TextView intro = root.findViewById(R.id.opensourceholderTextView3);
        TextView lic = root.findViewById(R.id.opensourceholderTextView4);
        name.setText(cur.getName());
        author.setText(cur.getAuthor());
        intro.setText(cur.getIntro());
        lic.setText(cur.getLicense());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

class QueryElementContainer extends RecyclerView.ViewHolder {
    private View hold;

    QueryElementContainer(View v) {
        super(v);
        hold = v;
    }

    public View getView() {
        return hold;
    }
}
package io.alcatraz.f12.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.alcatraz.f12.R;
import io.alcatraz.f12.beans.chrome.Page;

public class PagesAdapter extends BaseAdapter {
    private List<Page> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public PagesAdapter(Context context,List<Page> data){
        this.context = context;
        this.data = data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_page,null);
        }
        Page page = data.get(i);
        TextView title = view.findViewById(R.id.page_title);
        TextView url_text = view.findViewById(R.id.page_url);
        title.setText(page.getTitle());
        url_text.setText(page.getUrl());
        return view;
    }
}

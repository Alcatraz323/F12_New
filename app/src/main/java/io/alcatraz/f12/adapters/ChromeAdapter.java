package io.alcatraz.f12.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.f12.AsyncInterface;
import io.alcatraz.f12.LogBuff;
import io.alcatraz.f12.R;
import io.alcatraz.f12.activities.InDebuggerActivity;
import io.alcatraz.f12.beans.chrome.ChromeObj;
import io.alcatraz.f12.beans.chrome.InspectorInfo;
import io.alcatraz.f12.beans.chrome.Page;
import io.alcatraz.f12.extended.NoScrollListView;
import io.alcatraz.f12.socat.Forward;
import io.alcatraz.f12.utils.PackageCtlUtils;

public class ChromeAdapter extends RecyclerView.Adapter<ChromeAdapter.ChromeHolder> {
    private List<Forward> data;
    private Context context;
    private LayoutInflater layoutInflater;

    //float window widget control below
    private WebView webView;
    private LinearLayout load_layer;    //contains in picker root
    private FrameLayout picker_root;
    private ImageView icon;
    private TextView window_title;

    private boolean isForFloatWindow;

    public ChromeAdapter(Context context, List<Forward> data, boolean isForFloatWindow) {
        this.data = data;
        this.context = context;
        this.isForFloatWindow = isForFloatWindow;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ChromeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = layoutInflater.inflate(R.layout.item_chrome_info, parent, false);
        return new ChromeHolder(root);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ChromeHolder holder, int position) {
        final Forward forward = data.get(position);
        final ChromeObj chromeObj = forward.getBrowser();
        holder.icon.setImageDrawable(PackageCtlUtils.getIcon(context, chromeObj.getAndroidPackage()));
        holder.title.setText(PackageCtlUtils.getLabel(context, chromeObj.getAndroidPackage()) +
                "(Devtools " + chromeObj.getProtocolVersion() + ")");
        holder.detail.setText(chromeObj.getBrowser());

        //Page list setup below
        final List<Page> pages = new ArrayList<>();
        final PagesAdapter pagesAdapter = new PagesAdapter(context, pages);
        @SuppressLint("HandlerLeak") final Handler updater = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                pages.clear();
                pages.addAll(((InspectorInfo)msg.obj).getPage());
                pagesAdapter.notifyDataSetChanged();
            }
        };
        holder.pages_list.setAdapter(pagesAdapter);

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.pages_list.getVisibility() == View.GONE) {
                    holder.pages_list.setVisibility(View.VISIBLE);
                    holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    forward.getThread().connectForPages(new AsyncInterface<InspectorInfo>() {
                        @Override
                        public void onDone(InspectorInfo result) {
                            Message message = new Message();
                            message.obj = result;
                            updater.sendMessage(message);
                        }

                        @Override
                        public void onFailure(String why) {
                            Looper.prepare();
                            Toast.makeText(context, why, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    pages.clear();
                    pagesAdapter.notifyDataSetChanged();
                    holder.pages_list.setVisibility(View.GONE);
                    holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });
        if(isForFloatWindow){   
            holder.pages_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Page clicked_page = pages.get(i);
                    webView.loadUrl(clicked_page.getDevtoolsFrontendUrl());
                    window_title.setText(chromeObj.getBrowser());
                    icon.setImageDrawable(PackageCtlUtils.getIcon(context,chromeObj.getAndroidPackage()));
                    load_layer.setVisibility(View.VISIBLE);
                    picker_root.setVisibility(View.VISIBLE);
                }
            });
        }else {
            holder.pages_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Page clicked_page = pages.get(i);
                    Intent intent = new Intent(context, InDebuggerActivity.class);
                    intent.putExtra(InDebuggerActivity.KEY_DEBUGGER_URL,clicked_page.getDevtoolsFrontendUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public void setLoad_layer(LinearLayout load_layer) {
        this.load_layer = load_layer;
    }

    public void setPicker_root(FrameLayout picker_root) {
        this.picker_root = picker_root;
    }

    public void setWindow_title(TextView window_title) {
        this.window_title = window_title;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    class ChromeHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageButton expand;
        TextView title;
        TextView detail;
        NoScrollListView pages_list;

        ChromeHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_chrome_icon);
            expand = itemView.findViewById(R.id.item_chrome_expand);
            title = itemView.findViewById(R.id.item_chrome_title);
            detail = itemView.findViewById(R.id.item_chrome_detail);
            pages_list = itemView.findViewById(R.id.item_chrome_pages);
        }
    }
}

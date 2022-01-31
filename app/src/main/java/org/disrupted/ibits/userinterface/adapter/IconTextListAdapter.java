package org.disrupted.ibits.userinterface.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.disrupted.ibits.R;

import java.util.List;

/**
 * @author
 */
public class IconTextListAdapter extends BaseAdapter {

    private static final String TAG = "NeighborListAdapter";

    private final LayoutInflater inflater;
    private List<IconTextItem> itemlist;

    public IconTextListAdapter(Activity activity, List<IconTextItem> itemlist) {
        this.inflater = LayoutInflater.from(activity);
        this.itemlist = itemlist;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View item = inflater.inflate(R.layout.item_icontext, viewGroup, false);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        icon.setImageResource(itemlist.get(i).getIcon());
        text.setText(itemlist.get(i).getText());
        return item;
    }

    @Override
    public Object getItem(int i) {
        return itemlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return itemlist.size();
    }
}


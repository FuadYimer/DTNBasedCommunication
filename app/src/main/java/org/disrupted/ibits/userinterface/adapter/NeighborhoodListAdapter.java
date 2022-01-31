package org.disrupted.ibits.userinterface.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.disrupted.ibits.R;
import org.disrupted.ibits.network.NeighbourManager;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothLinkLayerAdapter;
import org.disrupted.ibits.network.linklayer.wifi.WifiLinkLayerAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author
 */
public class NeighborhoodListAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "NeighborListAdapter";

    private final LayoutInflater inflater;
    private List<NeighbourManager.Neighbour> neighborhood;
    private static final TextDrawable.IBuilder builder = TextDrawable.builder().rect();

    public NeighborhoodListAdapter(Activity activity, Set<NeighbourManager.Neighbour> neighborhood) {
        this.inflater     = LayoutInflater.from(activity);
        this.neighborhood = new LinkedList<NeighbourManager.Neighbour>();
        this.neighborhood.addAll(neighborhood);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NeighbourManager.Neighbour neighbour = neighborhood.get(i);

        View neighborView = inflater.inflate(R.layout.item_neighbour_list, viewGroup, false);
        TextView name = (TextView) neighborView.findViewById(R.id.neighbour_item_name);
        TextView id = (TextView) neighborView.findViewById(R.id.neighbour_item_link_layer_name);
        ImageView bluetoothIcon = (ImageView) neighborView.findViewById(R.id.neighbour_item_bluetooth);
        ImageView wifiIcon = (ImageView) neighborView.findViewById(R.id.neighbour_item_wifi);
        ImageView avatarView = (ImageView) neighborView.findViewById(R.id.neighbour_item_avatar);

        name.setText(neighbour.getFirstName());
        id.setText(neighbour.getSecondName());

        if(neighbour instanceof NeighbourManager.ContactNeighbour) {
            ColorGenerator generator = ColorGenerator.DEFAULT;
            avatarView.setImageDrawable(
                    builder.build(
                            neighbour.getFirstName().substring(0, 1),
                            generator.getColor(neighbour.getSecondName()))
            );
        } else {

        }

        if(neighbour.isReachable(BluetoothLinkLayerAdapter.LinkLayerIdentifier)) {
            bluetoothIcon.setVisibility(View.VISIBLE);
            if(neighbour.isConnected(BluetoothLinkLayerAdapter.LinkLayerIdentifier))
                bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_white_18dp);
            else
                bluetoothIcon.setImageResource(R.drawable.ic_bluetooth_grey600_18dp);
        } else {
            bluetoothIcon.setVisibility(View.GONE);
        }
 
        if(neighbour.isReachable(WifiLinkLayerAdapter.LinkLayerIdentifier)) {
            wifiIcon.setVisibility(View.VISIBLE);
            if(neighbour.isConnected(WifiLinkLayerAdapter.LinkLayerIdentifier))
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_4_bar_white_18dp);
            else
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_0_bar_grey600_18dp);
        } else {
            wifiIcon.setVisibility(View.GONE);
        }

        return neighborView;
    }

    @Override
    public Object getItem(int i) {
        return neighborhood.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return neighborhood.size();
    }

    @Override
    public void onClick(View view) {
    }

    public void swap(Set<NeighbourManager.Neighbour> newNeighborhood) {
        this.neighborhood.clear();
        this.neighborhood.addAll(newNeighborhood);
    }
}


package org.disrupted.ibits.network.linklayer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.util.NetUtil;

/**
 * @author
 */
public class BluetoothNeighbour implements LinkLayerNeighbour {

    private String bluetoothMacAddress;

    public BluetoothNeighbour(String macAddress){
        this.bluetoothMacAddress = macAddress;
    }

    @Override
    public String getLinkLayerIdentifier() {
        return BluetoothLinkLayerAdapter.LinkLayerIdentifier;
    }


    @Override
    public String getLinkLayerAddress() {
        return bluetoothMacAddress;
    }

    @Override
    public String getLinkLayerMacAddress() throws NetUtil.NoMacAddressException {
        return bluetoothMacAddress;
    }

    @Override
    public boolean isLocal() {
        if(bluetoothMacAddress.equals(BluetoothUtil.getBluetoothMacAddress()))
            return true;

        return false;
    }

    public String getBluetoothDeviceName() {
        BluetoothAdapter adapter = BluetoothUtil.getBluetoothAdapter(RumbleApplication.getContext());
        if(adapter != null) {
            BluetoothDevice remote = adapter.getRemoteDevice(this.bluetoothMacAddress);
            if(remote != null)
                return remote.getName();
            else
                return bluetoothMacAddress;
        } else {
            return bluetoothMacAddress;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o instanceof  BluetoothNeighbour) {
            BluetoothNeighbour neighbour = (BluetoothNeighbour) o;
            return bluetoothMacAddress.equals(neighbour.bluetoothMacAddress);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return bluetoothMacAddress.hashCode();
    }
}

package org.disrupted.ibits.database.objects;

import org.disrupted.ibits.util.HashUtil;

/**
 * @author
 */
public class Interface {

    public static final String TAG = "Interface";

    private long   interfaceDBID;
    private String hash;
    private String macAddress;

    public Interface(long interfaceDBID, String hash, String macAddress) {
        this.interfaceDBID = interfaceDBID;
        this.hash = hash;
        this.macAddress = macAddress;
    }

    public Interface(String macAddress, String protocolID) {
        this.interfaceDBID = -1;
        this.hash = HashUtil.computeInterfaceID(macAddress,protocolID);
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o instanceof Interface) {
            Interface anInterface = (Interface)o;
            return this.hash.equals(anInterface.hash);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.hash.hashCode();
    }

    @Override
    public String toString() {
        return macAddress;
    }


}

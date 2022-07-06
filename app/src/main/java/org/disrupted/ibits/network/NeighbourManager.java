

package org.disrupted.ibits.network;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.network.events.ContactConnected;
import org.disrupted.ibits.network.events.ScannerNeighbourSensed;
import org.disrupted.ibits.network.events.ScannerNeighbourTimeout;
import org.disrupted.ibits.network.linklayer.LinkLayerNeighbour;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothNeighbour;
import org.disrupted.ibits.network.linklayer.events.LinkLayerStopped;
import org.disrupted.ibits.network.linklayer.events.NeighborhoodChanged;
import org.disrupted.ibits.network.events.NeighbourReachable;
import org.disrupted.ibits.network.events.NeighbourUnreachable;
import org.disrupted.ibits.network.linklayer.wifi.WifiNeighbour;
import org.disrupted.ibits.network.protocols.ProtocolChannel;
import org.disrupted.ibits.network.events.ChannelConnected;
import org.disrupted.ibits.network.events.ChannelDisconnected;
import org.disrupted.ibits.network.events.ContactDisconnected;
import org.disrupted.ibits.network.protocols.events.ContactInformationReceived;
import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;
import org.disrupted.ibits.util.NetUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/*
 * This class keeps an up-do-date view of the neighborhood on both a LinkLayer level and a
 * "Contact" level. NeighbourManager merges multiple linklayerneighbour together if it is
 * the same contact (like bluetooth and wifi for the same ContactNeighbour).
 * It works by listenning to the different events:
 *   - NeighbourReachable / NeighbourUnreachable throws by the Scanner
 *   - NeighbourConnected / NeighbourDisconnected throws by the LinkLayerConnection
 *   - ContactInformationReceived throws by ProtocolChannel which identifies a LinkLayerNeighbour
 *
 * NeighbourManager is also the only one to throw a ContactDisconnected event which is thrown
 * whenever a NeighbourDisconnected (for instance a bluetooth connection ended) and no other
 * channels exists to reach the given Contact.
 */
public class NeighbourManager {


    private static final String TAG = "NeighbourManager";

    private final Object managerLock = new Object();

    private class NeighbourDetail {
        public long reachable_time_nano;
        public Set<ProtocolChannel> channels;

        public NeighbourDetail() {
            this.reachable_time_nano = System.nanoTime();
            this.channels = new HashSet<ProtocolChannel>();
        }
    }

    private  Map<LinkLayerNeighbour, NeighbourDetail> neighborhood;
    private  Map<Contact, Set<ProtocolChannel>>   contacts;

    public NeighbourManager() {
        this.neighborhood = new HashMap<>();
        this.contacts = new HashMap<>();
    }

    /*
     * Starting/Stopping the neighbour manager
     */
    public void startMonitoring() {
        EventBus.getDefault().register(this);
    }

    public void stopMonitoring() {
        synchronized (managerLock) {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);

            neighborhood.clear();
            contacts.clear();
        }
    }

    public void onEvent(LinkLayerStopped event) {
        synchronized (managerLock) {
            Iterator<Map.Entry<LinkLayerNeighbour, NeighbourDetail>> it = neighborhood.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<LinkLayerNeighbour, NeighbourDetail> mapEntry = it.next();
                LinkLayerNeighbour neighbour = mapEntry.getKey();
                if (neighbour.getLinkLayerIdentifier().equals(event.linkLayerIdentifier)) {
                    NeighbourDetail detail = mapEntry.getValue();
                    EventBus.getDefault().post(new NeighbourUnreachable(neighbour,
                            detail.reachable_time_nano,
                            System.nanoTime()));
                    it.remove();
                }
            }
        }
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    /**
     * Events thrown by Scanner classes
     */
    public void onEvent(ScannerNeighbourSensed event) {
        if(event.neighbour.isLocal())
            return;
        NeighbourDetail detail;
        synchronized (managerLock) {
            detail = neighborhood.get(event.neighbour);
            if(detail != null)
                return;
            detail = new NeighbourDetail();
            neighborhood.put(event.neighbour, detail);
        }
        EventBus.getDefault().post(new NeighbourReachable(event.neighbour, detail.reachable_time_nano));
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    public void onEvent(ScannerNeighbourTimeout event) {
        if(event.neighbour.isLocal())
            return;
        NeighbourDetail detail;
        synchronized (managerLock) {
            detail = neighborhood.get(event.neighbour);
            if (detail == null)
                return;
            if (!detail.channels.isEmpty())
                return;
            neighborhood.remove(event.neighbour);
        }
        EventBus.getDefault().post(new NeighbourUnreachable(event.neighbour,
                detail.reachable_time_nano, System.nanoTime()));
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    /*
     * Events thrown by ProtocolChannel
     */
    public void onEvent(ChannelConnected event) {
        synchronized (managerLock) {
            NeighbourDetail detail = neighborhood.get(event.neighbour);
            if(detail == null) {
                // it is possible that a peer connect to us before we even detect it
                // but then the server should normally call ScannerNeighbourSensed before
                // accepting the connection
                detail = new NeighbourDetail();
                neighborhood.put(event.neighbour, detail);
            }
            detail.channels.add(event.channel);
        }
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    public void onEvent(ChannelDisconnected event) {
        synchronized (managerLock) {
            NeighbourDetail detail = neighborhood.get(event.neighbour);
            if(detail == null)
                return;
            detail.channels.remove(event.channel);

            // throw ContactDisconnected event if a Contact doesn't have any channel left
            Iterator<Map.Entry<Contact, Set<ProtocolChannel>>> it = contacts.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Contact, Set<ProtocolChannel>> contactEntry = it.next();
                if(contactEntry.getValue().contains(event.channel)) {
                    contactEntry.getValue().remove(event.channel);
                    if(contactEntry.getValue().isEmpty()) {
                        EventBus.getDefault().post(new ContactDisconnected(contactEntry.getKey()));
                        it.remove();
                    }
                }
            }

            /*
             * It is conceptually wrong to remove a neighbour from the neighborhood once the
             * connection has disconnected. But this is only to force a NeighbourReachable
             * Next time it is discover because we don't have yet a ConnectionManager
             */
            if (detail.channels.isEmpty()) {
                neighborhood.remove(event.neighbour);
                EventBus.getDefault().post(new NeighbourUnreachable(event.neighbour,
                        detail.reachable_time_nano,
                        System.nanoTime()));
            }
        }
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    public void onEvent(ContactInformationReceived event) {
        boolean connected = false;
        synchronized (managerLock) {
            Set<ProtocolChannel> channels = contacts.get(event.contact);
            if (channels == null) {
                channels = new HashSet<>();
                contacts.put(event.contact, channels);
                connected = true;
            }
            if (!channels.contains(event.channel))
                channels.add(event.channel);
        }
        if(connected)
            EventBus.getDefault().post(new ContactConnected(event.contact, event.channel));
        EventBus.getDefault().post(new NeighborhoodChanged());
    }

    /*
     * List of neighbour for the UI Adapter
     */
    public interface Neighbour {
        public String getFirstName();
        public String getSecondName();
        public boolean isReachable(String linkLayerIdentifier);
        public boolean isConnected(String linkLayerIdentifier);
    }

    public static class UnknowNeighbour implements Neighbour {

        private LinkLayerNeighbour   neighbour;
        private Set<ProtocolChannel> channels;

        public UnknowNeighbour(LinkLayerNeighbour neighbour, Set<ProtocolChannel> channels) {
            this.neighbour = neighbour;
            this.channels  = new HashSet<>(channels);
        }

        @Override
        public String getFirstName() {
            if(neighbour instanceof BluetoothNeighbour)
                return ((BluetoothNeighbour)neighbour).getBluetoothDeviceName();
            else
                return neighbour.getLinkLayerAddress();
        }

        @Override
        public String getSecondName() {
            if (neighbour instanceof BluetoothNeighbour)
                return ((BluetoothNeighbour) neighbour).getLinkLayerAddress();
            try {
                if (neighbour instanceof WifiNeighbour)
                    return neighbour.getLinkLayerMacAddress();
            } catch(NetUtil.NoMacAddressException ignore) {
            }
            return "";
        }

        @Override
        public boolean isReachable(String linkLayerIdentifier) {
            return neighbour.getLinkLayerIdentifier()
                    .equals(linkLayerIdentifier);
        }

        @Override
        public boolean isConnected(String linkLayerIdentifier) {
            for(ProtocolChannel channel : channels) {
                if(channel.getLinkLayerConnection().getLinkLayerIdentifier()
                        .equals(linkLayerIdentifier))
                    return true;
            }
            return false;
        }
    }

    public static class ContactNeighbour implements Neighbour {

        private Contact contact;
        private Set<ProtocolChannel> channels;

        public ContactNeighbour(Contact contact, Set<ProtocolChannel> channels) {
            this.contact = contact;
            this.channels = new HashSet<>(channels);
        }

        public void addChannel(ProtocolChannel channel) {
            this.channels.add(channel);
        }

        @Override
        public String getFirstName() {
            return contact.getName();
        }

        @Override
        public String getSecondName() {
            return contact.getUid();
        }

        @Override
        public boolean isReachable(String linkLayerIdentifier) {
            for(ProtocolChannel channel : channels) {
                if(channel.getLinkLayerIdentifier().equals(linkLayerIdentifier))
                    return true;
            }
            return false;
        }

        @Override
        public boolean isConnected(String linkLayerIdentifier) {
            for(ProtocolChannel channel : channels) {
                if(channel.getLinkLayerIdentifier().equals(linkLayerIdentifier))
                    return true;
            }
            return false;
        }
    }

    public Set<Neighbour> getNeighbourList(boolean everybody) {
        Set<Neighbour> ret = new HashSet<Neighbour>();

        synchronized (managerLock) {

            for(Map.Entry<Contact, Set<ProtocolChannel>> mapEntry : contacts.entrySet()) {
                Contact contact = mapEntry.getKey();
                Set<ProtocolChannel> channels = mapEntry.getValue();
                ret.add(new ContactNeighbour(contact, channels));
            }

            for(Map.Entry<LinkLayerNeighbour, NeighbourDetail> mapEntry : neighborhood.entrySet()) {
                LinkLayerNeighbour neighbour = mapEntry.getKey();
                NeighbourDetail detail = mapEntry.getValue();
                boolean found = false;

                // we only add this linklayerneighbour if no contact is bound to it
                outer:
                for(ProtocolChannel channel : detail.channels) {
                    Iterator<Map.Entry<Contact,Set<ProtocolChannel>>> it = contacts.entrySet().iterator();
                    while(it.hasNext()) {
                        Map.Entry<Contact,Set<ProtocolChannel>> entry = it.next();
                        Set<ProtocolChannel> contactChannels = entry.getValue();
                        if(contactChannels.contains(channel)) {
                            found = true;
                            break outer;
                        }
                    }
                }
                if(!found) {
                    if(!everybody) {
                        if (neighbour instanceof BluetoothNeighbour) {
                            BluetoothNeighbour btn = (BluetoothNeighbour) neighbour;
                            if (btn.getBluetoothDeviceName() == null)
                                continue;
                            if (!btn.getBluetoothDeviceName().startsWith(RumbleProtocol.RUMBLE_BLUETOOTH_PREFIX))
                                continue;
                        }
                    }
                    ret.add(new UnknowNeighbour(neighbour, detail.channels));
                }
            }
        }

        return ret;
    }

    public ProtocolChannel chooseBestChannel(Contact contact) {
        ProtocolChannel ret = null;
        synchronized (managerLock) {
            Set<ProtocolChannel> channels = contacts.get(contact);
            if(channels == null)
                return null;

            for (ProtocolChannel channel : channels) {
                if (ret == null)
                    ret = channel;
                else
                    ret = (ret.getChannelPriority() >
                            channel.getChannelPriority() ? ret : channel);
            }
        }
        return ret;
    }
}

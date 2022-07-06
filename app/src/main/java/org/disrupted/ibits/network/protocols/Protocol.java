

package org.disrupted.ibits.network.protocols;


import org.disrupted.ibits.network.NetworkCoordinator;
import org.disrupted.ibits.network.linklayer.events.LinkLayerStarted;
import org.disrupted.ibits.network.linklayer.events.LinkLayerStopped;
import org.disrupted.ibits.network.events.NeighbourReachable;
import org.disrupted.ibits.network.events.NeighbourUnreachable;

/**
 * @author
 */
public interface Protocol {

    public NetworkCoordinator getNetworkCoordinator();

    /*
     * priority is used to determine which protocol is best when multiple protocol
     * are available to a certain contact
     */
    public static final int PROTOCOL_HIGH_PRIORITY   = 2;
    public static final int PROTOCOL_MIDDLE_PRIORITY = 1;
    public static final int PROTOCOL_LOW_PRIORITY    = 0;
    public abstract int getProtocolPriority();

    /*
     * Protocol identification
     */
    public String getProtocolIdentifier();

    /*
     * Protocol management
     */
    public void protocolStart();

    public void protocolStop();

    /*
     * Protocol must catch those event to deal with clients
     */
    public void onEvent(LinkLayerStarted event);

    public void onEvent(LinkLayerStopped event);

    public void onEvent(NeighbourReachable event);

    public void onEvent(NeighbourUnreachable event);

}

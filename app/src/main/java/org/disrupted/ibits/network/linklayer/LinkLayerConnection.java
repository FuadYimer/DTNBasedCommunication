package org.disrupted.ibits.network.linklayer;

import org.disrupted.ibits.network.linklayer.exception.LinkLayerConnectionException;


/**
 * LinkLayerConnection is an interface for a class to implement connect() and disconnect()
 * method that are link-layer specifics.
 *
 * @author Lucien Loiseau
 */
public interface LinkLayerConnection {

    /*
     * priority is used to determine which channel is the best when multiple channel
     * are available to a certain neighbour
     */
    public static final int LINK_LAYER_HIGH_PRIORITY = 10;
    public static final int LINK_LAYER_MIDDLE_PRIORITY = 5;
    public static final int LINK_LAYER_LOW_PRIORITY = 0;

    public int getLinkLayerPriority();

    public String getLinkLayerIdentifier();

    public String getConnectionID();

    public void connect() throws LinkLayerConnectionException;

    public void disconnect() throws LinkLayerConnectionException;

    public LinkLayerNeighbour getLinkLayerNeighbour();
}

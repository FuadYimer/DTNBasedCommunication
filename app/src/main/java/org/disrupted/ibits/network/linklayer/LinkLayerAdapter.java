

package org.disrupted.ibits.network.linklayer;

/**
 * LinkLayerAdapter is an Interface that is responsible of managing a LinkLayer interface such
 * as Bluetooth or Wifi. It is directly under the responsibility of NetworkCoordinator and
 * all the LinkLayerAdapter methods are called from it.
 *
 * @author Lucien Loiseau
 */
public interface LinkLayerAdapter {

     boolean isActivated();

     String getLinkLayerIdentifier();

     void linkStart();

     void linkStop();

}

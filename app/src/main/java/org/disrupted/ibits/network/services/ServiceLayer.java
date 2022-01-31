package org.disrupted.ibits.network.services;

/**
 * @author Lucien Loiseau
 */
public interface ServiceLayer {

    //public void register(String protocolIdentifier);

    //public void unregister(String protocolIdentifier);

    public String getServiceIdentifier();

    public void startService();

    public void stopService();
}



package org.disrupted.ibits.network;

/**
 * A worker is a thread, running into a WorkerPool. Because of resource limitation, we maintain
 * a fixed number of worker in the pool. When a worker is added to the pool, the pool will only
 * start the thread (by calling startWorker) if there is enough resources.
 *
 * A worker is also bounded to a specific protocol and a specifil linklayer, in other word,
 * he is the glue between one (or multiple) link-layer neighbour (Bluetooth, IPv4, IPv6) and
 * a protocol (like firechat or ibits).
 *
 * @author
 */
public interface Worker {

    public String getWorkerIdentifier();

    public String getProtocolIdentifier();

    public String getLinkLayerIdentifier();

    public void cancelWorker();

    public void startWorker();

    public void stopWorker();

    public boolean isWorking();

}

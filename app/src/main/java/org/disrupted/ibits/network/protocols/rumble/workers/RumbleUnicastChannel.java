
package org.disrupted.ibits.network.protocols.rumble.workers;

import android.os.Handler;
import org.disrupted.ibits.util.Log;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.network.linklayer.UnicastConnection;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothClientConnection;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothConnection;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothServerConnection;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.linklayer.exception.LinkLayerConnectionException;
import org.disrupted.ibits.network.protocols.ProtocolChannel;
import org.disrupted.ibits.network.protocols.command.Command;
import org.disrupted.ibits.network.protocols.command.CommandSendKeepAlive;
import org.disrupted.ibits.network.protocols.events.CommandExecuted;
import org.disrupted.ibits.network.protocols.events.ContactInformationReceived;
import org.disrupted.ibits.network.events.ChannelConnected;
import org.disrupted.ibits.network.events.ChannelDisconnected;
import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;
import org.disrupted.ibits.network.protocols.rumble.RumbleStateMachine;
import org.disrupted.ibits.network.protocols.rumble.packetformat.BlockHeader;
import org.disrupted.ibits.network.protocols.rumble.packetformat.BlockProcessor;
import org.disrupted.ibits.network.protocols.rumble.packetformat.CommandProcessor;
import org.disrupted.ibits.network.protocols.rumble.packetformat.exceptions.MalformedBlock;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class RumbleUnicastChannel extends ProtocolChannel {

    private static final String TAG = "RumbleUnicastChannel";

    private static final int KEEP_ALIVE_TIME = 2000;
    private static final int SOCKET_TIMEOUT_UDP  = 5000;
    private static final int SOCKET_TIMEOUT_BLUETOOTH  = 20000;

    private boolean working;
    private Contact remoteContact;

    private BlockProcessor   blockProcessor;
    private CommandProcessor commandProcessor;
    private Handler keepAlive;
    private Handler socketTimeout;

    public RumbleUnicastChannel(RumbleProtocol protocol, UnicastConnection con) {
        super(protocol, con);
        remoteContact = null;
        keepAlive     = new Handler(protocol.getNetworkCoordinator().getServiceLooper());
        socketTimeout = new Handler(protocol.getNetworkCoordinator().getServiceLooper());
    }

    @Override
    public void cancelWorker() {
        RumbleStateMachine connectionState = ((RumbleProtocol)protocol).getState(
                con.getLinkLayerNeighbour().getLinkLayerAddress());
        if(working) {
            Log.e(TAG, "[!] should not call cancelWorker() on a working Worker, call stopWorker() instead !");
            stopWorker();
        } else
            connectionState.notConnected();
    }

    @Override
    public void startWorker() {
        if(isWorking())
            return;
        working = true;
        EventBus.getDefault().register(this);

        RumbleProtocol     rumbleProtocol = (RumbleProtocol)protocol;
        RumbleStateMachine connectionState = rumbleProtocol.getState(
                con.getLinkLayerNeighbour().getLinkLayerAddress());

        try {
            if (con instanceof BluetoothClientConnection) {
                if (!connectionState.getState().equals(RumbleStateMachine.RumbleState.CONNECTION_SCHEDULED))
                    throw new RumbleStateMachine.StateException();

                ((BluetoothClientConnection) con).waitScannerToStop();
            }

            con.connect();

            try {
                connectionState.lock.lock();
                connectionState.connected(getWorkerIdentifier());
            } finally {
                connectionState.lock.unlock();
            }

            /*
             * Bluetooth hack to synchronise the client and server
             * if I don't do this, they sometime fail to connect ? :/ ?
             */
            if (con instanceof BluetoothServerConnection)
                ((BluetoothConnection)con).getOutputStream().write(new byte[]{0},0,1);
            if (con instanceof BluetoothClientConnection)
                ((BluetoothConnection)con).getInputStream().read(new byte[1], 0, 1);

        } catch (RumbleStateMachine.StateException state) {
            Log.e(TAG, "[-] client connected while trying to connect");
            stopWorker();
            return;
        } catch (LinkLayerConnectionException llce) {
            Log.e(TAG, "[!] FAILED CON: " + getWorkerIdentifier() + " - " + llce.getMessage());
            stopWorker();
            connectionState.notConnected();
            return;
        } catch (IOException io) {
            Log.e(TAG, "[!] FAILED CON: " + getWorkerIdentifier() + " - " + io.getMessage());
            stopWorker();
            connectionState.notConnected();
            return;
        }

        try {
            Log.d(TAG, "[+] connected");
            EventBus.getDefault().post(new ChannelConnected(
                            con.getLinkLayerNeighbour(),
                            this)
            );

            onChannelConnected();
        } finally {
            Log.d(TAG, "[+] disconnected");
            EventBus.getDefault().post(new ChannelDisconnected(
                            con.getLinkLayerNeighbour(),
                            this,
                            error)
            );
            stopWorker();
            connectionState.notConnected();
        }
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    protected void processingPacketFromNetwork(){
        try {
            android.util.Log.d("CheckDebug", "RumbleUnicastChannel :  Processing Packet From Netowrk");
            InputStream in = ((UnicastConnection)this.getLinkLayerConnection()).getInputStream();
            blockProcessor = new BlockProcessor(in, this);
            while (true) {
                // read next block header (blocking)
                BlockHeader header = BlockHeader.readBlockHeader(in);

                // channel is alive, cancel timeout during block processing
                socketTimeout.removeCallbacks(socketTimeoutFires);

                // process block
                blockProcessor.processBlock(header);

                // set timeout
                if(con instanceof BluetoothConnection)
                    socketTimeout.postDelayed(socketTimeoutFires, SOCKET_TIMEOUT_BLUETOOTH);
                else
                    socketTimeout.postDelayed(socketTimeoutFires, SOCKET_TIMEOUT_UDP);
            }
        } catch (IOException silentlyCloseConnection) {
            Log.d(TAG, " "+silentlyCloseConnection.getMessage());
        } catch (InputOutputStreamException silentlyCloseConnection) {
            Log.d(TAG, " "+silentlyCloseConnection.getMessage());
        } catch (MalformedBlock e) {
            error = true;
            Log.d(TAG, "[!] malformed block: " + e.reason + "("+e.bytesRead+")");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onCommandReceived(Command command) {
        try {
            if(commandProcessor == null)
                commandProcessor = new CommandProcessor(((UnicastConnection)this.getLinkLayerConnection()).getOutputStream(), this);

            // remove keep alive if any
            keepAlive.removeCallbacks(keepAliveFires);

            commandProcessor.processCommand(command);

            if(!command.getCommandID().equals(Command.CommandID.SEND_KEEP_ALIVE))
                EventBus.getDefault().post(new CommandExecuted(this, command, true));

            // schedule a keep alive to send
            keepAlive.postDelayed(keepAliveFires, KEEP_ALIVE_TIME);

            //EventBus.getDefault().post(new CommandExecuted(this, command, false));
            return true;
        } catch(InputOutputStreamException ignore) {
            ignore.printStackTrace();
            Log.d(TAG, "[!] "+command.getCommandID()+" "+ignore.getMessage());
        } catch(IOException ignore){
            ignore.printStackTrace();
            Log.d(TAG, "[!] "+command.getCommandID()+" "+ignore.getMessage());
        }
        return false;
    }

    @Override
    public void stopWorker() {
        if(!working)
            return;
        working = false;
        try {
            con.disconnect();
        } catch (LinkLayerConnectionException ignore) {
            //Log.d(TAG, "[-]"+ignore.getMessage());
        }
        finally {
            keepAlive.removeCallbacks(keepAliveFires);
            socketTimeout.removeCallbacks(socketTimeoutFires);
            if(EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public Set<Contact> getRecipientList() {
        Set<Contact> ret = new HashSet<Contact>(1);
        if(remoteContact != null)
            ret.add(remoteContact);
        return ret;
    }
    public void onEvent(ContactInformationReceived event) {
        if(event.channel.equals(this))
            this.remoteContact = event.contact;
    }

    /*
     * keep-alive handler related method
     */
    private Runnable keepAliveFires = new Runnable() {
        @Override
        public void run() {
            CommandSendKeepAlive sendKeepAlive = new CommandSendKeepAlive();
            //executeNonBlocking(sendKeepAlive);
        }
    };
    private Runnable socketTimeoutFires = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "channel seems dead");
            //stopWorker();
        }
    };
}

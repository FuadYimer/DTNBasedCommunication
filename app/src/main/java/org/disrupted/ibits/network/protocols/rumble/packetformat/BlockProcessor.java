package org.disrupted.ibits.network.protocols.rumble.packetformat;

import android.util.Log;

import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.objects.Group;
import org.disrupted.ibits.network.linklayer.UnicastConnection;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.protocols.ProtocolChannel;
import org.disrupted.ibits.network.protocols.events.ChatMessageReceived;
import org.disrupted.ibits.network.protocols.events.ContactInformationReceived;
import org.disrupted.ibits.network.protocols.events.FileReceived;
import org.disrupted.ibits.network.protocols.events.PushStatusReceived;
import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;
import org.disrupted.ibits.network.protocols.rumble.packetformat.exceptions.MalformedBlock;
import org.disrupted.ibits.util.EncryptedInputStream;
import org.disrupted.ibits.util.CryptoUtil;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class BlockProcessor {

    public static final String TAG = "BlockProcessor";

    /* necessary attributes */
    private InputStream in;
    private ProtocolChannel channel;

    /* bundle context, reset at the end of every bundle (when last_block flag is set) */
    private EncryptedInputStream eis;
    private BlockPushStatus blockPushStatus;

    public BlockProcessor(InputStream in, ProtocolChannel channel) {
        this.in = in;
        this.channel = channel;
        resetContext();
    }

    public void resetContext() {
        try {
            if (eis != null)
                eis.close();
        } catch(IOException e){ //ignore
        }
        eis = null;
        blockPushStatus = null;
    }

    public void processBlock(BlockHeader header) throws IOException, InputOutputStreamException, MalformedBlock {
        long timeToTransfer = System.nanoTime();

        if(header.isEncrypted() && (eis == null)) {
            BlockNull nullBlock = new BlockNull(header);
            channel.bytes_received += nullBlock.readBlock(in);
            channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
        } else {
            InputStream is = in;
            if(header.isEncrypted()) {
                eis.setLimit((int)header.getBlockLength());
                is = eis;
            }
            switch (header.getBlockType()) {
                case BlockHeader.BLOCKTYPE_PUSH_STATUS:
                    Log.d("CheckDebug", "BlockProcessor: BLOCKTYPE_PUSH_STATUS ");
                    BlockPushStatus blockStatus = new BlockPushStatus(header);
                    channel.bytes_received += blockStatus.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    if(!blockStatus.status.hasAttachedFile()) {
                        channel.status_received++;
                        EventBus.getDefault().post(new PushStatusReceived(
                                        blockStatus.status,
                                        blockStatus.group_id_base64,
                                        blockStatus.sender_id_base64,
                                        "",
                                        RumbleProtocol.protocolID,
                                        channel.getLinkLayerIdentifier())
                        );
                        blockPushStatus = null;
                    } else {
                        blockPushStatus = blockStatus;
                    }
                    break;
                case BlockHeader.BLOCKTYPE_FILE:
                    Log.d("CheckDebug", "BlockProcessor: BLOCKTYPE_FILE");
                    BlockFile blockFile = new BlockFile(header);
                    channel.bytes_received += blockFile.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    if(blockPushStatus != null) {
                        channel.status_received++;
                        EventBus.getDefault().post(new PushStatusReceived(
                                        blockPushStatus.status,
                                        blockPushStatus.group_id_base64,
                                        blockPushStatus.sender_id_base64,
                                        blockFile.filename,
                                        RumbleProtocol.protocolID,
                                        channel.getLinkLayerIdentifier())
                        );
                    } else {
                        EventBus.getDefault().post(new FileReceived(
                                        blockFile.filename,
                                        blockFile.status_id_base64,
                                        RumbleProtocol.protocolID,
                                        channel.getLinkLayerIdentifier())
                        );
                    }
                    break;
                case BlockHeader.BLOCKTYPE_CONTACT:
                    BlockContact blockContact = new BlockContact(header);
                    channel.bytes_received += blockContact.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    UnicastConnection con = (UnicastConnection)channel.getLinkLayerConnection();
                    EventBus.getDefault().post(new ContactInformationReceived(
                                    blockContact.contact,
                                    blockContact.flags,
                                    channel,
                                    con.getLinkLayerNeighbour())
                    );
                    break;
                case BlockHeader.BLOCKTYPE_CHAT_MESSAGE:
                    BlockChatMessage blockChatMessage = new BlockChatMessage(header);
                    channel.bytes_received += blockChatMessage.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    EventBus.getDefault().post(new ChatMessageReceived(
                                    blockChatMessage.chatMessage,
                                    channel)
                    );
                    break;
                case BlockHeader.BLOCKTYPE_KEEPALIVE:
                    BlockKeepAlive blockKA = new BlockKeepAlive(header);
                    channel.bytes_received += blockKA.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    break;
                case BlockHeader.BLOCK_CIPHER:
                    BlockCipher blockCipher = new BlockCipher(header);
                    channel.bytes_received += blockCipher.readBlock(is);
                    channel.in_transmission_time += (System.nanoTime() - timeToTransfer);
                    if (blockCipher.type.equals(BlockCipher.CipherType.TYPE_CIPHER_GROUP)
                            && (blockCipher.group_id_base64 != null)
                            && (blockCipher.ivBytes != null)){
                        try {
                            Group group = DatabaseFactory.getGroupDatabase(RumbleApplication.getContext())
                                    .getGroup(blockCipher.group_id_base64);
                            if (group == null)
                                throw new CryptoUtil.CryptographicException();
                            eis = CryptoUtil.getCipherInputStream(
                                    in,
                                    blockCipher.algo,
                                    blockCipher.block,
                                    blockCipher.padding,
                                    group.getGroupKey(),
                                    blockCipher.ivBytes);
                        } catch (CryptoUtil.CryptographicException e) {
                            eis = null;
                        }
                    } else {
                        eis = null;
                    }
                    blockCipher.dismiss();
                    break;
                default:
                    throw new MalformedBlock("Unknown header type: " + header.getBlockType(), 0);
            }
        }

        if(header.isLastBlock()) {
            resetContext();
        }
    }

}


package org.disrupted.ibits.network.protocols.rumble.packetformat;

import android.util.Log;

import org.disrupted.ibits.database.objects.PushStatus;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothLinkLayerAdapter;
import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.protocols.ProtocolChannel;
import org.disrupted.ibits.network.protocols.command.Command;
import org.disrupted.ibits.network.protocols.command.CommandSendChatMessage;
import org.disrupted.ibits.network.protocols.command.CommandSendKeepAlive;
import org.disrupted.ibits.network.protocols.command.CommandSendLocalInformation;
import org.disrupted.ibits.network.protocols.command.CommandSendPushStatus;
import org.disrupted.ibits.network.protocols.events.ChatMessageSent;
import org.disrupted.ibits.network.protocols.events.ContactInformationSent;
import org.disrupted.ibits.network.protocols.events.PushStatusSent;
import org.disrupted.ibits.network.protocols.rumble.RumbleProtocol;
import org.disrupted.ibits.util.CryptoUtil;
import org.disrupted.ibits.util.EncryptedOutputStream;
import org.disrupted.ibits.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class CommandProcessor {

    public static final String TAG = "CommandProcessor";

    private ProtocolChannel channel;
    private OutputStream out;

    public CommandProcessor(OutputStream out, ProtocolChannel channel) {
        this.out = out;
        this.channel = channel;
    }

    public boolean processCommand(Command command) throws InputOutputStreamException, IOException{

        int bytes_transmitted = 0;
        long timeToTransfer = System.nanoTime();

        switch (command.getCommandID()) {
            case SEND_LOCAL_INFORMATION:
                Log.d("CheckDebug",  "Here at Command Processor: SEND_LOCAL_INFORMATION");
                BlockContact blockContact = new BlockContact((CommandSendLocalInformation) command);
                bytes_transmitted += blockContact.writeBlock(out, null);
                channel.out_transmission_time += (System.currentTimeMillis() - timeToTransfer);
                EventBus.getDefault().post(new ContactInformationSent(
                                blockContact.contact,
                                channel)
                );
                break;
            case SEND_CHAT_MESSAGE:
                BlockChatMessage blockChatMessage = new BlockChatMessage((CommandSendChatMessage) command);
                blockChatMessage.writeBlock(out, null);
                channel.out_transmission_time += (System.nanoTime() - timeToTransfer);
                EventBus.getDefault().post(new ChatMessageSent(
                                blockChatMessage.chatMessage,
                                RumbleProtocol.protocolID,
                                BluetoothLinkLayerAdapter.LinkLayerIdentifier)
                );
                break;
            case SEND_KEEP_ALIVE:
                Log.d("CheckDebug",  "Here at Command Processor: SEND_KEEP_ALIVE");
                BlockKeepAlive blockKA = new BlockKeepAlive((CommandSendKeepAlive) command);
                blockKA.writeBlock(out, null);
                break;
            case SEND_PUSH_STATUS:
                Log.d("CheckDebug",  "Here at Command Processor: SEND_PUSH_STATUS");
                CommandSendPushStatus commandSendPushStatus = (CommandSendPushStatus) command;
                PushStatus status = commandSendPushStatus.getStatus();

                /* prepare the blockstatus and blockfile for attached file, if any */
                BlockPushStatus blockPushStatus = new BlockPushStatus(commandSendPushStatus);
                BlockFile blockFile = null;
                if(status.hasAttachedFile()) {
                    File attachedFile;
                    if(status.getFileName().endsWith(".zip")){
                        attachedFile = new File(FileUtil.getReadableZipStorageDir(), status.getFileName());
                    }else{
                        attachedFile = new File(FileUtil.getReadableAlbumStorageDir(), status.getFileName());
                    }

                    if(!(attachedFile.exists() && attachedFile.isFile())) {
                        BlockDebug.e(TAG, "attached file doesn't exist, abort sending push status");
                        return false;
                    }
                    blockFile = new BlockFile(status.getFileName(), status.getUuid());
                }

                /* if the group is private, send a BlockCipher AES128/CBC/PKCS5 first */
                EncryptedOutputStream eos = null;
                if(status.getGroup().isPrivate()) {
                    try {
                        byte[] iv = CryptoUtil.generateRandomIV(16);
                        eos = CryptoUtil.getCipherOutputStream(out,
                                CryptoUtil.CipherAlgo.ALGO_AES,
                                CryptoUtil.CipherBlock.BLOCK_CBC,
                                CryptoUtil.CipherPadding.PADDING_PKCS5,
                                status.getGroup().getGroupKey(),
                                iv);
                        BlockCipher blockCipher = new BlockCipher(status.getGroup().getGid(), iv);
                        blockCipher.header.setLastBlock(false);
                        bytes_transmitted += blockCipher.writeBlock(out, eos);
                        blockCipher.dismiss();
                    } catch(CryptoUtil.CryptographicException e) {
                        BlockDebug.e(TAG, "cannot send PushStatus, failed to setup encrypted stream", e);
                        return false;
                    }
                }

                /* send block status */
                blockPushStatus.header.setLastBlock(!status.hasAttachedFile() && (eos == null));
                blockPushStatus.header.setEncrypted(eos != null);
                bytes_transmitted+=blockPushStatus.writeBlock(out, eos);
                if(eos != null)
                    eos.flush();
                blockPushStatus.dismiss();

                /* send block file if any */
                if(blockFile != null) {
                    blockFile.header.setLastBlock(eos == null);
                    blockFile.header.setEncrypted(eos != null);
                    bytes_transmitted += blockFile.writeBlock(out, eos);
                    if(eos != null)
                        eos.flush();
                    blockFile.dismiss();
                }

                /* send a cleartext block */
                if(eos != null) {
                    BlockCipher cleartext = new BlockCipher();
                    cleartext.header.setLastBlock(true);
                    bytes_transmitted+=cleartext.writeBlock(out, eos);
                    cleartext.dismiss();
                    eos.close();
                }

                channel.status_sent++;
                channel.out_transmission_time += (System.nanoTime() - timeToTransfer);
                EventBus.getDefault().post(new PushStatusSent(
                                status,
                                channel.getRecipientList(),
                                RumbleProtocol.protocolID,
                                BluetoothLinkLayerAdapter.LinkLayerIdentifier)
                );

                break;
            default:
                return false;
        }

        channel.out_transmission_time += timeToTransfer;
        channel.bytes_sent += bytes_transmitted;
        return true;
    }
}

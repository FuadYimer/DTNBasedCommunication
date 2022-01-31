
package org.disrupted.ibits.util;

import android.util.Base64;

import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.database.objects.Group;
import org.disrupted.ibits.database.objects.PushStatus;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author
 */
public class HashUtil {

    public static final int expectedEncodedSize(int size) {
        return (int)(4*Math.ceil((double) size/3));
    }

    public static final String computeInterfaceID(String linkLayerAddress,String protocol) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(linkLayerAddress.getBytes());
            md.update(protocol.getBytes());
            return Base64.encodeToString(md.digest(), 0, 16, Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    public static final String computeStatusUUID(String author_uid, String group_gid, String post, long timeOfCreation) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(author_uid.getBytes());
            md.update(group_gid.getBytes());
            md.update(post.getBytes());
            md.update(ByteBuffer.allocate(8).putLong(timeOfCreation).array());
            return Base64.encodeToString(md.digest(),0, PushStatus.STATUS_ID_RAW_SIZE,Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    public static final String computeChatMessageUUID(String author_uid, String message, long timeOfCreation) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(author_uid.getBytes());
            md.update(message.getBytes());
            md.update(ByteBuffer.allocate(8).putLong(timeOfCreation).array());
            return Base64.encodeToString(md.digest(),0,PushStatus.STATUS_ID_RAW_SIZE,Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    public static final String computeContactUid(String name, long time) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(name.getBytes());
            md.update(ByteBuffer.allocate(8).putLong(time).array());
            return Base64.encodeToString(md.digest(),0, Contact.CONTACT_UID_RAW_SIZE,Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    public static final String computeGroupUid(String name, boolean isPrivate) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(name.getBytes());
            if(isPrivate)
                md.update(ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array());
            return Base64.encodeToString(md.digest(),0, Group.GROUP_GID_RAW_SIZE,Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    public static boolean isBase64Encoded(String str)
    {
        try
        {
            byte[] data = Base64.decode(str, Base64.NO_WRAP);
            return true;
        } catch(Exception e)
        {
            return false;
        }
    }

    public static String generateRandomString(int size) {
        char[] chars = "ABCDEF+=012!GHIJKL@345MNOPQR678STUVWXYZ9/".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            char c1 = chars[random.nextInt(chars.length)];
            sb.append(c1);
        }
        return sb.toString();
    }
}

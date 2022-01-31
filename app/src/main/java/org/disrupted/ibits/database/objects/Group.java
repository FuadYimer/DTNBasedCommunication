package org.disrupted.ibits.database.objects;

import android.util.Base64;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.util.CryptoUtil;
import org.disrupted.ibits.util.HashUtil;

import java.nio.ByteBuffer;

import javax.crypto.SecretKey;

/**
 * @author
 */
public class Group {

    public static final String TAG = "Group";

    public static final String DEFAULT_PUBLIC_GROUP = "ibits.public";
    public static final int GROUP_NAME_MAX_SIZE = 50;
    public static final int GROUP_DESC_MAX_SIZE = 140;
    public static final int GROUP_GID_RAW_SIZE  = 8;
    public static final int GROUP_KEY_AES_SIZE  = CryptoUtil.KEYSIZE/8;

    public static final Group NOGROUP = new Group("nogroup","nogroup",null);

    private String    name;
    private String    gid;
    private SecretKey key;
    private String    desc;
    private boolean   isPrivate;

    public static Group getDefaultGroup() {
        try {
            Group defaultGroup = createNewGroup(DEFAULT_PUBLIC_GROUP, false);
            defaultGroup.setDesc(RumbleApplication.getContext().getString(R.string.default_rumble_group_desc));
            return defaultGroup;
        } catch ( CryptoUtil.CryptographicException impossible ) {
            return null;
        }
    }

    public static Group createNewGroup(String name, boolean isPrivate) throws CryptoUtil.CryptographicException{
        SecretKey key = null;
        if(isPrivate)
            key = CryptoUtil.generateRandomAESKey();
        String gid = HashUtil.computeGroupUid(name, isPrivate);
        return new Group(name, gid, key);
    }

    public Group(String name, String gid, SecretKey key) {
        this.name = name;
        this.gid  = gid;
        this.key  = key;
        this.isPrivate = (key != null);
        this.desc = "";
    }

    public final String getName() {        return name;      }
    public final String getGid() {         return gid;       }
    public final SecretKey getGroupKey() { return key;       }
    public boolean isPrivate() {         return isPrivate; }
    public final String getDesc() {        return desc;      }

    public void setDesc(String desc) {     this.desc = desc; }


    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o instanceof Group) {
            Group group = (Group)o;
            return this.gid.equals(group.gid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.gid.hashCode();
    }

    @Override
    public String toString() {
        return "Group Name="+name+" GID="+gid;
    }

    public String getGroupBase64ID() {
        ByteBuffer byteBuffer;
        byte[] keybytes;
        if(isPrivate())
            keybytes = key.getEncoded();
        else
            keybytes = new byte[0];

        byteBuffer = ByteBuffer.allocate(2 + name.length() + gid.length() + keybytes.length);

        // encode group name
        byteBuffer.put((byte)name.length());
        byteBuffer.put(name.getBytes(),0,name.length());

        // encode group ID
        byteBuffer.put((byte)gid.length());
        byteBuffer.put(gid.getBytes());

        // encode key
        byteBuffer.put(keybytes);
        return Base64.encodeToString(byteBuffer.array(),Base64.NO_WRAP);
    }

    public static Group getGroupFromBase64ID(String base64ID) {
        try {
            if(!HashUtil.isBase64Encoded(base64ID))
                throw new Exception();

            byte[] resultbytes = Base64.decode(base64ID, Base64.NO_WRAP);
            ByteBuffer byteBuffer = ByteBuffer.wrap(resultbytes);

            // extract group name
            int namesize = byteBuffer.get();
            if ((namesize < 0) || (namesize > Group.GROUP_NAME_MAX_SIZE))
                throw new Exception();
            byte[] name = new byte[namesize];
            byteBuffer.get(name, 0, namesize);

            // extract group ID
            int gidsize = byteBuffer.get();
            if ((gidsize < 0) || (gidsize > HashUtil.expectedEncodedSize(Group.GROUP_GID_RAW_SIZE)))
                throw new Exception();
            byte[] gid = new byte[gidsize];
            byteBuffer.get(gid, 0, gidsize);

            // extract group Key
            int keysize = (resultbytes.length - 2 - namesize - gidsize);
            if ((keysize < 0) || (keysize > HashUtil.expectedEncodedSize(Group.GROUP_KEY_AES_SIZE)))
                throw new Exception();
            byte[] key = new byte[keysize];
            byteBuffer.get(key, 0, keysize);

            return new Group(new String(name), new String(gid), CryptoUtil.getSecretKeyFromByteArray(key));
        } catch(Exception e) {
            return null;
        }
    }
}

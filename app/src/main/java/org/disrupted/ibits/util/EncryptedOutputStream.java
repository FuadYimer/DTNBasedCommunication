

package org.disrupted.ibits.util;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NullCipher;

/**
 * @author
 */
public class EncryptedOutputStream extends FilterOutputStream {

    private final Cipher cipher;

    public EncryptedOutputStream(OutputStream os, Cipher c) {
        super(new BufferedOutputStream(os));
        cipher = c;
    }

    protected EncryptedOutputStream(OutputStream os) {
        this(os, new NullCipher());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        byte[] result = cipher.update(b, off, len);
        if (result != null) {
            out.write(result);
        }
    }

    @Override
    public void flush() throws IOException {
        byte[] result;
        try {
            if (cipher != null) {
                result = cipher.doFinal();
                if (result != null) {
                    out.write(result);
                }
            }
            if (out != null) {
                out.flush();
            }
        } catch (BadPaddingException e) {
            throw new IOException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (out != null)
            out.flush();
    }
}

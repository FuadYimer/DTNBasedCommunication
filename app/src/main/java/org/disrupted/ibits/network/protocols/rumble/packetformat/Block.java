/*
 * Copyright (C) 2014 Lucien Loiseau
 *
 * This file is part of Rumble.
 *
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.ibits.network.protocols.rumble.packetformat;

import org.disrupted.ibits.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.ibits.network.protocols.rumble.packetformat.exceptions.MalformedBlockPayload;
import org.disrupted.ibits.util.EncryptedOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author
 */
public abstract class Block {

    public BlockHeader  header;

    public Block(BlockHeader header) {
        this.header = header;
    }

    public abstract long readBlock(InputStream in) throws MalformedBlockPayload, IOException, InputOutputStreamException;

    public abstract long writeBlock(OutputStream out, EncryptedOutputStream eos) throws IOException, InputOutputStreamException;

    public abstract void dismiss();

}

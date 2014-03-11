package org.linuxguy.movemoov;

import java.io.IOException;

/**
 * Created by Adam Jensen on 3/8/14
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */

public class Atom {
    public String type;
    public int    size;
    public long   startPos;

    public Atom(int size, String type) {
        this.size = size;
        this.type = type;
        this.startPos = -1;
    }

    public static Atom fromStream(MoovByteArrayInputStream inStream) throws IOException {
        return new Atom(inStream.readInt(), inStream.readType());
    }

    public static Atom fromStream(MP4FileInputStream inStream) throws IOException {
        return new Atom(inStream.readInt(), inStream.readType());
    }

    public boolean isHeaderType() {
        return type != null &&
              (Constants.TYPE_FTYP.equalsIgnoreCase(type) ||
               Constants.TYPE_MOOV.equalsIgnoreCase(type) ||
               Constants.TYPE_FREE.equalsIgnoreCase(type));
    }

    public boolean isChunkOffsetType() {
        return type != null &&
              (Constants.TYPE_STCO.equalsIgnoreCase(type) ||
               Constants.TYPE_CO64.equalsIgnoreCase(type));
    }

    public boolean isParentType() {
        return type != null &&
              (Constants.TYPE_TRAK.equalsIgnoreCase(type) ||
               Constants.TYPE_MDIA.equalsIgnoreCase(type) ||
               Constants.TYPE_MINF.equalsIgnoreCase(type) ||
               Constants.TYPE_STBL.equalsIgnoreCase(type));
    }

    @Override
    public String toString() {
        return String.format("[type: %s, size: %d, position: %d", type, size, startPos);
    }
}
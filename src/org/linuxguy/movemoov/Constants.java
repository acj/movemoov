package org.linuxguy.movemoov;

/**
 * Created by Adam Jensen on 3/8/14.
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
public class Constants {
    static final int CHUNK_SIZE        = 8192;
    static final int BOX_TYPE_BYTES    = 4;
    static final int BOX_LENGTH_BYTES  = 4;
    static final int BOX_VERSION_BYTES = 2;
    static final int BOX_FLAGS_BYTES   = 2;

    static final String TYPE_FTYP = "ftyp";
    static final String TYPE_MOOV = "moov";
    static final String TYPE_MDAT = "mdat";
    static final String TYPE_FREE = "free";

    // Chunk Offset Atoms
    static final String TYPE_STCO = "stco";
    static final String TYPE_CO64 = "co64";

    // Parent Atoms
    static final String TYPE_TRAK = "trak";
    static final String TYPE_MDIA = "mdia";
    static final String TYPE_MINF = "minf";
    static final String TYPE_STBL = "stbl";
}

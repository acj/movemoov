package org.linuxguy.movemoov;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Adam Jensen on 3/8/14.
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
class MoovByteArrayInputStream extends ByteArrayInputStream {
    private byte[]     mShortBuffer;
    private byte[]     mLongBuffer;

    public MoovByteArrayInputStream(byte[] bytes, int i, int i2) {
        super(bytes, i, i2);

        allocateBuffers();
    }

    public MoovByteArrayInputStream(byte[] bytes) {
        super(bytes);

        allocateBuffers();
    }

    private void allocateBuffers() {
        mShortBuffer = new byte[Integer.SIZE / 8];
        mLongBuffer = new byte[Long.SIZE / 8];
    }

    public int getCurrentPosition() {
        return pos;
    }

    public int readInt() throws IOException {
        read(mShortBuffer);

        return ByteHelpers.toInt(mShortBuffer);
    }

    public long readLong() throws IOException {
        read(mLongBuffer);

        return ByteHelpers.toLong(mLongBuffer);
    }

    public String readType() throws IOException {
        read(mShortBuffer);

        return new String(mShortBuffer);
    }
}

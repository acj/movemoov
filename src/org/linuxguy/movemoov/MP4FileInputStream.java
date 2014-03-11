package org.linuxguy.movemoov;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Adam Jensen on 3/8/14.
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
class MP4FileInputStream extends FileInputStream {
    private byte[]     mShortBuffer;
    private byte[]     mLongBuffer;

    public MP4FileInputStream(File file) throws FileNotFoundException {
        super(file);

        allocateBuffers();
    }

    private void allocateBuffers() {
        mShortBuffer = new byte[Integer.SIZE / 8];
        mLongBuffer = new byte[Long.SIZE / 8];
    }

    public int readInt() throws IOException {
        read(mShortBuffer);

        return ByteHelpers.toInt(mLongBuffer);
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

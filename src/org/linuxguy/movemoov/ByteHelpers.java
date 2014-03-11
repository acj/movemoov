package org.linuxguy.movemoov;

/**
 * Created by Adam Jensen on 3/9/14.
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
public class ByteHelpers {

    static int toInt(byte[] buf) {
        int n = 0;
        for (byte b : buf) {
            n = (n << 8) + (b & 0xff);
        }

        return n;
    }

    static long toLong(byte[] buf) {
        int n = 0;
        for (byte b : buf) {
            n = (n << 8) + (b & 0xff);
        }

        return n;
    }

    static byte[] toByteArray(int num) {
        final int INT_BYTES = Integer.SIZE / 8;

        byte[] bytes = new byte[INT_BYTES];
        for (int i = 0; i < INT_BYTES; i++) {
            bytes[(INT_BYTES - 1) - i] = (byte) (num >>> 8 * i);
        }

        return bytes;
    }

    static byte[] toByteArray(long num) {
        final int LONG_BYTES = Long.SIZE / 8;

        byte[] bytes = new byte[LONG_BYTES];
        for (int i = 0; i < LONG_BYTES; i++) {
            bytes[(LONG_BYTES - 1) - i] = (byte) (num >>> 8 * i);
        }

        return bytes;
    }
}

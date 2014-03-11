package org.linuxguy.movemoov;

import java.io.File;
import java.io.IOException;

/**
 * Created by Adam Jensen on 3/8/14.
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
public class InstrumentTest {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println(String.format("usage: %s <input.mp4> <output.mp4>", InstrumentTest.class));
            System.exit(0);
        }

        try {
            MoovMover.process(new File(args[0]), new File(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MoovMover.FileAlreadyOptimizedException e) {
            e.printStackTrace();
        }
    }
}

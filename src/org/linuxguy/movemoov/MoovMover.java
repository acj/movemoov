package org.linuxguy.movemoov;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam Jensen on 3/8/14
 *
 * Part of moovmover <https://github.com/acj/movemoov>
 */
public class MoovMover {

    public static class FileAlreadyOptimizedException extends Throwable {}

    public static boolean process(File inputFile, File outputFile) throws IOException,
                                                                          FileAlreadyOptimizedException {
        Atom ftypAtom = null;
        Atom moovAtom = null;
        Atom mdatAtom = null;

        long freePadding = 0L;

        MP4FileInputStream inStream = new MP4FileInputStream(inputFile);
        List<Atom> atoms = buildAtomIndex(inStream);

        for (Atom atom : atoms) {
            if (Constants.TYPE_MOOV.equalsIgnoreCase(atom.type)) {
                moovAtom = atom;
            } else if (Constants.TYPE_MDAT.equalsIgnoreCase(atom.type)) {
                mdatAtom = atom;
            } else if (Constants.TYPE_FTYP.equalsIgnoreCase(atom.type)) {
                ftypAtom = atom;
            } else if (Constants.TYPE_FREE.equalsIgnoreCase(atom.type)) {
                if (mdatAtom == null || atom.startPos < mdatAtom.startPos) {
                    freePadding += atom.size;
                }
            }
        }

        long moovChildrenOffset = moovAtom.size - freePadding;

        if (moovAtom.startPos < mdatAtom.startPos) {
            moovChildrenOffset -= moovAtom.size;

            if (freePadding == 0) {
                throw new FileAlreadyOptimizedException();
            }
        }

        inStream.getChannel().position(moovAtom.startPos);

        byte[] moovData = new byte[moovAtom.size];
        inStream.read(moovData);

        MoovByteArrayInputStream moovInStream = new MoovByteArrayInputStream(moovData);
        ByteArrayOutputStream moovOutStream = new ByteArrayOutputStream(moovData.length);

        moovInStream.skip(Constants.BOX_TYPE_BYTES + Constants.BOX_LENGTH_BYTES);

        Atom atom;
        while ((atom = seekToNextChunkOffsetAtom(moovInStream)) != null) {
            moovInStream.skip(Constants.BOX_VERSION_BYTES + Constants.BOX_FLAGS_BYTES);

            int numberOfEntries = moovInStream.readInt();
            int entriesOffset = moovInStream.getCurrentPosition();

            moovOutStream.write(moovData, moovOutStream.size(), entriesOffset - moovOutStream.size());

            if (atom.type.equalsIgnoreCase(Constants.TYPE_STCO)) {
                for (int i = 0; i < numberOfEntries; i++) {
                    moovOutStream.write(ByteHelpers.toByteArray(moovInStream.readInt() + (int) moovChildrenOffset));
                }
            } else {
                for (int i = 0; i < numberOfEntries; i++) {
                    moovOutStream.write(ByteHelpers.toByteArray(moovInStream.readLong() + moovChildrenOffset));
                }
            }
        } 
        if (moovOutStream.size() < moovData.length) {
            moovOutStream.write(moovData, moovOutStream.size(), moovData.length - moovOutStream.size());
        }

        if (!outputFile.exists()) {
            outputFile.setWritable(true, true);
            outputFile.delete();
        }

        outputFile.createNewFile();

        FileOutputStream outStream = new FileOutputStream(outputFile);

        // FTYP atom
        if (ftypAtom != null) {
            inStream.getChannel().position(ftypAtom.startPos);
            byte[] ftypContent = new byte[ftypAtom.size];
            inStream.read(ftypContent);
            outStream.write(ftypContent);
        }

        // MOOV atom
        moovOutStream.writeTo(outStream);

        moovInStream.close();
        moovOutStream.close();

        // Remaining atoms
        for (Atom nextAtom : atoms) {
            if (nextAtom.isHeaderType()) {
                continue;
            }

            inStream.getChannel().position(nextAtom.startPos);

            byte[] chunk = new byte[Constants.CHUNK_SIZE];
            for (int i = 0; i < (nextAtom.size / Constants.CHUNK_SIZE); i++) {
                inStream.read(chunk);
                outStream.write(chunk);
            }

            int residue = nextAtom.size % Constants.CHUNK_SIZE;
            if (residue > 0) {
                inStream.read(chunk, 0, residue);
                outStream.write(chunk, 0, residue);
            }

            inStream.close();
            outStream.flush();
            outStream.close();
        }

        return true;
    }

    private static List<Atom> buildAtomIndex(MP4FileInputStream inStream) throws IOException {
        ArrayList<Atom> atomIndex = new ArrayList<Atom>();

        while (inStream.available() > 0) {
            Atom nextAtom = Atom.fromStream(inStream);

            int skip = 8;
            if (nextAtom.size == 1) {
                nextAtom.size = inStream.readInt();
                skip = 16;
            }

            nextAtom.startPos = inStream.getChannel().position() - skip;

            atomIndex.add(nextAtom);

            if (nextAtom.size == 0) {
                break;
            }

            inStream.skip(nextAtom.size - skip);
        }

        return atomIndex;
    }

    private static Atom seekToNextChunkOffsetAtom(MoovByteArrayInputStream inStream) throws IOException {
        while (inStream.available() > 0) {
            Atom nextAtom = Atom.fromStream(inStream);
            if (nextAtom.isChunkOffsetType()) {
                return nextAtom;
            } else if (nextAtom.isParentType()) {
                continue;
            } else {
                inStream.skip(nextAtom.size - (Constants.BOX_TYPE_BYTES + Constants.BOX_LENGTH_BYTES));
            }
        }

        return null;
    }
}

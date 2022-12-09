package ru.vsu.cs.leonov_m.cg_4.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;

public class LZWDecomp {
    private int dictSize = 256, currentword, previousword;
    private byte[] buffer = new byte[3];
    private boolean isLeft = true;

    public byte[] decompress(byte[] array) throws IOException {
        String[] arrayOfChar = new String[4096];
        int i;
        Queue<Byte> in = new LinkedList<>();
        for (byte b : array) in.add(b);

        for (i = 0; i < 256; i++) arrayOfChar[i] = Character.toString((char) i);

        File file = File.createTempFile("test", ".bin");
        RandomAccessFile test = new RandomAccessFile(file, "rw");

        buffer[0] = in.poll();
        buffer[1] = in.poll();
        previousword = getIntValue(buffer[0], buffer[1], isLeft);
        isLeft = !isLeft;
        test.writeBytes(arrayOfChar[previousword]);

        while (!in.isEmpty()){

            if (isLeft) {
                buffer[0] = in.poll();
                buffer[1] = in.poll();
                currentword = getIntValue(buffer[0], buffer[1], isLeft);
            } else {
                buffer[2] = in.poll();
                currentword = getIntValue(buffer[1], buffer[2], isLeft);
            }
            isLeft = !isLeft;

            if (currentword >= dictSize) {
                if (dictSize < 4096) {
                    arrayOfChar[dictSize] = arrayOfChar[previousword] + arrayOfChar[previousword].charAt(0);
                }
                dictSize++;
                test.writeBytes(arrayOfChar[previousword] + arrayOfChar[previousword].charAt(0));
            } else {
                if (dictSize < 4096) {
                    arrayOfChar[dictSize] = arrayOfChar[previousword] + arrayOfChar[currentword].charAt(0);
                }
                dictSize++;
                test.writeBytes(arrayOfChar[currentword]);
            }
            previousword = currentword;
        }
        test.close();

        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }

        return bytes;
    }

    public static int getIntValue(byte b1, byte b2, boolean isLeft) {
        StringBuilder t1 = new StringBuilder(Integer.toBinaryString(b1));
        StringBuilder t2 = new StringBuilder(Integer.toBinaryString(b2));

        while (t1.length() < 8) t1.insert(0, "0");
        if (t1.length() == 32) t1 = new StringBuilder(t1.substring(24, 32));

        while (t2.length() < 8) t2.insert(0, "0");
        if (t2.length() == 32) t2 = new StringBuilder(t2.substring(24, 32));

        if (isLeft) return Integer.parseInt(t1 + t2.substring(0, 4), 2);
        else return Integer.parseInt(t1.substring(4, 8) + t2, 2);
    }
}

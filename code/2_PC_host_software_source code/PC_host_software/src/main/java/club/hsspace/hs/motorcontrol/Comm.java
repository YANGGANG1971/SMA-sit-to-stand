package club.hsspace.hs.motorcontrol;

import java.io.Closeable;


public interface Comm extends Closeable {

    void send(byte[] data);

    default void sendASCII(String cmd) {
        send(cmd.trim().getBytes());
    }

    default void sendData8(byte key, byte mode, byte value1, byte value2) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xaa;
        bytes[1] = (byte) 0x55;
        bytes[2] = key;
        bytes[3] = mode;
        bytes[4] = value1;
        bytes[5] = value2;
        bytes[6] = (byte) 0x0d;
        bytes[7] = (byte) 0x0a;
        send(bytes);
    }

    default void sendData16(byte mode, int value, int status) {
        byte[] bytes = new byte[16];
        bytes[0] = (byte) 0xaa;
        bytes[1] = (byte) 0x55;
        bytes[2] = (byte) 0x04;
        bytes[3] = mode;
        int2bytes(value, bytes, 4);
        int2bytes(status, bytes, 12, 2);
        bytes[14] = (byte) 0x0d;
        bytes[15] = (byte) 0x0a;
        send(bytes);
    }

    default void sendData32(byte mode, int s, int p, int i, int d, int max, int min, int status) {
        byte[] bytes = new byte[32];
        bytes[0] = (byte) 0xaa;
        bytes[1] = (byte) 0x55;
        bytes[2] = (byte) 0x02;
        bytes[3] = mode;
        int2bytes(s, bytes, 4);
        int2bytes(p, bytes, 8);
        int2bytes(i, bytes, 12);
        int2bytes(d, bytes, 16);
        int2bytes(max, bytes, 20);
        int2bytes(min, bytes, 24);
        int2bytes(status, bytes, 28, 2);
        bytes[30] = (byte) 0x0d;
        bytes[31] = (byte) 0x0a;
        send(bytes);
    }

    private static void int2bytes(int value, byte[] bytes, int from) {
        int2bytes(value, bytes, from, 4);
    }

    private static void int2bytes(int value, byte[] bytes, int from, int len) {
        for (int i = 0; i < len; i++)
            bytes[from + i] = (byte) (value >>> (i * 8));
    }




    default int bytes2int(byte[] bytes, int from) {
        return bytes2int(bytes, from, 4);
    }

    default int bytes2int(byte[] bytes, int from, int len) {
        int res = 0;
        for (int i = 0; i < len; i++)
            res += Byte.toUnsignedInt(bytes[from + i]) << (i * 8);
        return res;
    }

    default int[] bytes2ints(int start, byte[] bytes) {
        return new int[]{bytes2int(bytes, 2, 1),
                bytes2int(bytes, 3, 1),
                bytes2int(bytes, 4),
                bytes2int(bytes, 8),
                bytes2int(bytes, 12),
                bytes2int(bytes, 16),
                bytes2int(bytes, 20),
                bytes2int(bytes, 24),
                bytes2int(bytes, 28),
                bytes2int(bytes, 32),
                bytes2int(bytes, 36),
                bytes2int(bytes, 40),
                bytes2int(bytes, 44),
                bytes2int(bytes, 48, 2),
                start
        };
    }
}

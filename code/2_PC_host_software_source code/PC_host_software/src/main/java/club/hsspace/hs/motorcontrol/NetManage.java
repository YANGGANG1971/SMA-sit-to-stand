package club.hsspace.hs.motorcontrol;


import java.io.*;
import java.net.Socket;

/**
 * CopyRright: (c)2022
 * Project: MotorControl
 * Comments: Revised by Yuebing Li on 2024/12/28
 *
 * @Author Qing_ning
 * @Date 2022/1/13 2:14
 * @Version 1.0
 */

public class NetManage implements Runnable, Comm {

    private OutputStream writer;
    private InputStream reader;

    private Socket sock;

    private DataInterface di;

    public NetManage(String ip, int port, DataInterface di) throws IOException {
        this.di = di;
        sock = new Socket(ip, port);
        writer = sock.getOutputStream();
        reader = sock.getInputStream();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            int start = 0;
            while (true) {
                if (reader.read() == 187 && reader.read() == 68) {
                    byte[] bytes = new byte[52];
                    bytes[0] = (byte) 0xaa;
                    bytes[1] = (byte) 0x55;
                    reader.readNBytes(bytes, 2, 50);
                    if (Byte.toUnsignedInt(bytes[2]) == 0) {
                        di.handleReceive(bytes, bytes2ints(start, bytes));
                        start += 10;
                    } else if (Byte.toUnsignedInt(bytes[2]) == 2) {
                        di.getPid(bytes, bytes2ints(start, bytes));
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("diconnect");
        }
    }

    @Override
    public synchronized void send(byte[] data) {
        di.sendData(data);
        try {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        sock.close();
    }
}

package club.hsspace.hs.motorcontrol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class ComManage implements Comm {

    private DataInterface di;
    private SerialPort sp;
    private SettingManage sm;

    private byte[] buffer = new byte[8192];
    private int bufferPtr = 0;
    private int internalTick = 0;

    // Constants defined by the protocol
    private static final int LEN_PID = 32;
    private static final int LEN_MINFO = 52;
    private static final byte HEAD1 = (byte) 0xBB;
    private static final byte HEAD2 = (byte) 0x44;
    private static final byte TAIL1 = (byte) 0x0D; // \r
    private static final byte TAIL2 = (byte) 0x0A; // \n

    public ComManage(SerialPort sp, DataInterface di, SettingManage sm) {
        this.sp = sp;
        this.di = di;
        this.sm = sm;

        int baudRate = Integer.parseInt(sm.getProperty("com.baudrate", "460800"));
        sp.setBaudRate(baudRate);
        sp.setNumDataBits(8);
        sp.setNumStopBits(1);
        sp.setParity(0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10, 0);

        if (sp.openPort()) {
            System.out.println(">>> [SUCCESS] Port opened: " + sp.getSystemPortName());
        }

        sp.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();
                for (byte b : newData) {
                    if (bufferPtr < buffer.length) buffer[bufferPtr++] = b;

                    // Core state machine: identifies different packets based on frame header, length, and tail
                    while (bufferPtr >= 2) {
                        if (buffer[0] == HEAD1 && buffer[1] == HEAD2) {

                            int frameLen = 0;

                            // 1. Try to match PID packet (32 bytes)
                            if (bufferPtr >= LEN_PID && buffer[30] == TAIL1 && buffer[31] == TAIL2) {
                                frameLen = LEN_PID;
                            }
                            // 2. Try to match MINFO packet (52 bytes)
                            else if (bufferPtr >= LEN_MINFO && buffer[50] == TAIL1 && buffer[51] == TAIL2) {
                                frameLen = LEN_MINFO;
                            }

                            if (frameLen > 0) {
                                // Extract the complete frame
                                byte[] frame = Arrays.copyOfRange(buffer, 0, frameLen);
                                processFrame(frame);

                                // Sliding window to remove processed data
                                System.arraycopy(buffer, frameLen, buffer, 0, bufferPtr - frameLen);
                                bufferPtr -= frameLen;
                            } else if (bufferPtr >= LEN_MINFO) {
                                // Enough length but cannot match the frame tail, indicating a bad packet; skip the frame header
                                shiftBuffer(1);
                            } else {
                                // Insufficient bytes to determine, wait for more data
                                break;
                            }
                        } else {
                            // Not a valid frame header, search ahead
                            shiftBuffer(1);
                        }
                    }
                }
            }
        });
    }

    private void shiftBuffer(int n) {
        if (bufferPtr > n) {
            System.arraycopy(buffer, n, buffer, 0, bufferPtr - n);
        }
        bufferPtr = Math.max(0, bufferPtr - n);
    }

    /**
     * Route to different DataInterface methods based on packet length
     */
    private synchronized void processFrame(byte[] frame) {
        int[] decodedData = bytes2ints(frame);
        if (frame.length == LEN_PID) {
            // Route to getPid processing logic
            di.getPid(frame, decodedData);
        } else {
            // Route to standard handleReceive processing logic
            di.handleReceive(frame, decodedData);
        }
    }

    public int[] bytes2ints(byte[] data) {
        int[] res = new int[15];
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        res[0] = Byte.toUnsignedInt(data[2]); // key/cmd
        res[1] = Byte.toUnsignedInt(data[3]); // mode

        // Determine how many int32 data bits to parse based on the packet length
        int intCount = (data.length == LEN_PID) ? 6 : 11;

        for (int i = 0; i < intCount; i++) {
            res[i + 2] = bb.getInt(4 + (i * 4));
        }

        // Status bit (at 28 for PID packet, at 48 for MINFO packet)
        int statusOffset = (data.length == LEN_PID) ? 28 : 48;
        res[13] = (data[statusOffset] & 0xFF) | ((data[statusOffset + 1] & 0xFF) << 8);

        res[14] = internalTick++;
        return res;
    }

    @Override
    public void send(byte[] data) {
        if (sp != null && sp.isOpen()) {
            sp.writeBytes(data, data.length);
        }
    }

    @Override
    public void close() throws IOException {
        if (sp != null) {
            sp.removeDataListener();
            sp.closePort();
        }
    }
}
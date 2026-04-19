package club.hsspace.hs.motorcontrol;

/**
 * CopyRright: (c)2022
 * Project: MotorControl
 * Comments: Revised by Yuebing Li on 2024/12/28
 *
 * @Author Qing_ning
 * @Date 2022/1/13 2:14
 * @Version 1.0
 */
public interface DataInterface {

    void handleReceive(byte[] bytes, int[] data);

    void getPid(byte[] bytes, int[] data);

    void sendData(byte[] data);
}

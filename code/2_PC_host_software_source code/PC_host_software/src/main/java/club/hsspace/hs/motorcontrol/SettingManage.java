package club.hsspace.hs.motorcontrol;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * CopyRright: (c)2022
 * Project: MotorControl
 * Comments: Revised by Yuebing Li on 2024/12/28
 *
 * @Author Qing_ning
 * @Date 2022/1/13 2:14
 * @Version 1.0
 */


public class SettingManage {

    private Properties props;

    private String path;

    private File file;

    public SettingManage() {

        props = new Properties();
        try {
            path = java.net.URLDecoder.decode(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
            file = new File(path).getParentFile();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = new File(this.file, "setting.properties");
        try {
            props.load(new FileReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            try {
                props.store(new FileOutputStream(file), "MotorVCS setting file");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public String getProperty(String key, String dfa) {
        return props.getProperty(key, dfa);
    }


}

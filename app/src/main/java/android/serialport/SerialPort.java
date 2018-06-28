
package android.serialport;

/**
 * Author: Edward
 * Date: 2018/6/28
 * Description:
 */

@SuppressWarnings("JniMissingFunction")
public class SerialPort {

    private int fdx = -1;
    private boolean isOpen;

    public SerialPort() {
    }

    /**
     * 打开串口
     *
     * @param dev 串口地址
     * @param brd 波特率
     */
    public void openSerial(String dev, int brd) {
        try {
            fdx = openport_easy(dev, brd);
            isOpen = fdx >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开串口
     *
     * @param dev      串口地址
     * @param baudRate 波特率
     * @param dataBit  数据位
     * @param stopBit  停止位
     * @param crc      校验位
     */
    public void openSerial(String dev, int baudRate, int dataBit, int stopBit, int crc) {
        try {
            fdx = openport(dev, baudRate, dataBit, stopBit, crc);
            isOpen = fdx >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 串口是否打开
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 写入字节数组
     *
     * @return 返回值是写入的长度
     */
    public int write(byte[] str) {
        return writeport(fdx, str);
    }

    /**
     * 写入字符串
     *
     * @return 返回值是写入的长度
     */
    public int write(String str) {
        return writestring(fdx, str, str.length());
    }

    /**
     * 读字节数组
     */
    public byte[] read(int len) {
        try {
            return readport(fdx, len, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读字符串
     */
    public String readString(int len) {
        try {
            byte[] tmp = readport(fdx, len, 50);
            return tmp == null ? null : new String(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读十六进制字符串
     */
    public String readHex(int len) {
        try {
            byte[] tmp = readport(fdx, len, 50);
            return tmp == null ? null : byteToHex(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数组转换为十六进制字符串
     */
    public String byteToHex(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
            result += hex;
        }
        return result;
    }

    public void closeSerial() {
        closeport(fdx);
    }

    private native int openport_easy(String port, int brd);

    private native int openport(String port, int brd, int bit, int stop, int crc);

    private native void closeport(int fd);

    private native byte[] readport(int fd, int count, int delay);

    private native int writeport(int fd, byte[] buf);

    public native static int writestring(int fd, String wb, int len);

    static {
        System.loadLibrary("serial_port");
    }

}

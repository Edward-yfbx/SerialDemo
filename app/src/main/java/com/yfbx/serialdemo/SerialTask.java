package com.yfbx.serialdemo;

import android.os.AsyncTask;

/**
 * Author:Edward
 * Date:2018/9/12
 * Description:
 */

public class SerialTask extends AsyncTask<Void, Void, byte[]> {

    private String dev;
    private int baudRate;
    private byte[] data;
    private OnSerialCallback callback;

    public static void execute(String dev, int baudRate, byte[] data, OnSerialCallback callback) {
        new SerialTask(dev, baudRate, data, callback).execute();
    }

    private SerialTask(String dev, int baudRate, byte[] data, OnSerialCallback callback) {
        this.dev = dev;
        this.baudRate = baudRate;
        this.data = data;
        this.callback = callback;
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        SerialManager serialPort = SerialManager.getInstance();
        serialPort.open(dev, baudRate);
        serialPort.write(data);
        byte[] read = serialPort.read();
        serialPort.close();
        return read;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        callback.onReadResult(bytes);
    }

    public interface OnSerialCallback {

        void onReadResult(byte[] data);
    }
}

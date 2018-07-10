package com.yfbx.serialdemo;

import android.app.Activity;
import android.os.Bundle;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements View.OnKeyListener, View.OnClickListener {

    private static final String TAG = "串口测试";
    Spinner portSpinner;
    Spinner baudSpinner;
    EditText editText;
    CheckBox hexBtn;
    TextView retTxt;
    SerialPort serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSpinner();
        initSerialPort();
    }

    private void initView() {
        portSpinner = findViewById(R.id.spinner1);
        baudSpinner = findViewById(R.id.spinner2);
        editText = findViewById(R.id.edit_txt);
        hexBtn = findViewById(R.id.hexBtn);
        retTxt = findViewById(R.id.ret_txt);
        findViewById(R.id.send_btn).setOnClickListener(this);
        findViewById(R.id.clear_btn).setOnClickListener(this);
        editText.setOnKeyListener(this);
        retTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initSpinner() {
        SerialPortFinder finder = new SerialPortFinder();
        String[] path = finder.getAllDevicesPath();
        List<String> list = Arrays.asList(path);
        String[] array = getResources().getStringArray(R.array.baudrates_value);
        List<String> baudList = Arrays.asList(array);

        portSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
        baudSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, baudList));

        //portSpinner.setSelection(10);
        //baudSpinner.setSelection(16);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                sendMsg(editText.getText().toString());
                break;
            case R.id.clear_btn:
                retTxt.setText("");
                break;
        }
    }

    private void initSerialPort() {
        serialPort = new SerialPort();
        new ReadThread().start();
    }

    private void sendMsg(String msg) {
        String dev = portSpinner.getSelectedItem().toString();
        int baudRate = Integer.parseInt(baudSpinner.getSelectedItem().toString());
        Log.i(TAG, "串口：" + dev + "，波特率：" + baudRate);
        serialPort.openSerial(dev, baudRate);

        byte[] data = hexBtn.isChecked() ? HexUtils.hexToByte(msg) : msg.getBytes();
        serialPort.write(data);
    }


    class ReadThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                byte[] read = serialPort.read(64);
                onReadResult(read);
            }
        }
    }


    private void onReadResult(byte[] result) {
        if (result == null) {
            return;
        }
        final String ret = hexBtn.isChecked() ? HexUtils.byteToHex(result, result.length) : new String(result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retTxt.append(ret);
                retTxt.append("\n");
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            sendMsg(editText.getText().toString());
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialPort.close();
    }
}

package com.yfbx.serialdemo;

import android.os.Bundle;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "串口测试";
    EditText editText;
    TextView retTxt;
    Spinner portSpinner;
    Spinner baudSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_txt);
        retTxt = findViewById(R.id.ret_txt);
        portSpinner = findViewById(R.id.port);
        baudSpinner = findViewById(R.id.baudRate);
        SerialPortFinder finder = new SerialPortFinder();
        String[] path = finder.getAllDevicesPath();
        List<String> list = Arrays.asList(path);
        String[] array = getResources().getStringArray(R.array.baudrates_value);
        List<String> baudList = Arrays.asList(array);
        portSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
        baudSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, baudList));
        portSpinner.setSelection(10);
        baudSpinner.setSelection(12);
        retTxt.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute(editText.getText().toString());
            }
        });
    }


    /**
     * 执行请求
     */
    public void execute(final String msg) {

        new Thread() {

            @Override
            public void run() {
                try {
                    test(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void test(String msg) throws IOException, InterruptedException {
        String dev = portSpinner.getSelectedItem().toString();
        int baudRate = Integer.parseInt(baudSpinner.getSelectedItem().toString());
        Log.i(TAG, "端口：" + dev + ",波特率：" + baudRate);

        SerialPort serialPort = new SerialPort();
        serialPort.openSerial(dev, baudRate);
        serialPort.write(msg.getBytes());

        String ret = serialPort.readString(1024);

        if (ret == null) {
            Log.i(TAG, "返回结果为空");
        } else {
            Log.i(TAG, "返回结果: " + ret);
            setText(ret);
        }
        serialPort.closeSerial();
    }

    private void setText(final String ret) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retTxt.append(ret);
                retTxt.append("\n");
            }
        });
    }
}

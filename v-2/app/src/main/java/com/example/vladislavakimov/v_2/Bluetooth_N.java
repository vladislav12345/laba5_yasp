package com.example.vladislavakimov.v_2;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vladislavakimov on 31.01.15.
 */
public class Bluetooth_N {

    public static ConnectedThread mConnectedThread;

    void init(){
        ConnectedThread mConnectedThread = new ConnectedThread(MainActivity.btSocket);
        mConnectedThread.start();
    }

    public void onResume() {

        Log.d(MainActivity.TAG, "...onResume - попытка соединения...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = MainActivity.btAdapter.getRemoteDevice(MainActivity.address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            MainActivity.btSocket = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
        } catch (IOException e) {
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        MainActivity.btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(MainActivity.TAG, "...Соединяемся...");
        try {
            MainActivity.btSocket.connect();
            Log.d(MainActivity.TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                MainActivity.btSocket.close();
            } catch (IOException e2) {
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(MainActivity.TAG, "...Создание Socket...");


    }

    public void onPause() {

        Log.d(MainActivity.TAG, "...In onPause()...");

        try     {
            MainActivity.btSocket.close();
        } catch (IOException e2) {
        }
    }

    //Ожидаем ответа
    public boolean waitAnswer(char sendChar, char waitChar) {
        byte mistakeCounter=0;
        byte buff_ = 0;
        //Toast.makeText(getBaseContext(), "отправка" + sendChar, Toast.LENGTH_SHORT).show();

        mConnectedThread.write(sendChar);
        getChar();

        //Toast.makeText(getBaseContext(), "get"+main.answer, Toast.LENGTH_SHORT).show();

        if(MainActivity.answer==waitChar){
            return true;
        }
        return false;

    }

    //Считываем символ
    void getChar(){
        MainActivity.answer=0;
        int counter=0;
        while(MainActivity.gotChar==0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            ++counter;
            if(counter==10){
                MainActivity.answer=1;
                return;
            }
        }
        MainActivity.answer=MainActivity.gotChar;
    }

    public class ConnectedThread extends Thread {
        public final BluetoothSocket mmSocket;
        public final InputStream mmInStream;
        public final OutputStream mmOutStream;
        public char gotChar=0;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes=0; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try  {
                    bytes = mmInStream.read(buffer);
                    for(int i=0;i<256;i++){
                        if(buffer[i]!=0) {
                            MainActivity.gotChar = (char) buffer[i];
                        }
                        buffer[i]=0;
                    }

                }catch (IOException e) {
                }
            }
        }

        // Call this from the main activity to send data to the remote device
        public void write(char message) {
            Log.d(MainActivity.TAG, "...Данные для отправки: " + message + "...");
            //byte[] msgBuffer = message.getBytes();

            byte msgBuffer = (byte)message;
            //Toast.makeText(getBaseContext(), "отправка"+message, Toast.LENGTH_SHORT).show();
            try {
                mmOutStream.write(msgBuffer);
                try {
                    sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.d(MainActivity.TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
            }
        }

        // Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}

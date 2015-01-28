package com.example.vladislavakimov.v_2;

import java.lang.String;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */

    private static final String TAG = "bluetooth1";

    Button btnForward, btnRight, btnAgo, btnLeft, btnFlash;
    EditText txtArduino;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static volatile char gotChar=0;
    public static volatile char answer;

    compiler comp=new compiler();

    private ConnectedThread mConnectedThread;

    // SPP UUID сервиса
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-адрес Bluetooth модуля
    private static String address = "20:14:10:10:21:11";

    //Считываем символ
    void getChar(){
        answer=0;
        int counter=0;
        while(gotChar==0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            ++counter;
            if(counter==10){
                answer=1;
                return;
            }
        }
        answer=gotChar;
    }

    //Ожидаем ответа
    boolean waitAnswer(char sendChar, char waitChar) {
        byte mistakeCounter=0;
        byte buff_ = 0;
        Toast.makeText(getBaseContext(), "отправка"+sendChar, Toast.LENGTH_SHORT).show();

        mConnectedThread.write(sendChar);
        getChar();

        Toast.makeText(getBaseContext(), "get"+answer, Toast.LENGTH_SHORT).show();

        if(answer==waitChar){
            return true;
        }
        return false;

    }

    public class compiler
    {
        public static final byte FB_LRMASK = 0x0C;
        public static final byte FB_CENTER = 0x00;
        public static final byte FB_LEFT = 0x04;
        public static final byte FB_RIGHT = 0x08;

        public static final byte FB_MASK = 0x03;
        public static final byte FB_STOP = 0x00;
        public static final byte FB_FORWARD = 0x01;
        public static final byte FB_BACK = 0x02;

        public static final byte CMD_MASK = 0x10;
        public static final byte CMD_SET = 0x10;
        public static final byte CMD_ADD = 0x30;
        public static final byte CMD_RR = 0x20;
        public static final byte CMD_MOTOR = 0x70;
        public static final byte CMD_GOTO = 0x40;
        public static final byte CMD_MODULE = 0x50;
        public static final byte CMD_CMP = 0x60;

        public static final byte MOTOR_SPEC_MASK = 0x0F;
        public static final byte MOTOR_SPEC_SPEEDV = 0x03;
        public static final byte MOTOR_SPEC_SPEED_INC = 0x07;
        public static final byte MOTOR_SPEC_SPEED_DEC = 0x0D;

        public static final byte VEC_INIT = 0;
        public static final byte VEC_PROCESS = 1;
        public static final byte VEC_MILLISEC = 2;

        public static final byte GOTO_MASK = 0x0E;
        public static final byte GOTO_SIMPLE = 0x0;
        public static final byte GOTO_EQUAL = 0x02;
        public static final byte GOTO_NOTEQUAL = 0x04;
        public static final byte GOTO_MORE = 0x06;
        public static final byte GOTO_LESS = 0x08;
        public static final byte GOTO_EQMORE = 0x0A;
        public static final byte GOTO_EQLESS = 0x0C;

        public static final byte RR_SET = 0x00;
        public static final byte RR_ADD = 0x04;
        public static final byte RR_SUB = 0x08;

        public static final byte RR_AND = 0x0C;
        public static final byte RR_OR = 0x09;
        public static final byte RR_CMP = 0x0E;

        public static final byte RR_NORMAL = 0x00;
        public static final byte RR_MINUS = 0x01;
        public static final byte RR_ABS = 0x02;
        public static final byte RR_NOT = 0x03;

        public static final short PROGRAM_BEGIN = 16;

        public byte[] byteCode = new byte[512];
        public char[] line=new char[512];
        public int lineLength;
        public int numberOfLine;

        public short cursor;

        public class struct_label 	{
            char[] name;
            byte adress;
        }

        public class struct_goto_label	{
            char[] name;
            byte adress;
        }

        boolean wordcmp(char[] str1, String str2){
            int cur=0;
            while(true){
                char c=str1[cur];
                if(cur==str2.length()){
                    if((c>='a' && c<='z')||(c>='A'&& c<='Z')){
                        return false;
                    }
                    return true;
                }else if(c!=str2.charAt(cur)){
                    return false;
                }
                ++cur;
            }
        }

        boolean wordcmp(char[] str1, String str2,int start){
            int cur=0;
            while(true){
                char c=str1[cur+start];
                if(cur==str2.length()){
                    if((c>='a' && c<='z')||(c>='A'&& c<='Z')){
                        return false;
                    }
                    return true;
                }else if(c!=str2.charAt(cur)){
                    return false;
                }
                ++cur;
            }
        }

        //Компилятор
        public byte compile(String code){
            String[] lines = code.split("\n");
            char[] str;
            int fig_skob=0;
            int fig_skob_if=0;
            byte[] adres_return = new byte[16];
            struct_label[] label = new struct_label[256];
            struct_goto_label[] goto_label = new struct_goto_label[256];
            int number_label=0;
            int number_goto_label=0;
            char[] str_for_label;
            int i,j;

            cursor=PROGRAM_BEGIN;

            for(numberOfLine=0;numberOfLine<lines.length;++numberOfLine)
            {
                lineLength=0;
                for (i=0; i<lines[numberOfLine].length(); i++){
                    char c=lines[numberOfLine].charAt(i);
                    if (c != ' ' && c != '\t' && c != '\r' && lineLength<512-1){
                        line[lineLength++]=lines[numberOfLine].charAt(i);
                    }
                }
                line[lineLength]=0;

                if (line[lineLength-1]=='{') fig_skob++;
                if (line[lineLength-1]=='}') fig_skob--;
                if (line[lineLength-1]=='{' && wordcmp(line,"if")) {
                    fig_skob_if++;
                    adres_return[fig_skob_if]=(byte)(cursor+3);
                }
                if (line[lineLength-1]=='}' && fig_skob_if>0 ) {
                    byteCode[adres_return[fig_skob_if]]=(byte)(cursor);
                    fig_skob_if--;
                }
                if (fig_skob==0)
                {
                    byteCode[cursor++]=0;
                    byteCode[cursor++]=0;
                }

                if (line[lineLength-1]==':')
                {
                    label[number_label].name=line;
                    label[number_label++].adress = (byte)(cursor);
                    continue;
                }
                if (wordcmp(line,"goto"))
                {
                    str_for_label = new char[lineLength-5];
                    for (i=0; i+6<lineLength; i++)
                        str_for_label[i]=line[i+5];
                    str_for_label[i] = ':';
                    str_for_label[i+1] = '\0';
                    goto_label[number_goto_label].name=str_for_label;
                    goto_label[number_goto_label++].adress = (byte)(cursor+1);
                    byteCode[cursor++]=CMD_GOTO|GOTO_SIMPLE;
                    byteCode[cursor++]=0;
                    continue;
                }


                if (wordcmp(line,"OnInit")) {
                    byteCode[0]=(byte)(cursor/2);
                    continue;
                }
                if (wordcmp(line,"OnProcess")) {
                    byteCode[1]=(byte)(cursor/2);
                    continue;
                }
                if (wordcmp(line,"OnMillisecond")) {
                    byteCode[2]=(byte)(cursor/2);
                    continue;
                }

                if (wordcmp(line,"r"))
                    if (action_register(line) == 1) {
                        return 0;
                    } else continue;

                if (wordcmp(line,"move"))
                    if (move(line) == 1) {
                        return 0;
                    } else continue;

                if (wordcmp(line,"speed"))
                    if (speed(line) == 1) {
                        return 0;
                    } else continue;

                if (wordcmp(line,"if"))
                    if (condition(line) == 1) {
                        return 0;
                    } else continue;

            }

            for (i=0; i<number_goto_label; i++)
            {
                for (j=0; j<number_label; j++)
                    if (label[j].name.equals(goto_label[i].name))
                    {
                        byteCode[goto_label[i].adress]=label[j].adress;
                        break;
                    }
            }

            return 1;
        }

        public int action_register(char[] qwerty)
        {
            byte answer1, answer2;
            int number1=0, number2=0;
            boolean otr=false;
            int i;

            answer1=0; answer2=0;

            for (i=1; Math.abs(qwerty[i]-52)<=5; i++)
            {
                number1=number1*10+(qwerty[i]-48);
            }

            if (qwerty[i]=='=' && !(qwerty[i+1]=='r' || qwerty[i+2]=='r') ) //0001
            {
                if (qwerty[i+1]=='-') {otr=true; i++;}
                for (i=i+1; Math.abs(qwerty[i]-52)<=5; i++)
                {
                    number2=number2*10+(qwerty[i]-48);
                }
                answer1=(byte)(CMD_SET|number1);
                answer2=(byte)number2;
                if( otr ) {answer2=(byte)(-answer2);}

                byteCode[cursor++]=answer1;
                byteCode[cursor++]=answer2;
                return 0;
            }

            if (qwerty[i]=='+' && qwerty[i+1]=='=' && !(qwerty[i+2]=='r' || qwerty[i+3]=='r') ) //0010
            {
                if (qwerty[i+2]=='-') {otr=true; i++;}
                for (i=i+2; Math.abs(qwerty[i]-52)<=5; i++)
                {
                    number2=number2*10+(qwerty[i]-48);
                }
                answer1=(byte)(CMD_ADD|number1);
                answer2=(byte)number2;
                if( otr ) {answer2=(byte)(-answer2);}

                byteCode[cursor++]=answer1;
                byteCode[cursor++]=answer2;
                return 0;
            }

            if (qwerty[i]=='+') { i++; answer1=RR_ADD; }
            if (qwerty[i]=='-') { i++; answer1=RR_SUB; }
            i++;

            if (qwerty[i]=='r') {answer1=(byte)(RR_NORMAL|answer1|CMD_RR); i--;} else
            if (qwerty[i]=='-') answer1=(byte)(RR_MINUS|answer1|CMD_RR); else
            if (qwerty[i]=='|') answer1=(byte)(RR_ABS|answer1|CMD_RR); else
            if (qwerty[i]=='!') answer1=(byte)(RR_NOT|answer1|CMD_RR); else
            if (qwerty[i]=='&') answer1=RR_AND|CMD_RR; else return 1;
            i=i+2;
            for (i=i; Math.abs(qwerty[i]-52)<=5; i++)
            {
                number2=number2*10+(qwerty[i]-48);
            }
            if ( (answer1==(byte)(RR_NOT|answer1|CMD_RR)) && qwerty[i]!='|')
            {
                answer1=RR_OR|CMD_RR;
            }

            byteCode[cursor++]=answer1;
            byteCode[cursor++]=(byte)(number1*16+number2);
            return 0;
        }

        public int move(char[] qwerty)
        {
            byte answer=CMD_MOTOR;
            int number=0;
            int i=5;
            char[] str;

            if (wordcmp(qwerty,"forever",5))
            {
                i+=7;
                number=0;
            }
            else
                for (i=i; Math.abs(qwerty[i]-52)<=5; i++)
                {
                    number=number*10+(qwerty[i]-48);
                }
            i++;

            if (wordcmp(qwerty,"forward",i))
            {
                i+=7;
                answer=(byte)(answer|FB_FORWARD);
            }else
            if (wordcmp(qwerty,"stop",i))
            {
                i+=4;
                answer=(byte)(answer|FB_STOP);
            }else
            if (wordcmp(qwerty,"back",i))
            {
                i+=4;
                answer=(byte)(answer|FB_BACK);
            }

            if (qwerty[i]==')') { answer=(byte)(answer|FB_FORWARD); }else
            {
                i++;
                if (wordcmp(qwerty,"left",i)) { answer=(byte)(answer|FB_LEFT); }else
                if (wordcmp(qwerty,"right",i)) { answer=(byte)(answer|FB_RIGHT); }else return 1;
            }

            byteCode[cursor++]=answer;
            byteCode[cursor++]=(byte)number;

            return 0;
        }

        public int speed(char[] qwerty)
        {
            byte answer=CMD_MOTOR;
            int number=0;
            int i=6;

            if (qwerty[i]=='+')
            {
                if (qwerty[i+1]=='1' && qwerty[i+2]=='0')
                {
                    byteCode[cursor++]=(byte)(answer|MOTOR_SPEC_SPEED_INC);
                    byteCode[cursor++]=0;
                    return 0;
                }
                else
                    return 1;
            }

            if (qwerty[i]=='-')
            {
                if (qwerty[i+1]=='1' && qwerty[i+2]=='0')
                {
                    byteCode[cursor++]=(byte)(answer|MOTOR_SPEC_SPEED_DEC);
                    byteCode[cursor++]=0;
                    return 0;
                }
                else
                    return 1;
            }

            if ( Math.abs(qwerty[i]-52)<=5 )
            {
                for (i=i; Math.abs(qwerty[i]-52)<=5; i++)
                {
                    number=number*10+(qwerty[i]-48);
                }
                byteCode[cursor++]=(byte)(answer|MOTOR_SPEC_SPEEDV);
                byteCode[cursor++]=(byte)number;
                return 0;
            }

            return 1;
        }

        public int condition(char[] qwerty)
        {
            int i=4, j,k;
            byte number1=0, number2=0;

            for (i=i; Math.abs(qwerty[i]-52)<=5; i++) {
                number1=(byte)(number1*10+(qwerty[i]-48)); }
            j=i;
            while (Math.abs(qwerty[j]-52)>5) { j++; }
            k=j;
            for (j=j; Math.abs(qwerty[j]-52)<=5; j++) {
                number2=(byte)(number2*10+(qwerty[j]-48)); }

            if (qwerty[k-1]=='r')
            {
                byteCode[cursor++]=CMD_RR|RR_CMP;
                byteCode[cursor++]=(byte)(number1*16+number2);
            }
            else
            {
                byteCode[cursor++]=(byte)(CMD_CMP|number1);
                byteCode[cursor++]=number2;
            }

            if (qwerty[i]=='=' && qwerty[i+1]=='=')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_NOTEQUAL;
                byteCode[cursor++]=0;
                return 0;
            }

            if (qwerty[i]=='!' && qwerty[i+1]=='=')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_EQUAL;
                byteCode[cursor++]=0;
                return 0;
            }

            if (qwerty[i]=='<' && qwerty[i+1]=='=')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_MORE;
                byteCode[cursor++]=0;
                return 0;
            }

            if (qwerty[i]=='>' && qwerty[i+1]=='=')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_LESS;
                byteCode[cursor++]=0;
                return 0;
            }

            if (qwerty[i]=='<')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_EQMORE;
                byteCode[cursor++]=0;
                return 0;
            }

            if (qwerty[i]=='>')
            {
                byteCode[cursor++]=CMD_GOTO|GOTO_EQLESS;
                byteCode[cursor++]=0;
                return 0;
            }
            return 1;
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnForward = (Button) findViewById(R.id.button4);
        btnRight = (Button) findViewById(R.id.button);
        btnAgo = (Button) findViewById(R.id.button1);
        btnLeft = (Button) findViewById(R.id.button5);
        btnFlash = (Button) findViewById(R.id.button2);
        txtArduino = (EditText) findViewById(R.id.editText);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        btnForward.setOnTouchListener(new OnTouchListener() {
             public boolean onTouch(View v, MotionEvent event) {

                     if (event.getAction() == MotionEvent.ACTION_DOWN) {
                         mConnectedThread.write('w'); //Отправляем 'w', пока нажата кнопка
                         //Toast.makeText(getBaseContext(), "Поехали!", Toast.LENGTH_SHORT).show();
                         return true;
                     } else if (event.getAction() == MotionEvent.ACTION_UP) {

                         mConnectedThread.write('W'); //Отправляем 'W', как только кнопка была отпущена
                         //Toast.makeText(getBaseContext(), "Остановились", Toast.LENGTH_SHORT).show();
                         return true;
                     }
                 return false;

             }
        });

        btnRight.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mConnectedThread.write('d'); //Отправляем 'd', пока нажата кнопка
                    //Toast.makeText(getBaseContext(), "Направо!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mConnectedThread.write('D'); //Отправляем 'D', как только кнопка была отпущена
                    //Toast.makeText(getBaseContext(), "Прямо", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        btnAgo.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mConnectedThread.write('s'); //Отправляем 's', пока нажата кнопка
                    //Toast.makeText(getBaseContext(), "Назад!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mConnectedThread.write('S'); //Отправляем 'S', как только кнопка была отпущена
                    //Toast.makeText(getBaseContext(), "Остановились", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        btnLeft.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mConnectedThread.write('a'); //Отправляем 'a', как только кнопка была отпущена
                    //Toast.makeText(getBaseContext(), "Налево!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mConnectedThread.write('A'); //Отправляем 'A', как только кнопка была отпущена
                    //Toast.makeText(getBaseContext(), "Прямо", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        btnFlash.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(comp.compile(txtArduino.getText().toString())==1) {

                    short byteCodeSize = 120;  //comp.cursor;

                    if (!(waitAnswer('p', 'r'))) {
                        return;
                    }
                    for(short i=0; i<=50; i+=2)
                    {
                        txtArduino.append("byteCode: "+comp.byteCode[i]+" "+comp.byteCode[i+1]+"\n");
                    }

                    short cur = 0;
                    while (cur < byteCodeSize) {

                        //вычисляем размер пакета
                        byte size = (byte) 64;
                        if (cur + 64 >= byteCodeSize) {
                            size = (byte) (byteCodeSize - cur);
                        }

                        //высчитываем контрольную сумму
                        short summ = 0;
                        for (int i = 0; i < size; i++) {
                            summ += comp.byteCode[cur + i];
                        }

                        //отправляем
                        byte mistakeCounter = 0;
                        while (true) {
                            ++mistakeCounter;

                            if (mistakeCounter == 5) {
                                Toast.makeText(getBaseContext(), "Timeout", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!(waitAnswer('c', 'r'))) {
                                continue;
                            }

                            char size_ = (char) size;
                            mConnectedThread.write(size_);
                            char sum1_ = (char) ((byte) summ / 256);
                            mConnectedThread.write(sum1_);
                            char sum2_ = (char) ((byte) summ % 256);
                            mConnectedThread.write(sum2_);

                            for (int i = 0; i < size; i++) {
                                char byteCode_ = (char) (comp.byteCode[cur + i]);
                                mConnectedThread.write(byteCode_);
                            }

                            getChar();  //получаем символ

                            if (answer == 'r') { // успешно-переходим на новый пакет
                                cur += size;
                                break;
                            }

                        }
                    }

                    if (!(waitAnswer('e', 'r'))) {
                        return;
                    }

                    Toast.makeText(getBaseContext(), "Прошилось успешно", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getBaseContext(), "error at line "+comp.numberOfLine, Toast.LENGTH_SHORT).show();
                }

            }
        });

        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");

        spec.setContent(R.id.tab1);
        spec.setIndicator("Ручное");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Прошивка");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Инфо");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - попытка соединения...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Создание Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth не поддерживается");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth включен...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message){
        finish();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
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
            Log.d(TAG, "...Данные для отправки: " + message + "...");
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
                Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
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
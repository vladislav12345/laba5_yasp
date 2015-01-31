package com.example.vladislavakimov.v_2;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by vladislavakimov on 31.01.15.
 */
public class bytePackage {

    public static List<Package> list = new LinkedList<>();
    public class Package{

        byte[] data ;

        short summ;
        byte size;

        public Package(byte[] bytecode, int begin,int size){
            data=new byte[64];
            //высчитываем контрольную сумму
            summ = 0;
            for (int i = 0; i < size; i++) {
                summ += bytecode[begin+ i];
                data[i]=bytecode[begin+i];
            }


        }

        public boolean send(){
            byte mistakeCounter = 0;
            while(true){
            ++mistakeCounter;

                if (mistakeCounter == 5) {
                    return false;
                }

                if (!(blue.waitAnswer('c', 'r'))) {
                    continue;
                }

                blue.mConnectedThread.write((char)size);
                char sum1_ = (char) ((byte) summ / 256);
                blue.mConnectedThread.write(sum1_);
                char sum2_ = (char) ((byte) summ % 256);
                blue.mConnectedThread.write(sum2_);

                for (int i = 0; i < size; i++) {
                    char byteCode_ = (char) (data[i]);
                    blue.mConnectedThread.write(byteCode_);
                }

                blue.getChar();  //получаем символ

                if (MainActivity.answer == 'r') { // успешно-переходим на новый пакет
                    return true;
                }

            }

        }
    }

        Bluetooth_N blue = MainActivity.blue;
        compiler comp = MainActivity.comp;

        int res;
        static short cur = 0;
        static short byteCodeSize = 120;  //comp.cursor;

        byte size = (byte) 64;
        short summ = 0;
        int count=0;

        public bytePackage(int res){
            this.res=res;
        }



        public void getPackage(){
            if(res==1) {


                        byte[] code = MainActivity.comp.byteCode;

                        short byteCodeSize = 120;  //comp.cursor;

                        if (!(blue.waitAnswer('p', 'r'))) {
                            return;
                        }

                        short cur = 0;
                        while(cur < byteCodeSize) {

                            //вычисляем размер пакета
                            byte size = (byte) 64;
                            if (cur + 64 >= byteCodeSize) {
                                size = (byte) (byteCodeSize - cur);
                            }
                            list.add(new Package(MainActivity.comp.byteCode,cur,size));
                            count++;

                        }

                        if (!(blue.waitAnswer('e', 'r'))) {
                            return;
                        }
                    }


            }//}else{
            //    Toast.makeText(getBaseContext(), "Error at line " + comp.numberOfLine, Toast.LENGTH_SHORT).show();
            //}

        public void sendPackage(){
            //отправляем
            for(int i=0;i<list.size();i++){
                if(!(list.get(i).send())){
                    break;
                }
            }
            //Toast.makeText(getBaseContext(), "Прошилось успешно", Toast.LENGTH_SHORT).show();
        }


}

package com.example.vladislavakimov.v_2;

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
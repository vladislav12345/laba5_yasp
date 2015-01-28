#define FB_LRMASK 0x0C
#define FB_CENTER 0x00
#define FB_LEFT 0x04
#define FB_RIGHT 0x08

#define FB_MASK 0x03
#define FB_STOP 0x00
#define FB_FORWARD 0x01
#define FB_BACK 0x02

#define CMD_MASK 0xF0
#define CMD_SET 0x10
#define CMD_ADD 0x30
#define CMD_RR 0x20
#define CMD_MOTOR 0x70
#define CMD_GOTO 0x40
#define CMD_MODULE 0x50
#define CMD_CMP 0x60

#define MOTOR_SPEC_MASK 0x0F
#define MOTOR_SPEC_SPEEDV 0x03
#define MOTOR_SPEC_SPEED_INC 0x07
#define MOTOR_SPEC_SPEED_DEC 0x0D

#define VEC_INIT 0
#define VEC_PROCESS 1
#define VEC_MILLISEC 2

#define GOTO_MASK 0x0E
#define GOTO_SIMPLE 0x0
#define GOTO_EQUAL 0x02
#define GOTO_NOTEQUAL 0x04
#define GOTO_MORE 0x06
#define GOTO_LESS 0x08
#define GOTO_EQMORE 0x0A
#define GOTO_EQLESS 0x0C

#define RR_SET 0x00
#define RR_ADD 0x04
#define RR_SUB 0x08

#define RR_AND 0x0C
#define RR_OR 0x0D
#define RR_CMP 0x0E

#define RR_NORMAL 0x00
#define RR_MINUS 0x01
#define RR_ABS 0x02
#define RR_NOT 0x03

#define _CRT_SECURE_NO_WARNINGS 1;

#include <string.h>
#include <math.h>
#include <stdint.h> 
#include <stdio.h> 

char* obrab_stroki(char*, int);
int move(char*);
int action_register(char*);
int speed(char*);
int condition(char*);

char answer1[25];
uint8_t Output[512];
uint32_t number_for_output=16;
typedef struct { char *name; uint8_t adress; } struct_label;
typedef struct { char *name; uint8_t adress; } struct_goto_label;

int main(int argc, char **argv) {
	char *str;
	int string_count=-1;
	int fig_skob=0;
	int fig_skob_if=0;
	uint8_t adres_return[16];
	struct_label label[256];
	struct_goto_label goto_label[256];
	int number_label=0;
	int number_goto_label=0;
	char* str_for_label;
	int i,j;
	
	char qwerty[][256]={
		"OnInit(){",
		"move(forever,forward)",
		"r2=1",
		"}",
		"OnProcess(){",
		"r3+=r2",
		"}",
		"OnMillisecond(){",
		"r1+=r2",
		"}",
		""
	};
	
	/*
	strcpy(qwerty[1],"r1=64");
	strcpy(qwerty[2],"r2=-32");
	strcpy(qwerty[3],"r1+=|r2|");
	strcpy(qwerty[4],"if(r1!=r2){");
	strcpy(qwerty[5],"move(forever,forward,left)");
	strcpy(qwerty[6],"speed(255)");
	strcpy(qwerty[7],"}");
	strcpy(qwerty[8],"r1=0");
	strcpy(qwerty[9],"goto(123)");
	strcpy(qwerty[10],"123:");	
	strcpy(qwerty[11],"goto(123)");
	strcpy(qwerty[12],"}");
	strcpy(qwerty[13],"");
	*/
	
	printf("go\n");

	while (strcmp(qwerty[++string_count],""))
	{
		str=obrab_stroki(qwerty[string_count], 0);
		printf("s:%s\n",str);
		
		if (!strcmp(qwerty[string_count]+strlen(qwerty[string_count])-1,"{")) fig_skob++;
		if (!strcmp(qwerty[string_count]+strlen(qwerty[string_count])-1,"}")) fig_skob--;				
		if (!strcmp(qwerty[string_count]+strlen(qwerty[string_count])-1,"{") && !strcmp(str,"if")) { fig_skob_if++; adres_return[fig_skob_if]=number_for_output+3; }
		if (!strcmp(qwerty[string_count]+strlen(qwerty[string_count])-1,"}") && fig_skob_if>0) { Output[adres_return[fig_skob_if]]=number_for_output; fig_skob_if--;}
		if (fig_skob==0)
		{
			Output[number_for_output++]=0;
			Output[number_for_output++]=0;
		}

		if (!strcmp(qwerty[string_count]+strlen(qwerty[string_count])-1,":")) 
		{ 
			label[number_label].name=qwerty[string_count];
			label[number_label++].adress = number_for_output;
			continue;
		}				
		if (!strcmp(str,"goto")) 
		{
			str_for_label = (char*)malloc((strlen(qwerty[string_count])-5) * sizeof(char));;
			for (i=0; i+6<strlen(qwerty[string_count]); i++) 
				str_for_label[i]=qwerty[string_count][i+5];
			str_for_label[i] = ':';
			str_for_label[i+1] = '\0';
			goto_label[number_goto_label].name=str_for_label;
			goto_label[number_goto_label++].adress = number_for_output+1;			
			Output[number_for_output++]=CMD_GOTO|GOTO_SIMPLE;
			Output[number_for_output++]=0;
			continue;
		}
		
		//printf("s:%s",str);
		if (!strcmp(str,"OnInit")) {Output[0]=number_for_output/2; continue;}
		if (!strcmp(str,"OnProcess")) {Output[1]=number_for_output/2; printf("on:\n"); continue;}
		if (!strcmp(str,"OnMillisecond")) {Output[2]=number_for_output/2; continue;}

		if (!strcmp(str,"r")) if (action_register(qwerty[string_count]) == 1) {printf("ERROR in string: %d", string_count); return 1;} else continue;
		if (!strcmp(str,"move")) if (move(qwerty[string_count]) == 1) {printf("ERROR in string: %d", string_count); return 1;} else continue;
		if (!strcmp(str,"speed")) if (speed(qwerty[string_count]) == 1) {printf("ERROR in string: %d", string_count); return 1;} else continue;
		if (!strcmp(str,"if")) if (condition(qwerty[string_count]) == 1) {printf("ERROR in string: %d", string_count); return 1;} else continue;
		
	}

	for (i=0; i<number_goto_label; i++)
	{
		for (j=0; j<number_label; j++)
			if (!strcmp(label[j].name,goto_label[i].name))
			{
				Output[goto_label[i].adress]=label[j].adress;
				break;
			}
	}
	
	for(i=0;i<50;i++){
		printf("%X %X %x\n",i*2,Output[i*2],Output[i*2+1]);
	}

	getchar();
	return 0;
}

int action_register(char* qwerty)
{
	uint8_t answer1, answer2;
	int number1=0, number2=0;
	int otr=0;
	int i;
	
	answer1=0; answer2=0;

	for (i=1; abs(qwerty[i]-52)<=5; i++)
	{
		number1=number1*10+(qwerty[i]-48);
	}

	if (qwerty[i]=='=' && !(qwerty[i+1]=='r' || qwerty[i+2]=='r') ) //0001
	{
		if (qwerty[i+1]=='-') {otr=1; i++;}
		for (i=i+1; abs(qwerty[i]-52)<=5; i++)
		{
			number2=number2*10+(qwerty[i]-48);
		}
		answer1=CMD_SET|number1;
		answer2=number2;
		if( otr ) {answer2=-answer2;}

		Output[number_for_output++]=answer1;
		Output[number_for_output++]=answer2;
		return 0;
	}

	if (qwerty[i]=='+' && qwerty[i+1]=='=' && !(qwerty[i+2]=='r' || qwerty[i+3]=='r') ) //0010
	{
		if (qwerty[i+2]=='-') {otr=1; i++;}
		for (i=i+2; abs(qwerty[i]-52)<=5; i++)
		{
			number2=number2*10+(qwerty[i]-48);
		}
		answer1=CMD_ADD|number1;
		answer2=number2;
		if( otr ) {answer2=-answer2;}
		
		Output[number_for_output++]=answer1;
		Output[number_for_output++]=answer2;
		return 0;
	}

	if (qwerty[i]=='+') { i++; 	answer1=RR_ADD; }
	if (qwerty[i]=='-') { i++; 	answer1=RR_SUB; }
	i++;

	if (qwerty[i]=='r') {answer1=RR_NORMAL|answer1|CMD_RR; i--;} else
	if (qwerty[i]=='-') answer1=RR_MINUS|answer1|CMD_RR; else
	if (qwerty[i]=='|') answer1=RR_ABS|answer1|CMD_RR; else
	if (qwerty[i]=='!') answer1=RR_NOT|answer1|CMD_RR; else
	if (qwerty[i]=='&') answer1=RR_AND|CMD_RR; else return 1;
	i=i+2;
		for (i; abs(qwerty[i]-52)<=5; i++)
		{
			number2=number2*10+(qwerty[i]-48);
		}
	if ( (answer1==RR_NOT|answer1|CMD_RR) && qwerty[i]!='|')
	{
		answer1=RR_OR|CMD_RR;
	}
	
	Output[number_for_output++]=answer1;
	Output[number_for_output++]=number1*16+number2;
	return 0;
}

int move(char* qwerty)
{
	uint8_t answer=CMD_MOTOR;
	int number=0;
	int i=5;
	char* str;

	str=obrab_stroki(qwerty, i);
	if (!strcmp(str,"forever"))
	{
		i+=strlen(str);
		number=0;
	}
	else
	for (i; abs(qwerty[i]-52)<=5; i++)
	{
		number=number*10+(qwerty[i]-48);
	}
	i++;

		str=obrab_stroki(qwerty, i);
		if (!strcmp(str,"forward"))
		{
			i+=strlen(str)+1;
			answer=answer|FB_FORWARD;
		}else
		if (!strcmp(str,"stop"))
		{
			i+=strlen(str)+1;
			answer=answer|FB_STOP;
		}else
		if (!strcmp(str,"back"))
		{
			i+=strlen(str)+1;
			answer=answer|FB_BACK;
		}
	
	if (qwerty[i-1]==')') { answer=answer|FB_FORWARD; }else
	{
		str=obrab_stroki(qwerty, i);
	if (!strcmp(str,"left")) { answer=answer|FB_LEFT; }else
	if (!strcmp(str,"right")) { answer=answer|FB_RIGHT; }else return 1;
	}
	
	Output[number_for_output++]=answer;
	Output[number_for_output++]=number;

	return 0;
}

int speed(char* qwerty)
{
	uint8_t answer=CMD_MOTOR;
	int number=0;
	int i=6;

	if (qwerty[i]=='+')
	{
		if (qwerty[i+1]=='1' && qwerty[i+2]=='0')
			{
				Output[number_for_output++]=answer|MOTOR_SPEC_SPEED_INC;
				Output[number_for_output++]=0;
				return 0;
			}
		else
			return 1;
	}

	if (qwerty[i]=='-')
	{
		if (qwerty[i+1]=='1' && qwerty[i+2]=='0')
			{
				Output[number_for_output++]=answer|MOTOR_SPEC_SPEED_DEC;
				Output[number_for_output++]=0;
				return 0;
			}
		else
			return 1;
	}

	if ( abs(qwerty[i]-52)<=5 )
	{
		for (i; abs(qwerty[i]-52)<=5; i++)
		{
			number=number*10+(qwerty[i]-48);
		}
		Output[number_for_output++]=answer|MOTOR_SPEC_SPEEDV;
		Output[number_for_output++]=number;
		return 0;
	}

	return 1;
}

int condition(char* qwerty)
{
	int i=4, j,k;
	uint8_t number1=0, number2=0;

	for (i; abs(qwerty[i]-52)<=5; i++) { number1=number1*10+(qwerty[i]-48); }
	j=i;
	while (abs(qwerty[j]-52)>5) { j++; }
	k=j;
	for (j; abs(qwerty[j]-52)<=5; j++) { number2=number2*10+(qwerty[j]-48); }

	if (qwerty[k-1]=='r')
	{
		Output[number_for_output++]=CMD_RR|RR_CMP;
		Output[number_for_output++]=number1*16+number2;
	}
	else
	{
		Output[number_for_output++]=CMD_CMP|number1;
		Output[number_for_output++]=number2;
	}

	if (qwerty[i]=='=' && qwerty[i+1]=='=')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_NOTEQUAL;
		Output[number_for_output++]=0;
		return 0;
	}
	
	if (qwerty[i]=='!' && qwerty[i+1]=='=')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_EQUAL;
		Output[number_for_output++]=0;
		return 0;
	}
	
	if (qwerty[i]=='<' && qwerty[i+1]=='=')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_MORE;
		Output[number_for_output++]=0;
		return 0;
	}
	
	if (qwerty[i]=='>' && qwerty[i+1]=='=')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_LESS;
		Output[number_for_output++]=0;
		return 0;
	}
	
	if (qwerty[i]=='<')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_EQMORE;
		Output[number_for_output++]=0;
		return 0;
	}
	
	if (qwerty[i]=='>')
	{
		Output[number_for_output++]=CMD_GOTO|GOTO_EQLESS;
		Output[number_for_output++]=0;
		return 0;
	}
	return 1;
}

//--------

char* obrab_stroki(char* qwerty, int i)
{
	int count;

	for (count=0; (qwerty[count+i]<='z' && qwerty[count+i]>='a') || (qwerty[count+i]<='Z' && qwerty[count+i]>='A'); count++)
	{
		answer1[count]=qwerty[count+i];
	}
	answer1[count]='\0';

	return answer1;
}

Code code;

void codeInit(){
	for(int i=0;i<512;i++){
		code.code[i]=0;
	}
	
	byte prog[]={
	0x08,0x0b,0xd,0,
	0,0,0,0,
	0,0,0,0,
	0,0,0,0,
	0,0,0x12,0x01,
	0,0,
	0x2d,0x32,0,0,
	0x2d,0x12,
	0,0
	};
	
	memcpy(code.code,prog,sizeof(prog));
	
	for(int i=0;i<16;i++){
		Serial.print(i*2);
		Serial.print(" ");
		Serial.print(code.code[i*2]);
		Serial.print(" ");
		Serial.println(code.code[i*2+1]);
	}

	
	for(int i=0;i<16;i++){
		code.reg[i]=0;
	}
};

void codeDo(byte cursor){
	byte cur=cursor;
	byte b1,b2,cmd;
	byte motorMoveNumber=0;
	
	while(true){
		b1=code.code[cur*2];
		b2=code.code[cur*2+1];
		Serial.println(b1);
		Serial.println(b2);
	
		cmd=b1 & CMD_MASK;
		
		if(cmd==CMD_EXIT){
			break;
		}
	
		switch (cmd){
			case CMD_SET:{
				byte regNum=b1 & SET_MASK;
				code.reg[regNum]=b2;
				++cur;
				break;
			}case CMD_RR:{
				byte operation1=b1 & RR_MASK;
				byte operation2;
				byte regNum1=b2 & RR_REG1MASK;
				byte regNum2=b2 & RR_REG2MASK;
				
				if(operation1<RR_ADD){
					operation2=operation1-RR_SET;
					operation1=RR_SET;
				}else if(operation1<RR_SUB){
					operation2=operation1-RR_ADD;
					operation1=RR_ADD;
				}else if(operation1<RR_AND){
					operation2=operation1-RR_SUB;
					operation1=RR_SUB;
				}else if(operation1==RR_AND){
					code.reg[regNum1]=code.reg[regNum1] & code.reg[regNum2];
					++cur;
					break;
				}else if(operation1==RR_OR){
					code.reg[regNum1]=code.reg[regNum1] | code.reg[regNum2];
					++cur;
					break;
				}else if(operation1==RR_CMP){
					code.cmpFlag=(char)code.reg[regNum1]-(char)code.reg[regNum2];
					++cur;
					break;
				}
				
				byte r2Value;
				switch (operation2){
					case RR_NORMAL:
						r2Value=code.reg[regNum2];
						break;
					case RR_MINUS:
						r2Value=-code.reg[regNum2];
						break;
					case RR_ABS:
						if(code.reg[regNum2]<0){
							r2Value=-code.reg[regNum2];
						}else{
							r2Value=code.reg[regNum2];
						}
						break;
					case RR_NOT:
						r2Value=~code.reg[regNum2];
						break;
				}
				
				switch (operation1){
					case RR_SET:
						code.reg[regNum1]=r2Value;
						break;
					case RR_ADD:
						code.reg[regNum2]+=r2Value;
						break;
					case RR_SUB:
						code.reg[regNum2]-=r2Value;
						break;
				}
				++cur;
				break;
			}case CMD_ADD:{
				byte regNum=b1 & SET_MASK;
				code.reg[regNum]+=b2;
				++cur;
				break;
			}case CMD_MOTOR:{
				byte fb=b1 & FB_MASK;
				
				if(fb==MOTOR_SPEC_END){
					byte specCmd=b1 & MOTOR_SPEC_MASK;
					switch (specCmd){
						case MOTOR_SPEC_SPEEDV:
							if(b2<=25){
								motorSpeed(b2);
							}
							break;
						case MOTOR_SPEC_SPEED_INC:
							if(motor.speed<25){
								motorSpeed(motor.speed+1);
							}
							break;
						case MOTOR_SPEC_SPEED_DEC:
							if(motor.speed>0){
								motorSpeed(motor.speed-1);
							}
							break;
					}
				}else{
					if(motorMoveNumber==0){
						byte lr=b1 & LR_MASK;
						motorMoveNumber=1;
						
						if(motor.seqCS==0){
							motor.seqCS=1;
							motor.seqHead=0;
							motor.seqTail=0;
							motorMove(b2,fb,lr);
							motor.seqCS=0;
						}
					}else{
						pushMotorSeq(b1,b2);
					}
				}
				++cur;
				break;
			}case CMD_GOTO:{
				byte condition=b1 & GOTO_MASK;
				byte changeCur=0;
				
				switch (condition){
					case GOTO_SIMPLE:
						changeCur=1;
						break;
					case GOTO_EQUAL:
						if(code.cmpFlag==0){
							changeCur=1;
						}
						break;
					case GOTO_NOTEQUAL:
						if(code.cmpFlag!=0){
							changeCur=1;
						}
						break;
					case GOTO_MORE:
						if(code.cmpFlag>0){
							changeCur=1;
						}
						break;
					case GOTO_LESS:
						if(code.cmpFlag<0){
							changeCur=1;
						}
						break;
					case GOTO_EQMORE:
						if(code.cmpFlag>=0){
							changeCur=1;
						}
						break;
					case GOTO_EQLESS:
						if(code.cmpFlag<=0){
							changeCur=1;
						}
				}
				
				if(changeCur!=0){
					cur=b2;
				}else{
					++cur;
				}
				break;
						
			}case CMD_CMP:{
				byte regNum=b1 & CMP_MASK;
				code.cmpFlag=(char)code.reg[regNum]-(char)b2;
				++cur;
				break;
			}
		}
	}
}

#define STATE_BEGIN 0
#define STATE_SIZE 1
#define STATE_SUMML 2
#define STATE_SUMMH 3
#define STATE_CODE 4

void loadCode(){
	unsigned int cur=0;
	unsigned int oldCur=0;
	unsigned int summ;
	unsigned int getSumm=0;
	byte size;
	byte state=STATE_BEGIN;
	
	
	while(true){
		if(Serial.available()){
			byte v=Serial.read();
			if(state==STATE_BEGIN){
				if(v=='e'){
					break;
				}else{
					state=STATE_SIZE;
				}
			}
			
			switch(state){
				case STATE_SIZE:
					size=v;
					state=STATE_SUMMH;
					break;
				case STATE_SUMMH:
					summ=v;
					state=STATE_SUMML;
					break;
				case STATE_SUMML:
					summ=summ*256+v;
					state=STATE_CODE;
					break;
				case STATE_CODE:
					byte v=v;
					code.code[oldCur+cur]=v;
					getSumm+=v;
					++cur;
					if(cur==size){
						char answer='r';
						if(summ!=getSumm){
							answer='e';
							oldCur+=cur;
						}
						cur=0;
						size=0;
						getSumm=0;
						state=STATE_BEGIN;
					
						Serial.print(answer);
					}
					break;
			}
		}
	}
	
	Serial.print('r');
	core.mode=MODE_DO_PROGRAM;
}

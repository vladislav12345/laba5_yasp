
Motor motor;

void motorMove(byte time, byte fb, byte lr){
	motor.time=time;
	motor.timeCounter=0;

	if(fb==FB_FORWARD){
		digitalWrite(MOTOR_FB_BPIN,LOW);
		motor.fbdelay=50;
	}else if(fb==FB_BACK){
		digitalWrite(MOTOR_FB_FPIN,LOW);
		motor.fbdelay=50;
	}else if(fb==FB_STOP){
		digitalWrite(MOTOR_FB_FPIN,LOW);
		digitalWrite(MOTOR_FB_BPIN,LOW);
	}

	if(lr==LR_LEFT){
		if(motor.lr==LR_RIGHT){
			digitalWrite(MOTOR_LR_RPIN,LOW);
			motor.lrdelay=100;
		}else{
			motor.lrdelay=50;
		}
		motor.lrPWM=12;
		digitalWrite(MOTOR_LR_LPIN,HIGH);
	}else if(lr==LR_RIGHT){
		if(motor.lr==LR_LEFT){
			digitalWrite(MOTOR_LR_LPIN,LOW);
			motor.lrdelay=100;
		}else{
			motor.lrdelay=50;
		}
		motor.lrPWM=12;
		digitalWrite(MOTOR_LR_RPIN,HIGH);
	}else if(lr==LR_CENTER){
		if(motor.lr==LR_LEFT){
			digitalWrite(MOTOR_LR_LPIN,LOW);
			motor.lrdelay=50;
			digitalWrite(MOTOR_LR_RPIN,HIGH);
			motor.lrPWM=12;
		}else if(motor.lr==LR_RIGHT){
			digitalWrite(MOTOR_LR_RPIN,LOW);
			motor.lrdelay=50;
			digitalWrite(MOTOR_LR_LPIN,HIGH);
			motor.lrPWM=12;
		}
	}
	
	motor.fbPWM=motor.speed;
	motor.lr=lr;
	motor.fb=fb;
}

void motorProcess(){
	if(motor.fbdelay>0){
		--motor.fbdelay;
		if(motor.fbdelay==0){
			
			if(motor.fb==FB_FORWARD){
				motor.lrPWM=12;
				digitalWrite(MOTOR_FB_FPIN,HIGH);
			}else if(motor.fb==FB_BACK){
				digitalWrite(MOTOR_FB_BPIN,HIGH);
			}
		}
	}

	if(motor.lrdelay>0){
		--motor.lrdelay;

		if(motor.lrdelay==0){
			if(motor.lr==LR_CENTER){
				digitalWrite(MOTOR_LR_LPIN,LOW);
				digitalWrite(MOTOR_LR_RPIN,LOW);
			}
			motor.lrPWM=4;
		}
	}

	if(motor.time!=0){
		++motor.timeCounter;

		if(motor.timeCounter==MAX_TIME_COUNTER){
			motor.timeCounter=0;
			--motor.time;
			if(motor.time==0){
				if(motor.seqTail!=motor.seqHead){
					if(motor.seqCS==0){
						motor.seqCS=1;
						byte tail=motor.seqTail;
						++motor.seqTail;
						motorMove(motor.seqs[tail].time, motor.seqs[tail].cmd & FB_MASK, motor.seqs[tail].cmd & LR_MASK);
						motor.seqCS=0;
					}else{
						motorMove(1,FB_STOP,LR_CENTER);
					}
				}else{
					motorMove(0,FB_STOP,LR_CENTER);
				}
			}
		}
	}
	

}

void motorSpeed(byte value){
	motor.speed=value;
	motor.fbPWM=value;
}

void motorInit(){
	pinMode(MOTOR_LR_LPIN,OUTPUT);
	pinMode(MOTOR_LR_RPIN,OUTPUT);
	pinMode(MOTOR_LR_SPEEDPIN,OUTPUT);

	pinMode(MOTOR_FB_FPIN,OUTPUT);
	pinMode(MOTOR_FB_BPIN,OUTPUT);
	pinMode(MOTOR_FB_SPEEDPIN,OUTPUT);
	
	motor.seqCS=0;
	motor.seqHead=0;
	motor.seqTail=0;
	
	motor.fbdelay=0;
	motor.lrdelay=0;
	motor.lrPWM=1;
	motorSpeed(8);
	motor.lr=LR_CENTER;
	motorMove(0,FB_STOP,LR_CENTER);
}

void pushMotorSeq(byte cmd,byte time){
	if(motor.seqCS==0){
		motor.seqCS=1;
		
		if(motor.seqHead<MOTOR_SEQNUMBER){
			motor.seqs[motor.seqHead].cmd=cmd;
			motor.seqs[motor.seqHead].time=time;
			
			++motor.seqHead;
		}
		
		motor.seqCS=0;
	}
}




void init(){
	core.mode=MODE_CONTROL;
	Serial.begin(SERIAL_FREQUENCITY);
	motorInit();
	eventQueueInit();
}

void process(){
	switch(core.mode){
		case MODE_DO_PROGRAM:{//режим исполнения программы
			byte event=getEvent();

			break;
		}case MODE_CONTROL:{//режим ручного управления
			if(Serial.available){
				byte cmd=Serial.read();

				if(cmd=='w'){
					motorMove(0,motor.lr,FB_FORWARD);
				}else if(cmd=='W' && motor.fb==FB_FORWARD){
					motorMove(0,motor.lr,FB_STOP);
				}

				if(cmd=='s'){
					motorMove(0,motor.lr,FB_BACK);
				}else if(cmd=='S' && motor.fb==FB_BACK){
					motorMove(0,motor.lr,FB_STOP);
				}

				if(cmd=='a'){
					motorMove(0,LR_LEFT,motor.fb);
				}else if(cmd=='A' && motor.lr==LR_LEFT){
					motorMove(0,LR_CENTER,motor.fb);
				}

				if(cmd=='d'){
					motorMove(0,LR_RIGHT,motor.fb);
				}else if(cmd=='D' && motor.lr==LR_RIGHT){
					motorMove(0,LR_CENTER,motor.fb);
				}

				if(cmd=='d'){
					core.mode=MODE_DO_PROGRAM;
				}else if(cmd=='P'){
					core.mode=MODE_PROGRAMMING;
				}
			}
	
			break;
		}case MODE_PROGRAMMING:{//режим программирования машинки

			break;
		}
	}
}



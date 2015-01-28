

void carInit(){
	//Init Analog
	#if FASTADC
		// set prescale to 16
		sbi(ADCSRA,ADPS2) ;
		cbi(ADCSRA,ADPS1) ;
		cbi(ADCSRA,ADPS0) ;
	#endif
		
	Serial.begin(SERIAL_FREQUENCY);
	motorInit();
	eventQueueInit();
	timerInit();
	codeInit();
	
	byte v=~MOTOR_LR_SPEEDMASK;
	Serial.println(v);

	core.mode=MODE_DO_PROGRAM;
	codeDo(code.code[VEC_INIT]);
}


void carProcess(){
	switch(core.mode){
		case MODE_DO_PROGRAM:{//режим исполнения программы
			while(isEvent()){
				byte event=popEvent();
				codeDo(code.code[eventQueue.events[event].vector]);
			}
			//codeDo(code.code[VEC_PROCESS]);
			if(Serial.available()){
				byte cmd=Serial.read();

				if(cmd=='w'){
					Serial.println("jjj");
					motorMove(0,FB_FORWARD,motor.lr);
				}else if(cmd=='W' && motor.fb==FB_FORWARD){
					motorMove(0,FB_STOP,motor.lr);
				}

				if(cmd=='s'){
					motorMove(0,FB_BACK,motor.lr);
				}else if(cmd=='S' && motor.fb==FB_BACK){
					motorMove(0,FB_STOP,motor.lr);
				}

				if(cmd=='a'){
					motorMove(0,motor.fb,LR_LEFT);
				}else if(cmd=='A' && motor.lr==LR_LEFT){
					motorMove(0,motor.fb,LR_CENTER);
				}

				if(cmd=='d'){
					motorMove(0,motor.fb,LR_RIGHT);
				}else if(cmd=='D' && motor.lr==LR_RIGHT){
					motorMove(0,motor.fb,LR_CENTER);
				}

				if(cmd=='P'){
					core.mode=MODE_PROGRAMMING;
					Serial.print('r');
				}
			}
	
			break;
		}case MODE_PROGRAMMING:{//режим программирования машинки
			loadCode();
			break;
		}
	}
}



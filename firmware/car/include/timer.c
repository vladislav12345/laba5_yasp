Timer timer;


void timerInit(){
	timer.counter=0;
	TCCR2B=TIMER_DIVIDER;// /64
	TIMSK2=4;//совпадение с B
	OCR2B=TIMER_STEP;
	
	sei();
}

ISR(TIMER2_COMPB_vect){//обработчик прерывания по совпадению А
	TCNT2=0;
	++timer.counter;
	if(motor.lrPWM==timer.counter){
		PORTD=PORTD & 0xf7;
	}
	if(motor.fbPWM==timer.counter){
		PORTB=PORTB & 0xf7;
	}
	if(timer.counter==25){
		PORTD=PORTD | MOTOR_LR_SPEEDMASK;
		PORTB=PORTB | MOTOR_FB_SPEEDMASK;
		timer.counter=0;
		
		pushEvent(VEC_MILLISEC);
		motorProcess();
		
	}
}
/*
void timerInit(){
	timer.counter=0;
	TCCR2B=4;// /64
	TIMSK2=4;//совпадение с B
	OCR2B=230;
	
	sei();
}

ISR(TIMER2_COMPB_vect){//обработчик прерывания по совпадению А
	cli();
	byte v=TCNT2;
	Serial.println(v);
	TCNT2=0;
	sei();
	
	if(TCNT2==motor.lrPWM){
		PORTD=PORTD & 0xf7;
		if(motor.fbPWM>TCNT2){
			timer.counter++;
			OCR2B=motor.fbPWM;
		}else{
			PORTB=PORTB & 0xf7;
			OCR2B=250;
		}
	}
	if(TCNT2==motor.fbPWM){
		timer.counter++;
		PORTB=PORTB & 0xf7;
		if(motor.lrPWM>TCNT2){
			OCR2B=motor.lrPWM;
		}else{
			PORTD=PORTD & 0xf7;
			OCR2B=250;
		}
	}
	
	if(TCNT2==250){
		PORTD=PORTD | 0x08;
		PORTB=PORTB | 0x08;
		
		if(motor.lrPWM<motor.fbPWM){
			OCR2B=motor.lrPWM;
		}else{
			OCR2B=motor.fbPWM;
		}
		
		pushEvent(VEC_MILLISEC);
		motorProcess();
		Serial.println(timer.counter);
		//Serial.println(motor.fbPWM);
		//Serial.println(OCR2B);
		TCNT2=0;
	}
	
	//Serial.println(TCNT2);
}
*/

/*
void timerInit(){
	timer.counter=0;
	TCCR2B=4;// /64
	TIMSK2=4;//совпадение с B
	OCR2B=230;
	
	sei();
}

ISR(TIMER2_COMPB_vect){//обработчик прерывания по совпадению А
	cli();
	byte v=TCNT2;
	Serial.println(v);
	TCNT2=0;
	sei();
	
	if(TCNT2==motor.lrPWM){
		PORTD=PORTD & 0xf7;
		if(motor.fbPWM>TCNT2){
			timer.counter++;
			OCR2B=motor.fbPWM;
		}else{
			PORTB=PORTB & 0xf7;
			OCR2B=250;
		}
	}
	if(TCNT2==motor.fbPWM){
		timer.counter++;
		PORTB=PORTB & 0xf7;
		if(motor.lrPWM>TCNT2){
			OCR2B=motor.lrPWM;
		}else{
			PORTD=PORTD & 0xf7;
			OCR2B=250;
		}
	}
	
	if(TCNT2==250){
		PORTD=PORTD | 0x08;
		PORTB=PORTB | 0x08;
		
		if(motor.lrPWM<motor.fbPWM){
			OCR2B=motor.lrPWM;
		}else{
			OCR2B=motor.fbPWM;
		}
		
		pushEvent(VEC_MILLISEC);
		motorProcess();
		Serial.println(timer.counter);
		//Serial.println(motor.fbPWM);
		//Serial.println(OCR2B);
		TCNT2=0;
	}
	
	//Serial.println(TCNT2);
}
*/


struct Timer{
	byte counter;
};

void timerInit();

ISR(TIMER2_COMPA_vect);



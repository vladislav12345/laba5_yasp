struct Timer{

}timer;


ISR(TIMER2_COMPA_vect){//обработчик прерывания по совпадению А
	OnMillisecond();
		TCNT2=0;
}



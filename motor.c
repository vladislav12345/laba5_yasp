
struct Motor{
	byte time;
	byte speed;
	byte lr;
	byte fb;	
}motor;

void motorMove(byte time, byte fb, byte lr){
	car.time=time;
	car.fb=fb;
	car.lr=lr;

	//включаем двигатели
}

void motorSpeed(byte value){
	motor.speed=value;

	//устанавливаем ШИМ
}

void motorInit(){
	pinMode(MOTOR_LR_LPIN,OUTPUT);
	pinMode(MOTOR_LR_RPIN,OUTPUT);
	pinMode(MOTOR_LR_SPEEDPIN,OUTPUT);

	pinMode(MOTOR_FB_FPIN,OUTPUT);
	pinMode(MOTOR_FB_BPIN,OUTPUT);
	pinMode(MOTOR_FB_SPEEDPIN,OUTPUT);
	
	motorSpeed(100);
	motorMove(0,FB_STOP,LR_CENTER);
}

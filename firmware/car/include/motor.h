struct MotorSeq{
	byte cmd;
	byte time;
};

struct Motor{
	byte time;
	byte timeCounter;
	byte speed;
	byte lr;
	byte fb;
	byte lrdelay;
	byte fbdelay;
	byte lrPWM;
	byte fbPWM;
	
	byte seqCS;
	MotorSeq seqs[MOTOR_SEQNUMBER];
	byte seqHead;
	byte seqTail;
};

void motorMove(byte time, byte fb, byte lr);

void motorProcess();

void motorSpeed(byte value);

void motorInit();

void pushMotorSeq(byte cmd,byte time);

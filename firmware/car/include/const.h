#define LR_MASK 0x0C
#define LR_CENTER 0x00
#define LR_LEFT 0x04
#define LR_RIGHT 0x08

#define FB_MASK 0x03
#define FB_STOP 0x00
#define FB_FORWARD 0x01
#define FB_BACK 0x02

#define CMD_MASK 0xF0
#define CMD_EXIT 0x00
#define CMD_SET 0x10
#define CMD_ADD 0x30
#define CMD_RR 0x20
#define CMD_MOTOR 0x70
#define CMD_GOTO 0x40
#define CMD_MODULE 0x50
#define CMD_CMP 0x60

#define MOTOR_SPEC_MASK 0x0F
#define MOTOR_SPEC_END 0x03
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

#define SET_MASK 0x0F
#define CMP_MASK 0x0F

#define RR_MASK 0x0F
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

#define RR_REG1MASK 0xF0
#define RR_REG2MASK 0x0F

#define MOTOR_LR_LPIN 2
#define MOTOR_LR_RPIN 4
#define MOTOR_LR_SPEEDPIN 3
#define MOTOR_LR_SPEEDMASK 0x08

#define MOTOR_FB_FPIN 8
#define MOTOR_FB_BPIN 7
#define MOTOR_FB_SPEEDPIN 11
#define MOTOR_FB_SPEEDMASK 0x08

#define MOTOR_SEQNUMBER 8

#define SERIAL_FREQUENCY 9600

#define EVENT_NUMBER 8

#define MODE_DO_PROGRAM 0
#define MODE_CONTROL 1
#define MODE_PROGRAMMING 2

#define MAX_TIME_COUNTER 200

#define TIMER_DIVIDER 4
#define TIMER_STEP 10


public final byte FB_LRMASK = 0x0C
public final byte FB_CENTER = 0x00
public final byte FB_LEFT = 0x04
public final byte FB_RIGHT = 0x08

public final byte FB_MASK = 0x03
public final byte FB_STOP = 0x00
public final byte FB_FORWARD = 0x01
public final byte FB_BACK = 0x02

public final byte CMD_MASK = 0xF0
public final byte CMD_SET = 0x10
public final byte CMD_ADD = 0x30
public final byte CMD_RR = 0x20
public final byte CMD_MOTOR = 0x30
public final byte CMD_GOTO = 0x40
public final byte CMD_MODULE = 0x50
public final byte CMD_CMP = 0x60

public final byte MOTOR_SPEC_MASK = 0x0F
public final byte MOTOR_SPEC_SPEEDV = 0x03
public final byte MOTOR_SPEC_SPEED_INC = 0x07
public final byte MOTOR_SPEC_SPEED_INC = 0x0D

public final byte VEC_INIT = 0
public final byte VEC_PROCESS = 1
public final byte VEC_MILLISEC = 2

public final byte GOTO_MASK = 0x0E
public final byte GOTO_SIMPLE = 0x0
public final byte GOTO_EQUAL = 0x02
public final byte GOTO_NOTEQUAL = 0x04
public final byte GOTO_MORE = 0x06
public final byte GOTO_LESS = 0x08
public final byte GOTO_EQMORE = 0x0A
public final byte GOTO_EQLESS = 0x0C

public final byte RR_SET = 0x00
public final byte RR_ADD = 0x04
public final byte RR_SUB = 0x08

public final byte RR_AND = 0x0C
public final byte RR_OR = 0x09
public final byte RR_CMP = 0x0E

public final byte RR_NORMAL = 0x00
public final byte RR_MINUS = 0x01
public final byte RR_ABS = 0x02
public final byte RR_NOT = 0x03

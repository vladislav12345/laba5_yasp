/*	Made by TrionProg	*
 *	Jan 2015			*
 *						*/

#include "Arduino.h"
#ifndef CAR
#define CAR

#define FASTADC 1

// defines for setting and clearing register bits
#ifndef cbi
#define cbi(sfr, bit) (_SFR_BYTE(sfr) &= ~_BV(bit))
#endif
#ifndef sbi
#define sbi(sfr, bit) (_SFR_BYTE(sfr) |= _BV(bit))
#endif

int freeRam ();

struct Core{
	byte mode;
};

#include "include/const.h"
#include "include/event.h"
#include "include/motor.h"
#include "include/timer.h"
#include "include/code.h"
#include "include/process.h"

#endif

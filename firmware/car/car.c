#include "car.h"

Core core;


#include "include/event.c"
#include "include/motor.c"
#include "include/timer.c"
#include "include/code.c"
#include "include/process.c"

int freeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

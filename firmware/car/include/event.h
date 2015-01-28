
struct Event{
	byte vector;
};

struct EventQueue{
	byte head;
	byte tail;
	byte cs;
	Event events[EVENT_NUMBER];
};

void pushEvent(byte vector);

byte isEvent();

byte popEvent();

void eventQueueInit();

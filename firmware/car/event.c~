

struct Event{
	byte vector;
};

struct EventQueue{
	byte head;
	byte tail;
	byte cs;
	Event events[EVENT_NUMBER];
}eventQueue;

void pushEvent(byte vector){
	if(cs==0){
		cs=1;
		eventQueue.events[head].vector=vector;
		++eventQueue.head;
		if(eventQueue.head==EVENT_NUMBER){
			eventQueue.head=0;
		}
		if(eventQueue.head==eventQueue.tail){
			++eventQueue.tail;
			if(eventQueue.tail==EVENT_NUMBER){
				eventQueue.tail=0;
			}
		}
		cs=0;
	}
}

byte isEvent(){
	return eventQueue.head-eventQueue.tail;
}

byte popEvent(){
	if(cs==0){
		cs=1;
		byte tail=eventQueue.tail;
		++eventQueue.tail;
		if(eventQueue.tail==EVENT_NUMBER){
			eventQueue.tail=0;
		}
		cs=0;
		return tail;
	}
	return 0;
}

void eventQueueInit(){
	eventQueue.head=0;
	eventQueue.tail=0;
	eventQueue.cs=0;
}

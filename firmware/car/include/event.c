
EventQueue eventQueue;

void pushEvent(byte vector){
	if(eventQueue.cs==0){
		eventQueue.cs=1;
		eventQueue.events[eventQueue.head].vector=vector;
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
		eventQueue.cs=0;
	}
}

byte isEvent(){
	return eventQueue.head-eventQueue.tail;
}

byte popEvent(){
	if(eventQueue.cs==0){
		eventQueue.cs=1;
		byte tail=eventQueue.tail;
		++eventQueue.tail;
		if(eventQueue.tail==EVENT_NUMBER){
			eventQueue.tail=0;
		}
		eventQueue.cs=0;
		return tail;
	}
	return 0;
}

void eventQueueInit(){
	eventQueue.head=0;
	eventQueue.tail=0;
	eventQueue.cs=0;
}

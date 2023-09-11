package event

import (
	log "github.com/sirupsen/logrus"
	"time"
)

type EventFirer struct {
	waitTime int
	move     func() error
	counter  int
}

func CreateEventFirer(waitTime int, move func() error) *EventFirer {
	return &EventFirer{
		waitTime: waitTime,
		move:     move,
		counter:  0,
	}
}

func (e *EventFirer) startCounter(timer int) {
	e.counter = timer
	for e.counter > 0 {
		// Wait for 1 second
		time.Sleep(1 * time.Second)
		e.counter--
		log.Info("Counter: " + string(e.counter))
	}
	e.move()
}

func (e *EventFirer) registerEvent() {
	if e.counter == 0 {
		go e.startCounter(e.waitTime)
	} else {
		e.counter = e.waitTime
	}
}

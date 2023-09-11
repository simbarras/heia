package event

import (
	"container-clusterviewer/pkg/cluster/objects"
	"context"
	log "github.com/sirupsen/logrus"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/watch"
	"k8s.io/client-go/kubernetes"
	"sync"
)

type ClusterListener struct {
	e                         EventFirer
	c                         *kubernetes.Clientset
	registeredNamespaces      map[string]watch.Interface
	registeredNamespacesMutex sync.Mutex
}

func CreateClusterListener(client *kubernetes.Clientset, cluster objects.Cluster) *ClusterListener {
	return &ClusterListener{
		c: client,
		// Map concurrent safe
		registeredNamespaces:      make(map[string]watch.Interface),
		registeredNamespacesMutex: sync.Mutex{},
		e: *CreateEventFirer(10, func() error {
			log.Warn("Event fired")
			cluster.Fetch(client)
			cluster.ToHtml()
			return nil
		}),
	}
}

func (l *ClusterListener) readRegisteredNamespaces(key string) watch.Interface {
	l.registeredNamespacesMutex.Lock()
	defer l.registeredNamespacesMutex.Unlock()
	return l.registeredNamespaces[key]
}

func (l *ClusterListener) writeRegisteredNamespaces(key string, value watch.Interface) {
	l.registeredNamespacesMutex.Lock()
	defer l.registeredNamespacesMutex.Unlock()
	l.registeredNamespaces[key] = value
}

func (l *ClusterListener) processEvent(namespace string, typeEvent string) {
	log.Info("Event received, namespace: " + namespace + " " + typeEvent)
	// Wait for 30 seconds of inactivity before sending the event
	go l.e.registerEvent()

}

func (l *ClusterListener) unregisterNamespace(namespace string) {
	log.Info("Unregister namespace: " + namespace)
	l.readRegisteredNamespaces(namespace).Stop()
}

func (l *ClusterListener) registerNamespace(namespace string) {
	watcher, err := l.c.CoreV1().Events(namespace).Watch(context.Background(), metav1.ListOptions{})
	if err != nil {
		log.Error(err)
		return
	}

	// Map concurrent safe
	l.writeRegisteredNamespaces(namespace, watcher)

	log.Info("Register namespace: " + namespace)
	for event := range watcher.ResultChan() {
		if event.Type == "" {
			return
		}
		item := event.Object.(*corev1.Event)
		l.processEvent(namespace, item.GetName())
	}
	log.Info("Unregister namespace: " + namespace)
}

func (l *ClusterListener) ListenToEvent() error {
	// Subscribe to event
	watcher, err := l.c.CoreV1().Namespaces().Watch(context.Background(), metav1.ListOptions{})

	if err != nil {
		return err
	}

	for event := range watcher.ResultChan() {
		item := event.Object.(*corev1.Namespace)
		switch event.Type {
		case watch.Added:
			{
				go l.registerNamespace(item.GetName())
				l.processEvent("general", string(event.Type))
			}
		case watch.Modified:
			{
				l.processEvent("general", string(event.Type))
			}
		case watch.Error:
		case watch.Deleted:
			{
				l.processEvent("general", string(event.Type))
				l.unregisterNamespace(item.GetName())
			}
		}
	}
	return nil
}

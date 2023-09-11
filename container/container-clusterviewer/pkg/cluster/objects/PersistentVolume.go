package objects

import (
	"fmt"
	"k8s.io/client-go/kubernetes"
)

type PersistentVolume struct {
	name   string
	parent Element
}

func (p *PersistentVolume) GetName() string {
	return p.name
}

func (p *PersistentVolume) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	return space + "persistentVolume-" + p.name + "[(" + p.name + ")]"
}

func (p *PersistentVolume) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching persistentvolume " + p.name)
	return nil
}

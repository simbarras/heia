package objects

import (
	"fmt"
	"k8s.io/client-go/kubernetes"
)

type Pod struct {
	parent Element
	name   string
}

func (p *Pod) GetName() string {
	return p.name
}

func (p *Pod) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	return space + "pod-" + p.name + "[" + p.name + "]"
}

func (p *Pod) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching pod " + p.name)
	return nil
}

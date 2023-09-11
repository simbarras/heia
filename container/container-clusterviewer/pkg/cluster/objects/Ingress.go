package objects

import (
	"fmt"
	"k8s.io/client-go/kubernetes"
)

type Ingress struct {
	name   string
	parent Element
}

func (i *Ingress) GetName() string {
	return i.name
}

func (i *Ingress) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	result := space + "ingress-" + i.name + "([" + i.name + "])"
	return result
}

func (i *Ingress) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching ingress " + i.name)
	return nil
}

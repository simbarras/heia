package objects

import (
	"context"
	"fmt"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

type Deployment struct {
	parent   Element
	name     string
	elements []Pod
}

func (d *Deployment) GetName() string {
	return d.name
}

func (d *Deployment) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	result := space + "subgraph deploy-" + d.name
	for _, p := range d.elements {
		result += "\n" + p.ToMermaid(depth+1)
	}
	result += "\n" + space + "end"
	return result
}

func (d *Deployment) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching deployment " + d.name)
	list, err := clientSet.CoreV1().Pods(d.parent.GetName()).List(context.Background(), v1.ListOptions{LabelSelector: "app=" + d.name})
	if err != nil {
		return err
	}
	for _, p := range list.Items {
		p := Pod{name: p.Name, parent: d}
		err = p.Fetch(clientSet)
		if err != nil {
			return err
		}
		d.elements = append(d.elements, p)
	}
	list, err = clientSet.CoreV1().Pods(d.parent.GetName()).List(context.Background(), v1.ListOptions{LabelSelector: "k8s-app=" + d.name})
	if err != nil {
		return err
	}
	for _, p := range list.Items {
		p := Pod{name: p.Name, parent: d}
		err = p.Fetch(clientSet)
		if err != nil {
			return err
		}
		d.elements = append(d.elements, p)
	}
	return nil
}

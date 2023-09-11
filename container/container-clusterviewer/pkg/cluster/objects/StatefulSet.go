package objects

import (
	"context"
	"fmt"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

type StatefulSet struct {
	name           string
	parent         Element
	elements       []Element
	linkedElements []Element
}

func (s *StatefulSet) GetName() string {
	return s.name
}

func (s *StatefulSet) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	//result := space + s.name + "[/" + s.name + "/]"
	result := space + "subgraph sts-" + s.name
	for _, e := range s.elements {
		result += "\n" + e.ToMermaid(depth+1)
	}
	result += "\n" + space + "end"
	for _, l := range s.linkedElements {
		result += "\n" + l.ToMermaid(depth)
		for _, e := range s.elements {
			result += "\n pod-" + e.GetName() + " --- persistentVolume-" + l.GetName()
		}
	}
	return result
}

func (s *StatefulSet) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching statefulset " + s.name)
	list, err := clientSet.CoreV1().Pods(s.parent.GetName()).List(context.Background(), v1.ListOptions{LabelSelector: "app.kubernetes.io/component=" + s.name})
	if err != nil {
		return err
	}
	for _, p := range list.Items {
		p := Pod{name: p.Name, parent: s}
		err = p.Fetch(clientSet)
		if err != nil {
			return err
		}
		s.elements = append(s.elements, &p)
	}

	// Fetch the persistent volume
	listVol, err := clientSet.CoreV1().PersistentVolumes().List(context.Background(), v1.ListOptions{LabelSelector: "app.kubernetes.io/component=" + s.name})
	if err != nil {
		return err
	}
	for _, p := range listVol.Items {
		p := PersistentVolume{name: p.Name, parent: s}
		err = p.Fetch(clientSet)
		if err != nil {
			return err
		}
		s.linkedElements = append(s.linkedElements, &p)
	}

	return nil
}

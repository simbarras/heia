package objects

import (
	"context"
	"fmt"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

const serviceIndex = 0
const deploymentIndex = 1
const ingressIndex = 2
const statefulSetIndex = 3

type Namespace struct {
	name     string
	parent   Element
	elements [][]Element
}

func (n *Namespace) GetName() string {
	return n.name
}
func (n *Namespace) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	result := space + "subgraph ns-" + n.name
	for _, es := range n.elements {
		for _, e := range es {
			result += "\n" + e.ToMermaid(depth+1)
		}
	}

	result += "\n" + space + "end"
	return result
}

func (n *Namespace) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching namespace " + n.name)

	// Init the slice
	var err error
	n.elements = make([][]Element, 5)

	// Fetch the elements
	err = n.fetchServices(clientSet)
	if err != nil {
		return err
	}
	err = n.fetchDeployments(clientSet)
	if err != nil {
		return err
	}
	err = n.fetchIngresses(clientSet)
	if err != nil {
		return err
	}
	err = n.fetchStatefulSets(clientSet)
	if err != nil {
		return err
	}
	return nil
}

func (n *Namespace) fetchDeployments(clientSet *kubernetes.Clientset) error {
	n.elements[deploymentIndex] = make([]Element, 0)
	list, err := clientSet.AppsV1().Deployments(n.name).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	for _, d := range list.Items {
		d := Deployment{name: d.Name, parent: n}
		err = d.Fetch(clientSet)
		if err != nil {
			return err
		}
		n.elements[deploymentIndex] = append(n.elements[deploymentIndex], &d)
	}
	return nil
}

func (n *Namespace) fetchServices(clientSet *kubernetes.Clientset) error {
	n.elements[serviceIndex] = make([]Element, 0)
	list, err := clientSet.CoreV1().Services(n.name).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	for _, s := range list.Items {
		s := Service{name: s.Name, parent: n}
		err = s.Fetch(clientSet)
		if err != nil {
			return err
		}
		n.elements[serviceIndex] = append(n.elements[serviceIndex], &s)
	}
	return nil
}

func (n *Namespace) fetchIngresses(clientSet *kubernetes.Clientset) error {
	n.elements[ingressIndex] = make([]Element, 0)
	list, err := clientSet.NetworkingV1().Ingresses(n.name).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	for _, i := range list.Items {
		i := Ingress{name: i.Name, parent: n}
		err = i.Fetch(clientSet)
		if err != nil {
			return err
		}
		n.elements[ingressIndex] = append(n.elements[ingressIndex], &i)
	}
	return nil
}

func (n *Namespace) fetchStatefulSets(clientSet *kubernetes.Clientset) error {
	n.elements[statefulSetIndex] = make([]Element, 0)
	list, err := clientSet.AppsV1().StatefulSets(n.name).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	for _, s := range list.Items {
		s := StatefulSet{name: s.Name, parent: n}
		err = s.Fetch(clientSet)
		if err != nil {
			return err
		}
		n.elements[statefulSetIndex] = append(n.elements[statefulSetIndex], &s)
	}
	return nil
}

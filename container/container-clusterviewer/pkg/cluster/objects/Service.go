package objects

import (
	"context"
	"fmt"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

type Service struct {
	name          string
	parent        Element
	podLinked     []Element
	IngressLinked []Element
}

func (s *Service) GetName() string {
	return s.name
}

func (s *Service) ToMermaid(depth int) string {
	space := ""
	for i := 0; i < depth; i++ {
		space += "  "
	}
	result := space + "service-" + s.name + "(" + s.name + ")"
	for _, e := range s.podLinked {
		result += "\n" + space + "service-" + s.name + " --- pod-" + e.GetName()
	}
	for _, e := range s.IngressLinked {
		result += "\n" + space + "service-" + s.name + " --- ingress-" + e.GetName()
	}
	return result
}

func (s *Service) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching service " + s.name)
	// Search pods that are linked to this service
	svc, _ := clientSet.CoreV1().Services(s.parent.GetName()).Get(context.Background(), s.name, v1.GetOptions{})
	listOptions := v1.ListOptions{
		LabelSelector: "app=" + svc.Spec.Selector["app"],
	}
	pods, _ := clientSet.CoreV1().Pods(s.parent.GetName()).List(context.Background(), listOptions)
	for _, pod := range pods.Items {
		p := Pod{
			name:   pod.Name,
			parent: s,
		}
		s.podLinked = append(s.podLinked, &p)
	}
	listOptions = v1.ListOptions{
		LabelSelector: "k8s-app=" + svc.Spec.Selector["k8s-	app"],
	}
	pods, _ = clientSet.CoreV1().Pods(s.parent.GetName()).List(context.Background(), listOptions)
	for _, pod := range pods.Items {
		p := Pod{
			name:   pod.Name,
			parent: s,
		}
		s.podLinked = append(s.podLinked, &p)
	}
	listOptions = v1.ListOptions{
		LabelSelector: "app.kubernetes.io/component=" + svc.Spec.Selector["app.kubernetes.io/component"],
	}
	pods, _ = clientSet.CoreV1().Pods(s.parent.GetName()).List(context.Background(), listOptions)
	for _, pod := range pods.Items {
		p := Pod{
			name:   pod.Name,
			parent: s,
		}
		s.podLinked = append(s.podLinked, &p)
	}
	// Search ingress that are linked to this service with the rules backend
	ingress, err := clientSet.NetworkingV1().Ingresses(s.parent.GetName()).List(context.Background(), v1.ListOptions{})
	if err != nil {
		fmt.Println(err)
		return err
	}

	for _, ing := range ingress.Items {
		for _, rule := range ing.Spec.Rules {
			for _, httpPath := range rule.HTTP.Paths {
				if httpPath.Backend.Service.Name == s.name {
					i := Ingress{
						name:   ing.Name,
						parent: s,
					}
					s.IngressLinked = append(s.IngressLinked, &i)
					break
				}
			}
		}
	}
	return nil
}

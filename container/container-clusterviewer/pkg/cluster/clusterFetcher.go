package cluster

/*
import (
	"container-clusterviewer/pkg/cluster/objects"
	"context"
	"fmt"
	appV1 "k8s.io/api/apps/v1"
	types "k8s.io/api/core/v1"
	types2 "k8s.io/api/networking/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
)

var (
	config, _    = clientcmd.BuildConfigFromFlags("", "./kubeconfig")
	clientset, _ = kubernetes.NewForConfig(config)
)

func FetchClusterObjects() (*objects.Cluster, error) {
	// Create Cluster object
	//cluster := objects.Cluster{}

	// Get the different objects
	nsList, err := fetchNamespaces()
	if err != nil {
		return nil, err
	}

	for n := range nsList.Items {
		ns := objects.Namespace{Name: nsList.Items[n].Name} // TODO - Add the other objects
		fmt.Println(ns.Name)

		fmt.Println("Namespaces ------------")

		fmt.Println("Pods ------------------")
		pods := fetchPods(nsList.Items[n].Name)
		for pod := range pods.Items { // pods
			pd := objects.Pod{pods.Items[pod].Name} // create pod object
			fmt.Println("\t" + pd.Name)
		}
		fmt.Println("Services ------------------")
		services := fetchServices(nsList.Items[n].Name)
		for service := range services.Items { // services
			svc := objects.Service{services.Items[service].Name}
			fmt.Println("\t" + svc.Name) // service
		}
		fmt.Println("Deployments ------------------") // TODO - Add podlist for each deployment
		deployments := fetchDeployment(nsList.Items[n].Name)
		for deployment := range deployments.Items {
			fmt.Println("\t" + deployments.Items[deployment].Name)
		}
		fmt.Println("Ingresses ------------------")
		ingresses := fetchIngress(nsList.Items[n].Name)
		for ingress := range ingresses.Items {
			ing := objects.Ingress{ingresses.Items[ingress].Name}
			fmt.Println("\t" + ing.Name)
		}
		fmt.Println("StatefulSet ------------------")
		statefulsets := fetchStatefulSet(nsList.Items[n].Name)
		for statefulset := range statefulsets.Items {
			sfs := objects.StatefulSet{statefulsets.Items[statefulset].Name}
			fmt.Println("\t" + sfs.Name)
		}
	}

	// Return the Cluster object
	return nil, nil
}

func fetchNamespaces() (*types.NamespaceList, error) {
	list, err := clientset.CoreV1().Namespaces().List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil, err
	}
	return list, nil
}

func fetchPods(namespace string) *types.PodList {
	list, err := clientset.CoreV1().Pods(namespace).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	return list
}

func fetchServices(namespace string) *types.ServiceList {
	list, err := clientset.CoreV1().Services(namespace).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	return list
}

func fetchDeployment(namespace string) *appV1.DeploymentList {
	list, err := clientset.AppsV1().Deployments(namespace).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	return list
}

func fetchStatefulSet(namespace string) *appV1.StatefulSetList {
	list, err := clientset.AppsV1().StatefulSets(namespace).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	return list
}

func fetchIngress(namespace string) *types2.IngressList {
	list, err := clientset.NetworkingV1().Ingresses(namespace).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil
	}
	return list
}*/

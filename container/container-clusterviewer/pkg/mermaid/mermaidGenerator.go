package mermaid

import (
	"context"
	"fmt"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
)

func CreateMermaid() {
	// Récupérer la configuration pour le cluster Kubernetes depuis le fichier kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", "C:\\Users\\cloed\\.kube\\config")

	// Créer un client pour accéder à l'API Kubernetes
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err.Error())
	}

	// Récupérer les informations sur les namespaces
	namespaces, err := clientset.CoreV1().Namespaces().List(context.Background(), metav1.ListOptions{})
	if err != nil {
		panic(err.Error())
	}

	// Générer le code Mermaid pour le diagramme
	fmt.Println("graph LR")

	// Subgraph pour le cluster Kubernetes
	fmt.Println("subgraph Kubernetes Cluster")

	// Subgraph pour les noeuds
	fmt.Println("subgraph Nodes")

	// Récupérer les informations sur les noeuds
	nodes, err := clientset.CoreV1().Nodes().List(context.Background(), metav1.ListOptions{})
	if err != nil {
		panic(err.Error())
	}

	// Créer un noeud pour chaque noeud
	for _, node := range nodes.Items {
		fmt.Printf("    N[%s]\n", node.Name)
	}

	fmt.Println("end")

	// Subgraph pour les namespaces
	fmt.Println("subgraph Namespaces")

	// Créer un noeud pour chaque namespace
	for _, namespace := range namespaces.Items {
		fmt.Printf("    NS[%s]\n", namespace.Name)

		// Récupérer les informations sur les volumes dans ce namespace
		volumes, err := clientset.CoreV1().PersistentVolumeClaims(namespace.Name).List(context.Background(), metav1.ListOptions{})
		if err != nil {
			panic(err.Error())
		}

		// Créer un noeud pour chaque volume
		for _, volume := range volumes.Items {
			fmt.Printf("    NS[%s] --> V[%s]\n", namespace.Name, volume.Name)
		}
	}

	fmt.Println("end")

	// Subgraph pour les pods
	fmt.Println("subgraph Pods")

	for _, namespace := range namespaces.Items {
		// Récupérer les pods dans ce namespace
		pods, err := clientset.CoreV1().Pods(namespace.Name).List(context.Background(), metav1.ListOptions{})
		if err != nil {
			panic(err.Error())
		}

		// Créer un noeud pour chaque pod
		for _, pod := range pods.Items {
			fmt.Printf("    NS[%s] --> P[%s]\n", namespace.Name, pod.Name)

			// Récupérer les containers dans ce pod
			for _, container := range pod.Spec.Containers {
				fmt.Printf("    P[%s] --> C[%s]\n", pod.Name, container.Name)
			}
		}
	}

	fmt.Println("end")
	fmt.Println("end")
	fmt.Println("end")
}

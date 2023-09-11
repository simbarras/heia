package main

import (
	"container-clusterviewer/pkg/cluster/objects"
	"container-clusterviewer/pkg/event"
	log "github.com/sirupsen/logrus"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
)

func main() {

	config, err := clientcmd.BuildConfigFromFlags("", "./kubeconfig")
	if err != nil {
		log.Error(err)
		return
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		log.Error(err)
		return
	}

	/*cluster, _ := cluster2.FetchClusterObjects()
	print(cluster)*/

	cluster := objects.Cluster{}
	cluster.Fetch(clientset)
	cluster.ToHtml()

	clusterLister := event.CreateClusterListener(clientset, cluster)
	err = clusterLister.ListenToEvent()
	if err != nil {
		return
	}

	// mermaid.CreateMermaid()
}

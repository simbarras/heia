// Copyright 2022 - Cluster viewer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/****************************************************************************
 * @file   Cluster.go
 * @author Simon Barras simon.barras.@edu.hefr.ch
 * @author Claudio Herren claudio.herren@edu.hefr.ch
 * @author Nicolas Terreaux nicolas.terreaux@edu.hefr.ch
 *
 * @brief Root of the composite pattern
 *
 * @version 1.0
 ***************************************************************************/

package objects

import (
	"context"
	"fmt"
	log "github.com/sirupsen/logrus"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"os"
)

const productionPath = "/usr/share/nginx/html/index.html"
const devPath = "index.html"

// Implement the Element interface
type Cluster struct {
	nsList []Element
}

func (c *Cluster) GetName() string {
	return ""
}

func (c *Cluster) ToMermaid(depth int) string {
	result := "graph LR"
	for _, ns := range c.nsList {
		result += "\n" + ns.ToMermaid(depth+1)
	}
	return result
}

func (c *Cluster) ToHtml() {
	path := "/usr/share/nginx/html/index.html"
	f, err := os.Create(path)

	if err != nil {
		log.Fatal(err)
	}

	defer f.Close()

	mermaidCode := c.ToMermaid(0)

	_, err2 := f.WriteString(
		`
<!DOCTYPE html>
<html>
<head>
	<title>Graph</title>
	<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
	<script>
		// Initialiser Mermaid
		mermaid.initialize({
			startOnLoad: true
		});
	</script>
</head>
<body>
<h1>Graph of your Cluster</h1>
<div class="mermaid">
` +
			mermaidCode + `
</div>
</body>
</html>
`,
	)

	if err2 != nil {
		log.Fatal(err2)
	}

	fmt.Println("Finish, file created at " + path)
}

func (c *Cluster) Fetch(clientSet *kubernetes.Clientset) error {
	fmt.Println("Fetching cluster")
	ls, err := clientSet.CoreV1().Namespaces().List(context.Background(), v1.ListOptions{})
	if err != nil {
		return err
	}
	c.nsList = make([]Element, 0)
	for _, ns := range ls.Items {
		n := Namespace{name: ns.Name, parent: c}
		err = n.Fetch(clientSet)
		if err != nil {
			return err
		}
		c.nsList = append(c.nsList, &n)
	}
	return nil
}

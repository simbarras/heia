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
 * @file   Element.go
 * @author Simon Barras simon.barras.@edu.hefr.ch
 * @author Claudio Herren claudio.herren@edu.hefr.ch
 * @author Nicolas Terreaux nicolas.terreaux@edu.hefr.ch
 *
 * @brief Interface to implement the composite pattern
 *
 * @version 1.0
 ***************************************************************************/

package objects

import "k8s.io/client-go/kubernetes"

type Element interface {
	GetName() string
	ToMermaid(depth int) string
	Fetch(clientSet *kubernetes.Clientset) error
}

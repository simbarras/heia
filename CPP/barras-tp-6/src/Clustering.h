//
// Created by beatw on 7/14/2021.
//

#ifndef CPPALGO_CLUSTERING_H
#define CPPALGO_CLUSTERING_H

#include "Point.h"
#include "Cluster.h"

namespace Clustering {

    std::vector<Cluster> kmeans(const std::vector<Point> &pts, size_t nbOfClusters, int seed = -1);

    std::vector<Cluster> hierarchical(const std::vector<Point> &pts, size_t nbOfClusters);

};


#endif //CPPALGO_CLUSTERING_H

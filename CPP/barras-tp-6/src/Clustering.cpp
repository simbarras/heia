//
// Created by beatw on 7/14/2021.
//

#include "Clustering.h"
#include "ClosestPair.h"

#include <vector>

#include <random>

namespace Clustering {

    /**
     * Initialized cluster for KMeans algorithm
     *
     * Randomly selects one of the points to be the center of each cluster
     *
     * @param pts
     * @param nbOfClusters
     * @param seed
     * @return
     */
    std::vector<Cluster> initKMeans(const std::vector<Point> &pts, size_t nbOfClusters, int seed) {
        std::random_device rd;
        std::default_random_engine gen(seed == -1 ? rd() : seed);
        std::uniform_int_distribution<> dis(0, pts.size() - 1);

        std::vector<Cluster> clusters;
        for (int i = 0; i < nbOfClusters; i++) {
            clusters.push_back(Cluster(pts[dis(gen)]));
        }
        return clusters;
    }

    /**
     * Return the closest cluster for a given point.
     *
     * @param clusters
     * @param p
     * @return
     */
    Cluster &closestCluster(std::vector<Cluster> &clusters, const Point &p) {
        auto closest = std::min_element(clusters.begin(), clusters.end(), [&p](const Cluster &a, const Cluster &b) {
            return a.distance(p) < b.distance(p);
        });
        return *closest;
    }


    /**
     * Perform the KMeans clustering algorithm.
     *
     *
     * @param pts
     * @param nbOfClusters
     * @param seed
     * @return
     */
    std::vector<Cluster> kmeans(const std::vector<Point> &pts, size_t nbOfClusters, int seed) {
        std::vector<Cluster> clusters = Clustering::initKMeans(pts, nbOfClusters, seed);
        bool change = true;
        while (change) {
            for (auto &cluster: clusters) {
                cluster.clear();
            }
            for (auto &p: pts) {
                Cluster &closest = Clustering::closestCluster(clusters, p);
                closest.addPoint(p);
            }
            change = false;
            for (auto &c: clusters) {
                change |= c.recomputeCenter();
            }
        }
        return clusters;
    }

    /**
     * Converts all points into clusters
     * @param pts
     * @return
     */
    std::vector<Cluster> initHierchical(const std::vector<Point> &pts) {

        std::vector<Cluster> clusters;
        for (auto &p: pts) {
            clusters.push_back(Cluster(p));
        }
        return clusters;
    }

    /**
     * Performs a hierarchical clustering with nbOfCluster number of clusters.
     *
     * @param pts
     * @param nbOfClusters
     * @return
     */
    std::vector<Cluster> hierarchical(const std::vector<Point> &pts, size_t nbOfClusters) {
        std::vector<Cluster> clusters = initHierchical(pts);
        ClosestPair<Cluster> closestPair;
        while (clusters.size() > nbOfClusters) {
            //merge two the closest clusters until there are only nbOfClusters left
            auto clusterPair = closestPair.closestPair(clusters);
            for (auto &p: clusterPair.second->getPoints()) {
                clusterPair.first->addPoint(p);
            }
            clusterPair.first->recomputeCenter();
            clusters.erase(std::remove(clusters.begin(), clusters.end(), *clusterPair.second), clusters.end());
        }

        return clusters;
    }
}
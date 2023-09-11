//
// Created by Beat Wolf on 16.07.2021.
//


#include "../includes/Catch2/catch.hpp"

#include "../src/Point.h"
#include "../src/Cluster.h"
#include "../src/Clustering.h"

void basicClusterTest(std::vector<Point> &points, std::vector<Cluster> &clusters, int expected_clusters){
    REQUIRE(expected_clusters == clusters.size());

    size_t pointCount = 0;

    for (const Cluster &cluster: clusters) {
        pointCount += cluster.size();
    }

    REQUIRE(pointCount == points.size());
}

TEST_CASE("KMeans test: Basic test") {
    std::vector<Point> points;
    for (Point* p : createRandomPoints(10, 0, 1000)){
        points.push_back(*p);
    }

    for(int i = 3; i < 10; i++){
        std::vector<Cluster> clusters = Clustering::kmeans(points, i);

        basicClusterTest(points, clusters, i);
    }
}

TEST_CASE("Hierarchical test: Basic test") {
    std::vector<Point> points;
    for (Point* p : createRandomPoints(10, 0, 1000)){
        points.push_back(*p);
    }

    for(int i = 3; i < 10; i++){
        std::vector<Cluster> clusters = Clustering::hierarchical(points, i);

        basicClusterTest(points, clusters, i);
    }
}

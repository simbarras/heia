//
// Created by "Beat Wolf" on 28.04.2022.
//


#include "../includes/Catch2/catch.hpp"
#include "../src/Distances.h"
#include "../src/base/SimpleDistance.h"
#include <numeric>

TEST_CASE("Distance: Compare optimized to reference") {

    int minCoordinates = 0;
    int maxCoordinates = 10000;

    for(int i = 0; i < 5; i++){
        std::vector<Point *> points = createRandomPoints(500, minCoordinates, maxCoordinates, 42);

        double referenceDistance = averageDistanceToNeighbours(points, minCoordinates, maxCoordinates, 0.2);
        double optimizedDistance = optimizedAverageDistanceToNeighbours(points, minCoordinates, maxCoordinates, 0.2);

        REQUIRE( referenceDistance == Approx(optimizedDistance));

        for (Point *point : points) {
            delete point;
        }
    }

}
//
// Created by "Beat Wolf" on 04.06.2022.
//

#ifndef CPPALGO_RANDOMPOINTS_H
#define CPPALGO_RANDOMPOINTS_H

#include "Point.h"
#include <vector>

/**
 * Simple class to create 2D points which are distributed in clusters
 */
class RandomPoints {

public:
    /**
     * Future points being created have ther x/y coordinates between axis_min and axis_max
     * @param axis_min
     * @param axis_max
     * @param seed
     */
    RandomPoints(double axis_min, double axis_max, int seed=-1);

    /**
     * Create n points
     * @param n
     * @return
     */
    std::vector<Point> createRandomPoint(int n);
private:
    double min;
    double max;
    int seed;
};


#endif //CPPALGO_RANDOMPOINTS_H

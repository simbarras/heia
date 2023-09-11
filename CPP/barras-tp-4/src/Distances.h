//
// Created by "Beat Wolf" on 28.04.2022.
//

#ifndef CPPALGO_DISTANCES_H
#define CPPALGO_DISTANCES_H

#include "Point.h"
#include <vector>


/**
 * optimized version of distance calucation.
 * This produces the same results as averageDistanceToNeighbours, but faster
 * @param points
 * @param min
 * @param max
 * @param region_size
 * @return
 */
double optimizedAverageDistanceToNeighbours(std::vector<Point *> points, double min, double max, double region_size);


#endif //CPPALGO_DISTANCES_H

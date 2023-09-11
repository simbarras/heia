//
// Created by beatw on 4/29/2022.
//

#ifndef CPPALGO_SIMPLEDISTANCE_H
#define CPPALGO_SIMPLEDISTANCE_H

#include "../Point.h"
#include "../Interval.h"

/**
 * Reference implementation of distance calculation
 * @param points
 * @param min
 * @param max
 * @param region_size
 * @return
 */
double averageDistanceToNeighbours(std::vector<Point *> points, double min, double max, double region_size);

std::vector<Interval<double, double>> createIntervals();

#endif //CPPALGO_SIMPLEDISTANCE_H

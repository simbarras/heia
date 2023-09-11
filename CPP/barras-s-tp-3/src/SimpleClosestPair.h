//
// Created by Asraniel on 30.11.2021.
//

#ifndef CPPALGO_SIMPLECLOSESTPAIR_H
#define CPPALGO_SIMPLECLOSESTPAIR_H

#include <vector>
#include "Point.h"

/**
 * Simple implementation of the closest pair problem.
 */
class SimpleClosestPair{
public:
    /**
     * Given a list of points, return the two closest ones
     * @param searchPoints
     * @return
     */
    std::pair<Point *, Point *> closestPair(std::vector<Point *> &searchPoints);
};

#endif //CPPALGO_SIMPLECLOSESTPAIR_H

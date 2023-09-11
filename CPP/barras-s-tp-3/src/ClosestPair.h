//
// Created by beatw on 7/8/2021.
//

#ifndef CPPALGO_CLOSESTPAIR_H
#define CPPALGO_CLOSESTPAIR_H

#include <vector>
#include <iostream>
#include <set>
#include <utility>

#include "Point.h"

class ClosestPair {
public:

    ClosestPair();
    std::pair<Point *, Point *> closestPair(std::vector<Point *> &searchPoints);
private:
    void handleEvent(Point *p);

    void shrinkCandidates(const Point *p);

    //Currently smallest distance
    double smallestDistance = std::numeric_limits<double>::infinity();
    //Current solution associated with that smallestDistance
    std::pair<Point *, Point *> solution;

    int leftMostCandidate = 0;

    //Temporary variables used for lower/upper bound search
    Point lowerSearch = {0, 0};
    Point upperSearch = {0, 0};

    std::vector<Point*> points; //All points
    std::set<Point *, std::function<bool(const Point *, const Point *)>> candidates; //Tree of current candidates
};

#endif //CPPALGO_CLOSESTPAIR_H

//
// Created by Beat Wolf on 30.11.2021.
//
#include "SimpleClosestPair.h"
#include <algorithm>

std::pair<Point *, Point *> SimpleClosestPair::closestPair(std::vector<Point*> &searchPoints){
    std::vector<Point *> points;
    for(Point *p : searchPoints){
        points.push_back(p);
    }

    //Sort on x
    int indexA = 0, indexB = 1;

    double smallestDistance = points.at(0)->distance(*points.at(1));

    //check all pairs
    for(size_t i = 0; i < points.size(); i++){
        for(size_t loop = 0; loop < points.size(); loop++){
            if(loop != i){
                const Point &a = *points[i];
                const Point &b = *points[loop];

                if(b.getX() - a.getX() < smallestDistance){ //Abort early if x distance is already too large
                    const double distance = a.distance(b);
                    if(distance < smallestDistance){
                        smallestDistance = distance;
                        indexA = i;
                        indexB = loop;
                    }
                }
            }
        }
    }

    return std::pair<Point *, Point *>(points[indexA], points[indexB]);
}
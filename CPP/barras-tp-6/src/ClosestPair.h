//
// Created by "Beat Wolf" on 08.02.2022.
//

#ifndef CPPALGO_TCLOSESTPAIR_H
#define CPPALGO_TCLOSESTPAIR_H

#include <vector>
#include <iostream>
#include <set>
#include <utility>

#include "Point.h"
#include <functional>

template <class ValueType = Point>
class ClosestPair {
public:

    ClosestPair(){
        const auto comp = []( const ValueType * a, const ValueType * b){
            if(a->getY() == b->getY()){
                return a->getX() < b->getX();
            }
            return a->getY() < b->getY();
        };

        candidates = std::set<ValueType *, std::function<bool(const ValueType *, const ValueType *)>>(comp);
    }

    std::pair<ValueType *, ValueType *> closestPair(std::vector<ValueType> &searchPoints){
        //Init
        points.clear();

        //Make pointers to input data
        for(ValueType &p : searchPoints){
            points.push_back(&p);
        }

        candidates.clear();

        leftMostCandidate = 0;

        std::sort(points.begin(), points.end(), [](const ValueType* a, const ValueType *b){
            if(a->getX() == b->getX()){
                return a->getY() < b->getY();
            }
            return a->getX() < b->getX();
        });
        //candidates.insert(points.begin(), points.end());

        //Init solution
        solution = std::make_pair(points[0], points[1]);
        smallestDistance = solution.first->distance(*solution.second);

        //Go through all points
        for(ValueType *p : points){
            handleEvent(p);
        }

        return solution;
    }
private:
    void handleEvent(ValueType *p){
        shrinkCandidates(p);

        int low = (int)std::floor((long)p->getY() - smallestDistance);
        int up = (int)std::ceil((long)p->getY() + smallestDistance);

        lowerSearch.setY(low);
        upperSearch.setY(up);

        auto pMin = candidates.lower_bound(&lowerSearch);
        auto pMax = candidates.upper_bound(&upperSearch);

        while(pMin != pMax){
            const double distance = p->distance(**pMin);

            if(distance < smallestDistance){
                smallestDistance = distance;

                solution = std::pair<ValueType *, ValueType *>(p, const_cast<ValueType *>(*pMin));
            }
            pMin++;
        }

        candidates.insert(p);
    }

    void shrinkCandidates(const ValueType *p){
        while(p->getX() - points[leftMostCandidate]->getX() > smallestDistance){
            candidates.erase(points[leftMostCandidate]);

            leftMostCandidate++;
        }
    }

    double smallestDistance = std::numeric_limits<double>::infinity();
    int leftMostCandidate = 0;

    ValueType lowerSearch = {0, 0};
    ValueType upperSearch = {0, 0};

    std::pair<ValueType *, ValueType *> solution;
    std::vector<ValueType*> points;

    std::set<ValueType *, std::function<bool(const ValueType *, const ValueType *)>> candidates;
};

#endif //CPPALGO_TCLOSESTPAIR_H
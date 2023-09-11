//
// Created by "Beat Wolf" on 08.02.2022.
//

#include "ClosestPair.h"

ClosestPair::ClosestPair() {
    const auto comp = [](const Point *a, const Point *b) {
        if (a->getY() == b->getY()) {
            return a->getX() < b->getX();
        }
        return a->getY() < b->getY();
    };

    candidates = std::set<Point *, std::function<bool(
            const Point *, const Point *)>>(comp);
}

std::pair<Point *, Point *> ClosestPair::closestPair(std::vector<Point *> &searchPoints) {
    //Init
    points = searchPoints;
    candidates.clear();
    leftMostCandidate = 0;
    std::sort(points.begin(), points.end(), [](const Point *a, const Point *b) {
        if (a->getX() == b->getX()) {
            return a->getY() < b->getY();
        }
        return a->getX() < b->getX();
    });

    //Init solution
    solution = std::make_pair(points[0], points[1]);
    smallestDistance = solution.first->distance(*solution.second);

    //itr on all points
    for (auto &point: points) {
        handleEvent(point);
    }

    return solution;
}

void ClosestPair::handleEvent(Point *p) {
    shrinkCandidates(p);

    lowerSearch = Point(p->getX(), p->getY() - smallestDistance);
    upperSearch = Point(p->getX(), p->getY() + smallestDistance);
    auto lowerItr = candidates.lower_bound(&lowerSearch);
    auto upperItr = candidates.lower_bound(&upperSearch);

    while (lowerItr != upperItr) {
        auto distance = p->distance(**lowerItr);
        if (distance < smallestDistance) {
            solution = std::make_pair(p, *lowerItr);
            smallestDistance = distance;
        }
        lowerItr++;
    }
    candidates.insert(p);
}

void ClosestPair::shrinkCandidates(const Point *p) {
    while (p->getX() - points[leftMostCandidate]->getX() > smallestDistance) {
        candidates.erase(points[leftMostCandidate]);

        leftMostCandidate++;
    }
}
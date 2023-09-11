//
// Created by Beat Wolf on 30.11.2021.
//

#include "SimpleRangeTree.h"

SimpleRangeTree::SimpleRangeTree(const std::vector<Point *> &points) : points(points) {};

std::vector<Point *> SimpleRangeTree::search(const Point &start, const Point &end) const {
    std::vector<Point *> result;
    double startX = start.getX();
    double startY = start.getY();
    double endX = end.getX();
    double endY = end.getY();
    for (Point *point: points) {
        if (point->getX() >= startX && point->getY() >= startY && point->getX() <= endX && point->getY() <= endY) {
            result.push_back(point);
        }
    }

    return result;
}

bool SimpleRangeTree::isEmpty() const {
    return points.empty();
}

size_t SimpleRangeTree::size() const {
    return points.size();
}
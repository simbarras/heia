//
// Created by Beat Wolf on 30.11.2021.
//

#ifndef CPPALGO_SIMPLERANGETREE_H
#define CPPALGO_SIMPLERANGETREE_H

#include <vector>
#include "../Point.h"

/**
 * Very simple implementation of a Range Tree.
 * It is basically a wrapper around a std::vector with a log(n) search.
 */
class SimpleRangeTree{

public:
    SimpleRangeTree(const std::vector<Point *> &points);

    /**
     * Returns all points between start and end
     * @param start
     * @param end
     * @return
     */
    std::vector<Point *> search(const Point &start, const Point &end) const;

    /**
     * Returns true if there are no elements in the collection
     * @return
     */
    bool isEmpty() const;

    /**
     * Returns the size of the collection
     * @return
     */
    size_t size() const;

private:
    std::vector<Point *> points;
};

#endif //CPPALGO_SIMPLERANGETREE_H

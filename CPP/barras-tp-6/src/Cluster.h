//
// Created by beatw on 7/14/2021.
//

#ifndef CPPALGO_CLUSTER_H
#define CPPALGO_CLUSTER_H

#include "Point.h"

#include <vector>

/**
 * Represents a group of points with the Cluster being located at the center of those points.
 * A cluster with no points is located at 0/0
 */
class Cluster : public Point {
public:

    Cluster(int x, int y);

    Cluster(const Point &center);

    Cluster(const std::vector<Point> &points);

    Cluster(const Cluster &a, const Cluster &b);

    bool operator==(const Cluster& other) const;
    bool operator!=(const Cluster& other) const;

    /**
     * When adding a point, recomputeCenter() needs to be called afterwards
     * @param point
     */
    void addPoint(const Point &point);

    /**
     * Removes all points from the cluster
     */
    void clear();

    /**
     * Returns the number of points in the cluster
     * @return
     */
    size_t size() const;

    /**
     * Returns all points
     * @return
     */
    const std::vector<Point> &getPoints() const;

    /**
     *  Recomputes the center of mass (this.x, this.y) from the points
     * @return Returns true if computation resulted in new center
     */
    bool recomputeCenter();

private:
    std::vector<Point> points;

};


#endif //CPPALGO_CLUSTER_H

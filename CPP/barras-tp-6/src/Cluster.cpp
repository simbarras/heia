//
// Created by beatw on 7/14/2021.
//

#include "Cluster.h"


Cluster::Cluster(int x, int y) : Cluster(Point(x, y)){
}

Cluster::Cluster(const Point &center) : Point(center) {
    points.push_back(center);
    recomputeCenter();
}

Cluster::Cluster(const std::vector<Point> &points) : points(points) {
    recomputeCenter();
}

Cluster::Cluster(const Cluster &a, const Cluster &b) : points(a.points) {
    points.insert(points.end(), b.points.begin(), b.points.end());
    recomputeCenter();
}

void Cluster::addPoint(const Point &point) {
    points.push_back(point);
}

void Cluster::clear() {
    points.clear();
}

size_t Cluster::size() const{
    return points.size();
}

bool Cluster::operator==(const Cluster& other) const{
    return Point::operator==(other) && points == other.points;
}

bool Cluster::operator!=(const Cluster& other) const{
    return Point::operator!=(other) || points != other.points;
}

const std::vector<Point> & Cluster::getPoints() const{
    return points;
}

bool Cluster::recomputeCenter() {
    Point oldCenter = *this;

    x = 0;
    y = 0;

    if(!points.empty()){
        for (const Point &p: points) {
            *this += p;
        }
        *this /= points.size();
    }

    return (oldCenter.getX() != x || oldCenter.getY() != y);
}

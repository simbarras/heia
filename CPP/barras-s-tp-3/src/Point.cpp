//
// Created by simon on 05.04.2022.
//

#include "Point.h"

Point::Point(Point &p) : x(p.x), y(p.y), name(p.name) {}

Point::Point(double x, double y, std::string name) : x(x), y(y), name(name) {}

Point::~Point() = default;

double Point::getX() const {
    return x;
}

double Point::getY() const {
    return y;
}

std::string Point::getName() const {
    return name;
}

void Point::setX(double x1) {
    Point::x = x1;
}

void Point::setY(double y1) {
    Point::y = y1;
}

double Point::distance(const Point &point) const {
    // Don't use sqrt() here, it's slow
    return pow(point.getX() - x, 2) + pow(point.getY() - y, 2);
}

// ALERT: move the point to the average position
Point &Point::operator+=(const Point &other) {
    x = x + other.getX();
    y = y + other.getY();
    name += " & " + other.getName();
    return *this;
}

Point &Point::operator-=(const Point &other) {
    x -= other.getX();
    y -= other.getY();
    return *this;
}

Point &Point::operator*=(double other) {
    x *= other;
    y *= other;
    return *this;
}

Point &Point::operator/=(double other) {
    if (other == 0) {
        throw std::invalid_argument("Division by zero");
    }
    x /= other;
    y /= other;
    return *this;
}

Point Point::operator+(Point &other) {
    return Point(x + other.getX(), y + other.getY());
}

Point Point::operator-(Point &other) {
    return Point(x - other.getX(), y - other.getY());
}

Point Point::operator*(double other) {
    return Point(x * other, y * other);
}

Point Point::operator/(double other) {
    if (other == 0) {
        throw std::invalid_argument("Division by zero");
    }
    return Point(x / other, y / other);
}
// Copyright 2022 Haute école d'ingénierie et d'architecture de Fribourg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/****************************************************************************
 * @file Point.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Implementation for the point from the TP3
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#include "Point.h"

Point::Point(const Point &p) : x(p.x), y(p.y), name(p.name) {}

Point::Point(double x, double y, std::string name) : x(x), y(y), name(name) {}

Point::~Point() = default;

double Point::getX() const {
    return x;
}

double Point::getY() const {
    return y;
}

void Point::setX(double x1) {
    Point::x = x1;
}

void Point::setY(double y1) {
    Point::y = y1;
}


std::string Point::getName() {
    return name;
}

double Point::distance(const Point &point) const {
    // Don't use sqrt() here, it's slow
    return pow(point.getX() - x, 2) + pow(point.getY() - y, 2);
}

Point &Point::operator+=(const Point &other) {
    x += other.getX();
    y += other.getY();
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
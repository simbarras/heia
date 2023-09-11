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
 * @file Point.h
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Point from the TP3
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#ifndef CPPALGO_POINT_H
#define CPPALGO_POINT_H

#include <functional>
#include <unordered_set>
#include <ostream>
#include <random>

/**
 * Simple 2D point using doubles. The point can have an optional name.
 * The point implements basic operators to manipulate the point, as well as
 */
class Point {
public:
    Point() : x(0), y(0), name("") {}

    Point(const Point &point);

    Point(double x, double y, std::string name = "");

    virtual ~Point();

    virtual double getX() const;

    virtual double getY() const;

    std::string getName() const;

    virtual void setX(double x);

    virtual void setY(double y);

    virtual double distance(const Point &point) const;

    virtual Point &operator+=(const Point &other);

    virtual Point &operator-=(const Point &other);

    virtual Point &operator*=(double other);

    virtual Point &operator/=(double other);

    virtual Point operator+(Point &other);

    virtual Point operator-(Point &other);

    virtual Point operator*(double other);

    virtual Point operator/(double other);

    virtual bool operator==(const Point &other) const;

    virtual bool operator!=(const Point &other) const;

protected:
    double x{}, y{};
    std::string name;
};

//Needs to be inline if defined in header
std::ostream &operator<<(std::ostream &os, const Point &point);

/**
 * Hashing class that allows to create a hash of a Point easily
 */
class PointHash {
public:
    std::size_t operator()(const Point *point) const {
        std::size_t h1 = std::hash<double>{}(point->getX());
        std::size_t h2 = std::hash<double>{}(point->getY());
        return h1 ^ (h2 << 1); // or use boost::hash_combine
    }
};

/**
 * Create a certain amount of random Points in a specified interval
 * @param count number of points to create
 * @param min minimum x and y value
 * @param max maximum x and y value
 * @param seed seed to be used to rng, -1 to not seed the rng
 * @return
 */
inline std::vector<Point *> createRandomPoints(size_t count, double min, double max, int seed = -1) {
    std::mt19937 random_engine;

    if (seed == -1) {
        std::random_device random_device;
        random_engine = std::mt19937(random_device());
    } else {
        random_engine = std::mt19937(seed);
    }
    std::uniform_int_distribution<int> distribution(min, max);

    std::unordered_set<Point *, PointHash> point_set;

    while (point_set.size() < count) {
        point_set.insert(new Point(distribution(random_engine), distribution(random_engine)));
    }

    return {point_set.begin(), point_set.end()};
}


#endif //CPPALGO_POINT_H

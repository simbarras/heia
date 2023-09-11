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
 * @file Distance.cpp
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Implementation of the distance.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#include "base/SimpleDistance.h"
#include "base/SimpleIntervalTree.h"
#include "base/SimpleRangeTree.h"
#include "Point.h"
#include <cmath>

void normalize(std::vector<double> &values) {
    //Calculate mean
    double totalDistance = 0;
    for (double value: values) {
        totalDistance += value;
    }
    double mean = totalDistance /= values.size();

    //Calculate SD
    double totalSD = 0;
    for (double value: values) {
        totalSD += std::pow(value - mean, 2);
    }
    double sd = std::sqrt(totalSD / values.size());

    //Normalize
    for (double &value: values) {
        value = (value - mean) / sd;
    }
}

/**
 * Calculate the average distance for all points with their neigbhours in a certain range
 * @param points
 * @param min
 * @param max
 * @param region_size
 * @return
 */
std::vector<double> calculateDistances(std::vector<Point *> points, double min, double max, double region_size) {
    SimpleRangeTree tree(points);

    std::vector<double> distances(points.size());

    int index = 0;
#pragma omp parallel for
    for (const Point *point: points) {
        double width = (max - min) * region_size / 2;

        Point start(*point);
        Point end(*point);
        Point offset(width, width);

        start -= offset;
        end += offset;

        std::vector<Point *> neighbours = tree.search(start, end);

        double totalDistance = 0;
        for (Point *neighbour: neighbours) {
            totalDistance += neighbour->distance(*point);
        }

        double averageDistanceToNeigbours = totalDistance / neighbours.size();

#pragma omp critical
        distances[index++] = averageDistanceToNeigbours;
    }

    return distances;
}

std::vector<double> filter(std::vector<double> distances) {
    std::vector<Interval<double, double>> intervals = createIntervals();

    SimpleIntervalTree<double> tree(intervals);

    std::vector<double> distancesFiltered;

#pragma omp parallel for
    for (double val: distances) {
        std::vector<Interval<double, double>> targets = tree.containing(val);
        if (targets.size() > 0) {
            for (Interval<double, double> v: targets) {
                val *= v.getValue();
            }

#pragma omp critical
            distancesFiltered.push_back(val);
        }
    }

    return distancesFiltered;
}

double optimizedAverageDistanceToNeighbours(std::vector<Point *> points, double min, double max, double region_size) {
    std::vector<double> distances = calculateDistances(points, min, max, region_size);

    normalize(distances);

    distances = filter(distances);

    //SUM
    double sum = 0;
    for (size_t i = 0; i < distances.size(); i++) {
        sum += distances[i];
    }

    return sum;
}
//
// Created by "Beat Wolf" on 08.02.2022.
//

#include "SimpleDistance.h"
#include "SimpleIntervalTree.h"
#include "SimpleRangeTree.h"
#include <random>
#include <cmath>

namespace{
    void normalize(std::vector<double> &values){
        //Calculate mean
        double totalDistance = 0;
        for(size_t i = 0; i < values.size(); i++){
            totalDistance += values.at(i);
        }
        double mean = totalDistance /= values.size();

        //Calculate SD
        double totalSD = 0;
        for(size_t i = 0; i < values.size(); i++){
            totalSD += std::pow(values.at(i) - mean, 2);
        }
        double sd = std::sqrt(totalSD / values.size());

        //Normalize
        for(size_t i = 0; i < values.size(); i++){
            values.at(i) = (values.at(i) - mean) / sd;
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
    std::vector<double> calculateDistances(std::vector<Point *> points, double min, double max, double region_size){
        SimpleRangeTree tree(points);

        std::vector<double> distances(points.size());

        int index = 0;
        for(const Point * point : points){
            double width = (max - min) * region_size / 2;

            Point start(*point);
            Point end(*point);
            Point offset(width, width);

            start -= offset;
            end += offset;

            std::vector<Point *> neighbours = tree.search(start, end);

            double totalDistance = 0;
            for(Point *neighbour : neighbours){
                totalDistance += neighbour->distance(*point);
            }

            double averageDistanceToNeigbours = totalDistance / neighbours.size();

            distances[index++] = averageDistanceToNeigbours;
        }

        return distances;
    }


    std::vector<double> filter(std::vector<double> distances){
        std::vector<Interval<double, double>> intervals = createIntervals();

        SimpleIntervalTree<double> tree(intervals);

        std::vector<double> distancesFiltered;

        for(double val : distances){
            std::vector<Interval<double, double>> targets = tree.containing(val);
            if(targets.size() > 0){
                for(Interval<double, double> v : targets){
                    val *= v.getValue();
                }

                distancesFiltered.push_back(val);
            }
        }

        return distancesFiltered;
    }
}


std::vector<Interval<double, double>> createIntervals(){
    std::vector<Interval<double, double>> intervals;
    std::mt19937_64 rng;
    rng.seed(42);
    std::uniform_real_distribution<double> unif(0.5, 1.5);
    for(double low = -1; low < 1; low += 0.01){
        intervals.push_back({low, low += 0.005, unif(rng)});
    }

    return intervals;
}

double averageDistanceToNeighbours(std::vector<Point *> points, double min, double max, double region_size){
    std::vector<double> distances = calculateDistances(points, min, max, region_size);

    normalize(distances);

    distances = filter(distances);

    //SUM
    double sum = 0;
    for(size_t i = 0; i < distances.size(); i++){
        sum += distances[i];
    }

    return sum;
}
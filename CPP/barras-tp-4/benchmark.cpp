//
// Created by "Beat Wolf" on 28.04.2022.
//


#include "src/Point.h"
#include "src/Distances.h"
#include <vector>
#include <iostream>
#include <chrono>

void benchmark(int repetitions, int pointCount, double region_size){

    std::cout << "Start calculation with " << pointCount << " points and " << repetitions << " repetitions" << std::endl;

    double min = 0;
    double max = 10000;
    std::vector<Point *> points = createRandomPoints(pointCount, min, max, 42);

    auto start = std::chrono::system_clock::now();

    for(int i = 0;i < repetitions ; i++) {
        optimizedAverageDistanceToNeighbours(points, min, max, region_size);
    }

    auto passedTime = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - start);

    std::cout << std::fixed << "Finished calculation (" << pointCount << ") in avg.\t" << (passedTime.count() / repetitions)<< "ms, total\t" << passedTime.count() << "ms" << std::endl;


    for(Point *point:points){
        delete point;
    }

}

int main(){

    benchmark(5, 1000, 0.3);

    benchmark(5, 30000, 0.05);

    benchmark(5, 30000, 0.2);

    benchmark(5, 30000, 0.6);
}
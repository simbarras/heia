//
// Created by "Beat Wolf" on 08.02.2022.
//
#include <fstream>
#include <iostream>
#include <sstream>

#include "src/Point.h"
#include "src/ClosestPair.h"

const double kGoalPercent = 90. / 100;

std::vector<Point *> readTsv(std::string fileName) {
    std::vector<Point *> points;
    std::ifstream fin(fileName);
    std::string line;
    // remove Header
    std::getline(fin, line);
    // read lines
    while (getline(fin, line)) {
        // Split line into tab-separated parts
        std::stringstream ss(line);
        std::string tmp;
        std::vector<std::string> words;
        while (getline(ss, tmp, '\t')) {
            words.push_back(tmp);
        }
        // get point
        double x = std::stod(words[11]);
        double y = std::stod(words[12]);
        points.push_back(new Point(x, y, words[2]));
    }
    fin.close();
    return points;
}

int main() {
    std::vector<Point *> points = readTsv("../data/us_starbucks.tsv");
    ClosestPair cp;

    int size = points.size();
    int goal = points.size() * kGoalPercent;
    std::cout << "Nbr of starbucks : " << size << " nbr of starbucks wanted : " << goal << std::endl;
    while (size >= goal) {
        std::pair<Point *, Point *> closest = cp.closestPair(points);
        *closest.first += *closest.second;
        *closest.first /= 2;

        // add delete
        delete closest.second;
        points.erase(std::remove(points.begin(), points.end(), closest.second), points.end());

        size = points.size();
        std::cout << "Fusion : " << closest.first->getName() << std::endl;
        std::cout << "Starbucks left : " << size << std::endl;
    }
    std::cout << " ---------------------------- " << std::endl;
    std::cout << "Final number of Starbucks : " << size << std::endl;
    // delete all pts
    for (Point *p: points) {
        delete p;
    }
}
//
// Created by beatw on 7/8/2021.
//

#include "../includes/Catch2/catch.hpp"
#include "../src/ClosestPair.h"
#include "../src/SimpleClosestPair.h"

#include <random>

/**
 * Create a set of bad points (Bad because they are all on the same x)
 * @param count
 * @return
 */
std::vector<Point*> createBadPoints(int count) {
    std::vector<Point*> points;

    for (int i = 0; i < count; i++) {
        points.push_back(new Point(0, 10 * i));
    }

    return points;
}

void testClosestPair(int nbOfPoints,
                     int nbOfDataSets,
                     int nbOfCalls,
                     bool randomPoints) {
    SimpleClosestPair fcp;
    ClosestPair cp;

    double totalFake = 0;
    double totalReal = 0;
    for (int i = 0; i < nbOfDataSets; i++) {
        std::vector<Point*> t;
        if (randomPoints) {
            t = createRandomPoints(nbOfPoints, 0, 100000);
        } else {
            t = createBadPoints(nbOfPoints);
        }

        //Test naive approach
        auto start = std::chrono::system_clock::now();
        std::pair<Point*, Point*> resFake;
        for (int loop = 0; loop < nbOfCalls; loop++) {
            resFake = fcp.closestPair(t);
        }

        auto elapsed_fake = std::chrono::system_clock::now() - start;

        //Test custom approach
        start = std::chrono::system_clock::now();
        std::pair<Point*, Point*> res;

        for (int loop = 0; loop < nbOfCalls; loop++) {
            res = cp.closestPair(t);
        }

        REQUIRE(resFake.first->distance(*resFake.second) == Approx(res.first->distance(*res.second))); //Custom approach requires same result as naive approach

        auto elapsed_real = std::chrono::system_clock::now() - start;
        totalFake += std::chrono::duration_cast<std::chrono::milliseconds>(elapsed_fake).count();
        totalReal += std::chrono::duration_cast<std::chrono::milliseconds>(elapsed_real).count();
    }

    if(randomPoints){
        std::cout << std::endl << "Using random points" << std::endl;
    }else{
        std::cout << std::endl << "Using worst case points" << std::endl;
    }

    std::cout << "NaivePairs run in " << totalFake  << " ms" << std::endl;
    std::cout << "FastPairs run in " <<totalReal << " ms" << std::endl;
}

TEST_CASE("ClosestPair: Test random") {
    testClosestPair(500, 5, 1, true);
}

TEST_CASE("ClosestPair: Test many small instances") {
    testClosestPair(10, 500, 1, true);
}

TEST_CASE("ClosestPair: Test vertically aligned") {
    testClosestPair(500, 5, 1, false);
}

TEST_CASE("ClosestPair: Benchmark") {
    int nbOfPoints = 200;
    int nbOfDataSets = 5;
    int nbOfCalls = 10;

    SECTION("random"){
        testClosestPair(1 * nbOfPoints, nbOfDataSets, nbOfCalls, true);
        testClosestPair(2 * nbOfPoints, nbOfDataSets, nbOfCalls, true);
        testClosestPair(4 * nbOfPoints, nbOfDataSets, nbOfCalls, true);
    }

    SECTION("worst_case"){
        testClosestPair(1 * nbOfPoints, nbOfDataSets, nbOfCalls, false);
        testClosestPair(2 * nbOfPoints, nbOfDataSets, nbOfCalls, false);
        testClosestPair(4 * nbOfPoints, nbOfDataSets, nbOfCalls, false);
    }

}
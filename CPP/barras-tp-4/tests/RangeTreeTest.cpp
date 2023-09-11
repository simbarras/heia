//
// Created by Beat Wolf on 27.06.2021.
//

#include "../includes/Catch2/catch.hpp"
#include "../src/RangeTree.h"
#include "../src/base/SimpleRangeTree.h"
#include "../../barras-tp-4/src/Point.h"

#include <vector>
#include <random>
#include <chrono>
#include <iostream>
#include <algorithm>

void delete_points(std::vector<Point *> points){
    for(Point * p :points){
        delete p;
    }
}

TEMPLATE_TEST_CASE("RangeTreeTestFake: Init test", "tags", SimpleRangeTree, RangeTree) {
    std::vector<Point*> points;
    points.push_back(new Point(1,2));
    points.push_back(new Point(3,4));
    TestType rangeTree(points);
    delete_points(points);

    REQUIRE_FALSE(rangeTree.isEmpty());
}

TEMPLATE_TEST_CASE("RangeTreeTestFake: Init test 2", "tags", SimpleRangeTree, RangeTree) {
    TestType rangeTree({});

    REQUIRE(rangeTree.isEmpty());
}

TEMPLATE_TEST_CASE("RangeTreeTestFake: Query simple", "tags", SimpleRangeTree, RangeTree) {
    std::vector<Point *> points = {new Point(1,2)};
    TestType rangeTree(points);
    std::vector<Point*> result = rangeTree.search(Point(0,0), Point(3,3));
    delete_points(points);
    REQUIRE(result.size() == 1);

    points = {new Point(1,2), new Point(5, 5)};
    rangeTree = TestType(points);
    result = rangeTree.search(Point(0,0), Point(3,3));
    delete_points(points);
    REQUIRE(result.size() == 1);
}

TEST_CASE("RangeTree: Equivalence test"){
    for(int i = 4; i < 100; i++){
        std::vector<Point*> points = createRandomPoints(i, -200, 200);

        SimpleRangeTree fakeTree(points);
        RangeTree binaryTree(points);

        REQUIRE(points.size() == fakeTree.size());
        REQUIRE(points.size() == binaryTree.size());

        //Repeat 1000 random queries
        std::random_device random_device;
        std::mt19937 random_engine(1);//random_device()
        std::uniform_int_distribution<int> distribution_base(-200, 100);
        std::uniform_int_distribution<int> distribution_range(0, 100);

        for(int i = 0; i < 1000; i++){
            int startX = distribution_base(random_engine);
            int startY = distribution_base(random_engine);
            int endX = startX + distribution_range(random_engine);
            int endY = startY + distribution_range(random_engine);

            Point start = Point(startX, startY);
            Point end = Point(endX, endY);

            std::vector<Point*> fakePoints = fakeTree.search(start, end);
            std::vector<Point*> binaryPoints = binaryTree.search(start, end);

            REQUIRE(std::is_permutation(fakePoints.begin(), fakePoints.end(), binaryPoints.begin(), binaryPoints.end()));
        }

        delete_points(points);
    }

}

template <class T>
int randomQueries(T& tree, std::mt19937 &random_engine, int repetitions){
    std::uniform_int_distribution<int> distribution_base(-10000, 5000);
    std::uniform_int_distribution<int> distribution_range(0, 1000);

    int counter = 0;
    for(int i = 0; i < repetitions; i++){
        int startX = distribution_base(random_engine);
        int startY = distribution_base(random_engine);
        int endX = startX + distribution_range(random_engine);
        int endY = startY + distribution_range(random_engine);

        counter += tree.search({static_cast<double>(startX), static_cast<double>(startY)},
                               {static_cast<double>(endX), static_cast<double>(endY)}).size();
    }

    return counter;
}

TEST_CASE("RangeTree: Benchmark"){
    std::vector<Point*> points = createRandomPoints(2000, -10000, 10000);

    auto start = std::chrono::system_clock::now();
    SimpleRangeTree fakeTree(points);
    std::cout << "FakeTree created in " << std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - start).count() << " ms" << std::endl;

    start = std::chrono::system_clock::now();
    RangeTree binaryTree(points);
    std::cout << "BinaryTree created in " << std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - start).count() << " ms" << std::endl;

    //Repeat 1000 random queries
    std::mt19937 random_engine(0); //Set seed to 0
    const int repetitions = 1000;

    //Run fake queries
    start = std::chrono::system_clock::now();
    int counter_fake = randomQueries(fakeTree, random_engine, repetitions);
    auto elapsed_fake = std::chrono::system_clock::now() - start;

    random_engine.seed(0); //Reset seed

    //Run binary queries
    start = std::chrono::system_clock::now();
    int counter_binary = randomQueries(binaryTree, random_engine, repetitions);
    auto elapsed_binary = std::chrono::system_clock::now() - start;

    delete_points(points);

    REQUIRE(counter_fake == counter_binary);

    std::cout << "FakeTree run in " << std::chrono::duration_cast<std::chrono::milliseconds>(elapsed_fake).count() << " ms" << std::endl;
    std::cout << "BinaryTree run in " << std::chrono::duration_cast<std::chrono::milliseconds>(elapsed_binary).count() << " ms" << std::endl;

    REQUIRE(elapsed_binary < elapsed_fake);
}
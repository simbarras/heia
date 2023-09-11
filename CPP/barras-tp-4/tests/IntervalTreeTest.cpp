//
// Created by Beat Wolf on 04.07.2021.
//

#include "../includes/Catch2/catch.hpp"
#include "../src/IntervalTree.h"
#include "../src/base/SimpleIntervalTree.h"

#include <vector>
#include <algorithm>


TEST_CASE("IntervalTree: Basic tests") {
    std::vector<Interval<int>> intervals{{0, 10}, {8, 15}, {200, 300}};

    IntervalTree tree(intervals);

    REQUIRE(tree.size() == intervals.size());

    REQUIRE(tree.containing(3).size() == 1);
    REQUIRE(tree.containing(20).size() == 0);
    REQUIRE(tree.containing(8).size() == 2);
    REQUIRE(tree.containing(9).size() == 2);
}


TEST_CASE("IntervalTree: Random reference comparison") {
    std::vector<Interval<int>> intervals;

    int minPosition = 0;
    int maxPosition = 10000;

    std::mt19937 random_engine;
    std::random_device random_device;
    random_engine = std::mt19937(random_device());

    std::uniform_int_distribution<int> distribution(minPosition, maxPosition);

    while(intervals.size() < 100){
        int start = distribution(random_engine);
        int end = distribution(random_engine);
        if(start != end) {
            intervals.push_back({std::min(start, end), std::max(start, end)});
        }
    }

    IntervalTree tree(intervals);
    SimpleIntervalTree treeSimple(intervals);

    for(int i = 0; i < 100 ; i++){
        int query = distribution(random_engine);

        std::vector<Interval<int>> result = tree.containing(query);
        std::vector<Interval<int>> truth = treeSimple.containing(query);

        REQUIRE(result.size() == truth.size());
        for(int loop = 0; loop < result.size(); loop++){
            REQUIRE(std::is_permutation(result.begin(), result.end(), truth.begin(), truth.end()));
        }
    }

}

TEST_CASE("IntervalTree: Empty test") {
    IntervalTree<int> tree({});

    REQUIRE(tree.size() == 0);
    REQUIRE(tree.containing(0).size() ==0);
    REQUIRE(tree.containing(10).size() == 0);
    REQUIRE(tree.containing(-1).size() == 0);
}

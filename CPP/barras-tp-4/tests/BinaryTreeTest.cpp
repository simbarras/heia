//
// Created by Beat Wolf on 04.07.2021.
//

#include "../includes/Catch2/catch.hpp"
#include "../src/base/BinaryTree.h"
#include <iostream>
#include <numeric>

TEST_CASE("BinaryTree: Search test") {
    BinaryTree<int> btree;

    REQUIRE_FALSE(btree.search(4));

    btree.insert(3);
    REQUIRE_FALSE(btree.search(4));
    REQUIRE(btree.search(3));
    REQUIRE(btree.size() == 1);
}

TEST_CASE("BinaryTree: Init test") {
    BinaryTree<int> btree;

    REQUIRE(btree.isEmpty());
    btree.insert(3);
    REQUIRE_FALSE(btree.isEmpty());
    REQUIRE(btree.size() == 1);

    btree = BinaryTree<int>({1, 4, 5});
    REQUIRE_FALSE(btree.isEmpty());
    REQUIRE(btree.size() == 3);
}

TEST_CASE("BinaryTree: Search range test") {
    BinaryTree<int> btree;
    btree = BinaryTree<int>({1, 4, 5, 8, 10, 44});
    REQUIRE_FALSE(btree.isEmpty());

    std::vector<int> results = btree.inRange(5, 11);
    REQUIRE(results == std::vector<int>{5, 8, 10});
}

std::vector<int> createRange(int start, int end){
    std::vector<int> values(end - start);
    std::iota(values.begin(), values.end(), start);

    return values;
}

TEST_CASE("BinaryTree: Construction and query test") {

    for(int i = 1; i < 100; i++){
        std::vector<int> values = createRange(0, i);

        BinaryTree<int> btree(values, [](int a){return a;});
        BinaryTree<int> btree2(values, [](int a){return a;}, true);
        BinaryTree<int> btree3(values);

        std::vector<BinaryTree<int> *> trees{&btree, &btree2, &btree3};

        for(BinaryTree<int> *tree: trees){
            REQUIRE(tree->size() == values.size());
            REQUIRE(tree->inRange(0, i) == values);

            int firstQuarter = int(1. / 4 * i);
            int thirdQuarter = int(3. / 4 * i);

            REQUIRE(tree->inRange(0, firstQuarter) == createRange(0, firstQuarter + 1));
            REQUIRE(tree->inRange(firstQuarter, thirdQuarter) == createRange(firstQuarter, thirdQuarter + 1));
            REQUIRE(tree->inRange(thirdQuarter, i) == createRange(thirdQuarter, i));
        }
    }
}
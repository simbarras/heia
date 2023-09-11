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
 * @file RangeTree.h
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Declaration of the RangeTree class.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#ifndef CPPALGO_RANGETREE_H
#define CPPALGO_RANGETREE_H

#include <vector>
#include <memory>
#include <numeric>
#include <algorithm>
#include "Point.h"
#include "base/BinaryTree.h"

class RangeTree {

public:
    /**
     * Build range tree given a list of points
     * @param points
     */
    RangeTree(std::vector<Point *> points);

    /**
     * Find all points in a given range (start to end)
     * @param start
     * @param end
     * @return
     */
    std::vector<Point *> search(const Point &start, const Point &end) const;

    /**
     * Return true if the RangeTree has no points
     * @return
     */
    bool isEmpty() const;

    /**
     * Return the amount of points in the RangeTree
     * @return
     */
    size_t size() const;

private:

    void search(std::vector<Point *> &result, BinaryTreeNode<BinaryTree<Point *, double>, Point *> *node, double xFrom,
                double xTo, double xMin, double xMax, double yFrom, double yTo) const;

    BinaryTree<BinaryTree<Point *, double>, Point *>
    build2DRangeTree(std::vector<Point *> &points, size_t left, size_t right) const;

    BinaryTree<BinaryTree<Point *, double>, Point *> xTree;
};


#endif //CPPALGO_RANGETREE_H

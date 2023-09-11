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
 * @file RangeTree.cpp
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Implementation of the RangeTree class.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#include "RangeTree.h"

RangeTree::RangeTree(std::vector<Point *> points) {
    //Sort all points by X
    std::sort(points.begin(), points.end(), [](Point *a, Point *b) {
        return a->getX() < b->getX();
    });
    //Build using all points (sorted in X)
    this->xTree = build2DRangeTree(points, 0, points.size());
}

BinaryTree<BinaryTree<Point *, double>, Point *> RangeTree::build2DRangeTree(std::vector<Point *> &points,
                                                                             size_t left,
                                                                             size_t right) const {
    BinaryTree<BinaryTree<Point *, double>, Point *> tx;

    if (left >= right) {
        return tx;
    }

    //Construct yTree
    std::vector<Point *> yPoints{points.begin() + left, points.begin() + right};
    BinaryTree<Point *, double> ty(yPoints,
                                   [](const Point *a) { return a->getY(); },
                                   true);

    //Construct xTree
    size_t mid = (left + right) / 2;
    tx.setRoot(points[mid], ty);

    //build left and right children
    //WARNING: build2DRangeTree returns a temporary BinaryTree.
    //Use getRootCopy() to get a copy of the root node
    tx.getRoot()->left = build2DRangeTree(points, left, mid).getRootCopy();
    tx.getRoot()->right = build2DRangeTree(points, mid + 1, right).getRootCopy();


    return tx;
}

std::vector<Point *> RangeTree::search(const Point &start, const Point &end) const {
    std::vector<Point *> result;
    search(result, xTree.getRoot(),
           start.getX(), end.getX(),
           std::numeric_limits<int>::min(), std::numeric_limits<int>::max(),
           start.getY(), end.getY());
    return result;
}

bool RangeTree::isEmpty() const {
    return xTree.isEmpty();
}

size_t RangeTree::size() const {
    return xTree.size();
}

void RangeTree::search(std::vector<Point *> &result,
                       BinaryTreeNode<BinaryTree<Point *, double>, Point *> *node,
                       double xFrom, double xTo,
                       double xMin, double xMax,
                       double yFrom, double yTo) const {

    if (node == nullptr) { return; }//node is nullptr if we are at the bottom of the tree
    auto m = node->key;
    if (m->getX() > xTo) {
        search(result, node->left, xFrom, xTo, xMin, m->getX(), yFrom, yTo);
    } else if (m->getX() < xFrom) {
        search(result, node->right, xFrom, xTo, m->getX(), xMax, yFrom, yTo);
    } else if (xMin >= xFrom && xMax <= xTo) {
        //Full in range
        //Whole tree match x range
        auto nodeYTree = node->value.getRootCopy();
        nodeYTree->inRange(result, yFrom, yTo);
        delete nodeYTree;
    } else {
        if (yFrom <= m->getY() && yTo >= m->getY()) {
            result.push_back(m);
        }
        search(result, node->left, xFrom, xTo, xMin, m->getX(), yFrom, yTo);
        search(result, node->right, xFrom, xTo, m->getX(), xMax, yFrom, yTo);
    }
}

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
 * @file IntervalTree.h
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Declaration of the interval tree.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#ifndef CPPALGO_INTERVALTREE_H
#define CPPALGO_INTERVALTREE_H

#include <vector>
#include <algorithm>

#include "base/BinaryTree.h"
#include "IntervalTreeElement.h"


/**
 * Binary interval tree
 * @tparam T
 */
template<typename T>
class IntervalTree {
public:
    IntervalTree(const std::vector<Interval<T>> &intervals);

    /**
     * Number of intervals in the interval tree
     * @return
     */
    size_t size() const;

    /**
     * Returns all intervals that contain a given point
     * @tparam T
     * @param x
     * @return
     */
    std::vector<Interval<T>> containing(T x) const;

private:
    void
    intersecting(std::vector<Interval<T>> &solution, BinaryTreeNode<IntervalTreeElement<T>, T> *node, T x) const;

    BinaryTreeNode<IntervalTreeElement<T>, T> *
    fromIntervals(const std::vector<Interval<T>> &intervals, BinaryTreeNode<IntervalTreeElement<T>, T> *parent);

    BinaryTree<IntervalTreeElement<T>, T> tree;
};


template<typename T>
IntervalTree<T>::IntervalTree(const std::vector<Interval<T>> &intervals) {
    tree.setRoot(fromIntervals(intervals, nullptr));
}

template<typename T>
size_t IntervalTree<T>::size() const {
    return tree.size();
}

template<typename T>
std::vector<Interval<T>> IntervalTree<T>::containing(T x) const {
    std::vector<Interval<T>> solution;
    intersecting(solution, tree.getRoot(), x);
    return solution;
}

template<typename T>
void IntervalTree<T>::intersecting(std::vector<Interval<T>> &solution,
                                   BinaryTreeNode<IntervalTreeElement<T>, T> *node, T x) const {

    if (node == nullptr) {
        return;
    }

    //Add intervals from current node that intersect
    for (auto n: node->value.intersecting(x)) {
        solution.push_back(n);
    }
    //Handle left/right children
    if (x < node->value.mid) {
        intersecting(solution, node->left, x);
    } else {
        intersecting(solution, node->right, x);
    }
}

template<typename T>
BinaryTreeNode<IntervalTreeElement<T>, T> *
IntervalTree<T>::fromIntervals(const std::vector<Interval<T>> &intervals,
                               BinaryTreeNode<IntervalTreeElement<T>, T> *parent) {
    if (intervals.empty()) {
        return nullptr;
    }

    std::vector<T> points;
    for (const Interval<T> &interval: intervals) {
        points.push_back(interval.getStart());
        points.push_back(interval.getEnd());
    }

    std::sort(points.begin(), points.end());

    T mid = points.at(intervals.size()); // so it's the median (of 2n values)

    std::vector<Interval<T>> midPart;
    std::vector<Interval<T>> leftPart;
    std::vector<Interval<T>> rightPart;

    //Split intervals to left, mid, right
    for (const Interval<T> &interval: intervals) {
        if (interval.getStart() > mid) {
            rightPart.push_back(interval);
        } else if (interval.getEnd() < mid) {
            leftPart.push_back(interval);
        } else {
            midPart.push_back(interval);
        }
    }
    //Create new BinaryTreeNode
    auto elt = IntervalTreeElement<T>(midPart, mid);
    auto node = new BinaryTreeNode<IntervalTreeElement<T>, T>(mid, elt, parent);
    //Initialize/Build left and right child of that new node
    node->left = fromIntervals(leftPart, node);
    node->right = fromIntervals(rightPart, node);
    //Return the new node
    return node;
}


#endif //CPPALGO_INTERVALTREE_H

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
 * @file IntervalTreeElement.h
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Declaration and implementation of an element of an interval tree.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#ifndef CPPALGO_INTERVALTREEELEMENT_H
#define CPPALGO_INTERVALTREEELEMENT_H

#include <vector>
#include <algorithm>
#include "Interval.h"

/**
 * Single element of the interval tree
 * @tparam T
 */
template<typename T>
class IntervalTreeElement {
public:
    /**
     * Building IntervalTreeElement
     * @param values
     * @param mid
     */
    IntervalTreeElement(std::vector<Interval<T>> &values, const T mid) : mid(mid) {

        //fill attributes
        std::sort(values.begin(), values.end(), [](Interval<T> a, Interval<T> b) {
            return a.getStart() < b.getStart();
        });
        for (auto value: values) {
            leftSorted.push_back(value);
        }
        std::sort(values.begin(), values.end(), [](Interval<T> a, Interval<T> b) {
            return a.getEnd() > b.getEnd();
        });
        for (auto value: values) {
            rightSorted.push_back(value);
        }
    }

    /**
     * Return all Intervals that intersect with a given point
     * @param x
     * @return
     */
    std::vector<Interval<T>> intersecting(T x) {
        std::vector<Interval<T>> result;
        size_t i = 0;
        if (x < this->mid) {
            while (i < this->leftSorted.size() && this->leftSorted[i].getStart() <= x) {
                result.insert(result.end(), this->leftSorted[i]);
                i++;
            }
        } else {
            while (i < this->rightSorted.size() && this->rightSorted[i].getEnd() >= x) {
                result.insert(result.end(), this->rightSorted[i]);
                i++;
            }
        }
        return result;
    }

    std::vector<Interval<T>> leftSorted;
    std::vector<Interval<T>> rightSorted;
    T mid;
};

#endif //CPPALGO_INTERVALTREEELEMENT_H

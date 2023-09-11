//
// Created by Beat Wolf on 30.11.2021.
//

#ifndef CPPALGO_SIMPLEINTERVALTREE_H
#define CPPALGO_SIMPLEINTERVALTREE_H

#include "../Interval.h"
#include <vector>

template<typename T = int>
class SimpleIntervalTree{
public:
    SimpleIntervalTree(const std::vector<Interval<T>> &intervals);

    /**
     * Number of Intervals in the collection
     * @return
     */
    int size() const;

    /**
     * Returns true if the position x is inside one of the intervals
     * @param x
     * @return
     */
    std::vector<Interval<T>> containing(T x) const;

private:
    std::vector<Interval<T>> intervals;
};

template<typename T>
SimpleIntervalTree<T>::SimpleIntervalTree(const std::vector<Interval<T>> &intervals) : intervals(intervals){
}

template<typename T>
int SimpleIntervalTree<T>::size() const{
    return intervals.size();
}

template<typename T>
std::vector<Interval<T>> SimpleIntervalTree<T>::containing(T x) const{
    std::vector<Interval<T>> solution;
    for(const Interval<T> &interval : intervals){
        if(interval.contains(x)){
            solution.push_back(interval);
        }
    }
    return solution;
}

#endif //CPPALGO_SIMPLEINTERVALTREE_H

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
 * @file Interval.h
 * @author Beat Wolf
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Template for the interval tree.
 *
 * @date 2022-05-16
 * @version 0.1.0
 ***************************************************************************/

#ifndef CPPALGO_INTERVAL_H
#define CPPALGO_INTERVAL_H


template<typename Bound = int, typename Value = double>
class Interval {
public:
    Interval(Bound start, Bound end, Value val = 0) : start_(start), end_(end), value_(val) {}

    bool contains(const Value val) const {
        return start_ <= val && val <= end_;
    }

    Bound getStart() const { return start_; }

    Bound getEnd() const { return end_; }

    Value getValue() const { return value_; }

    bool operator==(const Interval &other) const {
        return std::abs(start_ - other.start_) <= std::numeric_limits<Bound>::epsilon() &&
               std::abs(end_ - other.end_) <= std::numeric_limits<Bound>::epsilon() &&
               std::abs(value_ - other.value_) <= std::numeric_limits<Value>::epsilon();
    }

    bool operator!=(const Interval &other) const {
        return !(this == other);
    }

private:
    Bound start_;
    Bound end_;
    Value value_;
};

#endif //CPPALGO_INTERVAL_H

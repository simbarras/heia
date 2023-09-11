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

/**************************
 * @file Point.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Class to manage a point in 2D space
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include "Point.hpp"

Point::Point(int x, int y, bool isSet) : isSet(isSet), x(x), y(y) {}

Point::Point(const Point &p) : isSet(p.isSet), x(p.x), y(p.y) {}

Point &Point::operator=(Point p) {
    this->isSet = p.isSet;
    this->x = p.x;
    this->y = p.y;
    return *this;
}

void Point::clear() {
    this->isSet = false;
}

void Point::set() {
    this->isSet = true;
}

void Point::draw() {
    this->isSet = !this->isSet;
}

bool Point::is_set() {
    return this->isSet;
}

int Point::get_x() {
    return this->x;
}

int Point::get_y() {
    return this->y;
}
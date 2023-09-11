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
 * @file Shape.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Abstract class for all kind of shapes
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include "Shape.hpp"

Shape::Shape() : points(std::vector<Point>()) {}

Shape::Shape(const Shape &other) : points(other.points) {}


std::vector<Point> Shape::get_points() {
    return points;
}

Shape::~Shape() {
    points.clear();
}

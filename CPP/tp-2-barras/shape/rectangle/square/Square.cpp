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
 * @file Square.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Kind of rectangle
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include "Square.hpp"

Square::Square(int x, int y, int width) : Rectangle(x, y, width, width) {}

Square::Square(const Square &other) : Rectangle(other) {}

Square::~Square() {}

Square &Square::operator=(const Square &other) {
    points = other.points;
    return *this;
}

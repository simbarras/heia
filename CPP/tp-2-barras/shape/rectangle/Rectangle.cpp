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
 * @file Rectangle.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Class to draw a rectangle
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include <stdexcept>
#include "Rectangle.hpp"

Rectangle::Rectangle(int x, int y, int width, int height) : Shape() {
    if (x < 0 || y < 0 || width <= 0 || height <= 0) {
        throw std::invalid_argument("Rectangle: negative values are not allowed");
    }
    Rectangle::draw(x, y, width, height);
}

Rectangle::Rectangle(const Rectangle &rectangle) {
    points = rectangle.points;
}

Rectangle &Rectangle::operator=(Rectangle rectangle) {
    points = rectangle.points;
    return *this;
}

void Rectangle::draw(int x, int y, int width, int height) {
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            points.emplace_back(x + i, y + j, true);
        }
    }
}

Rectangle::~Rectangle() {}


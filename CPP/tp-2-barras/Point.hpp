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
 * @file Point.hpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Class to manage a point in 2D space
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#ifndef TP_2_BARRAS_POINT_HPP
#define TP_2_BARRAS_POINT_HPP


class Point {
public:
    Point(int x, int y, bool isSet = false);

    Point(const Point &p);

    Point &operator=(Point p);

    void clear();

    void set();

    void draw();

    bool is_set();

    int get_x();

    int get_y();

private:
    bool isSet;
    int x;
    int y;
};


#endif //TP_2_BARRAS_POINT_HPP

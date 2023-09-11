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
 * @file Circle.hpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Kind of ellipse
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#ifndef TP_2_BARRAS_CIRCLE_HPP
#define TP_2_BARRAS_CIRCLE_HPP

#include "../Ellipse.hpp"

class Circle : public Ellipse {
public:
    Circle(int x, int y, int diameter);

    Circle(const Circle &circle);

    Circle &operator=(const Circle &circle);

    ~Circle();
};


#endif //TP_2_BARRAS_CIRCLE_HPP

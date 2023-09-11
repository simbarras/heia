// Copyright 2021 Haute école d'ingénierie et d'architecture de Fribourg
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
 * @file RotaryHandler.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the Rotary
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef ROTARYHANDLER_HPP_
#define ROTARYHANDLER_HPP_

#include <stdint.h>

class RotaryHandler {
   public:
    RotaryHandler();
    void Tick(bool newA, bool newB);
    //-1 for left and 1 for right
    virtual void OnRotate(int direction) { (void)direction; };

   private:
    bool lastA_, lastB_;
};
#endif /* ROTARYHANDLER_HPP_ */
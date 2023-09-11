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
 * @file ButtonHandler.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the button and his action
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef BUTTONHANDLER_HPP_
#define BUTTONHANDLER_HPP_

#include <stdint.h>
#include <stdio.h>

class ButtonHandler {
   public:
    ButtonHandler(int longPressedTime = 1000, int repeatTime = 500);
    void Tick(bool pressed, uint32_t ms);

    virtual void OnPressed(){};
    virtual void OnReleased(){};
    virtual void OnLongPressed(){};
    virtual void OnRepeated(int repeated) { (void)repeated; };

   private:
    uint64_t longPressedTime_;
    int repeatTime_;
    int timePassed_;
    int repeat_;
    int lastState_;
};
#endif /* BUTTONHANDLER_HPP_ */
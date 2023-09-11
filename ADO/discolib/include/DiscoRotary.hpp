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
 * @file DiscoRotary.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the DiscoRotary
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef DISCOROTARY_HPP_
#define DISCOROTARY_HPP_

#include <ButtonHandler.hpp>
#include <ClickShiftRegister.hpp>
#include <RotaryHandler.hpp>
#include <Ticker.hpp>

class DiscoRotary : public RotaryHandler, public ButtonHandler, public Ticker {
   public:
    DiscoRotary(int longPressedTime, int repeatTime, int id = 1);
    virtual void ShowLedPattern(uint16_t pattern);
    void Tick();
    virtual void OnRotate(int position) { (void)position; };
    virtual void OnPressed(){};

   private:
    ClickShiftRegister register_;
    int id_;
    u_int32_t swPort_, swPin_;
    u_int32_t encaPort_, encaPin_;
    u_int32_t encbPort_, encbPin_;
};
#endif /* DISCOROTARY_HPP_ */
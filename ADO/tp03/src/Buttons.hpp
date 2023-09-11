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
 * @file Buttons.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Contains all implementations for the 5 buttons
 *
 * @date 2021-11-16
 * @version 0.1.0
 ***************************************************************************/
#include <DiscoConsole.h>
#include <assert.h>
#include <libopencm3/stm32/rcc.h>
#include <stdio.h>

#include <Disco7Segments.hpp>
#include <DiscoAssert.hpp>

#include "ButtonHandler.hpp"
#include "Joystick.hpp"

const int MAX_BRIGHTNESS = 100;
const int MIN_BRIGHTNESS = 0;
const int MAX_VALUE      = 99;
const int MIN_VALUE      = 0;

int counter   = 0;
int brightess = 50;

class ButtonRight : public ButtonHandler {
   public:
    ButtonRight() : ButtonHandler(), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && counter != MAX_VALUE) counter++;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };

    int state_;
};

class ButtonLeft : public ButtonHandler {
   public:
    ButtonLeft() : ButtonHandler(), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && counter != MIN_VALUE) counter--;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };

    int state_;
};

class ButtonUp : public ButtonHandler {
   public:
    ButtonUp() : ButtonHandler(), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && brightess != MAX_BRIGHTNESS) brightess += 5;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };

    int state_;
};

class ButtonDown : public ButtonHandler {
   public:
    ButtonDown() : ButtonHandler(), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && brightess != MIN_BRIGHTNESS) brightess -= 5;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };

    int state_;
};

class ButtonSelect : public ButtonHandler {
   public:
    ButtonSelect() : ButtonHandler(), state_{0} {}

    void OnLongPressed() override
    {
        if (state_ == 0) {
            counter = MIN_VALUE;
            state_  = 1;
        }
    };
    void OnReleased() override { state_ = 0; };

    int state_;
};
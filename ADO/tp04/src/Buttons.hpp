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
 * @author SIMON BARRAS <simon.barras@edu.hefr.ch>, NICOLAS TERREAUX <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Contains all implementations for the 5 buttons
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

/*** INCLUDES **************************************************************/
#include "ButtonHandler.hpp"
#include "Counter.hpp"

/*** CONSTANTS *************************************************************/
const int MAX_BRIGHTNESS    = 100;
const int MIN_BRIGHTNESS    = 0;
const int MAX_VALUE         = 99;
const int MIN_VALUE         = 0;
const int LONG_PRESSED_TIME = 1000;
const int REPEAT_TIME       = 50;

/*** VARIABLES *************************************************************/
int brightess = 50;

/**
 * @brief Class that defines the behavior of the right button
 * 
 */
class ButtonRight : public ButtonHandler {
   public:
    ButtonRight(Counter& counter)
        : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), counter_(counter), state_{0}
    {
    }
    void OnPressed() override
    {
        if (state_ == 0) counter_.Increment();
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        counter_.Increment();
    };

   private:
    Counter& counter_;
    int state_;
};

/**
 * @brief Class that defines the behavior of the left button
 * 
 */
class ButtonLeft : public ButtonHandler {
   public:
    ButtonLeft(Counter& counter)
        : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), counter_(counter), state_{0}
    {
    }
    void OnPressed() override
    {
        if (state_ == 0) counter_.Decrement();
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        counter_.Decrement();
    };

   private:
    Counter& counter_;
    int state_;
};

/**
 * @brief Class that defines the behavior of the up button
 * 
 */
class ButtonUp : public ButtonHandler {
   public:
    ButtonUp() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && brightess != MAX_BRIGHTNESS) brightess += 5;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        if (brightess != MAX_BRIGHTNESS) brightess += 5;
    };

    int state_;
};

/**
 * @brief Class that defines the behavior of the down button
 * 
 */
class ButtonDown : public ButtonHandler {
   public:
    ButtonDown() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override
    {
        if (state_ == 0 && brightess != MIN_BRIGHTNESS) brightess -= 5;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        if (brightess != MIN_BRIGHTNESS) brightess -= 5;
    };
    int state_;
};

/**
 * @brief Class that defines the behavior of the select button
 * 
 */
class ButtonSelect : public ButtonHandler {
   public:
    ButtonSelect(Counter& counter) : ButtonHandler(), counter_(counter), state_{0}
    {
    }

    void OnLongPressed() override
    {
        if (state_ == 0) {
            counter_.Reset();
            state_ = 1;
        }
    };
    void OnReleased() override { state_ = 0; };

   private:
    Counter& counter_;
    int state_;
};

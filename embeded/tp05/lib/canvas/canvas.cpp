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
 * @file canvas.cpp
 * @author Daniel Gachet <daniel.gachet@hefr.ch>
 * @author Jacques Supcik <jacques.supcik@hefr.ch>
 *
 * @brief Simple C interface to LCD Library
 *
 * @date 2021-12-19
 * @version 0.1.1
 ***************************************************************************/

#include "canvas.hpp"

#include <DiscoLcd.hpp>
#include <AdafruitGFX.hpp>
#include <FontsGFX/FreeSans9pt7b.h>
#include <PWM.hpp>

static DiscoLcdGFX* gfx = nullptr;

void canvas_init(void)
{
    static bool initialized = false;
    if (!initialized) {
        DiscoLcdSetup();
        auto backLight = PWM(PWM::PF5);
        backLight.SetDutyCycle(40);

        gfx = new DiscoLcdGFX(kLcdScreenWidth, kLcdScreenHeight);
        gfx->setFont(&FreeSans9pt7b);
        initialized = true;
    }
    gfx->fillScreen(0x0000);
}

void canvas_rectangle(int x0, int y0, int w, int h, unsigned color)
{
    gfx->fillRect(x0, y0, w, h, color);
}

void canvas_text_center(int y0, const char* text, unsigned color)
{
    int16_t x, y;
    uint16_t w, h;
    gfx->setTextColor(color);
    gfx->getTextBounds(text, LCD_FONT_HEIGHT, LCD_SCREEN_WIDTH, &x, &y, &w, &h);
    gfx->setCursor((LCD_SCREEN_WIDTH-w)/2, y0);
    gfx->write(text);
}

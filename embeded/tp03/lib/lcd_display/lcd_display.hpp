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

/****************************************************************************
 * @file lcd_display.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Interface to show data on the LCD display
 *
 * @date 2022-03-09 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#ifndef LCD_DISPLAY_HPP_
#define LCD_DISPLAY_HPP_

#include <mbed.h>

#include "disco_lcd_gfx.h"

class LCDDisplay {
   public:
    // The constructor allocates a new screen and initialises it.
    LCDDisplay();

    // Initialize the screen and print labels.
    void init();

    // Delete the previous value and print the new temperature
    // at the correct position.
    void printTemperature(double t);

    // Delete the previous value and print the new humidity
    // at the correct position.
    void printHumidity(double h);

    // Deletes the previous value and print the new preasure
    // at the correct position.
    void printPressure(double p);

   private:
    // TODO: add methods and attributes if needed

    // Set the font used for the next print operations
    void setFont(const GFXfont* f, const uint16_t color);

    // Print text centered at position (x,y)
    void printCenter(const char* text, int16_t x, int16_t y);

    // Display a filled rectangle with clearColor covering a text
    // centered at position (x,y).
    // Use the bounding box ot the text to compute the size and the
    // position of the rectangle.
    void clearCenterText(const char* text,
                         int16_t x,
                         int16_t y,
                         uint16_t clearColor = LCD_COLOR_BLACK);

    DiscoLcdGFX lcd_;
};

#endif /* LCD_DISPLAY_HPP_ */

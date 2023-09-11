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
 * @file lcd_display.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief File that contains the code for the LCD
 *
 * @date 2022-03-09 to 2020-03-30
 * @version 0.1.0
 ***************************************************************************/

#include <lcd_display.hpp>

#include "Fonts/IBMPlexSansBold24pt8b.h"
#include "Fonts/IBMPlexSansMedium12pt8b.h"
#include "Fonts/IBMPlexSansMedium18pt8b.h"
#include "mbed_trace.h"

constexpr int kLabelMargin = 20;
constexpr int kValueMargin = 70;

#define HALF_LCD_WIDTH (lcd_.width() / 2)
#define LABEL_HEIGHT_POSITION kLabelMargin + (lcd_.height() / 3)*
#define VALUE_HEIGHT_POSITION kValueMargin + (lcd_.height() / 3)*

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "lcd_display"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

// Public method

LCDDisplay::LCDDisplay() : lcd_() { tr_debug("Lcd_display created"); }

// Initialize the display
void LCDDisplay::init()
{
    tr_debug("Initialize LCD");
    lcd_.fillScreen(LCD_COLOR_BLACK);
    setFont(&IBMPlexSansMedium12pt8b, LCD_COLOR_WHITE);
    printCenter("Temp\351rature", HALF_LCD_WIDTH, LABEL_HEIGHT_POSITION 0);
    printCenter("Humidit\351 rel.", HALF_LCD_WIDTH, LABEL_HEIGHT_POSITION 1);
    printCenter("Pression atm.", HALF_LCD_WIDTH, LABEL_HEIGHT_POSITION 2);
    printTemperature(0);
    printHumidity(0);
    printPressure(0);
}

void LCDDisplay::printTemperature(double t)
{
    char temp_str[8];
    setFont(&IBMPlexSansMedium18pt8b, LCD_COLOR_YELLOW);
    //° --> 248
    sprintf(temp_str, "%2.1f C", t);
    printCenter(temp_str, HALF_LCD_WIDTH, VALUE_HEIGHT_POSITION 0);
}

void LCDDisplay::printHumidity(double h)
{
    setFont(&IBMPlexSansMedium18pt8b, LCD_COLOR_RED);

    // Check if humidity is between 0 and 100
    char hum_str[8];
    if (h < 0) {
        h = 0;
    } else if (h > 100) {
        h = 100;
    }
    sprintf(hum_str, "%2.1f %%", h);
    printCenter(hum_str, HALF_LCD_WIDTH, VALUE_HEIGHT_POSITION 1);
}

void LCDDisplay::printPressure(double p)
{
    setFont(&IBMPlexSansMedium18pt8b, LCD_COLOR_GREEN);
    char pres_str[11];

    // Check if pressure is greater than 0
    if (p < 0) {
        p = 0;
    }
    sprintf(pres_str, "%3.0f mbar", p);
    printCenter(pres_str, HALF_LCD_WIDTH, VALUE_HEIGHT_POSITION 2);
}

// Private method

void LCDDisplay::setFont(const GFXfont* f, const uint16_t color)
{
    lcd_.setFont(f);
    lcd_.setTextColor(color);
}

// Method to print a string centered on the screen (called every time when we will print something)
void LCDDisplay::printCenter(const char* text, int16_t x, int16_t y)
{
    clearCenterText(text, x, y);
    int16_t x1, y1;
    uint16_t w, h;
    lcd_.getTextBounds(text, 0, 0, &x1, &y1, &w, &h);
    lcd_.setCursor(x - w / 2, y);
    lcd_.write(text);
}

// Method to clear a text
void LCDDisplay::clearCenterText(const char* text,
                                 int16_t x,
                                 int16_t y,
                                 uint16_t clearColor)  // uint16_t clearColor = LCD_COLOR_BLACK
{
    tr_debug("Clear center text");
    int16_t x1, y1;
    uint16_t w, h;
    lcd_.getTextBounds(text, 0, 0, &x1, &y1, &w, &h);
    lcd_.fillRect(0, y - h - 1, lcd_.width(), h + 2, clearColor);
}
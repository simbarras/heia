#ifndef CANVAS_H_
#define CANVAS_H_

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
 * @file canvas.hpp
 * @author Daniel Gachet <daniel.gachet@hefr.ch>
 * @author Jacques Supcik <jacques.supcik@hefr.ch>
 *
 * @brief Simple C interface to LCD Library
 *
 * @date 2021-12-19
 * @version 0.1.1
 ***************************************************************************/

#define LCD_SCREEN_WIDTH 240
#define LCD_SCREEN_HEIGHT 240
#define LCD_FONT_HEIGHT 20

#ifndef __ASSEMBLER__

#ifdef __cplusplus
extern "C" {
#endif

extern void canvas_init(void);
extern void canvas_rectangle(int x0, int y0, int w, int h, unsigned color);
extern void canvas_text_center(int y0, const char* text, unsigned color);

#ifdef __cplusplus
}
#endif

#endif /* __ASSEMBLER__ */

#endif /* CANVAS_H_ */

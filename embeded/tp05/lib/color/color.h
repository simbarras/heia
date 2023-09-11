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
 * @file color.h
 * @author Jacques Supcik <jacques.supcik@hefr.ch>
 *
 * @brief Simple color library
 *
 * @date 2021-12-19
 * @version 0.1.0
 ***************************************************************************/

#ifndef COLOR_H_
#define COLOR_H_

#define COLOR_BLACK  0x0000
#define COLOR_RED    0xf800
#define COLOR_YELLOW 0xf7a0
#define COLOR_WHITE  0xffff

#ifndef __ASSEMBLER__

#ifdef __cplusplus
extern "C" {
#endif

void MakeRainbow(unsigned int* array, int size);

#ifdef __cplusplus
}
#endif

#endif /* __ASSEMBLER__ */

#endif /* COLOR_H_ */

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
 * @file ShiftRegister.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the ShiftRegister
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef SHIFTREGISTER_HPP_
#define SHIFTREGISTER_HPP_

#include <stdint.h>

class ShiftRegister {
   public:
    ShiftRegister(int id);
    void Reset();
    void SendByte(uint8_t data);
    void SendShort(uint16_t data);

   protected:
    void SendBit(int bit);
    void Latch();
    uint32_t mrPort_, mrPin_;
    uint32_t resetPort_, resetPin_;
    uint32_t sdiPort_, sdiPin_;
    uint32_t sdoPort_, sdoPin_;
    uint32_t sckPort_, sckPin_;
    uint32_t latchPort_, latchPin_;
};
#endif /* SHIFTREGISTER_HPP_ */
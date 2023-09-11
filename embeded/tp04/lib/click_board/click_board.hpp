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
 * @file click_board.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Contain the pin mapping for the ClickBoard.
 *
 * @date 2022-04-27 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#include "mbed.h"

#ifndef CLICKBOARD_HPP
#define CLICKBOARD_HPP

constexpr auto CS_LEFT   = PA_15;  // chip select at 1 at usage and 0 at startup
constexpr auto CS_RIGHT  = PA_15;  // chip select at 1 at usage and 0 at startup
constexpr auto RST_LEFT  = PC_4;   // chip select at 1 for reset and 0 at usage
constexpr auto RST_RIGHT = PC_4;   // chip select at 1 for reset and 0 at usage
constexpr auto RX_LEFT   = PG_9;   // serial receive
constexpr auto RX_RIGHT  = PG_9;   // serial receive
constexpr auto TX_LEFT   = PG_14;  // serial transmit
constexpr auto TX_RIGHT  = PG_14;  // serial transmit

class ClickBoard {
   public:
    enum struct Id : int { LEFT = 1, RIGHT = 2 };
    const PinName CS;
    const PinName RST;
    const PinName RX;
    const PinName TX;

    ClickBoard(Id id)
        : CS(id == Id::LEFT ? CS_LEFT : CS_RIGHT),
          RST(id == Id::LEFT ? RST_LEFT : RST_RIGHT),
          RX(id == Id::LEFT ? RX_LEFT : RX_RIGHT),
          TX(id == Id::LEFT ? TX_LEFT : TX_RIGHT)
    {
    }
};

#endif  // CLICKBOARD_HPP

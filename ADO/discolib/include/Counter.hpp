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
 * @file Counter.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the Counter
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef COUNTER_HPP_
#define COUNTER_HPP_

#include <stdint.h>

class Counter {
   public:
    Counter(int32_t resetValue, int32_t minValue, int32_t maxValue);
    void Increment(int32_t value = 1);
    void Decrement(int32_t value = 1);
    void Reset();
    int GetValue();

   private:
    int32_t resetValue_;
    int32_t value_;
    int32_t minVal_;
    int32_t maxVal_;
};

#endif

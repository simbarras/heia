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
 * @file Counter.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Class that implements a counter with limits
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#include "Counter.hpp"

#include <libopencm3/cm3/assert.h>
#include <stdint.h>

#include <DiscoAssert.hpp>

/**
 * @brief Construct a new Counter:: Counter object. resetValue must be inbetween
 * maxValue and minValue. Will be initialized to minValue otherwise. In case maxValue
 * is bigger or equal to minValue, maxValue will be initialized to minValue + 1.
 *
 * @param resetValue used as an initial value on initialization and reset
 * @param minValue defines lower limit for counter
 * @param maxValue defines upper limit for counter
 */
Counter::Counter(int32_t resetValue, int32_t minValue, int32_t maxValue)
    : resetValue_((resetValue >= minValue && resetValue <= maxValue) ? resetValue : minValue),
      value_(resetValue_),
      minVal_(minValue),
      maxVal_(maxValue)
{
}

/**
 * @brief Incrementing the counter by [value]. Negative values will be set to 1.
 * Cannot surpasse the maximum value, nor can ther be an overflow. maxVal_ will be used
 * as a new value in both cases.
 *
 * @param value value to increment to counter
 */
void Counter::Increment(int32_t value)
{
    value = value > 0 ? value : 1;
    if ((value_ + value) > maxVal_ || (value_ + value) < value_)
        value_ = maxVal_;
    else
        value_ += value;

    cm3_assert(value_ >= minVal_ && value_ <= maxVal_);
}

/**
 * @brief Decrement the counter by [value]. Negative values will be set to 1.
 * Cannot go beneath minimum value, nor can ther be an overflow. minVal_ will be used
 * as a new value in both cases.
 *
 * @param value value to decrement from counter
 */
void Counter::Decrement(int32_t value)
{
    value = value > 0 ? value : 1;
    if ((value_ - value) < minVal_ || (value_ - value) > value_)
        value_ = minVal_;
    else
        value_ -= value;

    cm3_assert(value_ >= minVal_ && value_ <= maxVal_);
}

/**
 * @brief Reset the counter to its initial value.
 *
 */
void Counter::Reset() { value_ = resetValue_; }

/**
 * @brief Get the current value of the counter.
 *
 * @return int current value
 */
int Counter::GetValue()
{
    cm3_assert(value_ >= minVal_ && value_ <= maxVal_);
    return value_;
}
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
 * @file test_compile.cpp
 * @author Jacques Supcik <jacques.supcik@hefr.ch>
 *
 * @brief Test the compilation of the template
 *
 * @date 2022-02-2
 * @version 0.1.0
 ***************************************************************************/

#include <unity.h>

#include "mbed.h"

void basic_test(void) { TEST_ASSERT_EQUAL(1, 1); }

int main()
{
    wait_us(2000000);
    UNITY_BEGIN();
    RUN_TEST(basic_test);
    UNITY_END();
    while (1) {
        asm volatile("nop");
    }
}
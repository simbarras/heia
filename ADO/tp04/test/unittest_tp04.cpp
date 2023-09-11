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
 * @file unittest_button.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Unit test for the button class.
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <unity.h>

#include <Counter.hpp>
#include <Rotary.hpp>

#include "ButtonHandler.hpp"
#include "Buttons.hpp"
#include "Joystick.hpp"
#include "Systick.hpp"
constexpr uint8_t kMaxNumber = 99;
constexpr uint8_t kMinNumber = 0;
static void setup(void)
{
    // Enable clock
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    SystickSetup();
}

class TestRotary : public Rotary {
   public:
    TestRotary(Counter& counter) : Rotary(counter, 2000, 2000) {}
    LedState getState() { return currentState; }
};

class SimpleButton : public ButtonHandler {
   public:
    SimpleButton() : ButtonHandler(), state_{0} {};
    void OnPressed() override { state_ = 1; };
    int state_;
};

void test_simple_button(void)
{
    SimpleButton b{};
    uint32_t timer = 0;
    b.Tick(false, timer += 10);  // First tick is needed to initialize the button
    TEST_ASSERT_EQUAL(b.state_, 0);
    b.Tick(false, timer += 10);  // Still released
    TEST_ASSERT_EQUAL(b.state_, 0);
    b.Tick(true, timer += 10);  // Pressed
    TEST_ASSERT_EQUAL(b.state_, 1);
    b.Tick(true, timer += 60000);  // Long pressed
    TEST_ASSERT_EQUAL(b.state_, 1);
    b.Tick(false, timer += 10);  // Released
    TEST_ASSERT_EQUAL(b.state_, 1);
}

void wait_2_sec()
{
    for (int i = 0; i < 54000000; i++) {
        asm volatile("nop");
    }
}
void test_reset_button(void)
{
    Counter counter(kMinNumber, kMinNumber, kMaxNumber);
    uint32_t timer     = 0;
    ButtonRight right  = ButtonRight(counter);
    ButtonSelect reset = ButtonSelect(counter);
    right.Tick(false, timer += 10);
    right.Tick(true, timer += 10);
    right.Tick(false, timer += 10);
    right.Tick(true, timer += 10);
    reset.Tick(false, timer += 10);
    TEST_ASSERT_EQUAL(2, counter.GetValue());
    reset.Tick(true, timer += 10);
    reset.Tick(true, timer += 1001);
    reset.Tick(false, timer += 10);
    TEST_ASSERT_EQUAL(0, counter.GetValue());
}

void test_left_right_simple(void)
{
    Counter counter(kMinNumber, kMinNumber, kMaxNumber);
    uint32_t timer    = 0;
    ButtonRight right = ButtonRight(counter);
    ButtonLeft left   = ButtonLeft(counter);
    right.Tick(false, timer += 10);
    right.Tick(true, timer += 10);
    right.Tick(false, timer += 10);
    right.Tick(true, timer += 10);
    right.Tick(false, timer += 10);

    TEST_ASSERT_EQUAL(2, counter.GetValue());
    TEST_MESSAGE("Joystick right OK");

    left.Tick(false, timer += 10);
    left.Tick(true, timer += 10);
    left.Tick(false, timer += 10);
    left.Tick(true, timer += 10);
    left.Tick(false, timer += 10);
    TEST_ASSERT_EQUAL(0, counter.GetValue());
    TEST_MESSAGE("Joystick left OK");
}

void test_up_down_simple(void)
{
    uint32_t timer  = 0;
    ButtonDown down = ButtonDown();
    ButtonUp up     = ButtonUp();
    down.Tick(true, timer += 10);
    down.Tick(false, timer += 10);
    down.Tick(true, timer += 10);
    down.Tick(false, timer += 10);
    down.Tick(true, timer += 10);
    down.Tick(false, timer += 10);
    TEST_ASSERT_EQUAL(35, brightess);
    TEST_MESSAGE("Joystick up OK");

    up.Tick(false, timer += 10);
    up.Tick(true, timer += 10);
    up.Tick(false, timer += 10);
    up.Tick(true, timer += 10);
    TEST_ASSERT_EQUAL(45, brightess);
    TEST_MESSAGE("Joystick down OK");
}

void test_rotary(void)
{
    Counter counter(kMinNumber, kMinNumber, kMaxNumber);
    Rotary rotary = Rotary(counter, 2000, 2000);

    // Turn the rotary ten times to the right
    for (int i = 0; i < 10; i++) {
        rotary.OnRotate(1);
    }
    TEST_ASSERT_EQUAL(10, counter.GetValue());
    TEST_MESSAGE("Rotate Right OK");

    // Turn the rotary twice to the left
    rotary.OnRotate(-1);
    rotary.OnRotate(-1);
    TEST_ASSERT_EQUAL(8, counter.GetValue());
    TEST_MESSAGE("Rotate Left OK");

    // Turn the rotary 100 times to the right
    for (int i = 0; i < 100; i++) {
        rotary.OnRotate(1);
    }
    TEST_ASSERT_EQUAL(99, counter.GetValue());
    TEST_MESSAGE("Rotate Max Right OK");

    // Turn the rotary 111 times to the left
    for (int i = 0; i < 111; i++) {
        rotary.OnRotate(-1);
    }
    TEST_ASSERT_EQUAL(0, counter.GetValue());
    TEST_MESSAGE("Rotate Max Left OK");

    // Turn the rotary 2 times to the right
    rotary.OnRotate(1);
    rotary.OnRotate(1);
    TEST_ASSERT_EQUAL(2, counter.GetValue());

    // Long press on the rotary
    rotary.OnLongPressed();
    TEST_ASSERT_EQUAL(0, counter.GetValue());
    TEST_MESSAGE("Rotary long pressed OK");
}

void test_rotary_state(void)
{
    Counter counter(kMinNumber, kMinNumber, kMaxNumber);
    TestRotary rotary(counter);
    TEST_ASSERT_EQUAL(LedState::LED_SINGLE, rotary.getState());
    rotary.OnPressed();
    TEST_ASSERT_EQUAL(LedState::LED_DUAL, rotary.getState());
    rotary.OnPressed();
    TEST_ASSERT_EQUAL(LedState::LED_QUAD, rotary.getState());
    rotary.OnPressed();
    TEST_ASSERT_EQUAL(LedState::LED_FILL, rotary.getState());
    rotary.OnPressed();
    TEST_ASSERT_EQUAL(LedState::LED_INVERTED, rotary.getState());
    rotary.OnPressed();
    TEST_ASSERT_EQUAL(LedState::LED_SINGLE, rotary.getState());
}

int main()
{
    setup();
    UNITY_BEGIN();
    wait_2_sec();
    RUN_TEST(test_simple_button);
    RUN_TEST(test_reset_button);
    RUN_TEST(test_left_right_simple);
    RUN_TEST(test_up_down_simple);
    RUN_TEST(test_rotary);
    RUN_TEST(test_rotary_state);
    UNITY_END();
    return 0;
}

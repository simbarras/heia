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
 * @file DiscoConsole.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Redefinition of the print in console
 *
 * @date 2021-10-12
 * @version 0.1.0
 ***************************************************************************/
#include "DiscoConsole.h"

#include <ctype.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/usart.h>
#include <stdio.h>
#include <stdlib.h>

#define DISCO_CONSOLE_SERIAL_SPEED 115200

int _write(int fd, char* ptr, int len);
int _read(int fd, char* ptr, int len);

void DiscoConsoleSetup(void)
{
    rcc_periph_clock_enable(RCC_GPIOA);
    rcc_periph_clock_enable(RCC_SYSCFG);
    rcc_periph_clock_enable(RCC_USART2);
    gpio_mode_setup(GPIOA, GPIO_MODE_AF, GPIO_PUPD_NONE, GPIO2 | GPIO3);
    gpio_set_af(GPIOA, GPIO_AF7, GPIO2 | GPIO3);

    usart_set_baudrate(USART2, DISCO_CONSOLE_SERIAL_SPEED);
    usart_set_databits(USART2, 8);
    usart_set_stopbits(USART2, USART_STOPBITS_1);
    usart_set_mode(USART2, USART_MODE_TX_RX);
    usart_set_parity(USART2, USART_PARITY_NONE);
    usart_set_flow_control(USART2, USART_FLOWCONTROL_NONE);
    usart_enable(USART2);
}

int _write(int fd, char* ptr, int len)
{
    int count = 0;
    if (fd < 1 || fd > 2) {
        return -1;
    }
    while (*ptr && (count < len)) {
        usart_send_blocking(USART2, *ptr);
        if (*ptr == '\n') {
            usart_send_blocking(USART2, '\r');
        }
        count++;
        ptr++;
    }
    return count;
}

int _read(int fd, char* ptr, int len)
{
    (void)fd;   // to avoid warning
    (void)ptr;  // to avoid warning
    (void)len;  // to avoid warning
    if (fd != 0) {
        return -1;
    }
    return 0;
}
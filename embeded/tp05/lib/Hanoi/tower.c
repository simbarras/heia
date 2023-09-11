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
 * @file tower.c
 * @author Barras Simon <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Tower of hanoi resolution with C
 *
 * @date 2021-12-14
 * @version 0.1.0
 ***************************************************************************/

#include "tower.h"

#include <DiscoConsole.h>
#include <stdio.h>

#include "canvas.hpp"
#include "color.h"
#include "constants.h"

#ifdef VARIANT_C
// CONSTANTS
#define GET_PEG_X(peg) (PEG_MARGE / 2 + (peg)*PEG_MARGE)
#define GET_DISK_HEIGHT(peg) \
    (HEIGHT_MAX + (nb_disks_ - pegs_occupation_[peg] - 1) * (DISK_MARGE + disk_height_))
#define GET_DISK_WIDTH(disk) (DISK_SIZE_MIN + disk * DISK_SIZE_DIFF)

static unsigned int disk_color[NB_OF_DISKS_MAX];
static int nb_disks_ = 6;
static int disk_height_;
static int pegs_occupation_[NUMBER_OF_PEGS];

void tower_of_hanoi_init(int peg, int nb_disks)
{
    canvas_init();
    MakeRainbow(&disk_color[0], nb_disks);
    for (int i = 0; i < NUMBER_OF_PEGS; i++) {
        pegs_occupation_[i] = 0;
    }
    nb_disks_    = nb_disks;
    disk_height_ = (PEG_HEIGHT - (nb_disks + 1) * DISK_MARGE - 10) / nb_disks;
    printf("Taille: %d\n", disk_height_);

    for (int i = NUMBER_OF_PEGS - 1; i >= 0; i--) {
        draw_peg(i);
    }

    for (int i = nb_disks - 1; i >= 0; i--) {
        push_disk(peg, i);
    }

    // Title
    canvas_text_center(LCD_FONT_HEIGHT, "Welcome to HANOI (C)", COLOR_WHITE);
    canvas_text_center(LCD_FONT_HEIGHT * 2, "By Sim and Nico", COLOR_WHITE);
    display_size(nb_disks_);
}

/**
 * @brief
 *
 */
static void clear_disk(int peg, int height)
{
    pegs_occupation_[peg]--;
    // put black rectangle
    auto x = GET_PEG_X(peg);
    auto y = GET_DISK_HEIGHT(peg);
    canvas_rectangle(x - (DISK_MAX_SIZE / 2), y, DISK_MAX_SIZE, disk_height_, COLOR_BLACK);
    // redraw peg
    canvas_rectangle(x, y, PEG_BORDER, disk_height_, COLOR_WHITE);
}

/**
 * method to draw a "disk" on given "peg" at the specified position "height"
 */
static void draw_disk(int peg, int height, int disk)
{
    auto width = GET_DISK_WIDTH(disk);
    auto x     = GET_PEG_X(peg) - (width / 2);
    canvas_rectangle(x, height, width, disk_height_, disk_color[disk]);
}

/**
 * @brief
 *
 */
void push_disk(int peg, int disk)
{
    // x,y,w,h,color
    // auto y = BOTTOM_HEIGHT - (disk + 1) * (disk_height_ + DISK_MARGE);
    auto y = GET_DISK_HEIGHT(peg);
    draw_disk(peg, y, disk);
    pegs_occupation_[peg]++;
}

/**
 * method to move a "disk" out of specified peg "from" to another one "to"
 */
static void move_disk(int from, int to, int disk)
{
    clear_disk(from, disk);
    push_disk(to, disk);
}

/**
 * @brief
 *
 *
 *
 * @param peg
 * @return * method
 */
// void draw_peg(int peg);
void draw_peg(int peg)
{
    // x,y,w,h,color
    canvas_rectangle(GET_PEG_X(peg), TOP, PEG_BORDER, PEG_HEIGHT, COLOR_WHITE);
    canvas_rectangle(
        GET_PEG_X(peg) - PEG_WIDTH / 2, TOP + PEG_HEIGHT, PEG_WIDTH, PEG_BORDER, COLOR_WHITE);
}

/**
 * @brief Resolve the problem of hanoi's towers
 *
 * @param from starting peg
 * @param to target peg
 * @param by
 * @param height height of the disk to move
 * @return int nb of moves
 */
int tower_of_hanoi_move(int from, int to, int by, int height)
{
    wait();
    int moves = 0;
    if (height > 0) {
        height--;
        moves += tower_of_hanoi_move(from, by, to, height);
        move_disk(from, to, height);
        moves++;
        moves += tower_of_hanoi_move(by, to, from, height);
    }
    return moves;
}
#endif

void display_moves(int moves)
{
    canvas_rectangle(0, LCD_SCREEN_HEIGHT - MARGIN_BOTTOM - 20, SCREEN_WIDTH, 40, COLOR_BLACK);
    char text[50];
    strcpy(text, "Moves: ");
    char moves_str[5];
    itoa(moves, moves_str, 10);
    strcat(text, moves_str);
    printf("%s\n", text);
    canvas_text_center(LCD_SCREEN_HEIGHT - MARGIN_BOTTOM, text, COLOR_RED);
}

void display_size(int size)
{
    canvas_rectangle(0, LCD_SCREEN_HEIGHT - MARGIN_BOTTOM - 20, SCREEN_WIDTH, 40, COLOR_BLACK);
    char text[50];
    strcpy(text, "Size: ");
    char size_str[5];
    itoa(size, size_str, 10);
    strcat(text, size_str);
    printf("%s\n", text);
    canvas_text_center(LCD_SCREEN_HEIGHT - MARGIN_BOTTOM, text, COLOR_RED);
}

void wait()
{
    for (int i = 0; i < 1000; i++) {
        asm("nop");
    }
}
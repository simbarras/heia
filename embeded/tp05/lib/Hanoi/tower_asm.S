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
 * @file tower_asm.S
 * @author Barras Simon <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Tower resolution with assembly
 *
 * @date 2022-01-25
 * @version 0.1.0
 ***************************************************************************/

#ifdef VARIANT_ASM

//! 2 - Includes
#include "constants.h"
#include "color.h"
#include "canvas.hpp"

// 3 Symboles (textual expressions)

// 4 Declaration of constants
    .section .rodata
    .align 2
    WELCOME_TEXT: .asciz "Welcome to HANOI (ASM)"
    BY_TEXT: .asciz "by Sim and Nico"

// Ex: MY_CONSTANT:    .long 10

// 5 Declaration of variables with non-zero initial value
    .section .data
    .align 2
    nb_disks:           .byte 10

// 6 Declaration of variables with zero initial value
    .section .bss
    .align 2
    disk_color:         .space 4 * NB_OF_DISKS_MAX
    .align 2
    pegs_occupation:    .space 4 * NUMBER_OF_PEGS
    .align 2
    disk_height:        .space 4


// 7 Function implementation
    .section .text
    .thumb
    .syntax unified
    .align 2

    .global tower_of_hanoi_init
    .global draw_peg
    .global push_disk
    .global draw_disk
    .global clear_disk
    .global tower_of_hanoi_move
    .global move_disk


//*****************************************************************************
    .type tower_of_haoi_init, %function
// void tower_of_hanoi_init(int peg, int nb_disks)
tower_of_hanoi_init:
    push    {r4-r5, lr}

    // canvas_init();
    mov     r4, r0
    ldr     r3, =nb_disks
    strb    r1, [r3]
    bl      canvas_init
    // MakeRainbow(&disk_color[0], nb_disks);
    ldr     r1, =nb_disks
    ldrb    r1, [r1]
    ldr     r0, =disk_color
    bl      MakeRainbow

    // for (int i = 0; i < NUMBER_OF_PEGS; i++) {
    //     pegs_occupation_[i] = 0;
    //  }
    mov     r0, #0
    mov     r5, #0
    ldr     r1, =pegs_occupation
    b 2f
1:  mov     r2, #0
    strb    r2, [r1, r5]
    add     r0, #1
    add     r5, #4
2:  cmp     r0, #NUMBER_OF_PEGS
    blt     1b

    // disk_height_ = (PEG_HEIGHT - (nb_disks + 1) * DISK_MARGE - 10) / nb_disks;
    ldr     r0, =PEG_HEIGHT - 1
    ldr     r3, =nb_disks
    ldrb    r3, [r3]
    sub     r0, r3
    ldr     r5, =DISK_MARGE
    mul     r0, r5
    sub     r0, #10
    sdiv    r2, r0, r3
    ldr     r3, =disk_height
    str     r2, [r3]

    // for (int i = NUMBER_OF_PEGS - 1; i >= 0; i--) {
    //     draw_peg(i);
    // }
    ldr     r5, =NUMBER_OF_PEGS - 1
    b 2f
1:  mov     r0, r5
    bl      draw_peg
    sub     r5, #1
2:  cmp     r5, #0
    bpl     1b

    // for (int i = nb_disks - 1; i >= 0; i--) {
    //     push_disk(peg, i);
    // }
    ldr     r5, =nb_disks
    ldrb    r5, [r5]
    sub     r5, #1
    b 2f
1:  mov     r0, r4
    mov     r1, r5
    bl      push_disk
    sub     r5, #1
2:  cmp     r5, #0
    bpl     1b

    // canvas_text_center(LCD_FONT_HEIGHT, "Welcome to HANOI (C)", COLOR_WHITE);
    ldr     r0, =LCD_FONT_HEIGHT
    ldr     r1, =WELCOME_TEXT
    ldr     r2, =COLOR_WHITE
    bl      canvas_text_center

    // canvas_text_center(LCD_FONT_HEIGHT * 2, "By Sim and Nico", COLOR_WHITE);
    ldr     r0, =LCD_FONT_HEIGHT * 2
    ldr     r1, =BY_TEXT
    ldr     r2, =COLOR_WHITE
    bl      canvas_text_center

    // display_size(nb_disks_);
    ldr     r0, =nb_disks
    ldrb    r0, [r0]
    bl      display_size

    pop     {r4-r5, pc}
    .size tower_of_hanoi_init, .-tower_of_hanoi_init


//*****************************************************************************
    .type draw_peg, %function
draw_peg:
    push    {r4-r5, lr}
    movs    r4, r0
    // Vertical bar
    // find x
    // (PEG_MARGE / 2 + (peg)*PEG_MARGE)
    subs    sp , #2 * 4

    ldr     r1, =COLOR_WHITE
    mov     r5, #0
    ldr     r1, [r1, r5, lsl #2]
    str     r1, [sp, #0]

    ldr     r0, =PEG_MARGE/2
    movs    r2, #PEG_MARGE
    mul     r1, r4, r2
    adds    r0, r0, r1
    movs    r1, #TOP
    movs    r2, #PEG_BORDER
    movs    r3, #PEG_HEIGHT
    movs    r5, r0
    bl      canvas_rectangle

    // Horizontal bar
    ldr     r1, =PEG_WIDTH/2
    subs    r0, r5, r1
    ldr     r1, =TOP + PEG_HEIGHT
    movs    r2, #PEG_WIDTH
    movs    r3, #PEG_BORDER
    bl      canvas_rectangle

    adds    sp, #2 * 4
    pop     {r4-r5, pc}
    .size draw_peg, .-draw_peg


//*****************************************************************************
    .type push_disk, %function
// static void push_disk(int peg, int disk)
push_disk:
    push    {r4-r6, lr}
    mov     r6, r0
    ldr     r4, =nb_disks
    ldrb    r4, [r4]
    mov     r2, r1
    // auto y = GET_DISK_HEIGHT(peg);
    // HEIGHT_MAX + (nb_disks_ - pegs_occupation_[peg] - 1) * (DISK_MARGE + disk_height_)


	ldr     r3, =DISK_MARGE
    ldr     r4, =disk_height
    ldrb    r4, [r4]
    adds    r3, r4

    ldr     r4, =nb_disks
    ldrb    r4, [r4]
    ldr     r5, =pegs_occupation
    ldr     r5, [r5, r0, lsl #2]
    subs    r4, r5
    subs    r4, #1
    mul     r3, r4
    ldr     r4, =HEIGHT_MAX
    adds    r1, r3, r4

    // draw_disk(peg, y, disk);
    bl     draw_disk

    // pegs_occupation_[peg]++;
    ldr     r1, =pegs_occupation
    ldr     r3, [r1, r6, lsl #2]
    add     r3, #1
    str     r3, [r1, r6, lsl #2]

    pop     {r4-r6, pc}
    .size push_disk, .-push_disk


//*****************************************************************************
    .type draw_disk, %function
// static void draw_disk(int peg, int height, int disk)
draw_disk:
    push    {r4-r6, lr}
    mov     r4, r2
    // auto width = GET_DISK_WIDTH(disk);
    // DISK_SIZE_MIN + disk * DISK_SIZE_DIFF
    // #define DISK_SIZE_DIFF ((DISK_MAX_SIZE - DISK_SIZE_MIN) / nb_disks_)
    ldr     r5, =DISK_MAX_SIZE - DISK_SIZE_MIN
    ldr     r6, =nb_disks
    ldr     r6, [r6]
    sdiv    r5, r5, r6
    mul     r2, r5
    add     r2, DISK_SIZE_MIN

    // auto x     = GET_PEG_X(peg) - (width / 2);
    // PEG_MARGE / 2 + (peg)*PEG_MARGE
    ldr     r5, =PEG_MARGE
    lsr     r3, r5, #1
    mul     r0, r5
    add     r0, r3
    lsr     r3, r2, #1
    subs    r0, r3

    // canvas_rectangle(x, height, width, disk_height_, disk_color[disk]);
    subs    sp, #2 * 4
    ldr     r5, =disk_color
    ldr     r3, [r5, r4, lsl #2]
    str     r3, [sp]
    ldr     r3, =disk_height
    ldr     r3, [r3]
    
    bl      canvas_rectangle

    adds    sp, #2 * 4
    pop     {r4-r6, pc}
    .size draw_disk, .-draw_disk
    

//*****************************************************************************
    .type clear_disk, %function
//static void clear_disk(int peg, int height)
clear_disk:
    push    {r4-r8, lr}
    mov     r4, r0

//  pegs_occupation_[peg]--;
    ldr    r1, =pegs_occupation
    ldr    r3, [r1, r4, lsl #2]
    subs   r3, #1
    str    r3, [r1, r4, lsl #2]

//  auto x = GET_PEG_X(peg);
//  GET_PEG_X(peg) (PEG_MARGE / 2 + (peg)*PEG_MARGE)
    ldr    r1, =PEG_MARGE
    lsr    r0, r1, #1
    mul    r1, r4
    add    r0, r1

//  auto y = GET_DISK_HEIGHT(peg);
//  GET_DISK_HEIGHT(peg) (HEIGHT_MAX + (nb_disks_ - pegs_occupation_[peg] - 1) * (DISK_MARGE + disk_height_))
    ldr    r1, =nb_disks
    ldr    r1, [r1]
    ldr    r3, =pegs_occupation
    ldr    r3, [r3, r4, lsl #2]
    subs   r1, r3
    subs   r1, #1
    ldr    r3, =DISK_MARGE
    ldr    r5, =disk_height
    ldrb   r5, [r5]
    adds   r5, r3
    mul    r1, r5
    ldr    r3, =HEIGHT_MAX
    adds   r1, r3

//  canvas_rectangle(x - (DISK_MAX_SIZE / 2), y, DISK_MAX_SIZE, disk_height_, COLOR_BLACK);
    subs   sp, #1 * 4
    mov    r3, #COLOR_BLACK
    str    r3, [sp]
    ldr    r3, =disk_height
    ldrb   r3, [r3]
    ldr    r2, =DISK_MAX_SIZE
    mov    r5, r0
    lsr    r6, r2, #1
    sub    r0, r6
    mov    r6, r1
    mov    r7, r3
    bl     canvas_rectangle

//  canvas_rectangle(x, y, PEG_BORDER, disk_height_, COLOR_WHITE);
    mov    r3, #COLOR_WHITE
    str    r3, [sp]
    ldr    r2, =PEG_BORDER
    mov    r0, r5
    mov    r1, r6
    mov    r3, r7
    bl     canvas_rectangle

    adds   sp, #1 * 4
    pop    {r4-r8, pc}
    .size clear_disk, .-clear_disk
    




    .type move_disk, %function
//static void move_disk(int from, int to, int disk)
move_disk:
    push   {r4-r5, lr}
    mov    r4, r1
    mov    r5, r2

//  clear_disk(from, disk);
    mov     r1, r5
    bl     clear_disk

//  push_disk(to, disk);
    mov     r0, r4
    mov     r1, r5
    bl      push_disk

    pop     {r4-r5, pc}
    .size move_disk, .-move_disk



    .type tower_of_hanoi_move, %function
//int tower_of_hanoi_move(int from, int to, int by, int height)
tower_of_hanoi_move:
    push   {r4-r8, lr}
    mov    r4, r0
    mov    r5, r1
    mov    r6, r2
    mov    r7, r3
//  wait();
    bl     wait

//  int moves = 0;
    mov    r8, #0

//  if (height > 0) {
    cmp    r7, #0
    bls    1f

//      height--;
    sub    r7, #1

//      moves += tower_of_hanoi_move(from, by, to, height);
    mov    r0, r4
    mov    r1, r6
    mov    r2, r5
    mov    r3, r7
    bl     tower_of_hanoi_move
    add    r8, r0

//      move_disk(from, to, height);
    mov    r0, r4
    mov    r1, r5
    mov    r2, r7
    bl     move_disk

//      moves++;
    add    r8, #1

//      moves += tower_of_hanoi_move(by, to, from, height);
    mov    r0, r6
    mov    r1, r5
    mov    r2, r4
    mov    r3, r7
    bl     tower_of_hanoi_move
    add    r8, r0

//  return moves;
1:  mov    r0, r8

    pop   {r4-r8, pc}
    .size tower_of_hanoi_move, .-tower_of_hanoi_move


#endif

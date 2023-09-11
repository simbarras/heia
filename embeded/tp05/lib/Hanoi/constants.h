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
 * @file constants.h
 * @author Barras Simon <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Define all the usefull constants
 *
 * @date 2022-01-25
 * @version 0.1.0
 ***************************************************************************/
//General
#define NUMBER_OF_PEGS 3
#define NB_OF_DISKS_START 10
#define DISPLAY_RED 0xf800
#define DISPLAY_YELLOW 0xf7a0
#define DISPLAY_WHITE 0xffff
#define MARGIN_BOTTOM 10
#define NB_OF_DISKS_MAX 16
#define SCREEN_WIDTH 240
#define TOP 55
#define HEIGHT_MAX (TOP + 10)
// PEG
#define PEG_HEIGHT 145
#define PEG_WIDTH (PEG_MARGE - 10)
#define PEG_MARGE (SCREEN_WIDTH / NUMBER_OF_PEGS)
#define PEG_BORDER 2
// DISK
#define DISK_MARGE 1
#define DISK_SIZE_MIN 5
#define DISK_MAX_SIZE PEG_WIDTH
#define DISK_SIZE_DIFF ((DISK_MAX_SIZE - DISK_SIZE_MIN) / nb_disks_)

// Copyright 2021, School of Engineering and Architecture of Fribourg
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

// author = "Simon Barras"
// date = "2021-11-23"
// version = "0.0.1"
// email = "simon.barras@edu.hefr.ch"
// userid = "simon.barras"
//


import java.util.Random;
import java.util.logging.Logger;

public class BabyBird {

    public static final int MAX_ARGS = 4; // maximum number of arguments
    public static final int SUCCESS_RATE_INDEX = 3; // index of success rate in arguments
    public static final Random RND = new Random(); // random number generator
    public static final int WAIT_BIRD = 10; // max wait time attributed to a bird
    public static final int WAIT_MAX = 50; // total max wait time
    public static final int NB_PARENTS = 2; // number of parents
    public static final int DEFAULT_NB_CHICKS = 17; // default number of baby bird
    public static final int DEFAULT_LIFE_CYCLE = 53; // default life cycle before leaving the nest
    public static final int DEFAULT_MAX_FOOD = 7; // default max food in the nest
    public static final int DEFAULT_HUNTING_RATE = 50; // default change of hunting something
    public static final int MAX_HUNTING_RATE = 100; // max hunting rate (100%)
    public static final Logger LOGGER = Logger.getLogger(BabyBird.class.getName()); // log thread safe

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n"); // set log format
    }

    /**
     * Simulate a bird's nest with threads and semaphores.
     *
     * @param args usage: java babybird [chicks [baby_iter [max_food_size [hunting_success_rate]]]]
     */
    public static void main(String[] args) {
        System.out.println("Lab3 - BabyBird simulation");
        Ctrl ctrl = new Ctrl(Logger.getLogger(BabyBird.class.getName()));

        ctrl.run(ctrl.parseArgs(args));

    }
}

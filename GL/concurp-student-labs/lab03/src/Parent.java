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


/**
 * Object for simlating a prent bird.
 * Use a thread with a routine to live.
 */
public class Parent {
    private Ctrl ctrl; // controller of the application

    private String name; // name of the parent
    private Thread spirit; // thread of the parent
    private long speed; // wait time attributed to this parent
    private int huntingRate; // change of funding something
    private int maxHunt; // number max of the hunting's result
    private int huntingResult; // result of the hunting

    public Parent(Ctrl ctrl, int maxHunt, int huntingRate) {
        this.ctrl = ctrl;
        this.maxHunt = maxHunt;
        this.huntingRate = huntingRate;
        this.speed = BabyBird.RND.nextInt(BabyBird.WAIT_BIRD + 1);

        spirit = new Thread(() -> {
            // Routine of the bird's life
            while (ctrl.hasChild()) {
                hunt();
                depositFood();
                rest();
            }
            BabyBird.LOGGER.info(name + " is dying of sorrow");
        });
    }

    /**
     * Start the thread and the routine
     *
     * @param name name of the bird
     */
    public void born(String name) {
        this.name = "Parent " + name;
        huntingResult = 0;
        spirit.start();
    }

    /**
     * Close the thread
     *
     * @throws InterruptedException
     */
    public void funeral() throws InterruptedException {
        spirit.join();
        BabyBird.LOGGER.info(name + " is buried");
    }

    /**
     * Wait a random time and the try to hunt.
     * The probability for the parent to find something is defined in the parameters.
     * The number of food fin is a random number: 0 <= food <= maxHunt.
     */
    private void hunt() {
        BabyBird.LOGGER.info(name + " is hunting");
        ctrl.nap(spirit, speed);
        if (BabyBird.RND.nextInt(BabyBird.MAX_HUNTING_RATE + 1) < huntingRate) {
            huntingResult = BabyBird.RND.nextInt(maxHunt + 1);
            BabyBird.LOGGER.info(name + " found " + huntingResult + " worms");
        }
    }

    /**
     * Put food only if the tank is empty and nobody is using the variable of the tank.
     */
    private void depositFood() {
        if (huntingResult > 0) {
            ctrl.putFood(huntingResult);
            BabyBird.LOGGER.info(name + " is throwing in the nest");
        }
    }

    /**
     * Random timer
     */
    private void rest() {
        ctrl.nap(spirit, speed);
        BabyBird.LOGGER.info(name + " is taking a coffee");
    }

}

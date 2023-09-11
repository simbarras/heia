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

import java.util.concurrent.Semaphore;

/**
 * Object for simulating a baby bird.
 * Use a thread with a routine to live.
 */
public class Chick {
    private Ctrl ctrl; // controller of the application

    private String name; // name of the bird
    private Thread spirit; // thread of the bird
    private long speed; // wait time attributed to the bird
    private Semaphore live; // release when the child leave the nest

    public Chick(Ctrl ctrl, int nbDayToAdult) {
        this.ctrl = ctrl;
        speed = BabyBird.RND.nextInt(BabyBird.WAIT_BIRD + 1);
        spirit = new Thread(() -> {
            // Routine of the bird's life
            for (int i = 0; i < nbDayToAdult; i++) {
                sleep();
                getFood();
                eat();
                digestAnd_();
            }
            leaveNest();
        });
    }

    /**
     * Start the thread and the routine
     *
     * @param name the name of the bird
     */
    public void born(String name) {
        this.name = "Chick " + name;
        live = new Semaphore(0);
        spirit.start();
    }

    /**
     * Call by the main thread for waiting until the bird left the nest
     *
     * @throws InterruptedException
     */
    public void farewellParty() throws InterruptedException {
        live.acquire();
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
     * Wait a random time
     */
    private void sleep() {
        BabyBird.LOGGER.info(name + " is sleeping");
        ctrl.nap(spirit, speed);
    }

    /**
     * Take food only if the tank isn't empty and nobody is using the variable for the stock
     */
    private void getFood() {
        BabyBird.LOGGER.info(name + " is getting food");
        ctrl.getFood();
    }

    /**
     * Random timer
     */
    private void eat() {
        BabyBird.LOGGER.info(name + " is eating");
        ctrl.nap(spirit, speed);
    }

    /**
     * Random timer
     */
    private void digestAnd_() {
        BabyBird.LOGGER.info(name + " is digesting");
        ctrl.nap(spirit, speed);
    }

    /**
     * Last method of the life cycle.
     * Release the semaphore
     */
    private void leaveNest() {
        BabyBird.LOGGER.info(name + " is leaving the nest");
        live.release();
    }

}

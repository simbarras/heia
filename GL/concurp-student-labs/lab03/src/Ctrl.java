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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * This class is the controller of the application. It wraps all the methods which aren't in a class int he Lab02.
 */
public class Ctrl {
    private Logger logger;
    private Chick[] chicks; // array of the chicks
    private Parent[] parents; // array of the parents
    private int nbMaxWorm; // number of max worms that can be put in the nest
    private volatile Integer preNbWorm; // number of worms fund by the 2 parents
    private volatile int nbWorm; // number of worms in the nest
    private Semaphore waitEat; // semaphore to wait to get food of the nest
    private Semaphore waitHunt; // semaphore to wait to put food in the nest
    private CyclicBarrier barrier; // the first parent wait the second to put some food in the nest
    private boolean hasChild; // ture if steal at least one child in the nest

    /**
     * Constructor of the class.
     *
     * @param logger logger that will be used to log
     */
    public Ctrl(Logger logger) {
        this.logger = logger;
    }

    /**
     * Start the application.
     *
     * @param args parsed arguments
     */
    public void run(int[] args) {
        System.out.println("Lab03 - BabyBird simulation");
        building_nest(args[0], args[1], args[2], args[3]);
        simulating();
        destroy_nest();
        System.out.println("Lab03 - finish simulation");
    }

    /**
     * Initialize the nest.
     *
     * @param nbChicks     number of chicks
     * @param babyItr      number of life cycle
     * @param foodCapacity max number of worms in the nest
     * @param huntingRate  chance to find something
     */
    private void building_nest(int nbChicks, int babyItr, int foodCapacity, int huntingRate) {
        chicks = new Chick[nbChicks];
        parents = new Parent[BabyBird.NB_PARENTS];
        hasChild = true;
        nbMaxWorm = foodCapacity;
        preNbWorm = 0;
        waitEat = new Semaphore(0);
        waitHunt = new Semaphore(1);
        barrier = new CyclicBarrier(BabyBird.NB_PARENTS, () -> {
            try {
                // food put in the nest by the barrier
                waitHunt.acquire();
                int lostWorm = preNbWorm - nbWorm;
                BabyBird.LOGGER.info("Parent hunted " + preNbWorm + " food, " + (lostWorm > 0 ? lostWorm : 0) + " will be lost");
                wormTank(preNbWorm > nbMaxWorm ? nbMaxWorm : preNbWorm);
                preNbWorm = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < nbChicks; i++) {
            chicks[i] = new Chick(this, babyItr);
        }
        for (int i = 0; i < BabyBird.NB_PARENTS; i++) {
            parents[i] = new Parent(this, foodCapacity, huntingRate);
        }
    }

    /**
     * Run the nest life
     */
    private void simulating() {
        int i = 1;
        for (Chick chick : chicks) {
            chick.born(i++ + "");
        }
        i = 1;
        for (Parent parent : parents) {
            parent.born(i++ + "");
        }
        for (Chick chick : chicks) {
            try {
                chick.farewellParty();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        hasChild = false;
        for (Parent parent : parents) {
            waitHunt.release();
        }

    }

    /**
     * Clos all threads.
     */
    private void destroy_nest() {
        for (Chick chick : chicks) {
            try {
                chick.funeral();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Parent parent : parents) {
            try {
                barrier.reset();
                parent.funeral();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call by the parent's thread routine
     *
     * @return true if at least one chick is alive
     */
    public boolean hasChild() {
        return hasChild;
    }

    /**
     * Call to eat some food
     */
    public void getFood() {
        try {
            waitEat.acquire();
            wormTank(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Class to add some food
     *
     * @param worm number of worms hunted
     */
    public void putFood(int worm) {
        if (hasChild()) {
            synchronized (preNbWorm) {
                preNbWorm += worm;
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                BabyBird.LOGGER.info("Barrier broken");
            }
        }

    }

    /**
     * Call when we want to modify the number of worms in the tank
     *
     * @param nbWorm worms to add
     */
    private synchronized void wormTank(int nbWorm) {
        this.nbWorm += nbWorm;
        if (nbWorm > 0) {
            waitEat.release(nbWorm);
        } else if (this.nbWorm == 0) {
            waitHunt.release();
        }
    }

    /**
     * Pause thread
     *
     * @param t    thread to pause
     * @param time at least time to wait
     */
    public void nap(Thread t, long time) {
        try {
            t.sleep(time + BabyBird.RND.nextInt(BabyBird.WAIT_MAX - BabyBird.WAIT_BIRD + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Format arguments
     *
     * @param args original arguments
     * @return formatted arguments
     */
    public int[] parseArgs(String[] args) {
        int[] result = new int[BabyBird.MAX_ARGS];
        try {
            for (int i = 0; i < args.length; i++) {
                result[i] = checkArgs(i, args[i]);
            }
            for (int i = args.length; i < BabyBird.MAX_ARGS; i++) {
                switch (i) {
                    case 0:
                        result[i] = BabyBird.DEFAULT_NB_CHICKS;
                        break;
                    case 1:
                        result[i] = BabyBird.DEFAULT_LIFE_CYCLE;
                        break;
                    case 2:
                        result[i] = BabyBird.DEFAULT_MAX_FOOD;
                        break;
                    case 3:
                        result[i] = BabyBird.DEFAULT_HUNTING_RATE;
                        break;
                }
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + "\n Please follow usage bellow\n java babybird [chicks [baby_iter [max_food_size [hunting_success_rate]]]]");
        }
    }

    /**
     * Check type and value of arguments
     * Also check the number of args
     *
     * @param index index of the argument
     * @param arg   argument to check
     * @return value of the argument in int
     * @throws Exception if the argument is not correct
     */
    private int checkArgs(int index, String arg) throws IllegalArgumentException {
        if (index >= BabyBird.MAX_ARGS) {
            throw new IllegalArgumentException("To many arguments. Max: 4.");
        } else {
            int value = Integer.parseInt(arg);
            if (index == BabyBird.SUCCESS_RATE_INDEX) {
                if (0 > value || value > BabyBird.MAX_HUNTING_RATE)
                    throw new IllegalArgumentException("Success rate must be between 0 and 100.");
            } else {
                if (0 >= value) throw new IllegalArgumentException("Argument must be bigger than 0.");
            }
            return value;
        }
    }

}

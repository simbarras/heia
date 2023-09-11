# Baby bird java

For this new lab, we need to reuse the code from the previous lab ([documentation here](../../lab02/doc/README.md)). The main job is to translate the code from python to java but the tricky part was to implement the barrier.

## Barier

We need to implement this barrier to complete this fact:

> _One parent waits until the other parent comes with or without (hunting chance!) food. They must deposit their food together in the nest. Of course, the sum of the food of both parents must not excite the maximum food capacity `C`. Possible excess food is disposed by the parents (this food-waste must be indicated in the simulation logs!)_

To modify my code, I didn't touch to the class `Chick` and `Parent`. I only need to change the method `putFodd` to add an `await()` statement:

```Java
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
```

The real modification of the tank's value is in the release of the barrier.

```Java
/**
 * Initialize the nest.
 *
 * @param nbChicks     number of chicks
 * @param babyItr      number of life cycle
 * @param foodCapacity max number of worms in the nest
 * @param huntingRate  chance to find something
 */
private void building_nest(int nbChicks, int babyItr, int foodCapacity, int huntingRate) {
    [...]
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

   [...]
}
```

## Conclusion

This is not my favorite lab, maybe because I don't like to do 2 times the same job. But I think it's a very good lab and I think he will be very useful. The last lab I expected the 6 but I got 5,5, I try to learn of my fault and I think I patch all the mistakes.

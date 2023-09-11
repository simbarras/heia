package ch.epfl.javancox.experiments.builder.test;

import java.util.Arrays;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

public class TestExperiment implements Experiment {
	
	public TestExperiment(int[] rr, Animal[] animals) {
		System.out.println("tt");		
	}
	
	public TestExperiment(TestEnum e) {
		
	}
	
	public TestExperiment(TestEnum[] e) {
		if (e == null) {
			System.out.println("null");
		} else {
			System.out.println(Arrays.toString(e));
		}
	}
	
	public TestExperiment(Animal a, Dog d) {
		
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		// TODO Auto-generated method stub
		
	}

	
	public static abstract class Animal {
		
	}
	
	public static class Cat extends Animal {
		public Cat(boolean blind) {
			
		}
		
		public Cat(TestEnum en) {
			
		}
	}
	
	public static class Dog extends Animal {
		
		private int age = 0;
		
		public Dog(int age) {
			this.age = age;
		}
		
		public Dog(Dog g) {
			age = -1;
		}
		
		public String toString() {
			return "Dog age is " + age;
		}
	}
	
	public static enum TestEnum {
		ONE, TWO, THREE
	}
}

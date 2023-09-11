package ch.epfl.general_libraries.simulation;


public class DiscreteState implements EventParameter {

	private int stepNumber = 0;
	private Time totalTime = null;
	private Time currentStepLength = null;
	private double currentStepLengthF;
	//private String unit = null;

	public Class<? extends DiscreteState> getDefaultClass() {
		return DiscreteState.class;
	}

	public DiscreteState(Time length) {
		setStepLenght(length);
		totalTime = new Time(0,length.getUnit());
	}

	public void setStepLenght(Time length) {
		if (length == null) {
			throw new NullPointerException("DiscreteState cannot be created using null length");
		}
		currentStepLength = length;
		currentStepLengthF = currentStepLength.getValue();
	}

	public void nextStep() {
		stepNumber++;
		totalTime = totalTime.plus(currentStepLength);
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public Time getEndOfStepTotalTime() {
		return totalTime.plus(currentStepLength);
	}

	public double getEndOfStepTotalTimeMicroSeconds() {
		return totalTime.getValue() + currentStepLengthF;
	}

	public Time getTotalTime() {
		return totalTime;
	}

	public Time getCurrentStepLength() {
		return currentStepLength.thisTime();
	}

	@Override
	public String toString() {
		return "State : step #" + this.stepNumber + " [" + totalTime + " --> " + getEndOfStepTotalTime() + "]";
	}

}

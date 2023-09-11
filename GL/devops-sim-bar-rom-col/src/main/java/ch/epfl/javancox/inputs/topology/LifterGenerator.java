package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.transformation.GraphLift;
import javancox.topogen.AbstractTopologyGenerator;

public class LifterGenerator extends AbstractTopologyGenerator {
	
	private AbstractTopologyGenerator parent;
	private int lift;
	private PRNStream s;
	
	public LifterGenerator(@ParamName(name="base structure") AbstractTopologyGenerator parent, 
			@ParamName(name="Number of lifts") int lift, 
			PRNStream s) {
		this.parent = parent;
		this.lift = lift;
		this.s = s;
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		parent.generate(agh);
		GraphLift.liftGraph(agh, lift, s);
	}

	@Override
	public String getName() {
		return lift + "-lifted_" + parent.getName(); 
	}

	@Override
	public String getFullName() {
		return lift + "-lifted_" + parent.getFullName(); 

	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		Map<String, String> ma = parent.getGeneratorParameters();
		ma.put("Lift", lift+"");
		ma.put("Lift seed", ""+s.getSeed());
		return ma;
	}

	@Override
	public int getNumberOfNodes() {
		return lift * parent.getNumberOfNodes();
	}
	
	public static class Lifter_ extends WebTopologyGeneratorStub {

		/**
		 * Generate a dragonfly
		 * @param agh The AbstractGraphHandler
		 * @param layerName The layer
		 * @param torusLimit The torus dimension
		 * @param sideLength The length of the side
		 */
		@MethodDef
		public String generateLifter(AbstractGraphHandler agh,
				@ParameterDef (name="Parent generator") AbstractTopologyGenerator parent,
				@ParameterDef (name="Lifts") int n,
				@ParameterDef (name="Seed") int l) {

			LifterGenerator lg = new LifterGenerator(parent, n, PRNStream.getDefaultStream(l));
			lg.generate(agh);
			return null;
		}
		
		@MethodDef
		public String generateLifter(AbstractGraphHandler agh,
				@ParameterDef (name="Nodes in base ring") int ring,
				@ParameterDef (name="Lifts") int n,
				@ParameterDef (name="Seed") int l) {

			LifterGenerator lg = new LifterGenerator(new PolygonGenerator(ring, true, 0, 300, 300), n, PRNStream.getDefaultStream(l));
			lg.generate(agh);
			return null;
		}		

	}	

}

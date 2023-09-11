package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator.WebTopologyGeneratorStub;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class Stacked2DHyperX extends AbstractPlanarDeterministicGenerator {
	
	private int radix;
	private boolean position;
	
	public Stacked2DHyperX(int radix, boolean position) {
		this.radix = radix;
		this.position = position;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		for (int p = 0 ; p < radix/2 ; p++) {
			int xOff = p*(radix/3 + 2)*50;
			int yOff = p*50;			
			for (int x = 0 ; x < radix/3 + 1 ; x++) {
				NodeContainer initial = null;
				NodeContainer previous = null;
				NodeContainer current = null;
				for (int y = 0 ; y < radix/3 + 1 ; y++ ) {
					if (position) {
						current = agh.newNode(xOff + x*50, yOff + y*50);
					} else {
						current = agh.newNode();
					}
					if (previous != null) {
						agh.newLink(previous, current);
					} else {
						initial = current;
					}
					previous = current;
				}
				agh.newLink(current, initial);
			}
		}
		
		for (int i = 0 ; i < (radix/3 + 1)*(radix/3 + 1) ; i++) {
			NodeContainer global;
			if (position) {
				global = agh.newNode(i*40, -100);
			} else {
				global = agh.newNode();
			}
		}
		
		/*	for (int p = 0 ; i < radix/2 ; p++) {
				int pOff = p*(radix/3 + 1)*(radix/3 + 1);
				//line
				for (int line = 0 ; line < (radix/3 + 1) ; line++) {
					for (int j = 0 ; j < (radix/3 + 1) - 1 ; j++) {
						for (int k = j+1 ; k < (radix/3 + 1) ; k++) {
							int start = pOff + line + j*(radix/3 + 1);
							int end = pOff + line + k*(radix/3 + 1);
							agh.newLink(start, global.getIndex());
							agh.newLink(end, global.getIndex());
						}
					}
				}
				
			}
		}*/
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static class Stacked2DHyperX_ extends WebTopologyGeneratorStub {
		@MethodDef
		public String generateMoore(AbstractGraphHandler agh,
				@ParameterDef (name="radix") int d,
				@ParameterDef (name="position") boolean b) throws InstantiationException{
			
			Stacked2DHyperX gen = new Stacked2DHyperX(d, b);
			gen.generate(agh);
			return null;
		}			
	}

}

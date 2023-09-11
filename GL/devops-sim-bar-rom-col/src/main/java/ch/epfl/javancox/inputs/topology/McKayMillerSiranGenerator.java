package ch.epfl.javancox.inputs.topology;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.math.PrimeNumbers;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class McKayMillerSiranGenerator extends AbstractDeterministicGenerator {
	
	private int target;
	private int q;
	private int d;
	private int w;
	private int generator_;
	
	public McKayMillerSiranGenerator(int target) {
		this.target = target;
		findQandD();
	}
	
	public McKayMillerSiranGenerator(long q_) {
		int qq = (int)q_;
		int rest = qq % 4;
		if (qq <= 165) {
			if (rest != 2) {
				if (PrimeNumbers.isPrimePower(qq)) {
					if ((generator_ = PrimeNumbers.findGenerator(qq)) >= 0) {
						q = qq;
						w = qq/4;
						d = rest;
						if (rest == 3) {
							d = -1;
							w++;
						}
						target = 2*q*q;	
					//	System.out.println("q=" + q);
					}
				}
			}
		}
	}
	
	public boolean isValid() {
		return q >= 0;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		NodeContainer[][][] nodeA = new NodeContainer[2][q][q];
		for (int i = 0 ; i < 2 ; i++) {
			for (int j = 0 ; j < q ; j++) {
				for (int k = 0 ; k < q ; k++) {
					nodeA[i][j][k] = agh.newNode((q+3)*30*i + j*30, k*30);
				}
			}
		}
		BigInteger generator = new BigInteger(generator_+"");
		BigInteger qbi = new BigInteger(q+"");
		
		ArrayList<Integer> X = new ArrayList<Integer>();
		ArrayList<Integer> Xp = new ArrayList<Integer>();
		
		if (d == 0 || d == 1) {
			X.add(1);
			for (int i = 2 ; i <= 4*w-2 ; i = i+ 2) {
				int prod = 1;
				for (int p = 0 ; p < i ; p++) {
					prod = (prod*generator_) % q;
				}
				X.add(prod);
				
			//	X.add(generator.pow(i).mod(qbi).intValue());
			}
			for (int i = 1 ; i <= 4*w-1 ; i = i + 2) {
				Xp.add(generator.pow(i).mod(qbi).intValue());
			}
		} else if (d == -1) {
			X.add(1);
			for (int i = 2 ; i <= 2*w-2 ; i = i+ 2) {
				X.add(generator.pow(i).mod(qbi).intValue());
			}
			for (int i = 2*w-1 ; i <= 4*w-3 ; i = i+ 2) {
				X.add(generator.pow(i).mod(qbi).intValue());
			}
			
			for (int i = 1 ; i <= 2*w-3 ; i = i + 2) {
				Xp.add(generator.pow(i).mod(qbi).intValue());
			}
			for (int i = 2*w ; i <= 4*w-2 ; i = i + 2) {
				Xp.add(generator.pow(i).mod(qbi).intValue());
			}			
			
		}
	//	System.out.println(X);
	//	System.out.println(Xp);
		
		/*if (d <= 1) {
			for (int i = 0 ; i < q - 1 ; i++) {
				if (i % 2 == 0) {
					X.add(accum.intValue() % q);
				} else {
					Xp.add(accum.intValue() % q);
				}
				accum = accum.multiply(generator);
			}
		} else if (d == 0) {
			X.add(1);
			for (int i = 1 ; i <= w ; i++) {
				X.add(generator.pow(i*4 - 2).intValue());
			}
			for (int i = 1 ; i <= w ; i++) {
				Xp.add(generator.pow(4*i-1).intValue());
			}
			
		} else if (d == -1) {
			X.add(1);
			for (int i = 1 ; i <= w - 1 ; i++) {
				X.add(generator.pow(2*i).intValue());
			}
			X.add(generator.pow(2*w-1).intValue());
			
		}*/
		
	/*	X.clear();
		Xp.clear();
		X.add(2);
		X.add(3);
		X.add(6);
		X.add(7);
		Xp.add(3);
		Xp.add(4);
		Xp.add(5);
		Xp.add(6);	*/	
		for (int x1 = 0 ; x1 < q ; x1++) {
			for (int y1 = 0 ; y1 < q ; y1++) {
				for (int x2 = 0 ; x2 < q ; x2++) {
					for (int y2 = 0 ; y2 < q ; y2++) {
						if (x1 == x2) {
							NodeContainer nc1 = nodeA[0][x1][y1];
							NodeContainer nc2 = nodeA[0][x2][y2];
							int m = /*Math.abs(*/y1 - y2/*)*/;
							if (X.contains(m)) {
								if (agh.getLinkContainer(nc1.getIndex(), nc2.getIndex(), false) == null) 
									agh.newLink(nc1, nc2);
							}
							if (Xp.contains(m)) {
								if (agh.getLinkContainer(nodeA[1][x1][y1].getIndex(), nodeA[1][x2][y2].getIndex(), false) == null) 
									agh.newLink(nodeA[1][x1][y1], nodeA[1][x2][y2]);
							} 
						}
						if (y1 == (x2*x1 + y2) % q) {
							agh.newLink(nodeA[0][x1][y1], nodeA[1][x2][y2]);
						}
					}
				}
			}
		}
		
	}

	@Override
	public String getName() {
		return "McKayMillerSiran";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		SimpleMap<String, String> m = new SimpleMap<String, String>();
		m.put("target nodes", target+"");
		m.put("q value", q+"");
		m.put("d value", d+"");
		return m;
	}

	@Override
	public int getNumberOfNodes() {
		return 2*q*q;
	}
	
	private void findQandD() {
		//findest the smaller q that is larger than target
		double sqrtTarget = Math.pow(target/2d,0.5);
		int minW = (int)Math.floor((Math.pow(target/2d,0.5) - 1)/4d);
		for (int w = minW ; w < 60 ; w++) {
			int q1 = (4*w)+1;
			if (!PrimeNumbers.isPrimePower(q1)) continue;
			if (PrimeNumbers.findGenerator(q1) < 0) continue;
			if (q1 >= sqrtTarget) {
				q = q1;
				d = 1;
				this.w = w;
				break;
			}
		}
		minW = (int)Math.floor((Math.pow(target/2d,0.5) + 1)/4d);
		for (int w = minW ; w < 17 ; w++) {
			int q1 = (4*w)-1;
			if (!PrimeNumbers.isPrimePower(q1)) continue;
			if (PrimeNumbers.findGenerator(q1) < 0) continue;
			if (q1 >= sqrtTarget) {
				if (q1 < q) {
					q = q1;
					d = -1;
					this.w = w;
				}
				break;
			}
		}
		minW = (int)Math.floor((Math.pow(target/2d,0.5))/4d);
		for (int w = minW ; w < 16 ; w++) {
			int q1 = (4*w);
			if (!PrimeNumbers.isPrimePower(q1)) continue;
			if (PrimeNumbers.findGenerator(q1) < 0) continue;
			if (q1 >= sqrtTarget) {
				if (q1 < q) {
					q = q1;
					d = 0;
					this.w = w;
				}
				break;
			}
		}
		generator_ = PrimeNumbers.findGenerator(q);
	}
	
	public static class McKayMillerSiranGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateMcKey(AbstractGraphHandler agh,
				@ParameterDef (name="target")int target,
				@ParameterDef (name="pcol?") boolean pcol) {
					
			McKayMillerSiranGenerator gen = new McKayMillerSiranGenerator(target);
			gen.generate(agh);

			if (pcol) {
				int clients = agh.getNumberOfNodes();
				double[][] dist = new double[clients][clients];
				for (int i = 0 ; i < clients ; i++) {
					Path[] p = BFS.getShortestPathsUndirectedFrom(agh, i);
					for (int j = 0 ; j < clients ; j++) {
						if (j != i && p[j] != null) {
							dist[i][j] = p[j].getNumberOfHops();
						}
					}
				}
				dist = Matrix.normalize(dist);
				PcolorGUI gui = new PcolorGUI(dist);
				gui.showInFrame();			
			}			
			
			return null;
		}
		
		@MethodDef
		public String generateMcKay(AbstractGraphHandler agh,
				@ParameterDef (name="q")int q) {
					
			McKayMillerSiranGenerator gen = new McKayMillerSiranGenerator((long)q);
			gen.generate(agh);
			return null;
		}		
	}

	public int getRadix() {
		return (3*q - d)/2;
	}

}

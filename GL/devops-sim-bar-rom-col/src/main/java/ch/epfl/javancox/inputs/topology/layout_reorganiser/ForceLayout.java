package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.awt.Point;
import java.util.Map;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;


public class ForceLayout extends AbstractTopologyLayout {
	
	@Override
	public Map<String, String> getLayoutParameters() {
		return getEmptyMap();
	}

	@Override
	public void assignNodesPosition(int max_screen_x, int max_screen_y, AbstractGraphHandler agh){
		int nbNodes = agh.getHighestNodeIndex()+1;
		
		double[] force_tx = new double[nbNodes];
		double[] force_ty = new double[nbNodes];
		boolean eventstatus = agh.isEventEnabled();
		agh.setModificationEventEnabled(false, new CasualEvent(this));
		double[] posx = new double[nbNodes];
		double[] posy = new double[nbNodes];
		for (int j = 0 ; j < nbNodes ; j++) {
			NodeContainer first = agh.getNodeContainer(j);
			Point fir = first.getCoordinate();
		   	posx[j] = fir.getX();
		   	posy[j] = fir.getY();
		}		
		for (int i = 0 ; i < 800 ; i++) {
			for (int j = 0 ; j < nbNodes ; j++) {
		    	force_tx[j] = 0;
		    	force_ty[j] = 0;
		    }
			for (int j = 0 ; j < nbNodes-1 ; j++) {
				for (int k = j+1 ; k < nbNodes ; k++) {
					double dist = Point.distance(posx[j], posy[j], posx[k], posy[k]);
					if (dist == 0) dist = 0.00001;
					double vec_x = (posx[k] - posx[j]) / dist;
					double vec_y = (posy[k] - posy[j]) / dist;
					double rsx = repulseStrong(vec_x, dist);
					double rsy = repulseStrong(vec_y, dist);
		            if (rsx > 200 || rsy > 200) {
		        		@SuppressWarnings("unused")
		            	int gff = 0;
		            	gff++;
		            	rsx = repulseStrong(vec_x, dist);
						rsy = repulseStrong(vec_y, dist);
		            }  
					
					force_tx[j] -= rsx;
					force_tx[k] += rsx;
					force_ty[j] -= rsy;
					force_ty[k] += rsy;
					if (agh.getLinkContainer(j,k) != null || agh.getLinkContainer(k,j) != null) {
						double ax = attract(vec_x, dist);
						double ay = attract(vec_y, dist);
						force_tx[j] += ax;
						force_tx[k] -= ax;
						force_ty[j] += ay;
						force_ty[k] -= ay;
					} else {
			            double rx = repulse(vec_x, dist);
			            double ry = repulse(vec_y, dist);
			            if (rx > 200 || ry > 200) {
			        		@SuppressWarnings("unused")
			            	int g = 0;
			            	g++;
			            }        
			            force_tx[j] -= rx;
			            force_tx[k] += rx;
			            force_ty[j] -= ry;
			            force_ty[k] += ry;
					}
				}
			}
			for (int j = 0 ; j < nbNodes ; j++) {
			//	NodeContainer node = agh.getNodeContainer(j);
			//	int x = node.attribute("pos_x").intValue();
			//	int y = node.attribute("pos_y").intValue();
				if (force_tx[j] < 0) {
					force_tx[j] = Math.max(-10, force_tx[j]);
				}
				if (force_ty[j] < 0) {
					force_ty[j] = Math.max(-10, force_ty[j]);
				}
				if (force_tx[j] >= 0) {
					force_tx[j] = Math.min(10, force_tx[j]);
				}
				if (force_ty[j] >= 0) {
					force_ty[j] = Math.min(10, force_ty[j]);
				}
				if (force_tx[j] > 100) {
				}
				posx[j] = posx[j] + force_tx[j];
				posy[j] = posy[j] + force_ty[j];		
								

		    }
			
			animate(agh, posx, posy, nbNodes);
			
			// uncomment this for animation
	    
		}
		for (int j = 0 ; j < nbNodes ; j++) {	
			NodeContainer node = agh.getNodeContainer(j);	
			node.attribute("pos_x").setValue(posx[j]);
			node.attribute("pos_y").setValue(posy[j]);	
		}
		agh.setModificationEventEnabled(eventstatus, new CasualEvent(this));
	}		

	protected void animate(AbstractGraphHandler agh, double[] posx, double[] posy, int nbNodes) {
	}



	double attract(double var, double dist) {
	    return 0.2*(var*dist);
	}
	double repulse(double var, double dist) {
		if (dist == 0) {
			return Math.random()*100;
		}
	    return 15*(var/Math.pow(dist,0.9));
	}
	double repulseStrong(double v1, double dist) {
		if (dist < 0.01) {
			return Math.random()*100;
		}
	    if (v1 == 0) {
	        return 0;
	    }
	//    if (dist < 50) {
	        return v1*Math.pow(20, 100/dist);
	  //  }
	  //  return 0;
	}
}


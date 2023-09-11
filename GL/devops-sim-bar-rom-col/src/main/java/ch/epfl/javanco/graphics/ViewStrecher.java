package ch.epfl.javanco.graphics;

import java.awt.Dimension;
import java.awt.Rectangle;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class ViewStrecher {

	public static Rectangle strech(float fact, Rectangle view) {
		return strech(fact, fact, view);
	}

	public static Rectangle strech(float factx, float facty, Rectangle view) {
		int widthPast = view.width;
		int heightPast = view.height;
		view.width = Math.round(view.width * factx);
		view.height = Math.round(view.height * facty);
		view.x = Math.round((float)(widthPast  - view.width)  / (float)2) + view.x;
		view.y = Math.round((float)(heightPast - view.height) / (float)2) + view.y;
		return view;
	}

	public static Rectangle getBestFit(AbstractGraphHandler agh, Dimension d) {
		return getBestFit(agh, (int)d.getWidth(), (int)d.getHeight());
	}
	
	public static Rectangle getBestFit(AbstractGraphHandler agh, int targetWidth, int targetHeight) {
		// 1. Computation of the destination rectangle ratio
		// 2. Retrieval of the graph node space
		int[] extre = agh.getExtremitiesWithMargin();
		// 3. Computation of the input ratio
		int inputWidth = (extre[1]-extre[0]);
		int inputHeight = (extre[3]-extre[2]);
		return getBestFit(new Rectangle(extre[0],extre[2], inputWidth, inputHeight), targetWidth, targetHeight);
	}
	
	public static Rectangle getBestFit(Rectangle totalView, Dimension target) {
		if (target != null) {
			return getBestFit(totalView, target.width, target.height);
		} else {
			return getBestFit(totalView, new Dimension(totalView.width, totalView.height));
		}
	}
	
	public static Rectangle getBestFit(Rectangle totalView, int targetWidth, int targetHeight) {
		int inputWidth = totalView.width;
		int inputHeight = totalView.height;		

		double inputR = (double)(inputWidth)/(double)(inputHeight);
		double destR = (double)targetWidth/(double)targetHeight;
		if (inputR > destR) {
			// add vertical padding
					//int virtVertApperture = (targetHeight*inputHeight)/targetWidth;
			int virtVertApperture = (int)((double)inputWidth/destR);
			int diffInApperture = virtVertApperture - inputHeight;
			
			return new Rectangle(totalView.x, totalView.y-diffInApperture/2, inputWidth, virtVertApperture);
		} else {
					//int virtHorApperture = (targetWidth*inputWidth)/targetHeight;
			int virtHorApperture = (int)((double)inputHeight*destR);
			int diffInApperture = virtHorApperture - inputWidth;
			return new Rectangle(totalView.x-diffInApperture/2, totalView.y, virtHorApperture, inputHeight);
		}
	}
	
	public static Rectangle getMaintainedKeepingOrigin(Rectangle view, Dimension size) {
		int xp = Math.round((float)size.width*(float)view.height/(float)size.height);
		return new Rectangle(view.x, view.y, xp, view.height);
	}
	
	public static Rectangle getBestFit(GraphDisplayInformationSet set) {
		Rectangle view = set.getTotalView();
		Dimension d = set.getDisplaySize();
		float rap_x = (float)d.width/(float)view.width;
		float rap_y = (float)d.height/(float)view.height;
		float prop  = (float)d.width/(float)d.height;
		float prop2 = (float)view.width/(float)view.height;
		if (rap_x < rap_y) {
			// width bouge
			view = strech(1, 1f*prop2/prop, view);
			return view;
		} else {
			view = strech(1f*prop/prop2, 1, view);
			return view;
		}		
	}

	@Deprecated
	public static Rectangle getBestFit(Rectangle view, GraphDisplayInformationSet set) {
		if (view == null) {
			if (set.getNodeHCopy().size() == 0) {
				view = new Rectangle(0,0,10,10);
			} else {
				view = set.getTotalView();
			}
		}
		Dimension d = set.getDisplaySize();
		float rap_x = (float)d.width/(float)view.width;
		float rap_y = (float)d.height/(float)view.height;
		float prop  = (float)d.width/(float)d.height;
		float prop2 = (float)view.width/(float)view.height;
		if (rap_x < rap_y) {
			// width bouge
			view = strech(1, 1f*prop2/prop, view);
			return view;
		} else {
			view = strech(1f*prop/prop2, 1, view);
			return view;
		}
	}

	/*	public static Rectangle setNative(Rectangle view, GraphDisplayInformationSet set) {
		float factx = (float)set.getDisplaySize().width/(float)view.width;
		float facty = (float)set.getDisplaySize().height/(float)view.height;
		return strech(factx, facty, view);
	}
	public static Rectangle setTrunked(Rectangle view, GraphDisplayInformationSet set) {
		float rap_x = (float)set.getDisplaySize().width/(float)view.width;
		float rap_y = (float)set.getDisplaySize().height/(float)view.height;
		float prop  = (float)set.getDisplaySize().width/(float)set.getDisplaySize().height;
		float prop2 = (float)view.width/(float)view.height;
		if (rap_x < rap_y) {
			// width bouge
			view = strech(prop2/prop, 1, view);
			return view;
		} else {
			view = strech(1, prop2/prop, view);
			return view;
		}
	}	*/
}

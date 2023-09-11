package ch.epfl.general_libraries.charts;

import java.awt.Paint;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.general_libraries.charts.paints.Grid;
import ch.epfl.general_libraries.charts.paints.HorizontalScratches;
import ch.epfl.general_libraries.charts.paints.LeftScratches;
import ch.epfl.general_libraries.charts.paints.RightScratches;
import ch.epfl.general_libraries.charts.paints.Texture;
import ch.epfl.general_libraries.charts.paints.Uniform;
import ch.epfl.general_libraries.charts.paints.VerticalScratches;

public class TextureSupplier {
	private List<Class<? extends Texture>> list;
	private Iterator<Class<? extends Texture>> it;


	public TextureSupplier() {
		this.list = new LinkedList<Class<? extends Texture>>();
		this.fillList();
		this.it = list.iterator();
	}

	private void fillList() {
		this.list.add(Uniform.class);
		this.list.add(HorizontalScratches.class);
		this.list.add(VerticalScratches.class);
		this.list.add(LeftScratches.class);
		this.list.add(RightScratches.class);
		this.list.add(Grid.class);
	}

	public Paint getNext () {
		if (this.it.hasNext())  {
			try {
				Class<? extends Paint> cl = this.it.next();
				return cl.newInstance();
			} catch (Exception e) {
				return null;
			}
		} else {
			this.it = this.list.iterator();
			return this.getNext();
		}
	}
}

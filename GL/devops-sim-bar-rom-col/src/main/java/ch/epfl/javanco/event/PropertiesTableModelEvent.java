package ch.epfl.javanco.event;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import ch.epfl.javanco.ui.swing.XMLDefinitionEditor;

/**
 * This class extends {@link javax.swing.event.TableModelEvent} in order
 * to add information to the event (the name, value and type of the row which has changed).<br>
 * This is needed by {@link ch.epfl.javanco.gui.XMLDefinitionEditor.PropertiesTableModelListener}
 * because the {@link ch.epfl.javanco.gui.XMLDefinitionEditor.PropertiesTableModel}
 * content may be different from the attribute list of the element the table represents
 * (for example if an attribute has been deleted),
 * since the changes are executed on the element only once the user clicks on "ok".
 * Therefore the  row number of the table on which a change has occured is not enough to
 * find the corresponding attribute.
 * @author lchatela
 */
public class PropertiesTableModelEvent extends TableModelEvent {

	public static final long serialVersionUID = 0;

	private String name = null;
	private String value = null;
	private String attType = null;

	/**
	 * Creates a new <code>PropertiesTableModelEvent</code> event with information about
	 * the position in the table at which the event occurred and the attribute on which
	 * the event occurred.
	 * @param source The {@link TableModel} on which the Event initially occurred
	 * @param row The row on which the Event occurred
	 * @param column The column on which the Event occurred
	 * @param type The type of event (must be one of {@link TableModelEvent#INSERT},
	 * 		{@link TableModelEvent#DELETE}, {@link TableModelEvent#UPDATE})
	 * @param name The name of the related attribute
	 * @param value The value of the related attribute
	 * @param attType The type of the related attribute
	 */
	public PropertiesTableModelEvent(TableModel source, int row, int column, int type, String name, String value, String attType) {
		super(source, row, row, column, type);
		this.name = name;
		this.value = value;
		this.attType = attType;
	}

	/** Returns the name of the attribute on which the event occurred. */
	public String getAttName() {
		return name;
	}

	/** Returns the value of the attribute on which the event occurred. */
	public String getAttValue() {
		return value;
	}

	/** Returns the displayed type of the attribute on which the event occurred. */
	public String getAttType() {
		return attType;
	}

	/**
	 * Returns the value of the given column.
	 * @param col the desired column (should be one of <code>XMLDefinitionEditor#COL_VALUE</code>,
	 * 		<code>XMLDefinitionEditor#COL_TYPE</code>)
	 * @return the value of the given column.
	 */
	public String getCol(int col) {
		switch(col) {
		case XMLDefinitionEditor.COL_VALUE:
			return getAttValue();
		case XMLDefinitionEditor.COL_TYPE:
			return getAttType();
		default:
			throw new IllegalStateException("This field cannot be modified.");
		}
	}
}

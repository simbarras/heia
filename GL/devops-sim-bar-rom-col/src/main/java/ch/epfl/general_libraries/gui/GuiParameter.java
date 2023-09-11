package ch.epfl.general_libraries.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class GuiParameter<U> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	abstract public boolean isValidParam();
	abstract public U getParameter();
	//	abstract public void setParameter(U parameter);

	@SuppressWarnings("unchecked")
	public static <U> GuiParameter<U> createParameter(Class<U> c, Object[] possibleValues) {
		if (Integer.TYPE.isAssignableFrom(c) || Integer.class.isAssignableFrom(c)) {
			ParamInt param = new ParamInt();
			return (GuiParameter<U>) param;
		}
		else if (Double.TYPE.isAssignableFrom(c) || Double.class.isAssignableFrom(c)) {
			ParamDouble param = new ParamDouble();
			return (GuiParameter<U>) param;
		}
		else if (Float.TYPE.isAssignableFrom(c) || Float.class.isAssignableFrom(c)) {
			ParamFloat param = new ParamFloat();
			return (GuiParameter<U>) param;
		}
		else if (String.class.isAssignableFrom(c)) {
			ParamString param = new ParamString(possibleValues);
			return (GuiParameter<U>) param;
		}
		else if (Boolean.TYPE.isAssignableFrom(c) || Boolean.class.isAssignableFrom(c)) {
			ParamBoolean param = new ParamBoolean();
			return (GuiParameter<U>) param;
		}
		else {
			ParamObject param = new ParamObject(c);
			return param;
		}
	}

	static class ParamObject<X> extends GuiParameter<Object> implements ActionListener, /*NewObjectPanel.*/Notifiable<X> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JButton jButton = new JButton("configure");
		private final JLabel label = new JLabel();

		X createdParam;
		Class<? extends X> c;

		public ParamObject(Class<? extends X> c) {
			this.c = c;
			setLayout(new FlowLayout());
			add(jButton);
			add(label);
			jButton.addActionListener(this);
		}

		@Override
		public X getParameter() {
			return createdParam;
		}

		@Override
		public boolean isValidParam() {
			return (createdParam != null);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				new NewObjectPanel<X>(c, this);
			}
			catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		}

		public void notify_(X x) {
			createdParam = x;
			label.setText(x.getClass().getSimpleName());

		}
	}

	static class ParamInt extends GuiParameter<Integer> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField jTextField = new JTextField(6);
		public ParamInt() {
			setLayout(new BorderLayout());
			add(jTextField, BorderLayout.CENTER);
		}

		@Override
		public Integer getParameter() {
			try {
				return Integer.parseInt(jTextField.getText().trim());
			}
			catch (NumberFormatException e) {
				return null;
			}
		}

		//		@Override
		public void setParameter(Integer parameter) {
			jTextField.setText(parameter.toString());
		}
		@Override
		public boolean isValidParam() {
			try {
				Integer.parseInt(jTextField.getText().trim());
				return true;
			}
			catch (NumberFormatException e) {
				return false;
			}
			catch (NullPointerException e) {
				return false;
			}
		}
	}

	static class ParamDouble extends GuiParameter<Double> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField jTextField = new JTextField(6);
		public ParamDouble() {
			setLayout(new BorderLayout());
			add(jTextField, BorderLayout.CENTER);
		}

		@Override
		public Double getParameter() {
			try {
				return Double.parseDouble(jTextField.getText().trim());
			}
			catch (NumberFormatException e) {
				return null;
			}
		}

		//		@Override
		public void setParameter(Double parameter) {
			jTextField.setText(parameter.toString());
		}

		@Override
		public boolean isValidParam() {
			try {
				Double.parseDouble(jTextField.getText().trim());
				return true;
			}
			catch (NumberFormatException e) {
				return false;
			}
		}
	}

	static class ParamFloat extends GuiParameter<Float> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField jTextField = new JTextField(6);
		public ParamFloat() {
			setLayout(new BorderLayout());
			add(jTextField, BorderLayout.CENTER);
		}

		@Override
		public Float getParameter() {
			try {
				return Float.parseFloat(jTextField.getText().trim());
			}
			catch (NumberFormatException e) {
				return null;
			}
		}

		//		@Override
		public void setParameter(Float parameter) {
			jTextField.setText(parameter.toString());
		}

		@Override
		public boolean isValidParam() {
			try {
				Float.parseFloat(jTextField.getText().trim());
				return true;
			}
			catch (NumberFormatException e) {
				return false;
			}
			catch (NullPointerException e) {
				return false;
			}
		}

	}


	static class ParamString extends GuiParameter<String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField jTextField;
		private final JComboBox jBox;
		private boolean box = true;
		public ParamString(Object[] possibleValues) {
			setLayout(new BorderLayout());
			if (possibleValues != null) {
				jTextField = null;
				jBox = new JComboBox(possibleValues);
				add(jBox, BorderLayout.CENTER);
				box = true;
			} else {
				jBox = null;
				jTextField = new JTextField(6);
				add(jTextField, BorderLayout.CENTER);
				box = false;
			}
		}

		@Override
		public String getParameter() {
			if (box) {
				return (String)jBox.getSelectedItem();
			} else {
				return jTextField.getText();
			}
		}

		//		@Override
		public void setParameter(String parameter) {
			if (box) {
			} else {
				jTextField.setText(parameter);
			}
		}

		@Override
		public boolean isValidParam() {
			if (jTextField == null && jBox == null) {
				return false;
			}
			//	if (jTextField== null) return false;
			//	if (jTextField.getText().equals("test")) return false;
			return true;
		}

	}

	static class ParamBoolean extends GuiParameter<Boolean> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JCheckBox jCheckBox = new JCheckBox();
		public ParamBoolean() {
			setLayout(new BorderLayout());
			add(jCheckBox, BorderLayout.CENTER);
		}

		@Override
		public Boolean getParameter() {
			return jCheckBox.isSelected();
		}

		//		@Override
		public void setParameter(Boolean parameter) {
			jCheckBox.setSelected(parameter);
		}

		@Override
		public boolean isValidParam() {
			return true;
		}

	}



}

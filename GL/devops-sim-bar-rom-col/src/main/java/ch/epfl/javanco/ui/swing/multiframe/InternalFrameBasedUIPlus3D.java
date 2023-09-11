package ch.epfl.javanco.ui.swing.multiframe;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.GL3DViewer;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.graphics.GL3DViewer.NodeRepresentation;
import ch.epfl.javanco.ui.GlobalInterface;

import com.jogamp.opengl.util.Animator;


public class InternalFrameBasedUIPlus3D extends InternalFrameBasedUI {

	private JFrame view3d = null;
	private Animator animator = null;
	private GL3DViewer gl3dViewer = null;

	public InternalFrameBasedUIPlus3D(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
	}


	/*	@Override
	public void paintItToComponent(Graphics g, JComponent comp) {
		super.paintItToComponent(g, comp);
		if (gl3dViewer != null)
			gl3dViewer.setGraphDisplayInformationSet(getGraphDisplayInformationSet());
	}*/


	/**
	 * Returns the <code>JFrame</code> used to paint the 3d view. If no 3d view is display, returns null.
	 * 
	 * <BR>#author fmoulin
	 * @return the <code>JFrame</code> used to paint the 3d view if any, otherwise null
	 */
	public JFrame getView3dFrame() {
		return view3d;
	}

	/**
	 * Shows or hides a 3d view depending on the value of parameter <code>visible</code>.
	 * 
	 * <BR>#author fmoulin
	 * @param visible if true, makes the 3d view visible, otherwise hides it.
	 */
	public void setView3dVisible(boolean visible) {
		if (visible) {
			if (view3d == null) {
				view3d = new JFrame("3d view");
				view3d.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						setView3dVisible(false);
					}
				});
				gl3dViewer = new GL3DViewer(this);
				gl3dViewer.setGraphDisplayInformationSet(this.getGraphDisplayInformationSet());
				javax.media.opengl.awt.GLJPanel panel = new javax.media.opengl.awt.GLJPanel();
				panel.setFocusable(true);
				panel.addGLEventListener(gl3dViewer);
				Dimension dim = associatedFrame.getPreferredSize();
				dim.setSize(dim.width/2, dim.height/2);
				panel.setPreferredSize(dim);

				animator = new Animator();
				animator.add(panel);
				animator.start();

				JMenuBar menuBar3D = new JMenuBar();
				JMenu view = new JMenu("View");
				JMenuItem useTextures = new JCheckBoxMenuItem("Texture");
				useTextures.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setUseTexture(((JCheckBoxMenuItem)e.getSource()).isSelected());
					}

				});

				JMenuItem useTransparency = new JCheckBoxMenuItem("Transparency");
				useTransparency.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setUseTransparency(((JCheckBoxMenuItem)e.getSource()).isSelected());
					}

				});


				//----- Sphere ------
				JRadioButtonMenuItem sphere = new JRadioButtonMenuItem("Sphere");
				sphere.setSelected(true);
				sphere.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setNodeRepresentation(NodeRepresentation.SPHERE);
					}
				});


				//----- Cube ------
				JRadioButtonMenuItem cube = new JRadioButtonMenuItem("Cube");
				cube.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setNodeRepresentation(NodeRepresentation.CUBE);
					}
				});

				//----- Teapot ------
				JRadioButtonMenuItem teapot = new JRadioButtonMenuItem("Teapot");
				teapot.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setNodeRepresentation(NodeRepresentation.TEAPOT);
					}
				});

				//----- Cylinder ------
				JRadioButtonMenuItem cylinder = new JRadioButtonMenuItem("Cylinder");
				cylinder.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gl3dViewer.setNodeRepresentation(NodeRepresentation.CYLINDER);
					}
				});

				ButtonGroup group = new ButtonGroup();
				group.add(sphere);
				group.add(cube);
				group.add(teapot);
				group.add(cylinder);


				menuBar3D.add(view);
				view.add(useTextures);
				view.add(useTransparency);
				view.add(new JSeparator());
				view.add(sphere);
				view.add(cube);
				view.add(teapot);
				view.add(cylinder);
				view3d.setJMenuBar(menuBar3D);


				view3d.getContentPane().add(panel);
				view3d.pack();
				view3d.setVisible(true);
			}
		}
		else {
			if (view3d != null) {
				if (animator != null) {
					animator.stop();
					animator = null;
				}
				view3d.setVisible(false);
				view3d.dispose();
				view3d = null;
				gl3dViewer = null;
			}
		}

		menuBar.setView3dChecked(visible);
	}

}

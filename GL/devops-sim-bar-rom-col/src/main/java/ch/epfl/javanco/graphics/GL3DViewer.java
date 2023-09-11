/**
 * 
 */
package ch.epfl.javanco.graphics;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector3f;

import ch.epfl.general_libraries.graphics.ToolBox;
import ch.epfl.javanco.ui.swing.XMLDefinitionEditor;
import ch.epfl.javanco.ui.swing.multiframe.InternalFrameBasedUIPlus3D;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;


/**
 * Class used to draw a 3D view form a <code>GraphDisplayInformationSet</code>.
 * 
 * @author fmoulin
 * 
 */
public class GL3DViewer extends Util3DFunctions implements GLEventListener,
MouseListener, MouseWheelListener, MouseMotionListener, KeyListener {

	private boolean useTransparency = false;
	private boolean useTexture = false;

	/**
	 * Map that stores the textures
	 * <BR>#author fmoulin
	 */
	private final Map<String, Texture> textures = new HashMap<String, Texture>();
	/**
	 * Textures used in the last rendering process
	 * <BR>#author fmoulin
	 */
	private final Set<String> usedTextures = new HashSet<String>();

	private GraphDisplayInformationSet set = null;

	//private final Quat4f camQuat = new Quat4f(0, 0, 0, 1);

	//Informations about the camera's angles
	private float azimuthAngle = 0.0f;
	private float zenithAngle = 0.0f;
	private float oldAzimuthAngle = 0.0f;
	private float oldZenithAngle = 0.0f;
	private float deltaAzimuthAngle = 0.0f;
	private float deltaZenithAngle = 0.0f;
	private boolean cameraModeTopView = false;
	private boolean cameraSwitchingModes = false;
	private long cameraAnimationLastRedraw = 0;

	//Informations about the position the camera looks at
	private float lookAtX = 0.0f;
	private float lookAtY = 0.0f;
	private float lookAtZ = 0.0f;
	private float deltaLookAtX = 0.0f;
	private float deltaLookAtY = 0.0f;
	private float deltaLookAtZ = 0.0f;
	private float camDistance = 1500.0f;
	private float factorCamDistance = 1.0f;

	//Informations about the cursor
	private int curseurx = 0;
	private int curseury = 0;
	private int prevMouseX = 0;
	private int prevMouseY = 0;
	private int button = 0;
	private long lastClick = 0;

	// Informations about the keyboard
	private boolean keyZeroPressed = false;
	private boolean keyLeftPressed = false;
	private boolean keyRightPressed = false;
	private boolean keyUpPressed = false;
	private boolean keyDownPressed = false;
	private boolean keyPageDownPressed = false;
	private boolean keyPageUpPressed = false;
	private boolean keyShiftPressed = false;
	private boolean keyGPresssed = false;
	private boolean keyCPressed = false;

	// Various rendering settings
	private boolean drawGround = true;
	private boolean groundLookLikeChess = false;

	//Pointed node, link and layer
	private PaintableNode pointedNode = null;
	private PaintableLink pointedLink = null;
	private PaintableLayer pointedLayer = null;

	// Various stuff related to picking elements
	private Vector<Picked> idMapping = new Vector<Picked>();

	//Constants
	private static final GLU glu = new GLU();
	private static final GLUT glut = new GLUT();
	private static final float GRID_SIZE = 100.0f;
	private static final float GRID_RADIUS = 10000.0f;
	private static final float GRID_Z = - 0.1f;
	private static final float CLONE_SIZE_MODIFICATOR = 0.75f;

	// From last redraw
	private ProjectionSettings oldPs = null;
	private float oldViewWidth = Float.NaN;
	private float oldViewHeight = Float.NaN;
	private long oldTime = -1;

	// Loaded through System.getProperty
	private static boolean show3DDebugInfo = false;

	/**
	 * Defines the nodes' shape
	 * 
	 * @author fmoulin
	 */
	public enum NodeRepresentation {
		CUBE, SPHERE, TEAPOT, CYLINDER
	}


	/**
	 * Tests if textures are enable
	 * 
	 * <BR>#author fmoulin
	 * @return true If textures are enable, false otherwise
	 */
	public boolean isUseTexture() {
		return useTexture;
	}

	/**
	 * Sets if textures are enable
	 * 
	 * <BR>#author fmoulin
	 * @param useTexture
	 * If true enables textures; if false disables textures
	 */
	public void setUseTexture(boolean useTexture) {
		this.useTexture = useTexture;
	}

	/**
	 * Tests if transparency is used
	 * 
	 * <BR>#author fmoulin
	 * @return True if transparency is enable, false otherwise
	 */
	public boolean isUseTransparency() {
		return useTransparency;
	}

	/**
	 * Sets if transparency is enabled
	 * 
	 * <BR>#author fmoulin
	 * @useTransparency If true transparency is enable, if false transparency is
	 * disable
	 */
	public void setUseTransparency(boolean useTransparency) {
		this.useTransparency = useTransparency;
	}

	/**
	 * Returns the representation of a node
	 * 
	 * <BR>#author fmoulin
	 * @return A <code>NodeRepresentation</code> of the nodes
	 */
	public NodeRepresentation getNodeRepresentation() {
		return nodeRepresentation;
	}

	/**
	 * Sets the representation of a node
	 * 
	 * <BR>#author fmoulin
	 * @param nodeRepresentation
	 * The new representation of a node
	 */
	public void setNodeRepresentation(NodeRepresentation nodeRepresentation) {
		this.nodeRepresentation = nodeRepresentation;
	}

	private final InternalFrameBasedUIPlus3D suiP;
	private NodeRepresentation nodeRepresentation = NodeRepresentation.SPHERE;

	/**
	 * Creates a new <code>GL3DViewer</code> instance using the given
	 * <code>SinwgUIManager</code>
	 * 
	 * <BR>#author fmoulin
	 * @param swingUIManager
	 * A <code>SwingUIManager</code>
	 */
	public GL3DViewer(InternalFrameBasedUIPlus3D swingUI) {
		suiP = swingUI;

		// Well this code is no exactly correct but it's for debugging purpose only so it doesn't matter too much
		//
		// What it does:
		// Intercepts key events to know if they have been handled correctly
		boolean debug3dKeyEvents = System.getProperty("debug3dKeyEvents") != null;
		if (debug3dKeyEvents) {
			java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new java.awt.event.AWTEventListener() {
				public void eventDispatched(java.awt.AWTEvent event) {
					System.out.println(event.getSource());
				}
			}, java.awt.AWTEvent.KEY_EVENT_MASK);
		}
	}

	/**
	 * Sets the <code>GraphDisplayInformationSet</code>
	 * 
	 * <BR>#author fmoulin
	 * @param set
	 * The <code>GraphDisplayInformationSet</code> to set
	 */
	public void setGraphDisplayInformationSet(GraphDisplayInformationSet set) {
		this.set = set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable gLDrawable) {
		final GL2 gl = (GL2)gLDrawable.getGL();

		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glEnable(GL2.GL_MAP2_TEXTURE_COORD_2);
		gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
		gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
		
		if (gLDrawable instanceof GLJPanel) {
			GLJPanel p = (GLJPanel)gLDrawable;
			p.addMouseMotionListener(this);
			p.addMouseListener(this);
			p.addMouseWheelListener(this);
			p.addKeyListener(this);
		}
		if (gLDrawable instanceof GLCanvas) {
			GLCanvas p = (GLCanvas)gLDrawable;
			p.addMouseMotionListener(this);
			p.addMouseListener(this);
			p.addMouseWheelListener(this);
			p.addKeyListener(this);
		}

		show3DDebugInfo = System.getProperty("debug3d") != null;
	}


	@Override
	public void doCam(GL2 gl) {
		computeKeyContribution();
		cameraAnimation();

		camDistance *= factorCamDistance;
		zenithAngle += deltaZenithAngle;
		azimuthAngle += deltaAzimuthAngle;

		lookAtX += deltaLookAtX;
		lookAtY += deltaLookAtY;
		lookAtZ += deltaLookAtZ;
		cameraBoundsCheck();

		deltaZenithAngle = 0;
		deltaAzimuthAngle = 0;
		factorCamDistance = 1;

		deltaLookAtX = 0;
		deltaLookAtY = 0;
		deltaLookAtZ = 0;

		gl.glTranslatef(0.0f, 0.0f, -camDistance);

		gl.glRotatef(-zenithAngle, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-azimuthAngle, 0.0f, 0.0f, 1.0f);

		drawCube(gl, new Vector3f(1, 1, 1), null);

		gl.glTranslatef(-lookAtX, -lookAtY, -lookAtZ);
	}

	/**
	 * Equivalent to a C-style structure, this class's sole purpose is to carry more than one value.
	 * The associated values are the parameters taken by gluPerspective(...)
	 * @author pvogt
	 */
	private class ProjectionSettings {
		/**
		 * Specifies the	field of view angle, in	degrees, in the y	direction.
		 * @author pvogt
		 */
		public float fovy;
		/**
		 * Specifies the	aspect ratio that determines the field of view in the x direction. The aspect ratio	is the ratio	of x (width) to	y (height).
		 * @author pvogt
		 */
		public float aspect;
		/**
		 * Specifies the	distance from the viewer to the	near clipping plane (always positive).
		 * @author pvogt
		 */
		public float zNear;
		/**
		 * Specifies the	distance from the viewer to the	far clipping plane (always positive).
		 * @author pvogt
		 */
		public float zFar;
	}

	/**
	 * Retrieve the projections settings used here.
	 * @param gl The OpenGL object
	 * @author pvogt
	 */
	private ProjectionSettings getProjectionSettings(GL gl) {
		ProjectionSettings ps = new ProjectionSettings();

		final IntBuffer viewPort = Buffers.newDirectIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort);

		final float viewWidth = viewPort.get(2) - viewPort.get(0);
		final float viewHeight = viewPort.get(3) - viewPort.get(1);
		oldViewWidth = viewWidth;
		oldViewHeight = viewHeight;

		ps.fovy = 50.0f;
		ps.aspect = viewWidth / viewHeight;
		ps.zNear = 1.0f;
		ps.zFar = 30000.0f;
		return ps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable gLDrawable) {
		final GL2 gl = (GL2)gLDrawable.getGL();

		//Without a set, we can't draw anything
		if (set == null) {
			System.out.println("eiorjgioeji");
			return;
		}

		//Initialisations
		usedTextures.clear();
		computePicked(gl);


		//Set the background
		final Vector3f bcolor = colorToVector3f(set.backgroundColor);
		gl.glClearColor(bcolor.x, bcolor.y, bcolor.z, 0.0f);

		//Clear all and enable useful thinks
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glEnable(GL2.GL_LIGHTING);

		//Set the perspective
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		ProjectionSettings ps = getProjectionSettings(gl);
		if (ps == null) {
			throw new NullPointerException("PS cannot be null");
		}
		oldPs = ps;
		final boolean ortho = (cameraModeTopView && azimuthAngle == 0.0f && zenithAngle == 0.0f);
		if (ortho) {
			// Here we try to be as close as possible to the perspective view
			// so that the user does not notice the transition too much.
			float top = (camDistance + lookAtZ) * (float) Math.tan(ps.fovy * (Math.PI / 360.0));
			float bottom = -top;
			float left = bottom * ps.aspect;
			float right = top * ps.aspect;
			gl.glOrtho(left, right, bottom, top, ps.zNear, ps.zFar);
		} else {
			glu.gluPerspective(ps.fovy, ps.aspect, ps.zNear, ps.zFar);
		}

		// Set the light
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glEnable(GL2.GL_LIGHT0);

		final int[] lightPos = { 10, 10, 10, 1 };
		gl.glLightiv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
		//drawCube(gl, new Vector3f(10f, 10f, 10f), 0.1f, new Vector3f(0, 0, 1));

		// Moves the scene for the camera
		doCam(gl);

		// Draw the ground
		if (drawGround) {
			drawGround(gl, ps);
		}

		// Draw the axis
		gl.glColor3f(1.0f, 0f, 0f);
		drawTriangle(gl, new Vector3f(1, 0, 0.1f), new Vector3f(0, 1, 0),
				new Vector3f(10, 0, 0));

		gl.glColor3f(0f, 1f, 0f);
		drawTriangle(gl, new Vector3f(1f, 0f, 0.1f),
				new Vector3f(0f, 0f, 0.1f), new Vector3f(0f, 10f, 0f));

		List<PaintableNode> nodes = set.getNodeHCopy();

		// Draws the nodes
		for (final PaintableNode node : nodes) {
			if (node != null) {
				if (pointedNode != null && node.id == pointedNode.id) {
					drawOutlinedNode(gl, node);
				} else {
					drawNode(gl, node);
				}
			}
		}

		// Draws the cloned nodes
		for (final PaintableNode node : set.nodesHClones) {
			if (node != null) {
				if (pointedNode != null && node.id == pointedNode.id) {
					drawOutlinedClonedNode(gl, node);
				} else {
					drawClonedNode(gl, node);
				}
			}
		}

		// Draws the links
		for (final PaintableLink link : set.getLinksHCopy()) {
			if (link == pointedLink) {
				drawOutlinedLink(gl, nodes, link);
			} else {
				drawLink(gl, nodes, link);
			}
		}

		// Now we need to use some transparency effect
		gl.glEnable (GL.GL_BLEND);
		gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// Draw the layers
		Vector3f layersColorTop = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f layersColorBottom = new Vector3f(1.0f, 0.0f, 0.0f);
		for (final PaintableLayer layer : set.layers) {
			drawLayer(gl, layer.getLayerZ(), set, layersColorTop, layersColorBottom);
		}

		// We can disable transparency again since we don't need it for
		// everything
		gl.glDisable (GL.GL_BLEND);

		// When debugging show
		if(show3DDebugInfo) {
			gl.glLoadIdentity();
			drawDebugInfoText(gLDrawable);
		}

		//Free unused textures
		freeUnusedTextures(gl);

		oldTime = new Date().getTime();
	}

	/**
	 * Returns the time elapsed in milliseconds since the last redraw
	 * @author pvogt
	 */
	private long getElapsedTime() {
		if (oldTime != -1) {
			long time = new Date().getTime();
			return time - oldTime;
		} else {
			return 0;
		}
	}

	/**
	 * Displays some debug text in the 3D view. This is usefull for discovering errors with the camera
	 * @param gLDrawable The surface where to draw the debug text
	 * @author pvogt
	 */
	private void drawDebugInfoText(GLAutoDrawable gLDrawable) {
		final TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
		textRenderer.beginRendering(gLDrawable.getWidth(), gLDrawable.getHeight());
		textRenderer.setColor(0.0f, 1.0f, 0.0f, 1f);
		int y = 10;
		textRenderer.draw("zenithAngle: " + zenithAngle, 10, y);
		y += 15;
		textRenderer.draw("memorized zenithAngle: " +  oldZenithAngle, 10, y);
		y += 15;
		textRenderer.draw("azimuthAngle: " + azimuthAngle, 10, y);
		y += 15;
		textRenderer.draw("memorized azimuthAngle: " +  oldAzimuthAngle, 10, y);
		y += 15;
		textRenderer.draw("lookat x,y,z: " + lookAtX + "," + lookAtY + "," + lookAtZ, 10, y);
		y += 15;
		textRenderer.draw("switching mode: " + (cameraSwitchingModes ? "yes" : "no"), 10, y);
		y += 15;
		textRenderer.draw("camera mode: " + (cameraModeTopView ? "top view" : "normal"), 10, y);
		y += 15;
		textRenderer.draw("camera distance: " + camDistance, 10, y);
		textRenderer.endRendering();
	}

	/**
	 * Removes from the set of all used textures the ones that are no more used
	 * 
	 * <BR>#author fmoulin
	 */
	private void freeUnusedTextures(GL gl) {
		final Set<String> allTextures = new HashSet<String>();
		allTextures.addAll(textures.keySet());
		if (usedTextures.size() < allTextures.size()) {
			allTextures.removeAll(usedTextures);
			for (final String t : allTextures) {
				/*final Texture tex = */textures.remove(t);
				//tex.dispose(gl);
			}
		}
	}

	/**
	 * Computes if a node or a link is pointed by the mouse. The result is
	 * stored in <code>pointedNode</code> and <code>pointedLink</code>
	 * 
	 * @param gl
	 * A <code>GL</code>
	 * @author fmoulin
	 * @author pvogt
	 */
	private void computePicked(GL2 gl) {
		initPickedColors();
		gl.glLoadIdentity();
		doCam(gl);

		gl.glClearColor(0f, 0f, 0f, 0.0f);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		gl.glDisable(GL.GL_DITHER);
		gl.glDisable(GL2.GL_LIGHTING);

		// Since we want the nodes to be selected rather than the
		// layers whenever possible, we draw them first here.
		PaintableLayer[] layers = new PaintableLayer[0];
		layers = set.layers.toArray(layers);
		for (int i = 0; i < layers.length; i++) {
			Vector3f col = pickedToNewColor(PickedType.LAYER, i);
			drawLayer(gl, layers[i].getLayerZ(), set, col, col);
		}

		// We clear the depth buffer again, so the layers are
		// "sent to the background" and nodes will overwrite them
		// in all cases.
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		List<PaintableNode> nodes = set.getNodeHCopy();
		for (int i = 0; i < nodes.size(); i++) {
			PaintableNode node = nodes.get(i);
			assert i != node.id : "TODO: Fix the problem with cloned nodes that has just appeared due to a change of the implementation of nodesH";
			if (node != null) {
				Vector3f col = pickedToNewColor(PickedType.NODE, i);
				drawNode(gl, node, col, 1f, false);
			}
		}

		// We also need to detect if a cloned node has been selected
		for (final PaintableNode node : set.nodesHClones) {
			if (node != null) {
				Vector3f col = pickedToNewColor(PickedType.NODE, node.id);
				drawNode(gl, node, col, 1f * CLONE_SIZE_MODIFICATOR, false);
			}
		}

		List<PaintableLink> links = set.getLinksHCopy();
		for (int i = 0; i < links.size(); i++) {
			Vector3f col = pickedToNewColor(PickedType.LINK, i);
			drawLink(gl, nodes, links.get(i), 1f, col);
		}

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_DITHER);

		final IntBuffer viewport = Buffers.newDirectIntBuffer(4);
		final ByteBuffer pixel = Buffers.newDirectByteBuffer(3);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);

		gl.glReadPixels(curseurx, viewport.get(3) - curseury, 1, 1, GL.GL_RGB,
				GL.GL_UNSIGNED_BYTE, pixel);

		final short r = (short) (pixel.get() & 0xFF);
		final short g = (short) (pixel.get() & 0xFF);
		final short b = (short) (pixel.get() & 0xFF);

		Picked p = colorToPickedElement(r, g, b);

		pointedNode = null;
		pointedLink = null;
		pointedLayer = null;
		switch(p.pt) {
		case NODE:
			pointedNode = nodes.get(p.id);
			break;
		case LINK:
			pointedLink = links.get(p.id);
			break;
		case LAYER:
			pointedLayer = layers[p.id];
			break;
		default:
			// Nothing to do
		}
	}

	/**
	 * Should be called before the first color is picked to avoid memory leaks
	 */
	private void initPickedColors() {
		idMapping.clear();
	}

	/**
	 * The type of the object to be picked
	 */
	enum PickedType {
		NODE,
		LINK,
		LAYER,
		NONE
	}

	/**
	 * A tuple containing an id and a type of a picked object
	 * @author pvogt
	 */
	private class Picked {
		/**
		 * Constructor
		 * @param pt The type of the picked object
		 * @param id The id of the object
		 */
		public Picked(PickedType pt, int id) {
			this.pt = pt;
			this.id = id;
		}

		/**
		 * The type of the picked object
		 */
		public PickedType pt;

		/**
		 * The id of the object
		 */
		public int id;
	}

	/**
	 * Gets a vector containing a RGB color whith R,G and B in the
	 * range [0,1] and remembers the type and id of the object
	 * associated with it.
	 * @param pt The type of the picked object
	 * @param id The id of the object
	 * @author pvogt
	 */
	private Vector3f pickedToNewColor(PickedType pt, int id) {
		return pickedToNewColor(new Picked(pt, id));
	}

	/**
	 * Gets a vector containing a RGB color whith R,G and B in the
	 * range [0,1] and remembers the type and id of the object
	 * associated with it.
	 * @param p The <code>Picked</code> to remember
	 * @author pvogt
	 */
	private Vector3f pickedToNewColor(Picked p) {
		idMapping.addElement(p);
		int i = idMapping.size();
		int pickRed = i / (256 * 256);
		int pickGreen = (i - pickRed * 256 * 256) / 256;
		int pickBlue = i - pickRed * 256 * 256 - pickGreen * 256;
		assert pickRed < 256 : "Not enough colors left";

		Vector3f color = new Vector3f(pickRed, pickGreen, pickBlue);
		color.scale(1 / 255f);

		return color;
	}

	/**
	 * Retrieves the <code>Picked</code> associated with the give
	 * color.
	 * @param r The Red component of the color in the range [0,255]
	 * @param g The Green component of the color in the range [0,255]
	 * @param b The Blue component of the color in the range [0,255]
	 * @author pvogt
	 */
	private Picked colorToPickedElement(short r, short g, short b) {
		try {
			Picked p = idMapping.get(256 * 256 * r + 256 * g + b - 1);
			if(p == null) {
				return new Picked(PickedType.NONE, -1);
			} else {
				return p;
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			return new Picked(PickedType.NONE, -1);
		}
	}

	/**
	 * Draws a node
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param node
	 * Node to draw
	 */
	private void drawNode(GL2 gl, PaintableNode node) {
		drawNode(gl, node, colorToVector3f(node.color), 1f, true);
	}

	/**
	 * Draws a cloned node
	 * @param gl The OpenGL object
	 * @param node The cloned node to draw
	 * @author pvogt
	 */
	private void drawClonedNode(GL2 gl, PaintableNode node) {
		drawNode(gl, node, colorToVector3f(node.color), 1f * CLONE_SIZE_MODIFICATOR, true);
	}

	/**
	 * Draws a node
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param node
	 * Node to draw
	 * @param color
	 * Node color
	 * @param scale
	 * Scale factor for the size
	 * @param icon
	 * If false nodes are drawn without any texture, if true display
	 * of textures depends on <code>useTexture</code>
	 */
	private void drawNode(GL2 gl, PaintableNode node, Vector3f color, float scale, boolean icon) {
		Texture tex = null;

		//Load textures if needed
		if (useTexture && icon && node.getIconImage() != null) {
			tex = textures.get(node.icon);
			usedTextures.add(node.icon);

			if (tex == null) {
				tex = loadTexture(gl, node.icon);
				textures.put(node.icon, tex);
			}
		}


		final float size = scale * (node.size / 2f);
		final Vector3f center = new Vector3f(node.posX, -node.posY, node.posZ + size);

		if (nodeRepresentation == NodeRepresentation.SPHERE) {
			if (!useTransparency && tex != null) {
				drawSphere(gl, glu, center, size * 0.99f, color, null);
			}
			drawSphere(gl, glu, center, size, color, tex);
		} else if (nodeRepresentation == NodeRepresentation.CUBE) {
			if (!useTransparency && tex != null) {
				drawCube(gl, center, size * 0.99f, color, null);
			}
			drawCube(gl, center, size, color, tex);
		} else if (nodeRepresentation == NodeRepresentation.TEAPOT) {
			if (!useTransparency && tex != null) {
				drawTeapot(gl, glut, center, size * 0.99f, color, null);
			}
			drawTeapot(gl, glut, center, size, color, tex);
		}	else if (nodeRepresentation == NodeRepresentation.CYLINDER) {
			final Vector3f pt1 = new Vector3f(center.x, center.y, center.z - size);
			final Vector3f pt2 = new Vector3f(center.x, center.y, center.z + size);
			if (!useTransparency && tex != null) {
				drawCylinder(gl, glu, pt1, pt2, size * 0.99f, 10, 2, color, tex, true);
			}
			drawCylinder(gl, glu, pt1, pt2, size, 10, 2, color, tex, true);
		}
	}

	/**
	 * Draws a layer
	 * @param gl The OpenGL object
	 * @param level The Z attribute of the Layer being drawn
	 * @param set The <code>GraphDisplayInformationSet</code> containing all informations
	 * @param colorTop The color of the layer when viewed from above
	 * @param colorBottom The color of the layer when viewed from below
	 * @author pvogt
	 */
	private void drawLayer(GL2 gl,int level, GraphDisplayInformationSet set, Vector3f colorTop, Vector3f colorBottom) {
		Vector3f bl = new Vector3f(set.layerGlobals.layersMinX, set.layerGlobals.layersMinY, level);
		Vector3f br = new Vector3f(set.layerGlobals.layersMaxX, set.layerGlobals.layersMinY, level);
		Vector3f tl = new Vector3f(set.layerGlobals.layersMinX, set.layerGlobals.layersMaxY, level);
		Vector3f tr = new Vector3f(set.layerGlobals.layersMaxX, set.layerGlobals.layersMaxY, level);
		// Top
		gl.glColor4f(colorTop.x, colorTop.y, colorTop.z, 0.1f);
		drawTriangle(gl, tr, bl, tl);
		drawTriangle(gl, tr, br, bl);
		// Bottom
		gl.glColor4f(colorBottom.x, colorBottom.y, colorBottom.z, 0.1f);
		drawTriangle(gl, tr, tl, bl);
		drawTriangle(gl, tr, bl, br);
	}

	/**
	 * Draws the ground.
	 * @param fovy The same param that has to be passed to gluPerspective
	 * @param aspect The same param that has to be passed to gluPerspective
	 */
	private void drawGround(GL2 gl, ProjectionSettings ps) {
		float alpha = (float) (Math.PI / 180.0f) * zenithAngle;
		float beta = (float) (Math.PI / 180.0f) * (Math.max(ps.fovy, ps.fovy * ps.aspect) / 2.0f);
		float height = (float) Math.cos(alpha) * camDistance;
		float offset = (float) Math.sin(alpha) * camDistance;
		int optimalNbSquares = Integer.MAX_VALUE;
		if (alpha + beta < (float) Math.PI / 2.0f) {
			optimalNbSquares = (int) (((float) Math.tan(alpha + beta) * height - offset) / GRID_SIZE) + 1;
		}
		// We multiply it by sqrt(2) to compensate with the fact that we are on a circle
		final int radiusNbSquares = Math.min( (int) (optimalNbSquares * Math.sqrt(2.0f)), (int) (GRID_RADIUS / GRID_SIZE));

		final int baseX = (int) (lookAtX / GRID_SIZE);
		final int baseY = (int) (lookAtY / GRID_SIZE);

		for (int ix = -radiusNbSquares; ix < radiusNbSquares; ix++) {
			for(int iy = -radiusNbSquares; iy < radiusNbSquares; iy++) {
				if (ix * ix + iy * iy > radiusNbSquares * radiusNbSquares) {
					continue;
				}
				final Vector3f bl = new Vector3f( (baseX + ix) * GRID_SIZE, (baseY + iy) * GRID_SIZE, GRID_Z);
				final Vector3f br = new Vector3f( (baseX + ix + 1) * GRID_SIZE, (baseY + iy) * GRID_SIZE, GRID_Z);
				final Vector3f tl = new Vector3f( (baseX + ix) * GRID_SIZE, (baseY + iy + 1) * GRID_SIZE, GRID_Z);
				final Vector3f tr = new Vector3f( (baseX + ix + 1) * GRID_SIZE, (baseY + iy + 1) * GRID_SIZE, GRID_Z);
				final boolean black = groundLookLikeChess && (Math.abs(ix + iy + baseX + baseY) % 2 == 0);
				if (black) {
					gl.glColor3f(0.0f, 0.0f, 0.0f);
				} else {
					gl.glColor3f(1.0f, 1.0f, 1.0f);
				}
				drawTriangle(gl, tr, bl, tl);
				drawTriangle(gl, tr, br, bl);
			}
		}
	}

	/**
	 * Draws an outlined node
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param node
	 * Node to draw
	 */
	private void drawOutlinedNode(GL2 gl, PaintableNode node) {
		// drawCube(gl, new Vector3f(node.posX/10f, -node.posY/10f, 0f),
		// 1f*(node.size/10f), new Vector3f(0, 1, 0));
		final Vector3f color = colorToVector3f(ToolBox
				.getInverseColor(node.color));
		drawNode(gl, node, color, 1.2f, true);
	}

	private void drawOutlinedClonedNode(GL2 gl, PaintableNode node) {
		final Vector3f color = colorToVector3f(ToolBox.getInverseColor(node.color));
		drawNode(gl, node, color, 1.2f * CLONE_SIZE_MODIFICATOR, true);
	}

	/**
	 * Draws an outlined link
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param Link
	 * link to draw
	 */
	private void drawOutlinedLink(GL2 gl, List<PaintableNode> nodes, PaintableLink link) {
		if (link.color != null) {
			final Vector3f color = colorToVector3f(link.color);
			drawLink(gl, nodes, link, 1.5f, true, color);
		} else {
			drawLink(gl, nodes, link, 1.5f, true, null);
		}
	}

	/**
	 * Draws the routing of a link if it exists.
	 * @param gl The OpenGL object
	 * @param link The link whose routing is to be drawn
	 * @author pvogt
	 */
	private void drawRouting(GL2 gl, PaintableLink link) {
		assert(link.routing != null);
		for(int i = 0; i < link.routing.length; i++) {
			drawRouting(gl, link, i);
		}
	}

	/**
	 * Draws one component of the routing of a link.
	 * @param gl The OpenGL object
	 * @param link The link whose routing is to be drawn
	 * @param routeIndex The index of the component
	 * @author pvogt
	 */
	private void drawRouting(GL2 gl, PaintableLink link, int routeIndex) {
		int[] routing = link.routing[routeIndex];
		for(int i = 0; i < routing.length - 1; i++) {
			drawRouteHop(gl, link, routing[i + 0], routing[i + 1], i == 0, i == routing.length - 2);
		}
	}

	/**
	 * Draws one hop of a component of the routing of a link.
	 * @param gl The OpenGL object
	 * @param link The link whose routing is to be drawn
	 * @param nodeIdA The node where it starts
	 * @param nodeIdB The node where it ends
	 * @param isStart Whether this is the start of the rounting component
	 * @param isEnd Whether this is the end of the rounting component
	 * @author pvogt
	 */
	private void drawRouteHop(GL2 gl, PaintableLink link, int nodeIdA, int nodeIdB, boolean isStart, boolean isEnd) {
		// Since we want to use CubicCurve2D we have to go from 3D to 2D and
		// then back the other way round.
		PaintableNode nodeA;
		PaintableNode nodeB;
		List<PaintableNode> nodes = set.getNodeHCopy();
		nodeA = nodes.get(nodeIdA);
		nodeB = nodes.get(nodeIdB);


		// First we need the distance between our 2 points
		float deltaAB_X = nodeB.posX - nodeA.posX;
		float deltaAB_Y = nodeB.posY - nodeA.posY;
		float distAB = (float) Math.sqrt(deltaAB_X * deltaAB_X + deltaAB_Y * deltaAB_Y);

		// Constants
		float speed = 1.6f;
		float intermediateNodeFactor = 3.5f;
		float endNodesFactor = 3.0f;
		float flatnessFactor = 0.01f;
		float widthFactor = 0.9f;

		float factorA = (isStart ? endNodesFactor : intermediateNodeFactor);
		float factorB = (isEnd ? endNodesFactor : intermediateNodeFactor);
		float flatness = flatnessFactor * (nodeA.size + nodeB.size) / 2.0f;

		// The Y coordinate will become the Z coordinate later
		float p1x = 0.0f;
		float p1y = 0.0f + factorA * nodeA.size;
		float cp1x = 0.0f + speed * nodeA.size;
		float cp1y = (speed + factorA) * nodeA.size;
		float cp2x = distAB - speed * nodeB.size;
		float cp2y = (speed + factorB) * nodeB.size;
		float p2x = distAB;
		float p2y = 0.0f + factorB * nodeB.size;

		CubicCurve2D c = new CubicCurve2D.Float(p1x, p1y, cp1x, cp1y, cp2x, cp2y, p2x, p2y);

		// Now we use a path iterator to get the points we want to draw
		PathIterator p = c.getPathIterator(null, flatness);
		float[][] coord = new float[2][6];
		int current = 0;
		float pt1x = 0.0f;
		float pt1y = 0.0f;
		float pt2x = 0.0f;
		float pt2y = 0.0f;
		while(!p.isDone()) {
			current = 1 - current;
			int type = p.currentSegment(coord[current]);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				pt2x = coord[current][0];
				pt2y = coord[current][1];
				break;
			case PathIterator.SEG_LINETO:
				pt1x = pt2x;
				pt1y = pt2y;
				pt2x = coord[current][0];
				pt2y = coord[current][1];
				// Now we need to convert the 2D coordinates to 3D again
				Vector3f p1 = new Vector3f(
						nodeA.posX + deltaAB_X * pt1x / distAB,
						-(nodeA.posY + deltaAB_Y * pt1x / distAB),
						link.layerZ + pt1y
				);
				Vector3f p2 = new Vector3f(
						nodeA.posX + deltaAB_X * pt2x / distAB,
						-(nodeA.posY + deltaAB_Y * pt2x / distAB),
						link.layerZ + pt2y
				);
				Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f);
				drawCylinder(gl, glu, p1, p2, link.width * widthFactor, 10, 1, color, null);
				break;
			default:
				assert(false);
				break;
			}
			p.next();
		}
	}

	/**
	 * Draws a link
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param link
	 * Link to draw
	 */
	private void drawLink(GL2 gl, List<PaintableNode> nodes, PaintableLink link) {
		if (link.color != null)
			drawLink(gl, nodes, link, 1f, colorToVector3f(link.color));
	}

	private void drawLink(GL2 gl, List<PaintableNode> nodes, PaintableLink link, float scale, Vector3f col) {
		drawLink(gl, nodes, link, scale, false, col);
	}

	/**
	 * Draws a link
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param link
	 * Link to draw
	 * @param scale
	 * Scale factor for the width
	 * @param col
	 * Link color
	 */
	private void drawLink(GL2 gl, List<PaintableNode> nodes, PaintableLink link, float scale, boolean selected, Vector3f col) {
		try {
			if (nodes.size() <= Math.max(link.orig, link.dest)) {
				return;
			}
			final PaintableNode n1 = nodes.get(link.orig);
			final PaintableNode n2 = nodes.get(link.dest);

			//float z1 = link.layerZ + n1.size / 2.0f;
			//float z2 = link.layerZ + n2.size / 2.0f;
			
			float z1 = n1.posZ + n1.size / 2.0f;
			float z2 = n2.posZ + n2.size / 2.0f;
	
			final Vector3f pt1 = new Vector3f(n1.posX, -n1.posY, z1);
			final Vector3f pt2 = new Vector3f(n2.posX, -n2.posY, z2);
	
			if (col != null)
				drawCylinder(gl, glu, pt1, pt2, link.width * scale, 10, 2, col, null);
	
			if (selected && link.routing != null) {
				drawRouting(gl, link);
			}
		}
		catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable,
	 * boolean, boolean)
	 */
/*	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {

	}*/


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable,
	 * int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		final GL2 gl = (GL2)gLDrawable.getGL();
		if (height <= 0) {
			height = 1;
		}
		final float h = (float) width / (float) height;

		textures.clear();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50.0f, h, 1.0, 1000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * Displays a window to modify the properties of <code>object</code>
	 * 
	 * <BR>#author fmoulin
	 * @param object
	 * Object to modify
	 */
	private void displayProperties(Clickable object) {
		final XMLDefinitionEditor editor = new XMLDefinitionEditor(
				suiP.getView3dFrame(), true, suiP.getAssociatedAbstractGraphHandler().isEditable());
		if (object != null) {
			editor.editElement(object.getElementContainer());
		} else {
			editor.editElement(suiP.getAssociatedAbstractGraphHandler().getEditedLayer());
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();
		// Dimension size = e.getComponent().getSize();

		if (button == MouseEvent.BUTTON1) {
			float realHeight = 2 * (oldPs.zNear + camDistance + lookAtZ * (float) Math.cos(zenithAngle * Math.PI / 180)) * (float) Math.tan(oldPs.fovy * (Math.PI / 360.0));
			float realWidth = realHeight * oldPs.aspect;

			final float dx = ((x - prevMouseX) / oldViewWidth) * realWidth;
			final float dy = ((prevMouseY - y) / oldViewHeight) * realHeight;
			moveCameraRelative(dx, dy, 0.0f);
		} else if(button == MouseEvent.BUTTON3) {
			rotateCameraLeft(x - prevMouseX);
			rotateCameraDown(y - prevMouseY);
		}
		prevMouseX = x;
		prevMouseY = y;
	}

	/**
	 * Rotates the camera to the left, or to the right if the angle is negative
	 * @param angle The angle in degrees
	 * @author pvogt
	 */
	private void rotateCameraLeft(double angle) {
		if(cameraSwitchingModes) {
			return;
		}
		deltaAzimuthAngle += angle;
		cameraModeTopView = false;
	}

	/**
	 * Rotates the camera down, or up if the angle is negative
	 * @param angle The angle in degrees
	 * @author pvogt
	 */
	private void rotateCameraDown(double angle) {
		if(cameraSwitchingModes) {
			return;
		}
		deltaZenithAngle += angle;
		cameraModeTopView = false;
	}

	/**
	 * Ensure that all camera parameters have correct values.
	 * This means the azimuth angle has to be in range [-180,180]
	 * the zenith angle has to be in the range [0,90]
	 * lookAtZ can not be negative.
	 * @author pvogt
	 */
	private void cameraBoundsCheck() {
		while (azimuthAngle > 180.0f) {
			azimuthAngle -= 360.0f;
		}
		while (azimuthAngle < -180.0f) {
			azimuthAngle += 360.0f;
		}

		if(zenithAngle > 90.0f) {
			zenithAngle = 90.0f;
		} else if(zenithAngle < 0.0f) {
			zenithAngle = 0.0f;
		}

		if(lookAtZ < 0.0f) {
			lookAtZ = 0.0f;
		}
	}

	/**
	 * Move the point the camera is looking at relative to the current angle
	 * settings.
	 * @param x Movement in apparent x direction.
	 * @param y Movement in apparent y direction.
	 * @param z Movement in apparent z direction.
	 */
	private void moveCameraRelative(float x, float y, float z) {
		// We need to convert back from viewed coordinates to real ones
		// Step 1: Revert zenith angle transformation
		float cosZenithAngle = (float) Math.cos(zenithAngle * Math.PI / 180);
		float sinZenithAngle = (float) Math.sin(zenithAngle * Math.PI / 180);
		float x_ = x;
		float y_ = cosZenithAngle * y - sinZenithAngle * z;
		float z_ = sinZenithAngle * y + cosZenithAngle * z;
		// Step 2: Revert azimuth angle transformation
		float cosAzimuthAngle = (float) Math.cos(azimuthAngle * Math.PI / 180);
		float sinAzimuthAngle = (float) Math.sin(azimuthAngle * Math.PI / 180);
		float x__ = cosAzimuthAngle * x_ - sinAzimuthAngle * y_;
		float y__ = sinAzimuthAngle * x_ + cosAzimuthAngle * y_;
		float z__ = z_;
		moveCamera(x__, y__, z__);
	}

	/**
	 * Move the point the camera is looking at.
	 * @param x Movement in apparent x direction.
	 * @param y Movement in apparent y direction.
	 * @param z Movement in apparent z direction.
	 */
	private void moveCamera(float x, float y, float z) {
		if(cameraSwitchingModes) {
			return;
		}
		deltaLookAtX -= x;
		deltaLookAtY -= y;
		deltaLookAtZ -= z;
	}

	/**
	 * Animates the camera when the user is switching between top view and normal
	 * view.
	 */
	private void cameraAnimation() {
		if(cameraSwitchingModes) {
			long now = System.currentTimeMillis();
			float elapsed = now - cameraAnimationLastRedraw;
			cameraAnimationLastRedraw = now;

			// Rotates 90° in 10 second
			float rotationSpeed = 90.0f / 1000.0f;
			float deltaAngle = rotationSpeed * elapsed;

			if(cameraModeTopView) {
				float sa = Math.signum(azimuthAngle);
				if(azimuthAngle * sa > deltaAngle) {
					azimuthAngle -= deltaAngle * sa;
				} else {
					azimuthAngle = 0.0f;
				}

				float sz = Math.signum(zenithAngle);
				if(zenithAngle * sz > deltaAngle) {
					zenithAngle -= deltaAngle * sz;
				} else {
					zenithAngle = 0.0f;
				}

				if(0.0f == azimuthAngle && 0.0f == zenithAngle) {
					cameraSwitchingModes = false;
				}
			} else {
				float sa = Math.signum(oldAzimuthAngle);
				if((oldAzimuthAngle - azimuthAngle) * sa > deltaAngle) {
					azimuthAngle += deltaAngle * sa;
				} else {
					azimuthAngle = oldAzimuthAngle;
				}

				float sz = Math.signum(oldZenithAngle);
				if((oldZenithAngle - zenithAngle) * sz > deltaAngle) {
					zenithAngle += deltaAngle * sz;
				} else {
					zenithAngle = oldZenithAngle;
				}

				if(oldAzimuthAngle == azimuthAngle && oldZenithAngle == zenithAngle) {
					cameraSwitchingModes = false;
				}
			}
		}
	}

	/**
	 * Switches the camera into the other mode (top view/normal). The camera
	 * is actually only moved later in cameraAnimation().
	 */
	private void switchCameraMode() {
		// Save our variables (but only if we are not allready switching
		// modes, to allow to undo/redo a switching as many time as we
		// want)
		if(!cameraModeTopView && !cameraSwitchingModes) {
			oldAzimuthAngle = azimuthAngle;
			oldZenithAngle = zenithAngle;
		}

		// Switch mode
		cameraModeTopView ^= true;
		cameraSwitchingModes = true;
		cameraAnimationLastRedraw = System.currentTimeMillis();

		// cameraAnimation() takes care of the rest
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		curseurx = e.getX();
		curseury = e.getY();
	}

	/**
	 * Applies a zoom to the camera
	 * @param factor
	 * @author pvogt
	 */
	private void zoomCamera(float factor) {
		factorCamDistance /= factor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int button = e.getButton();
		if (button == MouseEvent.BUTTON3) {
			Clickable temp = null;
			if (pointedNode != null) {
				temp = pointedNode;
			} else if (pointedLink != null) {
				temp = pointedLink;
			} else if (pointedLayer != null) {
				temp = pointedLayer;
			}

			final JPopupMenu jm = new JPopupMenu();
			final JMenuItem prop = new JMenuItem("Properties...");
			prop.addActionListener(new ActionListener() {
				Clickable object;

				ActionListener init(Clickable object) {
					this.object = object;
					return this;
				}

				public void actionPerformed(ActionEvent e) {
					displayProperties(object);
				}
			}.init(temp));
			jm.add(prop);

			if (temp != null) {
				if (suiP.getAssociatedAbstractGraphHandler().isEditable()) {
					final JMenuItem del = new JMenuItem("Delete");
					del.addActionListener((new ActionListener() {
						private Clickable c = null;

						ActionListener init(Clickable c) {
							this.c = c;
							return this;
						}

						public void actionPerformed(ActionEvent e) {
							suiP.getAssociatedAbstractGraphHandler().removeElement(c.getElementContainer());
						}
					}).init(temp));
					jm.add(del);
				}
			}

			jm.pack();
			jm.show(e.getComponent(), e.getX(), e.getY());

		} else if(button == MouseEvent.BUTTON1) {
			long now = new Date().getTime();
			if(now - lastClick < 300l) {
				switchCameraMode();
			}
			lastClick = now;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		button = e.getButton();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomCamera((float) Math.pow(1.1f, - e.getWheelRotation()));
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_0:
			keyZeroPressed = false;
			break;
		case KeyEvent.VK_LEFT:
			keyLeftPressed = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRightPressed = false;
			break;
		case KeyEvent.VK_UP:
			keyUpPressed = false;
			break;
		case KeyEvent.VK_DOWN:
			keyDownPressed = false;
			break;
		case KeyEvent.VK_PAGE_DOWN:
			keyPageDownPressed = false;
			break;
		case KeyEvent.VK_PAGE_UP:
			keyPageUpPressed = false;
			break;
		case KeyEvent.VK_SHIFT:
			keyShiftPressed = false;
			break;
		case KeyEvent.VK_G:
			keyGPresssed = false;
			break;
		case KeyEvent.VK_C:
			keyCPressed = false;
			break;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_0:
			keyZeroPressed = true;
			break;
		case KeyEvent.VK_LEFT:
			keyLeftPressed = true;
			break;
		case KeyEvent.VK_RIGHT:
			keyRightPressed = true;
			break;
		case KeyEvent.VK_UP:
			keyUpPressed = true;
			break;
		case KeyEvent.VK_DOWN:
			keyDownPressed = true;
			break;
		case KeyEvent.VK_PAGE_DOWN:
			keyPageDownPressed = true;
			break;
		case KeyEvent.VK_PAGE_UP:
			keyPageUpPressed = true;
			break;
		case KeyEvent.VK_SHIFT:
			keyShiftPressed = true;
			break;
		case KeyEvent.VK_G:
			keyGPresssed = true;
			break;
		case KeyEvent.VK_C:
			keyCPressed = true;
			break;
		}
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	/**
	 * If key heve been pressed take appropriate actions
	 * @author pvogt
	 */
	private void computeKeyContribution() {
		long elapsed = getElapsedTime();
		float rate = 1.0f;
		float angleRate = 0.1f;
		float zoomRate = 0.01f;

		if (keyCPressed) {
			keyCPressed = false;
			groundLookLikeChess ^= true;
		}

		if (keyGPresssed) {
			keyGPresssed = false;
			drawGround ^= true;
		}

		if (keyShiftPressed) {
			if (keyLeftPressed) {
				moveCameraRelative(- elapsed * rate, 0.0f, 0.0f);
			}
			if (keyRightPressed) {
				moveCameraRelative(elapsed * rate, 0.0f, 0.0f);
			}
			if (keyUpPressed) {
				moveCameraRelative(0.0f, elapsed * rate, 0.0f);
			}
			if (keyDownPressed) {
				moveCameraRelative(0.0f, - elapsed * rate, 0.0f);
			}
		} else {
			if (keyLeftPressed) {
				rotateCameraLeft(elapsed * angleRate);
			}
			if (keyRightPressed) {
				rotateCameraLeft(- elapsed * angleRate);
			}
			if (keyUpPressed) {
				rotateCameraDown(- elapsed * angleRate);
			}
			if (keyDownPressed) {
				rotateCameraDown(elapsed * angleRate);
			}
		}

		if (keyPageUpPressed) {
			zoomCamera((float) Math.pow(1.1f, elapsed * zoomRate));
		}
		if (keyPageDownPressed) {
			zoomCamera((float) Math.pow(1.1f, - elapsed * zoomRate));
		}

		if (keyZeroPressed) {
			lookAtZ = 0;
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}

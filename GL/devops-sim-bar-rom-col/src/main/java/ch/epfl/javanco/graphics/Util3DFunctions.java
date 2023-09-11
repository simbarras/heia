/**
 * 
 */
package ch.epfl.javanco.graphics;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Utility class that contains functions for 3D view
 * 
 * @author fmoulin
 */
public abstract class Util3DFunctions {
	/**
	 * Moves the scene to simulate the camera
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 */
	public abstract void doCam(GL2 gl);

	/**
	 * Draws a cube
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param center
	 *            Position of the cube's center
	 * @param width
	 *            face's width
	 * @param col
	 *            cube's color
	 * @param texture
	 *            cube's texture
	 */
	public void drawCube(GL2 gl, Vector3f center, float width, Vector3f col,
			Texture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(center.x, center.y, center.z);
		gl.glScalef(width, width, width);
		drawCube(gl, col, texture);
		gl.glPopMatrix();
	}

	/**
	 * Draws a cube without texture
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param center
	 *            Position of the cube's center
	 * @param width
	 *            face's width
	 * @param col
	 *            cube's color
	 */
	public void drawCube(GL2 gl, Vector3f center, float width, Vector3f col) {
		drawCube(gl, center, width, col, null);
	}

	/**
	 * Draws a cube at position [0, 0, 0]
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param col
	 *            cube's color
	 * @param texture
	 *            cube's texture
	 */
	public void drawCube(GL2 gl, Vector3f col, Texture texture) {
		gl.glColor3f(col.x, col.y, col.z);

		drawCube(gl, texture);
	}

	/**
	 * Draws a cube at position [0, 0, 0]
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param texture
	 *            cube's texture
	 */
	public void drawCube(GL2 gl, Texture texture) {
		Vector3f p000 = new Vector3f(-1, -1, -1);
		Vector3f p001 = new Vector3f(-1, -1,  1);
		Vector3f p010 = new Vector3f(-1,  1, -1);
		Vector3f p011 = new Vector3f(-1,  1,  1);
		Vector3f p100 = new Vector3f( 1, -1, -1);
		Vector3f p101 = new Vector3f( 1, -1,  1);
		Vector3f p110 = new Vector3f( 1,  1, -1);
		Vector3f p111 = new Vector3f( 1,  1,  1);

		// -x
		drawQuad(gl, p000, p010, p001, p011, texture);
		// +x
		drawQuad(gl, p100, p110, p101, p111, texture, true);

		// -y
		drawQuad(gl, p000, p100, p001, p101, texture, true);
		// +y
		drawQuad(gl, p010, p110, p011, p111, texture);

		// -z
		drawQuad(gl, p110, p010, p100, p000, texture);
		// +z
		drawQuad(gl, p001, p101, p011, p111, texture, true);
	}

	/**
	 * Draws a triangle
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param p1
	 *            First point
	 * @param p2
	 *            Second point
	 * @param p3
	 *            Third point
	 */
	public void drawTriangle(GL2 gl, Vector3f p1, Vector3f p2, Vector3f p3) {
		gl.glBegin(GL.GL_TRIANGLES);
		glNormal3f(gl, p1, p2, p3);
		glVertex3f(gl, p1);
		glVertex3f(gl, p2);
		glVertex3f(gl, p3);
		gl.glEnd();
	}

	/**
	 * Shorthand for <code>gl.glVertex3f(v.x, v.y, v.z)</code>
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param v
	 */
	public void glVertex3f(GL2 gl, Vector3f v) {
		gl.glVertex3f(v.x, v.y, v.z);
	}

	/**
	 * Draws a quad with a texture
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param p1
	 *            First point
	 * @param p2
	 *            Second point
	 * @param p3
	 *            Third point
	 * @param p4
	 *            Fourth point
	 * @param texture
	 *            Quad's texture
	 */
	public void drawQuad(GL2 gl, Vector3f p1, Vector3f p2, Vector3f p3,
			Vector3f p4, Texture texture) {
		drawQuad(gl, p1, p2, p3, p4, texture, false);
	}

	/**
	 * Draws a quad with a texture
	 * 
	 * <BR>#author fmoulin
	 * @param p1
	 *            First point
	 * @param p2
	 *            Second point
	 * @param p3
	 *            Third point
	 * @param p4
	 *            Fourth point
	 * @param texture
	 *            Quad's texture
	 * @param reverseNormal
	 *            If true the normal is compute as the cross product of p1-p2
	 *            and p3-p2, else it is inverted
	 */
	public void drawQuad(GL2 gl, Vector3f p1, Vector3f p2, Vector3f p3,
			Vector3f p4, Texture texture, boolean reverseNormal) {
		TextureCoords tc = null;
		if (texture != null) {
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL.GL_GREATER, 0);
			gl.glEnable(GL.GL_TEXTURE_2D);
			tc = texture.getImageTexCoords();
			texture.enable(gl);
			texture.bind(gl);

			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
					GL.GL_REPLACE);
		}

		gl.glBegin(GL2.GL_QUADS);

		if (reverseNormal) {
			glNormal3f(gl, p1, p3, p2);
		} else {
			glNormal3f(gl, p1, p2, p3);
		}

		if (tc != null) {
			gl.glTexCoord2f(tc.left(), tc.bottom());
		}
		glVertex3f(gl, p1);

		if (tc != null) {
			gl.glTexCoord2f(tc.right(), tc.bottom());
		}
		glVertex3f(gl, p2);

		if (tc != null) {
			gl.glTexCoord2f(tc.right(), tc.top());
		}
		glVertex3f(gl, p4);

		if (tc != null) {
			gl.glTexCoord2f(tc.left(), tc.top());
		}
		glVertex3f(gl, p3);
		gl.glEnd();

		if (texture != null) {
			gl.glDisable(GL.GL_TEXTURE_2D);
			texture.disable(gl);

			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			gl.glDisable(GL.GL_ALPHA);
			gl.glDisable(GL.GL_BLEND);
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	/**
	 * Computes and applies a normal as cross product of p1-p2 and p3-p2
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param p1
	 *            First point
	 * @param p2
	 *            Second point
	 * @param p3
	 *            Third point
	 */
	public void glNormal3f(GL2 gl, Vector3f p1, Vector3f p2, Vector3f p3) {
		Vector3f v1 = new Vector3f(), v2 = new Vector3f();
		v1.sub(p1, p2);
		v2.sub(p3, p2);

		Vector3f normal = new Vector3f();
		normal.cross(v1, v2);
		normal.normalize();

		gl.glNormal3f(normal.x, normal.y, normal.z);
	}

	/**
	 * Draws a cylinder
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param slices
	 *            Number of slices
	 * @param stacks
	 *            Number of stacks
	 * @param col
	 *            Cylinder's color
	 */
	public void drawCylinder(GL2 gl, GLU glu, int slices, int stacks, Vector3f col, Texture texture) {
		gl.glColor3f(col.x, col.y, col.z);
		drawCylinder(gl, glu, slices, stacks, texture);
	}

	public void drawFullCylinder(GL2 gl, GLU glu, int slices, int stacks, Vector3f col, Texture texture) {
		gl.glColor3f(col.x, col.y, col.z);
		drawCylinder(gl, glu, slices, stacks, texture);
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 1.0f);
		drawDisc(gl, glu, slices, stacks, texture);
		gl.glPopMatrix();
		drawDisc(gl, glu, slices, stacks, texture);
	}

	public void drawDisc(GL2 gl, GLU glu, int slices, int loops, Texture texture) {
		GLUquadric quadric = glu.gluNewQuadric();
		if (texture != null) {
			glu.gluQuadricTexture(quadric, true);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL.GL_GREATER, 0);

			gl.glEnable(GL.GL_TEXTURE_2D);
			texture.enable(gl);
			texture.bind(gl);
		}

		glu.gluDisk(quadric, 0, 1, slices, loops);
		glu.gluDeleteQuadric(quadric);

		if (texture != null) {
			texture.disable(gl);
			gl.glDisable(GL.GL_ALPHA);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	/**
	 * Draws a cylinder
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param slices
	 *            Number of slices
	 * @param stacks
	 *            Number of stacks
	 */
	public void drawCylinder(GL2 gl, GLU glu, int slices, int stacks, Texture texture) {
		GLUquadric quadric = glu.gluNewQuadric();
		if (texture != null) {
			glu.gluQuadricTexture(quadric, true);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL.GL_GREATER, 0);

			gl.glEnable(GL.GL_TEXTURE_2D);
			texture.enable(gl);
			texture.bind(gl);
		}

		glu.gluCylinder(quadric, 1, 1, 1, slices, stacks);
		glu.gluDeleteQuadric(quadric);

		if (texture != null) {
			texture.disable(gl);
			gl.glDisable(GL.GL_ALPHA);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LIGHTING);
		}

	}

	public void drawCylinder(GL2 gl, GLU glu, Vector3f pt1, Vector3f pt2,
			double width, int slices, int stacks, Vector3f col, Texture texture) {
		drawCylinder(gl, glu, pt1, pt2, width, slices, stacks, col, texture, false);
	}

	/**
	 * Draws a cylinder between two points
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param pt1
	 *            First point
	 * @param pt2
	 *            Second point
	 * @param width
	 *            Cylinder's width
	 * @param slices
	 *            Number of slices
	 * @param stacks
	 *            Number of stacks
	 * @param col
	 *            Cylinder's color
	 */
	public void drawCylinder(GL2 gl, GLU glu, Vector3f pt1, Vector3f pt2,
			double width, int slices, int stacks, Vector3f col, Texture texture, boolean full) {
		Vector3f pt = new Vector3f();
		pt.sub(pt1, pt2);

		double rho = pt.length();
		double phi = 0;
		if (rho != 0) {
			phi = Math.acos(pt.z / rho);
		}

		double teta = 0;
		if (pt.y != 0) {
			teta = Math.asin(pt.y / Math.sqrt(pt.x * pt.x + pt.y * pt.y));
		}

		if (pt.x < 0) {
			teta = Math.PI - teta;
		}

		phi -= Math.PI;
		Quat4f quat = eulerToQuat(0f, phi, teta);

		gl.glPushMatrix();
		gl.glTranslatef(pt1.x, pt1.y, pt1.z);
		AxisAngle4f aa = new AxisAngle4f();
		aa.set(quat);

		glRotatef(gl, aa);
		gl.glScaled(width, width, rho);

		if(full) {
			drawFullCylinder(gl, glu, slices, stacks, col, texture);
		}
		else {
			drawCylinder(gl, glu, slices, stacks, col, texture);
		}
		gl.glPopMatrix();
	}

	/**
	 * Draw a teapot
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glut
	 * @param center
	 *            Teapot's center
	 * @param size
	 *            Teapot's size
	 * @param col
	 *            Teapot's color
	 * @param texture
	 *            Teapot's texture
	 */
	public void drawTeapot(GL2 gl, GLUT glut, Vector3f center, float size,
			Vector3f col, Texture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(center.x, center.y, center.z);
		drawTeapot(gl, glut, size, col, texture);
		gl.glPopMatrix();
	}

	/**
	 * Draws a teapot
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glut
	 * @param size
	 *            Teapot's size
	 * @param col
	 *            Teapot's color
	 * @param texture
	 *            Teapot's texture
	 */
	public void drawTeapot(GL2 gl, GLUT glut, float size, Vector3f col,
			Texture texture) {
		gl.glColor3f(col.x, col.y, col.z);

		drawTeapot(gl, glut, size, texture);
	}

	/**
	 * Draws a teapot
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glut
	 * @param size
	 *            Teapot's size
	 * @param texture
	 *            Teapot's texture
	 */
	public void drawTeapot(GL2 gl, GLUT glut, float size, Texture texture) {
		gl.glPushMatrix();
		gl.glRotatef(90, 1, 0, 0);

		if (texture != null) {
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL.GL_GREATER, 0);

			gl.glEnable(GL.GL_TEXTURE_2D);
			texture.enable(gl);
			texture.bind(gl);
		}

		glut.glutSolidTeapot(size);

		if (texture != null) {
			texture.disable(gl);
			gl.glDisable(GL.GL_ALPHA);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LIGHTING);
		}
		gl.glPopMatrix();
	}

	/**
	 * Draws a sphere
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param center
	 *            Sphere's center
	 * @param radius
	 *            Sphere's radius
	 * @param col
	 *            Sphere's color
	 */
	public void drawSphere(GL2 gl, GLU glu, Vector3f center, double radius,
			Vector3f col) {
		drawSphere(gl, glu, center, radius, col, null);
	}

	/**
	 * Draws a sphere
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param center
	 *            Sphere's center
	 * @param radius
	 *            Sphere's radius
	 * @param col
	 *            Sphere's color
	 * @param texture
	 *            Sphere's texture
	 */
	public void drawSphere(GL2 gl, GLU glu, Vector3f center, double radius,
			Vector3f col, Texture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(center.x, center.y, center.z);
		drawSphere(gl, glu, radius, col, texture);
		gl.glPopMatrix();
	}

	/**
	 * Draws a sphere
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param radius
	 *            Sphere's radius
	 * @param col
	 *            Sphere's color
	 * @param texture
	 *            Sphere's texture
	 */
	public void drawSphere(GL2 gl, GLU glu, double radius, Vector3f col,
			Texture texture) {
		gl.glColor3f(col.x, col.y, col.z);

		drawSphere(gl, glu, radius, texture);
	}

	/**
	 * Draws a sphere
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param glu
	 * @param radius
	 *            Sphere's radius
	 * @param texture
	 *            Sphere's texture
	 */
	public void drawSphere(GL2 gl, GLU glu, double radius, Texture texture) {
		GLUquadric quadric = glu.gluNewQuadric();
		if (texture != null) {
			glu.gluQuadricTexture(quadric, true);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL.GL_GREATER, 0);

			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glRotatef(180f, 0f, 1f, 0f);
			texture.enable(gl);
			texture.bind(gl);
		}

		glu.gluSphere(quadric, radius, 20, 20);
		glu.gluDeleteQuadric(quadric);

		if (texture != null) {
			texture.disable(gl);
			gl.glDisable(GL.GL_ALPHA);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	/**
	 * Draws an icon
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param pt
	 *            Icon's center
	 * @param size
	 *            Icon's size
	 * @param texture
	 *            Icon's texture
	 */
	public void drawIcon(GL2 gl, Vector3f pt, float size, Texture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(pt.x - size / 2f, pt.y, pt.z - size / 2f);
		gl.glScalef(size, size, size);
		gl.glRotatef(90f, 1f, 0f, 0f);

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0);
		gl.glEnable(GL.GL_TEXTURE_2D);
		TextureCoords tc = texture.getImageTexCoords();
		texture.enable(gl);
		texture.bind(gl);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(tc.left(), tc.bottom());
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(tc.right(), tc.bottom());
		gl.glVertex3f(1, 0, 0);
		gl.glTexCoord2f(tc.right(), tc.top());
		gl.glVertex3f(1, 1, 0);
		gl.glTexCoord2f(tc.left(), tc.top());
		gl.glVertex3f(0, 1, 0);
		gl.glEnd();

		gl.glDisable(GL.GL_TEXTURE_2D);
		texture.disable(gl);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		gl.glDisable(GL.GL_ALPHA);
		gl.glDisable(GL.GL_BLEND);
		gl.glEnable(GL2.GL_LIGHTING);

		gl.glPopMatrix();
	}

	/**
	 * Converts a color (in range [0, 255]) to a <code>Vector3f</code> (in
	 * range [0, 1])
	 * 
	 * <BR>#author fmoulin
	 * @param color
	 *            Color to convert (in range [0, 255])
	 * @return <code>Vector3f</code> (in range [0, 1])
	 */
	public Vector3f colorToVector3f(Color color) {
		return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f,
				color.getBlue() / 255f);
	}

	/**
	 * Converts Euler's angles in a quaternion Angles are in radiant!!
	 * 
	 * @param x
	 *            Angle around the x axis
	 * @param y
	 *            Angle around the y axis
	 * @param z
	 *            Angle around the z axis
	 * @return a <code>Quat4f</code>
	 */
	public Quat4f eulerToQuat(double x, double y, double z) {
		double ex = x / 2d;
		double ey = y / 2d;
		double ez = z / 2d;
		double cosx = Math.cos(ex);
		double cosy = Math.cos(ey);
		double cosz = Math.cos(ez);

		double sinx = Math.sin(ex);
		double siny = Math.sin(ey);
		double sinz = Math.sin(ez);

		double cosyz = cosy * cosz;
		double sinyz = siny * sinz;

		return new Quat4f((float) (sinx * cosyz - cosx * sinyz), (float) (cosx
				* siny * cosz + sinx * cosy * sinz), (float) (cosx * cosy
						* sinz - sinx * siny * cosz), (float) (cosx * cosyz + sinx
								* sinyz));
	}

	/**
	 * Does a <code>glRotated</code> from an <code>AxisAngle4f</code>
	 * 
	 * <BR>#author fmoulin
	 * @param gl
	 * @param aa
	 *            <code>AxisAngle4f</code> for the rotation
	 */
	public void glRotatef(GL2 gl, AxisAngle4f aa) {
		gl.glRotated(Math.toDegrees(aa.angle), aa.x, aa.y, aa.z);
	}

	public Texture loadTexture(GL2 gl, String file) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(file), false);

			tex.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			tex.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tex;
	}
}

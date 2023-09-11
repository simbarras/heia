package ch.epfl.javanco.ui.swing;

import java.awt.Component;

import javax.swing.JFileChooser;

import ch.epfl.general_libraries.io.ExtensionFilter;
import ch.epfl.javanco.io.JavancoFile;

public class FileChooser extends JFileChooser {

	public static final long serialVersionUID = 0;
	private static String lastUndefinedSaveDir = System.getProperty("user.dir");
	private static String lastUndefinedOpenDir = System.getProperty("user.dir");


	public static JavancoFile promptForSelectDir(Component parent, String startDir, String title) {
		JavancoFile startDirFile;
		if (startDir != null) {
			startDirFile = new JavancoFile(startDir);
		} else {
			startDirFile = new JavancoFile(lastUndefinedOpenDir);
		}

		FileChooser fc = new FileChooser(startDirFile, null, null);
		fc.setDialogTitle(title);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			JavancoFile dir = new JavancoFile(fc.getSelectedFile().getAbsolutePath());
			if (startDir == null) {
				lastUndefinedOpenDir = dir.getAbsolutePath();
			}
			return dir;
		} else {
			return null;
		}
	}


	public static JavancoFile  promptForSaveFile(Component parent) {
		return promptForSaveFile(parent, null);
	}

	public static JavancoFile  promptForSaveFile(Component parent, String fileExtension) {
		JavancoFile  file = promptForSaveFile(parent, null, fileExtension);
		return file;
	}


	public static JavancoFile  promptForSaveFile(Component parent, String dir, String fileExtension) {
		JavancoFile  dirFile;
		if (dir != null) {
			dirFile = new JavancoFile (dir);
		} else {
			dirFile= new JavancoFile (lastUndefinedSaveDir);
		}
		String[] extensions = ((fileExtension==null || fileExtension=="") ? null : new String[]{fileExtension});
		FileChooser fc = new FileChooser(dirFile, extensions, null);

		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			JavancoFile file = new JavancoFile(fc.getSelectedFile().getAbsolutePath());
			if (dir == null) {
				lastUndefinedSaveDir = file.getParentFile().getAbsolutePath();
			}
			return file;
		} else {
			return null;
		}
	}

	public static JavancoFile  promptForOpenFile(Component parent) {
		return promptForOpenFile(parent, null);
	}

	public static JavancoFile  promptForOpenFileWithTitle(Component parent, String title) {
		return promptForOpenFileWithTitle(parent, null, title);
	}

	public static JavancoFile  promptForOpenFile(Component parent, String fileExtension) {
		return promptForOpenFile(parent, null, fileExtension, "Open file");
	}

	public static JavancoFile  promptForOpenFileWithTitle(Component parent, String fileExtension, String title) {
		return promptForOpenFile(parent, null, fileExtension, title);
	}
	/*
	public static File promptForOpenFile(Component parent, String dir, String fileExtension) {
		return promptForOpenFile(parent, dir, fileExtension, "Open file");
	}*/

	public static JavancoFile  promptForOpenFile(Component parent, String dir, String fileExtension, String title) {
		String[] extensions = ((fileExtension==null || fileExtension=="") ? null : new String[]{fileExtension});
		return promptForOpenFile(parent, dir, extensions, null, title);
	}

	public static JavancoFile  promptForOpenFile(Component parent, String dir, String[] fileExtensions, String filterDescription,  String title) {
		JavancoFile  dirFile;
		if (dir != null) {
			dirFile = new JavancoFile (dir);
		} else {
			dirFile = new JavancoFile (lastUndefinedOpenDir);
		}
		FileChooser fc = new FileChooser(dirFile, fileExtensions, filterDescription);
		fc.setDialogTitle(title);

		int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			JavancoFile file = new JavancoFile(fc.getSelectedFile().getAbsolutePath());
			if (dir == null) {
				lastUndefinedOpenDir = file.getParentFile().getAbsolutePath();
			}
			return file;
		} else {
			return null;
		}
	}

	private FileChooser(JavancoFile  currentDir, String[] extensions, String filterDescription) {
		super(currentDir);
		setAcceptAllFileFilterUsed(true);
		if (extensions != null && extensions.length != 0) {
			setFileFilter(new ExtensionFilter(extensions, filterDescription));
		}
	}
}
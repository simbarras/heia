package ch.heia.isc.ol.simulife.view;

import ch.heia.isc.gl1.simulife.interface_.AbstractView;
import ch.heia.isc.gl1.simulife.interface_.IUniverse;
import ch.heia.isc.ol.simulife.factory.CellEltFactory;
import ch.heia.isc.ol.simulife.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class DefaultView extends AbstractView {

    private final JFrame frame;
    private JButton[][] buttons;
    private IUniverse universe;
    private Runnable backwardSimulation;
    private Runnable forwardSimulation;
    private Consumer<Boolean> pauseSimulation;
    private BooleanSupplier pauseInfo;
    private BooleanSupplier stoppedInfo;

    private JButton pauseResume;
    private JToolBar toolBar;
    private JButton redo;

    public DefaultView(IUniverse universe) {
        this();
        setUniverse(universe);
    }

    public DefaultView() {

        pauseResume = new JButton("Play");

        toolBar = new JToolBar();
        toolBar.setSize(100, 100);
        toolBar.setFloatable(true);
        toolBar.setRollover(true);
        toolBar.add(pauseResume);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(750, 750);
        frame.setMinimumSize(new Dimension(750, 750));
        frame.setTitle("Simulife");
        frame.add(toolBar, BorderLayout.NORTH);
    }


    @Override
    public void refresh() {
        for (int y = 0; y < universe.getHeight(); y++) {
            for (int x = 0; x < universe.getWidth(); x++) {
                if (universe.getICell(x, y).getNumberOfElements() == 0) {
                    buttons[y][x].setBackground(Color.WHITE);
                    buttons[y][x].setIcon(null);
                } else {
                    buttons[y][x].setIcon(new ImageIcon(universe.getICell(x, y).getTopElement().getIconPath()));
                }
            }
        }
        redo.setEnabled(!stoppedInfo.getAsBoolean());
    }

    @Override
    public void setVisible(boolean b) {
        this.frame.setVisible(b);
    }

    /**
     * Set a new universe and update the view
     */
    @Override
    public void setUniverse(IUniverse universeLocal) {
        this.universe = universeLocal;
        //frame.getContentPane().removeAll();

        if (universe instanceof Board) {

            JButton undo = new JButton("Undo");
            undo.addActionListener(e -> {
                backwardSimulation.run();
                refresh();
            });


            redo = new JButton("Do");
            redo.addActionListener(e -> {
                forwardSimulation.run();
                refresh();
            });

            undo.setEnabled(true);
            redo.setEnabled(true);

            toolBar.add(undo);
            toolBar.add(redo);


            pauseResume.addActionListener(e -> {
                if (pauseInfo.getAsBoolean()) {
                    pauseSimulation.accept(false);
                    pauseResume.setText("Pause");
                    undo.setEnabled(false);
                    redo.setEnabled(false);
                } else {
                    pauseSimulation.accept(true);
                    pauseResume.setText("Resume");
                    undo.setEnabled(true);
                    redo.setEnabled(!stoppedInfo.getAsBoolean());
                }
            });
        }
        addPannel();
    }

    /**
     * Add the pannel to the frame
     */
    private void addPannel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(universe.getHeight(), universe.getWidth(), 1, 1));
        buttons = new JButton[universe.getHeight()][universe.getWidth()];
        for (int y = 0; y < universe.getHeight(); y++) {
            for (int x = 0; x < universe.getWidth(); x++) {
                buttons[y][x] = new JButton();
                buttons[y][x].setOpaque(true);
                buttons[y][x].setBorderPainted(false);
                buttons[y][x].setFocusPainted(false);
                int finalX = x;
                int finalY = y;
                buttons[y][x].addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            JPopupMenu popupMenu = new JPopupMenu();
                            for (CellEltFactory factory : CellEltFactory.factories) {
                                JMenuItem item = new JMenuItem("Add " + factory.getName());
                                item.addActionListener(l -> {
                                    factory.createAndAddElement((Board) universe, finalX, finalY);
                                    refresh();
                                });
                                popupMenu.add(item);
                            }
                            popupMenu.setLocation(e.getX(), e.getY());
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
                panel.add(buttons[y][x]);
            }
        }
        frame.add(panel, BorderLayout.CENTER);
    }

    public void registerUndoCommand(Runnable backwardSimulation) {
        this.backwardSimulation = backwardSimulation;
    }

    public void registerRedoCommand(Runnable forwardSimulation) {
        this.forwardSimulation = forwardSimulation;
    }

    public void registerPauseCommand(Consumer<Boolean> pauseSimulation) {
        this.pauseSimulation = pauseSimulation;
    }

    public void registerPauseInfo(BooleanSupplier pauseInfo) {
        this.pauseInfo = pauseInfo;
    }

    public void registerStoppedInfo(BooleanSupplier stoppedInfo){
        this.stoppedInfo = stoppedInfo;
    }

}

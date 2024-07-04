package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import simulator.control.Controller;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
public class MainWindow extends JFrame {
private Controller _ctrl;
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {		
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		ControlPanel controlPanel = new ControlPanel(_ctrl);
		mainPanel.add(controlPanel, BorderLayout.PAGE_START);
		StatusBar statusPanel = new StatusBar(_ctrl);
		mainPanel.add(statusPanel, BorderLayout.PAGE_END);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(_ctrl));
		speciesTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(speciesTable);
		
		InfoTable regionTable = new InfoTable("Regions", new RegionsTableModel(_ctrl));
		regionTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regionTable);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				 ViewUtils.quit(MainWindow.this);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}
}

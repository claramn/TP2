package simulator.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.*;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.io.File;
import simulator.control.Controller;
import simulator.misc.Utils;
import javax.swing.JTextField;
  class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; 
	private JButton _quitButton;
	 JButton _openButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _viewerButton;
	private JButton _regionsButton;
	private JSpinner _stepsSpinner;
	private MapWindow _mapWindow;
	private boolean _viewerCreated = false;
	private JTextField _dtField = new JTextField(5); 

		ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		}
		
		private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);

		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		_openButton = new JButton();
		_openButton.setToolTipText("open");
		_openButton.setIcon(new ImageIcon("resources/icons/open.png"));
	
		_openButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _openButton) {
					if(	_fc.showOpenDialog(ViewUtils.getWindow(ControlPanel.this)) == JFileChooser.APPROVE_OPTION) {
						File file = _fc.getSelectedFile();
						InputStream in;
						try {
							in = new FileInputStream(file);
							JSONObject f = load_JSON_file(in);
							_ctrl.reset(f.getInt("cols"), f.getInt("rows"), f.getInt("width"), f.getInt("height"));
							_ctrl.load_data(f);
						} 
						catch (FileNotFoundException e1) {
							e1.printStackTrace();
							ViewUtils.showErrorMsg("Fichero no aceptado");
						}
					}
				}
			}
		});
		_toolaBar.add(_openButton);
		_toolaBar.addSeparator();
		
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("viewer");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _viewerButton) 
					_mapWindow = new MapWindow(new JFrame(),_ctrl);
			}
		});
		_toolaBar.add(_viewerButton);
		
		_regionsButton = new JButton();
		_regionsButton.setToolTipText("regions");
		_regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regionsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _regionsButton) {
					if (!_viewerCreated) {
						_changeRegionsDialog = new ChangeRegionsDialog( _ctrl);
						_changeRegionsDialog.open(ViewUtils.getWindow(ControlPanel.this));
						_viewerCreated = true;
					}
					else _changeRegionsDialog.open(ViewUtils.getWindow(ControlPanel.this));
				}
			}
		});
		_toolaBar.add(_regionsButton);
		_toolaBar.addSeparator();
		
		_runButton = new JButton();
		_runButton.setToolTipText("run");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _runButton) {
					_regionsButton.setEnabled(false);
					_viewerButton.setEnabled(false);
					_quitButton.setEnabled(false);
					_openButton.setEnabled(false);
					_stopped = false;
					double dt = Double.parseDouble(_dtField.getText());	
					run_sim((int)_stepsSpinner.getValue(), dt);	
				}
			}
		});
		_toolaBar.add(_runButton);
		
		_stopButton = new JButton();
		_stopButton.setToolTipText("stop");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _stopButton) {
					_regionsButton.setEnabled(true);	
					_viewerButton.setEnabled(true);
					_quitButton.setEnabled(true);
					_openButton.setEnabled(true);
					_stopped = true;
				}
			}
		});
		_toolaBar.add(_stopButton);
		
		
		JLabel stepsLabel = new JLabel("Steps:");
		JLabel dtLabel = new JLabel("Delta-Time:");
		_stepsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 5));
		_stepsSpinner.setMaximumSize(new Dimension(80, 40));
		_stepsSpinner.setMinimumSize(new Dimension(80, 40));
		_stepsSpinner.setPreferredSize(new Dimension(80, 40));
		
		_stepsSpinner.setToolTipText("steps");
		
		_dtField.setToolTipText("delta-time");
		_dtField.setText("0.03");
		_dtField.addActionListener((e) -> {
			_dtField.setText(_dtField.getText());
		});
		_dtField.setMaximumSize(new Dimension(80, 40));
		_dtField.setMinimumSize(new Dimension(80, 40));
		_dtField.setPreferredSize(new Dimension(80, 40));		
		
		_toolaBar.add(stepsLabel);
		_toolaBar.add(_stepsSpinner);
		_toolaBar.add(dtLabel);
		_toolaBar.add(_dtField);
		
		_toolaBar.add(Box.createGlue()); 
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolaBar.add(_quitButton);

		 _changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
		}
		
		private void run_sim(int n, double dt) {	
			if (n > 0 && !_stopped) {
				try {
					_ctrl.advance(dt);
					Thread.sleep((long) (dt*1000));
					SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
				} 
				catch (Exception e) {
					ViewUtils.showErrorMsg(e.getMessage());	
					for (Component c : _toolaBar.getComponents()) {
						c.setEnabled(true);
					}
					_stopped = true;
				}
			} else {
				for (Component c : _toolaBar.getComponents()) {
					c.setEnabled(true);
				}
			_stopped = true;
			}
		}
		
		private static JSONObject load_JSON_file(InputStream in) {
			return new JSONObject(new JSONTokener(in));
		}

  }

package simulator.view;

import java.awt.BorderLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.json.JSONObject;
import javax.swing.JScrollPane;

import javax.swing.ScrollPaneConstants;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	JComboBox<String> regionsComboBox;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status = 0; 
	private MapInfo _mng;
	
	private JComboBox<String> toRow;
	private JComboBox<String> fromRow;
	private JComboBox<String> fromCol;
	private JComboBox<String> toCol;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		_ctrl.addObserver(this); 
		initGUI();
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JLabel helpT = new JLabel("<html><p>select a region type, the rows/cols interval, and provide values for the parameters in the Value column(default values are used for parameters with no value)</p></html>");
		helpT.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane scrollPane = new JScrollPane(helpT);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		mainPanel.add(helpT);

		JPanel tablePanel = new JPanel();
		JPanel comboBoxPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();

		_regionsInfo = Main.region_factory.get_info();

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		_dataTableModel.setColumnIdentifiers(_headers);

		JTable table = new JTable(_dataTableModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		tablePanel.setVisible(true);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		mainPanel.add(scroll);

		for (int i = 0; i < _regionsInfo.size(); i++) {
			select(i);
		}

		_regionsModel = new DefaultComboBoxModel<>();
		
		for (JSONObject r : _regionsInfo) { 
			_regionsModel.addElement((String) r.get("type"));
		}
		
		regionsComboBox = new JComboBox<>(_regionsModel);
		regionsComboBox.setSelectedIndex(0);
		regionsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_dataTableModel.setRowCount(0);
				select(regionsComboBox.getSelectedIndex());
			}
		});
		JPanel regions = new JPanel();
		JLabel type = new JLabel("Region Type:");
		regions.add(type);
		regions.add(regionsComboBox);
		comboBoxPanel.add(regions);

		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();
		
		JPanel rowsPanel = new JPanel();
		JPanel colsPanel = new JPanel();
		int i = _mng.get_rows();
		 fromRow = new JComboBox<>(_fromRowModel);
		putOnCombo(fromRow, 0, _mng.get_rows());
		fromRow.setSelectedIndex(0);
		toRow = new JComboBox<>(_toRowModel);
		putOnCombo(toRow, 0, i);
		toRow.setSelectedIndex(0);
		fromCol = new JComboBox<>(_fromColModel);
		putOnCombo(fromCol, 0, _mng.get_cols());
		fromCol.setSelectedIndex(0);
		toCol = new JComboBox<>(_toColModel);
		putOnCombo(toCol, 0, _mng.get_cols());
		toCol.setSelectedIndex(0);
		JLabel fromR = new JLabel("Row from/to:");
		rowsPanel.add(fromR);
		rowsPanel.add(fromRow);
		rowsPanel.add(toRow);
		comboBoxPanel.add(rowsPanel);
		JLabel fromC = new JLabel("Column from/to:");
		colsPanel.add(fromC);
		colsPanel.add(fromCol);
		colsPanel.add(toCol);
		comboBoxPanel.add(colsPanel);

		mainPanel.add(comboBoxPanel); 
		
		JButton okButton = new JButton("OK"); 
		okButton.addActionListener((e) -> {
			_status = 1;
			ChangeRegionsDialog.this.setVisible(false);
			try {
				_ctrl.set_regions(ChangeRegionsDialog.this.toJSON());
				_status = 1;
				this.setVisible(false);
			} catch (Exception e1) {
				ViewUtils.showErrorMsg(e1.getMessage());
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false); 
				_status = 0;
			}
		});
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		mainPanel.add(comboBoxPanel);
		mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);
		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	public JSONObject toJSON() {
		JSONObject region_data = new JSONObject();
		JSONObject spec = new JSONObject();
		String region_type;
		int row_from;
		int col_from;
		int row_to;
		int col_to;
		String clave = null;
		String valor;
		for (int i = 0; i < _dataTableModel.getRowCount(); i++) {
			if(_dataTableModel.getValueAt(i, 0) != null && _dataTableModel.getValueAt(i, 0).toString().isEmpty()) 
				clave = (String) _dataTableModel.getValueAt(i, 0);
			else 
				clave = "";
			
			if(_dataTableModel.getValueAt(i, 1) != null && _dataTableModel.getValueAt(i, 1).toString().isEmpty()) {
				valor = (String) _dataTableModel.getValueAt(i, 1);
				if (!valor.toString().isEmpty() && valor != null)
					region_data.put(clave, valor);
			}
			else 
				region_data.put(clave, "");
		}
		region_type = _regionsInfo.get(regionsComboBox.getSelectedIndex()).get("type").toString();
		row_from = Integer.parseInt(_fromRowModel.getSelectedItem().toString());
		col_from = Integer.parseInt(_fromColModel.getSelectedItem().toString());
		row_to = Integer.parseInt(_toRowModel.getSelectedItem().toString());
		col_to = Integer.parseInt(_toColModel.getSelectedItem().toString());
		JSONArray r = new JSONArray();
		r.put(row_from);
		r.put(row_to);
		JSONArray c = new JSONArray();
		c.put(col_from);
		c.put(col_to);
		JSONObject json = new JSONObject();
		json.put("row", r);
		json.put("col", c);
		spec.put("type", region_type);
		spec.put("data", region_data);
		json.put("spec", spec);

		JSONArray total = new JSONArray();
		total.put(json);
		JSONObject total2 = new JSONObject();
		total2.put("regions", total);

		return total2;
	}

	private void select(int n) {
		JSONObject info = _regionsInfo.get(n);
		JSONObject data = info.getJSONObject("data");
		for (String key : data.keySet()) {
			_dataTableModel.addRow(new Object[] { key, 0, data.get(key) });
		}
	}

	private void putOnCombo(JComboBox<String> c, int from, int to) {
		for (int i = from; i <= to-1; i++) {
			c.addItem(Integer.toString(i));
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		_mng = map;
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_mng = map;
		fromRow.removeAllItems();
		toRow.removeAllItems();
		fromCol.removeAllItems();
		toCol.removeAllItems();
		putOnCombo(fromRow, 0, _mng.get_rows());
		putOnCombo(toRow, 0, _mng.get_rows());
		putOnCombo(fromCol, 0, _mng.get_cols());
		putOnCombo(toCol, 0, _mng.get_cols());
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
	}
}

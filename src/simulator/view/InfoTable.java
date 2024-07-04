package simulator.view;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(_title));
		JTable table = new JTable(_tableModel);
		JScrollPane scroll = new JScrollPane(table);
		this.add(scroll);
	}
}

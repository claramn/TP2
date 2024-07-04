package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import javax.swing.*;

class StatusBar extends JPanel implements EcoSysObserver {

	private double tiempo;
	private int numAnimales;
	private int altura;
	private int anchura;
	private int filas;
	private int columnas;
	private Controller ctrl;
	private JLabel lAnimal;
	private JLabel lTime ;
	private JLabel lDim ;

	StatusBar(Controller ctrl) {
		this.ctrl = ctrl;
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		lTime = new JLabel("Time: " + tiempo);
		this.add(lTime);
		lAnimal = new JLabel("Num. Animals: " + numAnimales);
		this.add(lAnimal);
		lDim = new JLabel("Map: "+anchura+"x"+altura+" ["+filas+"x"+columnas+"]");
		this.add(lDim);
	}
	
	private void updateStatusBar(double time, MapInfo map, List<AnimalInfo> animals) {
		tiempo = time;
		numAnimales = animals.size();
		altura = map.get_height();
		anchura = map.get_width();
		filas = map.get_rows();
		columnas = map.get_cols();
		
		lTime.setText("Time: " + String.format("%.2f", tiempo));
		lAnimal.setText("Num. Animals: " + numAnimales);
		lDim.setText("Map: "+anchura+"x"+altura+" ["+filas+"x"+columnas+"]");
	}
		
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateStatusBar(time, map, animals);

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		tiempo = 0;
		numAnimales = 0;
		updateStatusBar(time, map, animals);

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		updateStatusBar(time, map, animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		updateStatusBar(time, map, animals);
	}
}

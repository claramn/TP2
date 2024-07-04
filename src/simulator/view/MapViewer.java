package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.*;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura/ de la simulación -- se supone que siempre van a ser iguales
	// al tamaño del componente
	private int _width;
	private int _height;

	// Número de filas/columnas de la simulación
	private int _rows;
	private int _cols;

	// Anchura/altura de una región
	int _rwidth;
	int _rheight;

	 // Mostramos sólo animales con este estado. Los posibles valores de _currState
	// son null, y los valores deAnimal.State.values(). Si es null mostramos todo.
	State _currState;		
	
	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;
		
		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}
	
	// Un mapa para la información sobre las especies
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto
	private Font _font = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no
	private boolean _showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 's':
					if(_currState == null)  
						_currState = State.values()[0];
					else if (_currState == State.values()[State.values().length - 1])
						_currState = null;
					else
						_currState = State.values()[_currState.ordinal() + 1];
					repaint();
				default:
					repaint();	
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}
		});

		// Por defecto mostramos todos los animales
		_currState = null;

		// Por defecto mostramos el texto de ayuda
		_showHelp = true;
		repaint();   
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto
		g.setFont(_font);

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		// TODO Mostrar el texto de ayuda si _showHelp es true. El texto a mostrar es el
		// siguiente (en 2 líneas):
		//
		// h: toggle help
		// s: show animals of a specific state
		if (_showHelp) {
			gr.setColor(Color.RED);
			gr.drawString("h: toggle help", 10, 20);	
			gr.drawString("s: show animals of a specific state", 10, 40);
		}

	}

	private boolean visible(AnimalInfo a) {
		// TODO Devolver true si el animal es visible, es decir si _currState es null o
		// su estado es igual a _currState.
		return (_currState == null || a.get_state() == _currState);
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		// TODO Dibujar el grid de regiones
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				g.drawRect(j * _rwidth, i * _rheight, _rwidth, _rheight);
			}
		}
		
		// Dibujar los animales
		for (AnimalInfo a : animals) {
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

			// TODO Si esp_info es null, añade una entrada correspondiente al mapa. Para el
			// color usa ViewUtils.get_color(a.get_genetic_code())
			if (esp_info == null) {	
				esp_info = new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code()));
				_kindsInfo.put(a.get_genetic_code(), esp_info);
			}
			// TODO Incrementar el contador de la especie (es decir el contador dentro de
			// tag_info)
			esp_info._count++;
			
			// TODO Dibujar el animal en la posicion correspondiente, usando el color
			// tag_info._color. Su tamaño tiene que ser relativo a su edad, por ejemplo
			// edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.
			g.setColor(esp_info._color);
			g.fillOval((int)a.get_position().getX(),(int) a.get_position().getY(),(int) a.get_age() / 2 + 2,(int) a.get_age() / 2 + 2);
		
		}

		JLabel etiqueta = new JLabel();
		
		if(_currState != null ) {
			etiqueta.setText("Estado: " + _currState.toString());
			g.drawString(etiqueta.getText(), 10, 60);
		}

		int y = 500;
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
		
			JLabel etiqueta3 = new JLabel();
			etiqueta3.setText(e.getKey() + ": " + e.getValue()._count);
			g.setColor(e.getValue()._color);
			drawStringWithRect(g, 10, y, etiqueta3.getText());
			y += 20;
			e.getValue()._count = 0;
		}
		
		JLabel etiqueta2 = new JLabel();
		g.setColor(Color.MAGENTA);
		etiqueta2.setText("Tiempo: " + String.format("%.3f", time));
		drawStringWithRect(g, 10, y, etiqueta2.getText());
	}

	// Un método que dibujar un texto con un rectángulo
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		_objs = objs;
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		_width = map.get_width();
		_height = map.get_height();
		_cols = map.get_cols();
		_rows = map.get_rows();
		_rwidth = map.get_region_width();
		_rheight = map.get_region_height();
		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		update(animals, time);
		paintComponent(getGraphics());
	}

}

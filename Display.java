import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;

public class Display extends JPanel implements ActionListener {
	JTextField searchField;
	JLabel searchLabel;
	JButton settings;
	
	JComboBox<String> genderSelect, birthSelect;
	
	JTable table;
	DefaultTableModel noEdit;
	JScrollPane pane;
	
	GridBagConstraints constraints;
	
	static Integer[] years;
	static final int yearsTotal = 141;
	
	static BufferedReader[] brList = new BufferedReader[yearsTotal];
	
	static Integer[] birthTotalsF = new Integer[yearsTotal];
	static Integer[] birthTotalsM = new Integer[yearsTotal];
	
	boolean showF = true; boolean showM = true; boolean showPercent = false; boolean normalize = false;
	
	///////////////////////////////////////////////////////////////////////////
	
	public Display() {
		super(new GridBagLayout());
		constraints = new GridBagConstraints();
		
		///////////////////////////////////////////////////////////////////////////
		
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        searchLabel = new JLabel("Search: ");
        add(searchLabel, constraints);
		
		searchField = new JTextField();
		searchField.addActionListener(this);
		searchField.setActionCommand("Search");
        
        constraints.weightx = 1.0;
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        add(searchField, constraints);
        
        settings = new JButton("Settings");
        settings.addActionListener(this);
        settings.setActionCommand("Settings");
		
		constraints.weightx = 0;
        constraints.gridx = 3;
        constraints.gridwidth = 1;
        add(settings, constraints);
        
        ///////////////////////////////////////////////////////////////////////////
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		String[] genderOptions = {"Both", "Female", "Male"};
		genderSelect = new JComboBox<>(genderOptions);
		genderSelect.addActionListener(this);
		genderSelect.setActionCommand("Gender");
		add(genderSelect, constraints);
		genderSelect.setVisible(false);
		
		constraints.gridx = 2;
		constraints.gridy = 1;
		String[] birthOptions = {"Number", "Percentage", "Normalize"};
		birthSelect = new JComboBox<>(birthOptions);
		birthSelect.addActionListener(this);
		birthSelect.setActionCommand("Birth");
		add(birthSelect, constraints);
		birthSelect.setVisible(false);
		
		///////////////////////////////////////////////////////////////////////////
		
		createTable();
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void fastUpdate(Name toSearch) {
		noEdit.setRowCount(0);
		
		for(int i = 0; i < yearsTotal; i++) {
			Object[] toPass = {years[i], toSearch.femaleRank[i], toSearch.femaleData[i], toSearch.maleRank[i], toSearch.maleData[i]};
			noEdit.addRow(toPass);
		}
	}
	
	private void genderUpdate(Name toSearch, boolean isF) {
		if(isF) {
			for(int i = 0; i < yearsTotal; i++) {
				Object[] toPass = {years[i], toSearch.femaleRank[i], toSearch.femaleData[i]};
				noEdit.addRow(toPass);
			}
		} else {
			for(int i = 0; i < yearsTotal; i++) {
				Object[] toPass = {years[i], toSearch.maleRank[i], toSearch.maleData[i]};
				noEdit.addRow(toPass);
			}
		}
	}

	private void nameSearch() {
		//final long startTime = System.currentTimeMillis();
		
        String text = searchField.getText();
        searchField.selectAll();
        
        Name toSearch = new Name(text, this);
        try {
			toSearch.getData();
		} catch (IOException e) {}
        
        if(showF) {
        	if(showM) {
        		fastUpdate(toSearch);
        	} else {
        		genderUpdate(toSearch, true);
        	}
        } else {
        	genderUpdate(toSearch, false);
        }
        
        //final long endTime = System.currentTimeMillis();
        //System.out.println("Time: " + (endTime-startTime));
        
        try {
			initBr();
		} catch (FileNotFoundException e) {}
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void showSettings() {
		genderSelect.setVisible(!genderSelect.isVisible());
		birthSelect.setVisible(!birthSelect.isVisible());
	}
	
	private void updateSettings() {
		switch((String) genderSelect.getSelectedItem()) {
		case "Both":
			showF = true;
			showM = true;
			break;
		case "Female":
			showF = true;
			showM = false;
			break;
		case "Male":
			showF = false;
			showM = true;
			break;
		}
		
		switch((String) birthSelect.getSelectedItem()) {
		case "Number":
			showPercent = false;
			normalize = false;
			break;
		case "Percentage":
			showPercent = true;
			normalize = false;
			break;
		case "Normalize":
			showPercent = false;
			normalize = true;
			break;
		}
	}
	
	private void createTable() {
		try {
			remove(pane);
		} catch (NullPointerException e) {}
		
		String[] columnHeads;
		
		if(showF && showM) {
			columnHeads = new String[]{"Year", "Female Rank", "Female Births", "Male Rank", "Male Births"};
		} else if (showF) {
			columnHeads = new String[]{"Year", "Female Rank", "Female Births"};
		} else {
			columnHeads = new String[]{"Year", "Male Rank", "Male Births"};
		}
		String[][] data = {};
		table = new JTable(data, columnHeads);
		noEdit = new DefaultTableModel(data, columnHeads) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(noEdit);
		pane = new JScrollPane(table);

		constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(pane, constraints);
	}
	
	public void actionPerformed(ActionEvent evt) {
		switch(evt.getActionCommand()) {
		case "Search":
			nameSearch();
			break;
		case "Settings":
			showSettings();
			break;
		default:
			updateSettings();
			createTable();
			showSettings();
			break;
		}
    }
	
	///////////////////////////////////////////////////////////////////////////
	
	private static void initYears() {
		years = new Integer[yearsTotal];
		for(int i = 0; i < yearsTotal; i++) {
			years[i] = i + 1880;
		}
	}
	
	private static void initBr() throws FileNotFoundException {
		File file;
		for(int i = 0; i < yearsTotal; i++) {
			file = new File("names\\yob" + (i+1880) + ".txt");
			brList[i] = new BufferedReader(new FileReader(file));
		}
	}
	
	private static void initTotals() throws IOException {
		File file = new File("names\\totals.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String currentStrLine;
		int index1, index2;
		int date, totalF, totalM;
		
		while((currentStrLine = br.readLine()) != null) {
			index1 = currentStrLine.indexOf(',');
			index2 = currentStrLine.indexOf(',', index1+1);
			
			date = Integer.parseInt(currentStrLine.substring(0, index1));
			totalF = Integer.parseInt(currentStrLine.substring(index1+1, index2));
			totalM = Integer.parseInt(currentStrLine.substring(index2+1));
			
			birthTotalsF[date-1880] = totalF;
			birthTotalsM[date-1880] = totalM;
		}
		br.close();
	}
	
	public static void main(String args[]) throws IOException {
		initYears();
		initBr();
		initTotals();
		
		JFrame frame = new JFrame("Name Search Extended");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Display());
        frame.setSize(500, 500);
        frame.setVisible(true);
	}
}
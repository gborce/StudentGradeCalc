// Bruce Gjorgjievski
//StudentGradeCalc.java
//Edits a text file and calculates my students' grade averages for me
//Uses Java AWT graphical windowing framework

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Roster extends Frame 
    implements ActionListener, WindowListener {

    private Button calcButton;
    private FileDialog loadNameBox, saveNameBox;
    private TextField nameField;
    private TextArea editArea;
    private MenuItem loadItem, saveItem, exitItem, aboutItem;

    public static void main(String [] args) {
        Roster r = new Roster();
        r.setSize(700,500);
        r.buildGUI();
        r.setVisible(true);
    }

    public void buildGUI() {
	Panel northPanel, southPanel;
	MenuBar menuBar = new MenuBar();
	Menu fileMenu, helpMenu;
	
	setTitle("Borce's Students' Averages");
	
	//set the menuBar
	fileMenu = new Menu("File");
	helpMenu = new Menu("Help");
	
	loadItem = new MenuItem("Load");
	fileMenu.add(loadItem);
	loadItem.addActionListener(this);

	saveItem = new MenuItem("Save");
	fileMenu.add(saveItem);
	saveItem.addActionListener(this);

	fileMenu.addSeparator();
	
	exitItem = new MenuItem("Exit");
	fileMenu.add(exitItem);
	exitItem.addActionListener(this);
	
	aboutItem = new MenuItem("About");
	helpMenu.add(aboutItem);
	aboutItem.addActionListener(this);

	menuBar.add(fileMenu);
	menuBar.add(helpMenu);
	
	setMenuBar(menuBar);

	//set the actual window
	northPanel = new Panel();
	southPanel = new Panel();

        setLayout( new BorderLayout() );
        
	add("North", northPanel);
	add("South", southPanel);
        
	calcButton = new Button("Calculate Averages");
	northPanel.add(calcButton);
	calcButton.addActionListener(this);

	editArea = new TextArea(20, 40);
	add("Center", editArea);

	nameField = new TextField(30);
        southPanel.add(nameField);
	
        this.addWindowListener(this);    // for windowClosing
    }
      
    public void actionPerformed(ActionEvent e) { 
        String fileName = nameField.getText();
	BufferedReader loadFile;
	PrintWriter saveFile;
	
        if (e.getSource() == loadItem) {
            loadNameBox = new FileDialog(this, "Load File", FileDialog.LOAD);
            loadNameBox.show();

            // display the name
            fileName = loadNameBox.getFile();
            nameField.setText(fileName);

	    // try to load the file
            try {
                loadFile = new BufferedReader(new FileReader(fileName));
                editArea.setText("");       // clear the input area
                String line;

                while( ( line = loadFile.readLine() ) != null) {
                    editArea.append(line + "\n");
                }
               loadFile.close();
            }

            catch (IOException ioe) {
                System.err.println("Error while trying to access file " + fileName + ": " + ioe.toString() );
                System.exit(1);
            }
	    
	} //if loadItem
	else if (e.getSource() == saveItem) {
            saveNameBox = new FileDialog(this, "Save File", FileDialog.SAVE);
            saveNameBox.show();

            // get the name
            fileName = saveNameBox.getFile();
	    nameField.setText(fileName);

	    // try to save the file
            try {
                saveFile = new PrintWriter(new FileWriter(fileName), true);
                saveFile.print(editArea.getText());   
		saveFile.close();
            }

            catch (IOException ioe) {
                System.err.println("Error while trying to access file " + fileName + ": " + ioe.toString() );
                System.exit(1);
            }
	}//if saveItem
	else if (e.getSource() == exitItem) {
	    System.exit(0);
	}//if exitItem
	else if (e.getSource() == calcButton) {
	    calculateAverages(fileName);
	} //if calcButton

    }
/////////////////////////////////////////////////////////////////////////////////
    //Calculates weighted averages of students' grades according to the 
    //math given in the sylabus
    private void calculateAverages(String fileName) {
	final int numStudents = 11;
	final int numGrades = 5;
	final int numHw = 3; // change here and in calculation when adding new homeworks
	final int passingGrade = 36; //when final exam is done = 51

	BufferedReader calcFile;
	String studentGrades [] [] = new String [numStudents+3] [numGrades+2]; 
	int i=0, j;

	  try {
                calcFile = new BufferedReader(new FileReader(fileName));
                editArea.appendText("\n *** Averages *** (Current Passing Score is " + passingGrade + " Current Maximum score is "+(2*passingGrade-2) + ") \n\n");  
                String line;
		
		i =0 ;
                while( ( line = calcFile.readLine() ) != null) {
		    StringTokenizer extractGrades = new StringTokenizer(line, " \t\n");
		    j =0;
		    while (extractGrades.hasMoreTokens()) {
			studentGrades [i] [j] = extractGrades.nextToken();
			j++;
		    } // while there are tokens
  /////////////////////////////////////////////////////////////////////////////////
		    /* print tokens that were extracted
		    for (int k =0; k < j; k++)
			editArea.append("[k="+k+"] "+studentGrades[i][k]);
		    editArea.append(" i is " + i +"\n");
			*/

		    i++;
		} //while there are more lines in the file
               calcFile.close();
		} //try

            catch (IOException ioe) {
                System.err.println("Error while trying to access file " + fileName + ": " + ioe.toString() );
                System.exit(1);
            }
///////////////////////////////////////////////////////////////////////////////////
	//now calculate the weighted average for each student
	for (int k =2; k<i; k++) {
	double avEx=0, avWeighted=0, avHw=0;
	int proj=0, finalExam=0;

		editArea.append(studentGrades[k][0]+ " " + studentGrades[k][1]+" \t: ");

		// calculate Homeworks total
		avHw = (double) (Integer.parseInt(studentGrades[k][2]) + Integer.parseInt(studentGrades[k][5]) + Integer.parseInt(studentGrades[k][6])) / numHw;

		//calculate Exams Average
		avEx =Integer.parseInt(studentGrades[k][4]) / 1;

		//project
		proj = Integer.parseInt(studentGrades[k][3]);

		//finalExam

		/* output everything for debugging
		editArea.append("   avEx: "+avEx+" finalExam: " + finalExam + " avHw: " + avHw + " proj: " +proj + " avWeighted: ");
		*/

		//calculate weighted average 
		// max = 100*(0.4) + 100*(0.35) + 30*(0.67)+ 30*(0.33)
		avWeighted = avEx*(0.4) + finalExam*(0.35) + avHw*(0.67) + proj*(0.33); 
		
		editArea.append(Double.toString(Math.ceil(avWeighted)));
		if (avWeighted > passingGrade) 
			editArea.append(" \t---> \t passing \n");
		else
			editArea.append(" \t---> \t FAILING! \n");
		} //for calculate
    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }  
    public void windowIconified(WindowEvent e) {
    }
    public void windowOpened(WindowEvent e) {
    }
    public void windowClosed(WindowEvent e) {
    }
    public void windowDeiconified(WindowEvent e) {
    }
    public void windowActivated(WindowEvent e) {
    }
    public void windowDeactivated(WindowEvent e) {
    }
}



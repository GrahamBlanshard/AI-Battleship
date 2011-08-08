/****
 * Java Battleship
 * --
 * Applet code written by Kendra Cunningham (www.cunningk.ca)
 * Small modifications/additions by Graham Blanshard (www.pro-graham.com)
 */
package prograham.battleship;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prograham.battleship.board.Coordinate;
import prograham.battleship.board.Ship;
import prograham.battleship.player.AIPlayer;
import prograham.battleship.player.HumanPlayer;


public class BattleshipGUI extends javax.swing.JApplet {
	
	private static final String MISS = "  O"; //When a ship is missed
	private static final String HIT = "  X"; //When a ship is hit
	private static final String SHIP = "   *"; //When a ship is neither
	private static final int randomShots = 7; //How many random shots to fill the grid with
    
	private static final long serialVersionUID = 200265784L;
	//Applet objects
	private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel Table1ColumnHeader;
    private javax.swing.JLabel Table1Row1Label;
    private javax.swing.JLabel Table2ColumnHeader;
    private javax.swing.JLabel Table2Row1Label;
    private SelectionListener listener;
    private String print = "";
    
    //Battleship Objects
    private AIPlayer computer; //Opponent
    private HumanPlayer battle;//Human player
    private int x = -1; //X Selection
    private int y = -1; //Y Selection
    private Boolean shipsPlaced = false; //If we're placing ships
    private int count = 0; // Variable used for keeping track of # of placed ships
    
    //Board variables
    private String[] columnTitles = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
    private Object[][] rowData = new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
     };
    private String[][] ships = { {"Aircraft Carrier", "5"},
    								{"Battleship", "4"},
									{"Destroyer", "3"}, 
									{"Submarine", "3"}, 
									{"Patrol Boat", "2"} 
								};
    private String[] Alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    /** 
     * Initializes the applet 
     */
    public void init() {		
    	computer = new AIPlayer(this);
    	battle = new HumanPlayer(this);
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                    printToPane("Please select a coordinate on the leftmost grid to place your first ship.\n");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*Add random shots and print them on the grid*/
        Random r = new Random();
        for (int i = 0; i < randomShots; i++) {
        	int column = r.nextInt(battle.grid().getGridSize());
        	int row = r.nextInt(battle.grid().getGridSize());
        	
	        computer.grid().fire(new Coordinate(Coordinate.toAlpha(column),row));
	        battle.grid().fire(new Coordinate(Coordinate.toAlpha(column),row));
	        jTable1.setValueAt(MISS, row, column);
	        jTable2.setValueAt(MISS, row, column);
        }
    }

    /**
     * Places ships onto the grid. User places his/her ships manually while computer
     * does his at the end.
     */
    public void getShips() {
    	
        if(count < 5) {
            printToPane("Placing " + ships[count][0] + " (" + ships[count][1] + ") ");
            int orientation = getOrientation();
            boolean direction = false;
            
            if(orientation == 0)
                direction = true; // Horizontal
            
            if(battle.grid().addShip(Coordinate.toAlpha(x), y, direction, Integer.parseInt(ships[count][1]), ships[count][0])) {
            	printToPane("Ship added.\n");
                
            	//Draw the changes to the grid
                if(direction) { // Place Horizontal
                    for(int i = 0; i < Integer.parseInt(ships[count][1]); i++) {
                        jTable1.setValueAt(SHIP, y, x+i);
                    }             
                } else { //Vertical
                    for(int i = 0; i < Integer.parseInt(ships[count][1]); i++) {
                        jTable1.setValueAt(SHIP, y+i, x);
                    }                    
                }
                count++;
            }
            else
            	printToPane("Error placing " + ships[count][0] + " ship.\n");
        }
        
        if(count == 5) {
            shipsPlaced = true;
            computer.placeShip(); //Have the computer place his ships 
        }
    }

    /**
     * Write the specified string to the text panel
     * @param print String to print
     */
    public void printToPane(String print) {
        if(!this.print.equals(print)) {
            this.print = print;
            jTextArea1.append(print);
            jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
        }
        super.repaint();
    }
    
    /**
     * Blanks the text pane
     */
    public void clearPane() {
        jTextArea1.setText("");
        super.repaint();
    }
    
    /**
     * Used for detection when placing ships or attacking. It grabs the coordinate
     * @param table The table we're checking against
     * @param row Y
     * @param column X
     */
    public void selectedCoordinate(String table, int row, int column) {
    	if (battle.lost() || computer.lost()) {//Prevent further actions if the game is over
    		//TODO: Make popup so player can play again
    		//TODO: Report status of game!
    		clearPane();
    		String message = battle.lost() ? "\nGame over. You lose.\n" : "\nCongratulations! You won!\n";
    		if (battle.lost()) {
    			jTable1.setBackground(new Color(240,100,100));
    			jTable2.setBackground(new Color(128,255,128));
    		} else {
    			jTable2.setBackground(new Color(240,100,100));
    			jTable1.setBackground(new Color(128,255,128));
    		}
    		printToPane(message);
    		return;
    	}

        if(column != x || row != y) { // Checks for duplicate/repeated x/y coords
            x = column;
            y = row;
            
            if(table.equals("Table 1")) { //Player's grid was clicked            	
                if(shipsPlaced == false) { //We're placing ships
                    getShips();
                }
                else	//We're attacking!
                     printToPane("You can't fire at your own ships! Please fire at the opponent's grid!\n");
            }
            else { //Opponent's grid was clicked
            	if(shipsPlaced == false) //We're still placing ships!
            		printToPane("Please place your ships before firing at the opponent.\n");
            	else { //Taking a shot
                	boolean validShot = true;
                	printToPane("You fired at " + Coordinate.toAlpha(column) + row);
                	//Take the shot
                	int i = computer.grid().fire(new Coordinate(Coordinate.toAlpha(column),row));
                
	                if(i == 2) { //A hit
	                    jTable2.setValueAt(HIT, row, column);
	                    printToPane(" and hit!\n");
	                    battle.fire(new Coordinate(Coordinate.toAlpha(column),row), computer.grid());
	                }
                
	                if(i == 0) { //Invalid selection
	                    printToPane(". Try again.\n");
	                    validShot = false;
	                }
                
	                if(i==1) { //Miss!
	                    jTable2.setValueAt(MISS, row, column);
	                    printToPane(" and missed.\n");
	                }
                
	                //Player's turn is over. Computer takes his turn
	                if (!computer.lost() && validShot) {
	                    boolean compHit = computer.AIFire(battle.grid()); //He fires at your grid	                    
	                    Coordinate lastShot = computer.getLastShot(); //Grab what square he shot at
	                    //printToPane("Opponent fired at " + lastShot.get() + ".\n");
	                    String cX = lastShot.x(); //Grab X
	                    int xloc = 0;
	                    int cRow = lastShot.y(); //Grab Y, Convert to row num
	
	                    for (int j = 0; j < 10; j++) {
	                    	if (Alphabet[j].equals(cX.toUpperCase())) {
	                            xloc = j;
	                            break;
	                        }
	                    }

	                    int cColumn = xloc; //Converted X into column num
	
	                    if (compHit) //Did he hit or not?
	                        jTable1.setValueAt(HIT, cRow, cColumn);                    
	                    else
	                        jTable1.setValueAt(MISS, cRow, cColumn);                    
	                }

	                if (battle.lost()) { //Has the player lost now?
	                    clearPane();	    
	                    printToPane("\nGame over. You lose.\n");
	                    //TODO: Upload stats?-->
            			jTable1.setBackground(new Color(240,100,100));
            			jTable2.setBackground(new Color(128,255,128));
            			
            			//Reveal the computer's ships
            			for (Ship s : computer.getShips()) {
            				for (Coordinate c : s.getCoords()) {
            					jTable2.setValueAt(SHIP, c.y(), Coordinate.toInt(c.x()));
            				}
            			}
            			
	                    JOptionPane.showMessageDialog(jPanel1,
	                        "Game over. You lose.\n",
	                        "Lost!",
	                        JOptionPane.WARNING_MESSAGE);
	                    super.repaint();
	            	}
                
					if (computer.lost()) {					
						clearPane();
						printToPane("\nCongratulations! You won!\n");
		    			jTable2.setBackground(new Color(240,100,100));
		    			jTable1.setBackground(new Color(128,255,128));
                        JOptionPane.showMessageDialog(jPanel1,
                            "Congratulations! You won!\n",
                            "Won!",
                            JOptionPane.WARNING_MESSAGE);
                        super.repaint();
                      //TODO:<-- Upload stats?
					}
            	}
            }
        }
    }

    /**
     * Initialize the game's grid
     */
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTable2 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextArea1.setEditable(false);
        Table1ColumnHeader = new javax.swing.JLabel();
        Table2ColumnHeader = new javax.swing.JLabel();
        Table1Row1Label = new javax.swing.JLabel();
        Table2Row1Label = new javax.swing.JLabel();
        
        jTable1.setTableHeader(null);
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(rowData, columnTitles)
        {            
			private static final long serialVersionUID = 200265784L;
			
			boolean[] canEdit = new boolean[] 
            {
                false, false, false, false, false, false, false, false, false, false
            };
			
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit [columnIndex];
            }
        });
        
        jTable1.setCellSelectionEnabled(true);
        jTable1.setRowHeight(30);
        jTable1.getSelectionModel().addListSelectionListener(listener);
        jTable1.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        
        Font f = new Font("TableFont",1,18);
        Color light_blue = new Color(153, 217, 237);
        jTable1.setFont(f);
        jTable1.setBackground(light_blue);
        listener = new SelectionListener(jTable1);
        jTable1.getSelectionModel().addListSelectionListener(listener);
        jTable1.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        jTable2.setTableHeader(null);
        jTable2.setFont(f);
        jTable2.setBackground(light_blue);
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(rowData, columnTitles)
        {
			private static final long serialVersionUID = 200265784L;
			boolean[] canEdit = new boolean[] 
            {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit [columnIndex];
            }
        });
        
        jScrollPane2.setViewportView(jTable2);
        jTable2.setCellSelectionEnabled(true);
        jTable2.setRowHeight(30);

        Table1ColumnHeader.setText("      A         B         C          D          E          F         G          H          I           J");
        Table2ColumnHeader.setText("      A         B         C          D          E          F         G          H          I           J");

        Table1Row1Label.setText("<html><table><tr><td height=30>0</td></tr><tr><td height=30>1</td></tr>" +
                "<tr><td height=30>2</td></tr><tr><td height=30>3</td></tr><tr><td height=30>4</td></tr>" +
                "<tr><td height=30>5</td></tr><tr><td height=30>6</td></tr><tr><td height=30>7</td></tr>" +
                "<tr><td height=30>8</td></tr><tr><td height=30>9</td></tr></table></html>");
        Table2Row1Label.setText("<html><table><tr><td height=30>0</td></tr><tr><td height=30>1</td></tr>" +
                "<tr><td height=30>2</td></tr><tr><td height=30>3</td></tr><tr><td height=30>4</td></tr>" +
                "<tr><td height=30>5</td></tr><tr><td height=30>6</td></tr><tr><td height=30>7</td></tr>" +
                "<tr><td height=30>8</td></tr><tr><td height=30>9</td></tr></table></html>");

        SelectionListener listener2 = new SelectionListener(jTable2);
        jTable2.getSelectionModel().addListSelectionListener(listener2);
        jTable2.getColumnModel().getSelectionModel().addListSelectionListener(listener2);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Table1Row1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(Table1ColumnHeader, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Table2Row1Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Table2ColumnHeader, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Table1ColumnHeader)
                    .addComponent(Table2ColumnHeader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Table1Row1Label)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Table2Row1Label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

	/**
     * Get the ship's orientation selection
     * @return 0 if horizontal
     */
    public int getOrientation() {
        Object[] options = {"Horizontal", "Vertical"};
	    int n = JOptionPane.showOptionDialog(jPanel1, "Please select ship orientation:", "Orientation",
	    										JOptionPane.YES_NO_OPTION,
	    										JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	    super.repaint();
	    return n;
    }
    
	/**
	 * Selection listener class for handling user selections 
	 * @author Graham Blanshard
	 */
	class SelectionListener implements ListSelectionListener { 
	    private JTable table;
	
	    /**
	     * Each table needs a listener, this is the constructor
	     * @param table Table we're listening on
	     */
	    public SelectionListener(JTable table) { 
	        this.table = table;
	    }
	
	    /**
	     * User selected a different square on the table
	     */
	    public void valueChanged(ListSelectionEvent e) {
	        //What table did they select?
	    	String selectedTable = "";
	    
	        if(table == jTable1)
	            selectedTable = "Table 1";
	        else if(table == jTable2)
	            selectedTable = "Table 2";
	        
	        int[] selectedRow = table.getSelectedRows();
	        int[] selectedColumns = table.getSelectedColumns();
	
	        if (!e.getValueIsAdjusting()) {
	        	selectedCoordinate(selectedTable, selectedRow[0], selectedColumns[0]);
	        }
	    }
	}
}

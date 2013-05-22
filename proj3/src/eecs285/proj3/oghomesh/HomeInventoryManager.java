package eecs285.proj3.oghomesh;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;

/**
 * @author Omid Ghomeshi
 * This class manages the main GUI window for the Home Inventory Manager.
 * It sets up the appropriate GUI elements in the correct location.
 */
@SuppressWarnings("serial")
public class HomeInventoryManager extends JFrame
{
  public static boolean confirmed;
  public static String roomLocation;
  public static String description;
  public static String cost;
  public static String month;
  public static String day;
  public static String year;
  public static String path;
  public static double totalCost;
  public static String totalCostString;
  private String [] monthList = {"January", "February", "March", "April", 
      "May", "June", "July", "August", "September", "October", 
      "November", "December"};
  private String listElement;
  private String listFileElement;
  private int selectedIndex;
  private JPanel sortPanel;
  private JPanel totalValuePanel;
  private JPanel inventoryListPanel;
  private JMenuBar menuBar;
  private JMenu fileMenu;
  private JMenu editMenu;
  private JMenuItem load;
  private JMenuItem save;
  private JMenuItem quit;
  private JMenuItem add;
  private JMenuItem modify;
  private JMenuItem delete;
  private JComboBox sortItems;
  private JComboBox showItems;
  private JLabel value ;
  private JLabel sortBy;
  private JLabel showIn;
  private JList inventoryList;
  private DefaultListModel inventoryModel;
  private DefaultListModel inventoryFileModel;
  private DefaultListModel tempModel;
  private DefaultListModel tempFileModel;
  private DefaultListSelectionModel listSelection;
  private InventoryListener inventoryListener;
  private Vector<String> yearVector;
  private Vector<String> fileExtensions;
  private Vector<String> rooms;

  public HomeInventoryManager(String title)
  {
    super (title);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Inputting the possible years in a vector for future error checking
    yearVector = new Vector<String>();
    for (int i = 2013; i >= 1990; i--)
    {
      yearVector.add(Integer.toString(i));
    }
    yearVector.add("<1990");
    //Inputting the possible image extensions in a vector for future 
    //error checking
    fileExtensions = new Vector<String>();
    fileExtensions.add("jpg");
    fileExtensions.add("jpeg");
    fileExtensions.add("png");
    fileExtensions.add("gif");
    fileExtensions.add("bmp");
    //Inputting the possible rooms in a vector for future error checking
    rooms = new Vector<String>();
    rooms.add("Bedroom");
    rooms.add("DiningRoom");
    rooms.add("Garage");
    rooms.add("Kitchen");
    rooms.add("LivingRoom");
    rooms.add("Outdoors");

    //Setting up file menu
    inventoryListener = new InventoryListener();
    menuBar = new JMenuBar();
    fileMenu = new JMenu("File");
    editMenu = new JMenu("Edit");
    load = new JMenuItem("Load Inventory");
    load.addActionListener(inventoryListener);
    save = new JMenuItem("Save Inventory");
    save.addActionListener(inventoryListener);
    quit = new JMenuItem("Exit Program");
    quit.addActionListener(inventoryListener);
    add = new JMenuItem("Add Item");
    add.addActionListener(inventoryListener);
    modify = new JMenuItem("Modify Item");
    modify.addActionListener(inventoryListener);
    delete = new JMenuItem("Delete Item");
    delete.addActionListener(inventoryListener);
    fileMenu.add(load);
    fileMenu.add(save);
    fileMenu.add(quit);
    editMenu.add(add);
    editMenu.add(modify);
    editMenu.add(delete);
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    setJMenuBar(menuBar);


    //Creating text fields and combo boxes for the sort panel
    sortBy = new JLabel("Sort Items By: ", SwingConstants.RIGHT);
    showIn = new JLabel("Show Only Items In: ", SwingConstants.RIGHT);
    sortItems = new JComboBox();
    showItems = new JComboBox();
    //Adding items to Sort Items combo box
    sortItems.addItem("Item Name");
    sortItems.addItem("Location");
    sortItems.addItem("Value");
    sortItems.addItem("Purchase Date");
    sortItems.setSelectedIndex(-1);
    sortItems.addItemListener(inventoryListener);
    sortItems.addActionListener(inventoryListener);
    //Adding items to to Show Items combo box
    showItems.addItem("ALL");
    showItems.addItem("Bedroom");
    showItems.addItem("DiningRoom");
    showItems.addItem("Garage");
    showItems.addItem("Kitchen");
    showItems.addItem("LivingRoom");
    showItems.addItem("Outdoors");
    showItems.addItemListener(inventoryListener);
    showItems.addActionListener(inventoryListener);
    // Adding combo boxes and text fields to the sort panel
    sortPanel = new JPanel(new FlowLayout());
    sortPanel.add(sortBy);
    sortPanel.add(sortItems);
    sortPanel.add(showIn);
    sortPanel.add(showItems);
    add (sortPanel, BorderLayout.NORTH);

    //Setting up panel that list information about each item
    inventoryList = new JList();
    listSelection = new DefaultListSelectionModel();
    listSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    inventoryList.setSelectionModel(listSelection);
    inventoryListPanel = new JPanel(new BorderLayout());
    inventoryListPanel.add(inventoryList);
    inventoryModel = new DefaultListModel();
    inventoryFileModel = new DefaultListModel();
    tempModel = new DefaultListModel();
    tempFileModel = new DefaultListModel();
    add(inventoryListPanel);

    //Setting up panel with the total cost of items in inventory
    value = new JLabel("Total Value of Displayed Items: No Items Displayed");
    totalValuePanel = new JPanel(new FlowLayout());
    totalValuePanel.add(value);
    add(totalValuePanel, BorderLayout.SOUTH);
  }

  /**
   * This class serves as the listener for the Combo Boxes and other GUI
   * elements to make sure that appropriate changes are being read.
   * @author Omid
   */
  public class InventoryListener implements ActionListener, ItemListener
  {
    Scanner readFile;
    Scanner lineScan;
    JFileChooser fileChooser;
    FileWriter writeFile;
    PrintStream outFile;
    File selectedFile; 
    File savedFile;
    File inputtedFile;
    boolean success = true;
    String line;
    String error;
    String selectedItem;
    String selectedRoom;
    String imageElm;
    String selectedElem;
    JFrame frame = new JFrame("Inside Frame");
    int chooserReturn;
    int locationIndex;
    int valueIndex;
    int purchaseIndex;
    int dateIndex1;
    int dateIndex2;
    int imageIndex;
    int lines;
    int counter;
    int counter2;
    double costTemp;

    /**
     * This method makes sure the changes the GUI elements (such as the File
     * menu or edit menu) are read and the appropriate actions occur
     */
    public void actionPerformed(ActionEvent event)
    {
      selectedItem = (String) sortItems.getSelectedItem();
      selectedRoom = (String) showItems.getSelectedItem();

      //----------------------------------Load----------------------------------
      if (event.getSource() == load)
      {
        //Reads the selected file and checks if the file is empty
        System.out.println("You want to open a file");
        fileChooser = new JFileChooser();
        chooserReturn = fileChooser.showOpenDialog(fileChooser);

        if (chooserReturn == JFileChooser.APPROVE_OPTION)
        {
          lines = 0;
          counter2 = 0;
          selectedFile = fileChooser.getSelectedFile();
          try
          {
            BufferedReader reader = new BufferedReader
                (new FileReader(selectedFile));
            while (reader.readLine() != null) 
            {
              lines++;
            }
            reader.close();
          }
          catch (IOException exception)
          {
            System.out.println("Error in file");
          }

          try 
          {
            readFile = new Scanner(selectedFile);
          } catch (FileNotFoundException exception) 
          {
            System.out.println("File not found");
          }
          while (readFile.hasNext())
          {
            line = readFile.nextLine();
            for( int i=0; i<line.length(); i++ ) 
            {
              if( line.charAt(i) == '^' ) 
              {
                counter++;
              } 
            }
            if (counter != 6)
            {
              counter2++;
              if (counter2 == lines)
              {
                error = "This file was not able to be read properly."
                    + " Please check the format of the file and"
                    + " try again.";
                JOptionPane.showMessageDialog(null, error, "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
          }
          if (selectedFile.length() == 0)
          {
            error = "The selected file is empty. Nothing will load."
                + " Please load another file.";
            JOptionPane.showMessageDialog(null, error, "Warning", 
                JOptionPane.WARNING_MESSAGE);
            System.out.println("File is empty");
          }
          try 
          {
            //Traversing through every line of the selected file making sure
            //that the file has the correct elements. If any portion of the
            //the file is incorrect, the user is warned and it skips to the
            //next line.
            readFile = new Scanner(selectedFile);
            while (readFile.hasNext())
            {
              try
              {
                line = readFile.nextLine();
                counter = 0;
                for( int i=0; i<line.length(); i++ ) 
                {
                  if( line.charAt(i) == '^' ) 
                  {
                    counter++;
                  } 
                }
                if (counter != 6)
                {
                  error = "One of the lines was not formatted correctly."
                      + " This line will be skipped";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  continue;
                }
                lineScan = new Scanner (line).useDelimiter("[{^}]");;
                description = lineScan.next();
                try
                {
                  roomLocation = lineScan.next();
                  if (!rooms.contains(roomLocation))
                  {
                    throw new IllegalArgumentException();
                  }
                }
                //Error checking for the room
                catch (IllegalArgumentException exception)
                {
                  error = "\"" + roomLocation + "\" is not a valid input for "
                      + "room. Please make sure the room is Bedroom, DiningRoom" 
                      + ", Garage, Kitchen, LivingRoom, or Outdoors. This line "
                      + "will not be displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("Year value is invalid");
                  continue;
                }
                cost = lineScan.next();
                int indexOfDecimal = cost.indexOf('.');
                try 
                {
                  costTemp = Double.valueOf(cost);
                  if (indexOfDecimal != -1 && 
                      cost.substring(indexOfDecimal+1).length() != 0)
                  {
                    if (cost.substring(indexOfDecimal+1).length() != 2)
                    {

                      throw new NumberFormatException();
                    }
                  }
                }
                //Error checking for the cost
                catch (NumberFormatException exception)
                {
                  error = "\"" + cost + "\" is not a valid input for value." +
                      " Please input an integer or a number with 2 " +
                      " decimals. This line will not be displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("cost is not a valid number");
                  continue;
                }
                if (costTemp == Math.floor(costTemp)
                    && indexOfDecimal == -1)
                {
                  cost = cost + ".00";
                }
                else if (costTemp == Math.floor(costTemp)
                    && indexOfDecimal != -1 && 
                    cost.substring(indexOfDecimal+1).isEmpty())
                {
                  cost = cost + "00";
                }
                month = lineScan.next();
                try
                {
                  int monthNum = Integer.valueOf(month);
                  if (monthNum < 1 || monthNum > 12)
                  {
                    throw new NumberFormatException();
                  }
                }
                //Error checking for the month
                catch (NumberFormatException exception)
                {
                  error = "\"" + month + "\" is not a valid input for month." +
                      " Please make sure that month is between 1 and 12, " +
                      " inclusive. This line will not be displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("Month value is not a number");
                  continue;
                }
                day = lineScan.next();
                try
                {
                  @SuppressWarnings("unused")
                  int dayNum = Integer.valueOf(month);
                }
                //Error checking for the day
                catch (NumberFormatException exception)
                {
                  error = "\"" + day + "\" is not a valid input for day, in " 
                      + monthList[Integer.valueOf(month)-1] + ". Please make "
                      + "sure that day is a number within the number of days "
                      + "in " + monthList[Integer.valueOf(month)-1] + 
                      ". This line will not be displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("Month value is not a number");
                  continue;
                }
                try
                {
                  int monthNum = Integer.valueOf(month);
                  int dayNum = Integer.valueOf(day);
                  //These months have 31 days
                  if (monthNum == 1 || monthNum == 3 || monthNum == 5 ||
                      monthNum == 7 || monthNum == 8 || monthNum == 10 ||
                      monthNum == 12)
                  {
                    if (dayNum < 1 || dayNum > 31)
                      throw new IllegalArgumentException();
                  }
                  //These months have 30 days
                  else if (monthNum == 4 || monthNum == 6 || monthNum == 9 || 
                      monthNum == 11)
                  {
                    if (dayNum < 1 || dayNum > 30)
                      throw new IllegalArgumentException();
                  }
                  //February as 29 days
                  else if (monthNum == 2)
                  {
                    throw new IllegalArgumentException();
                  }
                }
                //Error checking for the day
                catch (IllegalArgumentException exception)
                {
                  error = "\"" + day + "\" is not a valid input for day in " 
                      + monthList[Integer.valueOf(month)-1] +". Please make "
                      + "sure that day is a number within the number of days "
                      + "in " + monthList[Integer.valueOf(month)-1] + 
                      ". This line will not be displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("Month value is not a number");
                  continue;
                }
                try
                {
                  year = lineScan.next();
                  if (!yearVector.contains(year))
                  {
                    throw new IllegalArgumentException();
                  }
                }
                //Error checking for the year
                catch (IllegalArgumentException exception)
                {
                  error = "\"" + year + "\" is not a valid input for year." +
                      " Please make sure that year is between 1990 and 2013, " 
                      + "inclusive, or \"<1990\". This line will not be " +
                      "displayed.";
                  JOptionPane.showMessageDialog(null, error, "Warning", 
                      JOptionPane.WARNING_MESSAGE);
                  System.out.println("Year value is invalid");
                  continue;
                }
                path = lineScan.next();
                if (path.compareTo("null") != 0)
                {
                  inputtedFile = new File(path);
                  //Error checking for the image
                  if (!inputtedFile.exists())
                  {
                    error = "\"" + inputtedFile + "\" does not exist." +
                        " Please make sure that the file exists on your " 
                        + " computer. This line will not be displayed.";
                    JOptionPane.showMessageDialog(null, error, "Warning", 
                        JOptionPane.WARNING_MESSAGE);
                    System.out.println("Image File does not exist");
                    continue;
                  }
                  try
                  {
                    int index = path.lastIndexOf('.');
                    String extension = path.substring(index+1);
                    if (!fileExtensions.contains(extension))
                    {
                      throw new NullPointerException();
                    }

                  }
                  //Error checking for the image
                  catch(NullPointerException exception)
                  {
                    error = "\"" + inputtedFile + "\" is not an image." +
                        " Please make sure that the file is an image." 
                        + " This line will not be displayed.";
                    JOptionPane.showMessageDialog(null, error, "Warning", 
                        JOptionPane.WARNING_MESSAGE);
                    continue;
                  }
                }
              }
              catch (InputMismatchException error)
              {
                System.out.println("File is not formatted correctly");
                continue;
              }
              updateList(listElement, listFileElement, inventoryModel,
                  inventoryFileModel);
            }
          }
          catch (FileNotFoundException error) 
          {
            System.out.println("File does not exist");
          }
          inventoryList = new JList(inventoryModel);
        }
        else if (chooserReturn == JFileChooser.CANCEL_OPTION)
        {
          System.out.println("Cancel was selected");
        }
      }
      //----------------------------------Save----------------------------------
      else if (event.getSource() == save)
      {
        //Running the save method
        save(fileChooser, chooserReturn, savedFile, outFile,inventoryFileModel);
      }
      //----------------------------------Quit----------------------------------
      else if (event.getSource() == quit)
      {
        //Closing the window
        dispose();
        System.exit(0);
      }
      //--------------------------------Add Item--------------------------------
      else if (event.getSource() == add)
      {
        //Sets up the JDialog when Add Item is selected
        path = null;
        System.out.println("Add item has been selected");
        AddItem addItem = new AddItem(frame, "Input Inventory Item", "null", 
            "null", "null", "null", "null", "null", 
            "null");
        addItem.pack();
        addItem.setVisible(true);
        if (confirmed == true)
        {
          //updates the list if needed
          updateList(listElement, listFileElement, inventoryModel,
              inventoryFileModel);
          inventoryList = new JList(inventoryModel);
          showItems.setSelectedIndex(0);
        }
      }
      //---------------------------------Modify---------------------------------
      else if (event.getSource() == modify)
      {
        //Finds the appropriate values for the selected item and
        //opens the add item JDialog with the current values
        //selectedRoom = (String) showItems.getSelectedItem();
        if (selectedRoom.equals("ALL"))
        {
          System.out.println("Selected Room = " + selectedRoom);
          selectedIndex = inventoryList.getSelectedIndex();
          System.out.println("Selected index = " + selectedIndex);
        }
        else
        {
          Object test = inventoryList.getSelectedValue();
          System.out.println("Test = " + test);
          selectedIndex = inventoryModel.indexOf(test);
        }
        if (selectedIndex != -1)
        {
          selectedElem = (String) inventoryModel.get(selectedIndex);
          imageElm = (String) inventoryFileModel.get(selectedIndex);
          setVariables(selectedElem, imageElm);
          File testFile = new File(path);
          if (!testFile.exists())
          {
            path = "null";
          }
          AddItem addItem = new AddItem(frame, "Input Inventory Item", 
              roomLocation, description, cost, month, 
              day, year, path);
          addItem.pack();
          addItem.setVisible(true);
          if (confirmed == true)
          {
            //updates the list if needed
            modifyList(selectedIndex, listElement, listFileElement, 
                inventoryModel, inventoryFileModel);

            inventoryList = new JList(inventoryModel);
            showItems.setSelectedIndex(0);
          }
        }
        else
        {
          //Let's the user know you have to select an item to modify.
          String info = "You must select and item to modify.";
          JOptionPane.showMessageDialog(null, info );
          System.out.println("You must select an item to modify");
        }
        System.out.println("Modify Item has been selected");
      }
      //---------------------------------Delete---------------------------------
      else if (event.getSource() == delete)
      {
        //Deletes the selected index
        //selectedIndex = inventoryList.getSelectedIndex();
        if (selectedRoom.equals("ALL"))
        {
          System.out.println("Selected Room = " + selectedRoom);
          selectedIndex = inventoryList.getSelectedIndex();
          System.out.println("Selected index = " + selectedIndex);
        }
        else
        {
          Object test = inventoryList.getSelectedValue();
          System.out.println("Test = " + test);
          selectedIndex = inventoryModel.indexOf(test);
        }
        if (selectedIndex != -1) 
        {
          System.out.println("Item has been deleted");
          inventoryModel.remove(selectedIndex);
          inventoryFileModel.remove(selectedIndex);
          inventoryList = new JList(inventoryModel);
          showItems.setSelectedIndex(0);
        }
        else
        {
          //Let's the user know you have to select an item to delete.
          String info = "You must select and item to delete.";
          JOptionPane.showMessageDialog(null, info );
          System.out.println("You must select an item to delete");
        }
      }
      //Sets up the JList and updates the GUI with the correct information
      //inventoryList = new JList(inventoryModel);
      listSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      inventoryList.setSelectionModel(listSelection);
      inventoryListPanel.add(inventoryList);
      calculateTotalCost((DefaultListModel) inventoryList.getModel());
      value.setText("Total Value of Displayed Items: " + totalCostString);
      totalValuePanel.add(value);
      System.out.println(path);
      validate();
      repaint();
      pack();
    }
    /**
     * This method makes sure the items are sorted accordingly or
     * only shows the items in the selected room
     */
    public void itemStateChanged(ItemEvent event)
    {

      //*******************************Sort Items*******************************
      selectedItem = (String) sortItems.getSelectedItem();
      //--------------------------------Item Name-------------------------------
      if (selectedItem == "Item Name")
      {
        sortItemName(inventoryModel, inventoryFileModel);
        System.out.println("Item Name selected");
      }
      //--------------------------------Location--------------------------------
      else if (selectedItem == "Location")
      {
        sortLocation(inventoryModel, inventoryFileModel);
        System.out.println("Location sort selected");
      }
      //---------------------------------Value----------------------------------
      else if (selectedItem == "Value")
      {
        sortValue(inventoryModel, inventoryFileModel);
        System.out.println("Value sort selected");
      }
      //----------------------------Purchase Date-------------------------------
      else if (selectedItem == "Purchase Date")
      {
        sortDate(inventoryModel, inventoryFileModel);
        System.out.println("Purchase Date selected"); 
      }
      //************************************************************************
      //*******************************Show Items*******************************
      selectedRoom = (String) showItems.getSelectedItem();
      //---------------------------------All Rooms------------------------------
      if (selectedRoom == "ALL")
      {
        inventoryList.removeAll();
        inventoryList = new JList(inventoryModel);
        System.out.println("selected all");
      }
      //---------------------------------Bedroom--------------------------------
      else if (selectedRoom == "Bedroom")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected Bedroom");
      }
      //-------------------------------DiningRoom-------------------------------
      else if (selectedRoom == "DiningRoom")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected DiningRoom");
      }
      //---------------------------------Garage---------------------------------
      else if (selectedRoom == "Garage")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected Garage");
      }
      //--------------------------------Kitchen---------------------------------
      else if (selectedRoom == "Kitchen")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected Kitchen");
      }
      //------------------------------LivingRoom--------------------------------
      else if (selectedRoom == "LivingRoom")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected LivingRoom");
      }
      //---------------------------------Outdoors-------------------------------
      else if (selectedRoom == "Outdoors")
      {
        showRoom(selectedRoom, tempModel, tempFileModel, inventoryModel, 
            inventoryFileModel);
        System.out.println("Selected Outdoors");
      }
      //************************************************************************
      //Adding the JList to the appropriate panel and setting the selection
      //mode to single selection
      listSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      inventoryList.setSelectionModel(listSelection);
      inventoryListPanel.removeAll();
      inventoryListPanel.add(inventoryList);
      calculateTotalCost((DefaultListModel) inventoryList.getModel());
      value.setText("Total Value of Displayed Items: " + totalCostString);
      totalValuePanel.add(value);
      validate();
      repaint();
      pack();
    }
    /**
     * This method saves the JList to a file in the correct format
     * @param fileChooser the fileChoose for the selected file
     * @param selectedOption integer to check if user clicked save or cancel
     * @param file creates the file based on the data from the file chooser
     * @param output the PrintStream so that the data can be outputted to
     *               this PrintStream
     * @param listModel The listModel that contains the appropriate data to
     *                  be outputted.
     */
    public void save(JFileChooser fileChooser, int selectedOption, File file,
        PrintStream output, DefaultListModel listModel)
    {
      fileChooser = new JFileChooser();
      selectedOption = fileChooser.showSaveDialog(fileChooser);
      if (selectedOption == JFileChooser.APPROVE_OPTION)
      {
        file = fileChooser.getSelectedFile();
        try
        {
          output = new PrintStream (file);
          for (int i = 0; i < listModel.getSize(); i++)
          {
            output.println(listModel.get(i));
          }
          output.close();
        }
        catch (FileNotFoundException error)
        {
          System.out.println("File not found");
        }
        System.out.println("Saving File");
      }
      else if (selectedOption == JFileChooser.CANCEL_OPTION)
      {
        System.out.println("Cancel was selected");
      }
    }
    /**
     * 
     * @param elem1
     * @param elem2
     */
    /**
     * This method sets the variables for the selected element accordingly
     * @param elem1 The selected element
     * @param elem2 The image element for the selected element
     */
    public void setVariables(String elem1, String elem2)
    {
      int locationIndex = elem1.indexOf("Location: ");
      int valueIndex = elem1.indexOf("Value: $");
      int purchaseIndex = elem1.indexOf("Purchased: ");
      int dateIndex1 = elem1.indexOf('/');
      int dateIndex2 = elem1.lastIndexOf('/');
      int imageIndex = elem2.lastIndexOf('^');
      description = elem1.substring(6, locationIndex-1);
      roomLocation = elem1.substring(locationIndex+10, valueIndex-1);
      cost = elem1.substring(valueIndex+8, purchaseIndex-1);
      month = elem1.substring(purchaseIndex+11, dateIndex1);
      month = monthList[Integer.valueOf(month)-1];
      day = elem1.substring(dateIndex1+1, dateIndex2);
      year = elem1.substring(dateIndex2+1, elem1.length());
      path = elem2.substring(imageIndex+1, elem2.length());
    }
    /**
     * This method updates the JList if the user adds an item to their
     * inventory
     * @param output The string that will hold the data in the list models
     * @param file The string that will hold the data for the file list model.
     * This is used so that the JList can be saved correctly.
     * @param listModel1 The list model that is displayed on GUI
     * @param listModel2 The list model that stores data for the output file.
     */
    public void updateList(String output, String file,
        DefaultListModel listModel1, 
        DefaultListModel listModel2)
    {
      output = "Item: " + description + " Location: " + 
          roomLocation + " Value: $" + cost + " Purchased: " 
          + month + "/" + day + "/" + year;
      file = description + "^" + roomLocation +"^" + cost +
          "^" + month + "^" + day + "^" + year + "^" + path;
      listModel1.addElement(output);
      listModel2.addElement(file);
    }
    /**
     * This method is similar to updateList except the JList is updated
     * when the user modifies an item in the inventory
     * @param index the index of the selected item
     * @param output The string that will hold the data in the list models
     * @param file The string that will hold the data for the file list model.
     * This is used so that the JList can be saved correctly.
     * @param listModel1 The list model that is displayed on GUI
     * @param listModel2 The list model that stores data for the output file.
     */
    public void modifyList(int index, String output, String file,
        DefaultListModel listModel1, 
        DefaultListModel listModel2)
    {
      output = "Item: " + description + " Location: " + roomLocation 
          + " Value: $" + cost + " Purchased: " + month + "/" 
          + day + "/" + year;
      file = description + "^" + roomLocation +"^" + cost +
          "^" + month + "^" + day + "^" + year + "^" + path;
      listModel1.set(index, output);
      listModel2.set(index, file);
    }
    /**
     * This method sorts the JList by item name.
     * @param listModel the listModel of the JList
     */
    public void sortItemName(DefaultListModel listModel, 
        DefaultListModel listModel2)
    {
      //Converts the list model to an array. The array is then sorted and
      //added back to the list model.
      Object [] content = listModel.toArray();
      Object [] content2 = listModel2.toArray();
      Arrays.sort(content);
      Arrays.sort(content2);
      listModel.removeAllElements();
      listModel2.removeAllElements();
      for (int i = 0; i < content.length; i++)
      {
        listModel.add(i, content[i]);
      }
      for (int j = 0; j < content2.length; j++)
      {
        listModel2.add(j, content2[j]);
      }
    }
    /**
     * This method sorts the JList by the item location
     * @param listModel the list model of the JList
     */
    public void sortLocation (DefaultListModel listModel, 
        DefaultListModel listModel2)
    {
      //Converts the JList to an array. The location of each element is found.
      //Then the array is sorted by the location. 
      Object [] content = listModel.toArray();
      Object [] content2 = listModel2.toArray();
      for (int i = 0; i < content.length; i++)
      {
        for (int j = 1; j <= content.length - 1; j++)
        {
          String currentElem1 = (String) content[j-1];
          String currentElem2 = (String) content[j];
          int locationIndex1 = currentElem1.indexOf("Location: ");
          int valueIndex1 = currentElem1.indexOf("Value: $");
          String roomLocation1 = currentElem1.substring(locationIndex1+10, 
              valueIndex1-1);
          int locationIndex2 = currentElem2.indexOf("Location: ");
          int valueIndex2 = currentElem2.indexOf("Value: $");
          String roomLocation2 = currentElem2.substring(locationIndex2+10, 
              valueIndex2-1);
          if (roomLocation1.compareTo(roomLocation2) > 0)
          {
            content[j] = currentElem1;
            content[j-1] = currentElem2;
          }
        }
      }
      for (int i = 0; i < content2.length; i++)
      {
        for (int j = 1; j <= content2.length - 1; j++)
        {
          String currentElem1 = (String) content2[j-1];
          String currentElem2 = (String) content2[j];
          int locationIndex1 = currentElem1.indexOf("^");
          int valueIndex1 = currentElem1.indexOf("^", locationIndex1+1);
          String roomLocation1 = currentElem1.substring(locationIndex1, 
              valueIndex1);
          int locationIndex2 = currentElem2.indexOf("^");
          int valueIndex2 = currentElem2.indexOf("^", locationIndex2+1);
          String roomLocation2 = currentElem2.substring(locationIndex2, 
              valueIndex2);
          if (roomLocation1.compareTo(roomLocation2) > 0)
          {
            content2[j] = currentElem1;
            content2[j-1] = currentElem2;
          }
        }
      }
      //This adds the array back into the list model
      listModel.removeAllElements();
      listModel2.removeAllElements();
      for (int i = 0; i < content.length; i++)
      {
        listModel.add(i, content[i]);
      }
      for (int i = 0; i < content2.length; i++)
      {
        listModel2.add(i, content2[i]);
      }
    }
    /**
     * This method sorts the JList by the cost of the item
     * @param listModel the list model used by the JList
     */
    public void sortValue (DefaultListModel listModel, 
        DefaultListModel listModel2)
    {
      //The list model is converted to an array. Then the cost of each element
      //in the array is found and sorted accordingly.
      Object [] content = listModel.toArray();
      Object [] content2 = listModel2.toArray();
      for (int i = 0; i < content.length; i++)
      {
        for (int j = 1; j <= content.length - 1; j++)
        {
          String currentElem1 = (String) content[j-1];
          String currentElem2 = (String) content[j];
          int valueIndex1 = currentElem1.indexOf("Value: $");
          int purchaseIndex1 = currentElem1.indexOf(" Purchased: ");
          String cost1 = currentElem1.substring(valueIndex1+8, purchaseIndex1);
          int valueIndex2 = currentElem2.indexOf("Value: $");
          int purchaseIndex2 = currentElem2.indexOf(" Purchased: ");
          String cost2 = currentElem2.substring(valueIndex2+8, purchaseIndex2);
          if (Double.valueOf(cost1) > Double.valueOf(cost2))
          {
            content[j] = currentElem1;
            content[j-1] = currentElem2;
          }
        }
      }
      for (int i = 0; i < content2.length; i++)
      {
        for (int j = 1; j <= content2.length - 1; j++)
        {
          String currentElem1 = (String) content2[j-1];
          String currentElem2 = (String) content2[j];
          int locationIndex1 = currentElem1.indexOf("^");
          int valueIndex1 = currentElem1.indexOf("^", locationIndex1+1);
          int purchaseIndex1 = currentElem1.indexOf("^", valueIndex1+1);
          String cost1 = currentElem1.substring(valueIndex1+1, purchaseIndex1);
          int locationIndex2 = currentElem2.indexOf("^");
          int valueIndex2 = currentElem2.indexOf("^", locationIndex2+1);
          int purchaseIndex2 = currentElem2.indexOf("^", valueIndex2+1);
          String cost2 = currentElem2.substring(valueIndex2+1, purchaseIndex2);
          if (Double.valueOf(cost1) > Double.valueOf(cost2))
          {
            content2[j] = currentElem1;
            content2[j-1] = currentElem2;
          }
        }
      }
      //The elements of the array are then added back into the list model.
      listModel.removeAllElements();
      listModel2.removeAllElements();
      for (int i = 0; i < content.length; i++)
      {
        listModel.add(i, content[i]);
      }
      for (int i = 0; i < content2.length; i++)
      {
        listModel2.add(i, content2[i]);
      }
    }
    /**
     * This method sorts the JList by the item purchase date
     * @param listModel the list model of the JList
     */
    public void sortDate (DefaultListModel listModel, 
        DefaultListModel listModel2)
    {
      //Converts the list model into an array. The purchase month, day, and
      //year are then found for each element of the array. The array is then
      //sorted accordingly
      Object [] content = listModel.toArray();
      Object [] content2 = listModel2.toArray();
      for (int i = 0; i < content.length; i++)
      {
        for (int j = 1; j <= content.length-1; j++)
        {
          String currentElem1 = (String) content[j-1];
          String currentElem2 = (String) content[j];
          int yearIndex1 = currentElem1.lastIndexOf('/');
          int yearIndex2 = currentElem2.lastIndexOf('/');
          String year1 = currentElem1.substring(yearIndex1+1, 
              currentElem1.length());
          String year2 = currentElem2.substring(yearIndex2+1, 
              currentElem2.length());
          if (year1.equals("<1990"))
          {
            year1 = "1989";
          }
          if (year2.equals("<1990"))
          {
            year2 = "1989";
          }
          if (year1.compareTo(year2) > 0)
          {
            content[j] = currentElem1;
            content[j-1] = currentElem2;
          }
          else if (year1.compareTo(year2) == 0)
          {
            for (int k = 0; k < content.length; k++)
            {
              for (int h = 1; h <= content.length -1; h++)
              {
                int monthIndex1 = currentElem1.indexOf('/');
                int monthIndex2 = currentElem2.indexOf('/');
                int purchaseIndex1 = currentElem1.indexOf("Purchased: ");
                int purchaseIndex2 = currentElem2.indexOf("Purchased: ");
                String month1 = currentElem1.substring(purchaseIndex1+11, 
                    monthIndex1);
                String month2 = currentElem2.substring(purchaseIndex2+11, 
                    monthIndex2);
                if (Integer.valueOf(month1) > Integer.valueOf(month2))
                {
                  content[j] = currentElem1;
                  content[j-1] = currentElem2;
                }
                else if (Integer.valueOf(month1) == Integer.valueOf(month2))
                {
                  int dayIndex1 = currentElem1.indexOf('/');
                  int dayIndex2 = currentElem2.indexOf('/');
                  int dayIndex11 = currentElem1.lastIndexOf('/');
                  int dayIndex22 = currentElem2.lastIndexOf('/');
                  String day1 = currentElem1.substring(dayIndex1+1, dayIndex11);
                  String day2 = currentElem2.substring(dayIndex2+1, dayIndex22);
                  for (int m = 0; m < content.length; m++)
                  {
                    for (int q = 1; q <= content.length-1; q++)
                    {
                      if (Integer.valueOf(day1) > Integer.valueOf(day2))
                      {
                        content[j] = currentElem1;
                        content[j-1] = currentElem2;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      for (int i = 0; i < content2.length; i++)
      {
        for (int j = 1; j <= content2.length-1; j++)
        {
          String currentElem1 = (String) content2[j-1];
          String currentElem2 = (String) content2[j];
          int locationIndex1 = currentElem1.indexOf("^");
          int valueIndex1 = currentElem1.indexOf("^", locationIndex1+1);
          int purchaseIndex1 = currentElem1.indexOf("^", valueIndex1+1);
          int monthIndex1 = currentElem1.indexOf("^", purchaseIndex1+1);
          int dayIndex1 = currentElem1.indexOf("^", monthIndex1+1);
          int yearIndex1 = currentElem1.lastIndexOf("^");
          int locationIndex2 = currentElem2.indexOf("^");
          int valueIndex2 = currentElem2.indexOf("^", locationIndex2+1);
          int purchaseIndex2 = currentElem2.indexOf("^", valueIndex2+1);
          int monthIndex2 = currentElem2.indexOf("^", purchaseIndex2+1);
          int dayIndex2 = currentElem2.indexOf("^", monthIndex2+1);
          int yearIndex2 = currentElem2.lastIndexOf("^");
          String year1 = currentElem1.substring(dayIndex1+1, 
              yearIndex1);
          String year2 = currentElem2.substring(dayIndex2+1, 
              yearIndex2);
          if (year1.equals("<1990"))
          {
            year1 = "1989";
          }
          if (year2.equals("<1990"))
          {
            year2 = "1989";
          }
          if (year1.compareTo(year2) > 0)
          {
            content2[j] = currentElem1;
            content2[j-1] = currentElem2;
          }
          else if (year1.compareTo(year2) == 0)
          {
            for (int k = 0; k < content2.length; k++)
            {
              for (int h = 1; h <= content2.length -1; h++)
              {
                String month1 = currentElem1.substring(purchaseIndex1+1, 
                    monthIndex1);
                String month2 = currentElem2.substring(purchaseIndex2+1, 
                    monthIndex2);
                if (Integer.valueOf(month1) > Integer.valueOf(month2))
                {
                  content2[j] = currentElem1;
                  content2[j-1] = currentElem2;
                }
                else if (Integer.valueOf(month1) == Integer.valueOf(month2))
                {
                  String day1 = currentElem1.substring(monthIndex1+1, 
                      dayIndex1);
                  String day2 = currentElem2.substring(monthIndex2+1, 
                      dayIndex2);
                  for (int m = 0; m < content2.length; m++)
                  {
                    for (int q = 1; q <= content2.length-1; q++)
                    {
                      if (Integer.valueOf(day1) > Integer.valueOf(day2))
                      {
                        content2[j] = currentElem1;
                        content2[j-1] = currentElem2;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      //The array elements are then added back into the list model
      listModel.removeAllElements();
      listModel2.removeAllElements();
      for (int i = 0; i < content.length; i++)
      {
        listModel.add(i, content[i]);
      }
      for (int i = 0; i < content2.length; i++)
      {
        listModel2.add(i, content2[i]);
      }
    }
    /**
     * This method shows items in the selected room.
     * @param room The room that user wants to items to be shown in
     * @param listModel1 The temporary list model for the updated JList
     * @param listModel2 THe temporary list model for the updates JList
     *                   in case this data should be saved
     * @param listModel3 The original list model of the JList
     * @param listModel4 The original list model of the JList for exporting
     *                   purposes. 
     */
    public void showRoom(String room, DefaultListModel listModel1, 
        DefaultListModel listModel2, 
        DefaultListModel listModel3,
        DefaultListModel listModel4)  
    {
      //Searched through all of the elements and displays only the elements
      //in the selected room
      listModel1.removeAllElements();
      listModel2.removeAllElements();
      for (int i = 0; i < listModel3.getSize(); i++)
      {
        String currentElem = (String) listModel3.get(i);

        int locationIndex = currentElem.indexOf("Location: ");
        int valueIndex = currentElem.indexOf("Value: $");
        String location = currentElem.substring(locationIndex+10, valueIndex-1);
        if (location.equals(room))
        {
          listModel1.addElement(currentElem);
          listModel2.addElement(listModel4.get(i));
        }
      }
      inventoryList.removeAll();
      inventoryList = new JList(listModel1);
    }
    /**
     * This method calculates the total cost of the items displayed.
     * @param listModel The list model of the JList
     */
    public void calculateTotalCost(DefaultListModel listModel)
    {
      totalCost = 0;
      //Goes through each element of the JList and updates the total cost.
      for (int i = 0; i < listModel.getSize(); i++)
      {
        String currentElem = (String) listModel.get(i);
        int valueIndex = currentElem.indexOf("Value: $");
        int purchaseIndex = currentElem.indexOf("Purchased: ");
        cost = currentElem.substring(valueIndex+8, purchaseIndex-1);
        totalCost = Double.valueOf(cost) + totalCost;
      }
      //Makes sure the total cost has only two decimal places.
      DecimalFormat df = new DecimalFormat("#.##");
      totalCostString =  "$" + df.format(totalCost);
      if (Double.valueOf(totalCost) == Math.floor(Double.valueOf(totalCost)))
      {
        totalCostString = totalCostString + ".00";
      }
      int decimalIndex = totalCostString.indexOf(".");
      if (totalCostString.substring(decimalIndex+1).length() ==1)
      {
        totalCostString = totalCostString + "0";
      }
      //No items displayed if there are no items.
      if (listModel.getSize() == 0)
      {
        totalCostString = "No Items Displayed";
      }
    }
  }
}




package eecs285.proj3.oghomesh;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.NoSuchElementException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * This class sets up the GUI when a user wants to add or modify an item.
 * This class also makes sure that the user inputs the correct values for
 * each input.
 */
@SuppressWarnings("serial")
public class AddItem extends JDialog
{
  //Declaring variables/GUI elements
  private JPanel topPanel;
  private JPanel topPanelEdit;
  private JPanel topPanelSelectImage;
  private JPanel imagePanel;
  private JPanel bottomPanel;
  private JTextField descriptionField;
  private JTextField valueField;
  private JComboBox locationSelector;
  private JComboBox monthSelector;
  private JComboBox daySelector;
  private JComboBox yearSelector;
  private JLabel description ;
  private JLabel value;
  private JLabel imageStatus;
  private JButton selectImage;
  private JButton confirm;
  private JButton cancel;
  private AddItemListener addItemListener;
  private ComboBoxModel days[];
  private DefaultComboBoxModel days31;
  private DefaultComboBoxModel days30;
  private DefaultComboBoxModel days29;
  
  /**
   * This constructor sets up the GUI window when adding/modifying items
   * @param homeInventory The name of the JFrame that the JDialog goes over
   * @param title The title of the window
   * @param location If the item, is being modified, the original location of
   *                 the item, otherwise null.
   * @param textDescription If the item is being modified, the original
   *                        description of the item, otherwise null
   * @param valueText If the item is being modified, the original cost of the
   *                  the item. Otherwise null.
   * @param monthText If the items is being modified, the original month the
   *                  the item was purchased, otherwise null.
   * @param day If the item is being modified, the original purchase day of
   *            the item, otherwise null.
   * @param yearText If the item is being modified, the original purchase
   *                 year of the item. Otherwise null.
   * @param path If the item is being modified, the original file path for the
   *             the image, otherwise null. If there is no image, this value
   *             should be null.
   */
  public AddItem(JFrame homeInventory, String title, String location, 
      String textDescription, String valueText, String monthText, 
      String day, String yearText, String path)
  {
    super(homeInventory, title, true);
    addItemListener = new AddItemListener();

    //Sets up location combo box
    locationSelector = new JComboBox();
    locationSelector.addItem("Bedroom");
    locationSelector.addItem("DiningRoom");
    locationSelector.addItem("Garage");
    locationSelector.addItem("Kitchen");
    locationSelector.addItem("LivingRoom");
    locationSelector.addItem("Outdoors");
    if (location != "null")
    {
      locationSelector.setSelectedItem(location);
    }

    //Sets up the description and value text box
    description = new JLabel("Description", SwingConstants.RIGHT);
    value = new JLabel("Value", SwingConstants.RIGHT);
    if (textDescription == "null")
    {
      descriptionField = new JTextField(20);
    }
    else
    {
      descriptionField = new JTextField(textDescription, 20);
    }
    if (valueText == "null")
    {
      valueField = new JTextField(20);
    }
    else
    {
      valueField = new JTextField(valueText, 20);
    }

    //Sets up the month combo box
    String [] month = {"January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November",
    "December"};
    monthSelector = new JComboBox(month);
    if (monthText != "null")
    {
      monthSelector.setSelectedItem(monthText);
    }
    monthSelector.addItemListener(addItemListener);

    //Sets up the day selector combo box
    //Creating three different models for months with 31, 30, or 29 days
    daySelector = new JComboBox();
    days = new ComboBoxModel[3];
    days31 = new DefaultComboBoxModel();
    days30 = new DefaultComboBoxModel();
    days29 = new DefaultComboBoxModel();
    for (int i = 1; i <= 31; i++)
    {
      days31.insertElementAt(Integer.toString(i), i-1);
    }
    for (int i = 1; i<= 30; i++)
    {
      days30.insertElementAt(Integer.toString(i), i-1);
    }
    for (int i = 1; i <= 29; i++)
    {
      days29.insertElementAt(Integer.toString(i), i-1);
    }
    days[0] = days31;
    days[1] = days30;
    days[2] = days29;
    int selectedValue = monthSelector.getSelectedIndex();
    if (selectedValue == 0 || selectedValue == 2 || selectedValue == 4 ||
        selectedValue == 6 || selectedValue == 7 || selectedValue == 9 ||
        selectedValue == 11)
    {
      daySelector.setModel(days[0]);
    }
    //These months have 30 days
    else if (selectedValue == 3 || selectedValue == 5 || selectedValue == 8 || 
        selectedValue == 10)
    {
      daySelector.setModel(days[1]);
    }
    //February as 29 days
    else if (selectedValue == 1)
    {
      daySelector.setModel(days[2]);
    }
    if (day != "null")
    {
      daySelector.setSelectedItem(day);
    }

    //Setting up select image button and label
    if (path.equals("null") || path == null)
    {
      imageStatus = new JLabel("No Image Selected");
    }
    else
    {
      try
      {
        File imageFile = new File(path);
        BufferedImage image = ImageIO.read(imageFile);
        imageStatus = new JLabel (new ImageIcon(image));
        validate();
        repaint();
        pack();
        HomeInventoryManager.path = path;
        System.out.println("Opening File");
        System.out.println("Image has been read");
      }
      catch (IOException error)
      {
        System.out.println("Path does not exist");
      }
    }
    selectImage = new JButton("Select Image");
    selectImage.addActionListener(addItemListener);

    //Setting up confirm and cancel button
    confirm = new JButton("Confirm");
    cancel = new JButton("Cancel");
    confirm.addActionListener(addItemListener);
    cancel.addActionListener(addItemListener);

    //Sets up the year selector combo box
    yearSelector = new JComboBox();
    for (int i = 2013; i >= 1990; i--)
    {
      yearSelector.addItem(Integer.toString(i));
    }
    yearSelector.addItem("<1990");
    if (yearText != "null")
    {
      yearSelector.setSelectedItem(yearText);
    }

    //Setting up top panel that contains the location combo box,
    //the item description, value, and purchase date. The top panel
    //also contains the button to select the image of the item
    topPanel = new JPanel (new GridLayout(2,1));
    topPanelEdit = new JPanel (new FlowLayout());
    topPanelEdit.add(locationSelector);
    topPanelEdit.add(description);
    topPanelEdit.add(descriptionField);
    topPanelEdit.add(value);
    topPanelEdit.add(valueField);
    topPanelEdit.add(monthSelector);
    topPanelEdit.add(daySelector);
    topPanelEdit.add(yearSelector);
    topPanelSelectImage = new JPanel ();
    topPanelSelectImage.add(selectImage);
    topPanel.add(topPanelEdit);
    topPanel.add(topPanelSelectImage);
    add(topPanel, BorderLayout.NORTH);

    //Setting up image panel that contains the image of the item
    //or the default label
    imagePanel = new JPanel();
    imagePanel.add(imageStatus);
    add(imagePanel, BorderLayout.CENTER); 

    //Setting up bottom panel that contains the confirm and cancel
    //buttons
    bottomPanel = new JPanel (new FlowLayout());
    bottomPanel.add(confirm);
    bottomPanel.add(cancel);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * This subclass implements the ActionListener and ItemListener so that
   * changes to the GUI can be read.
   * @author Omid Ghomeshi
   */
  public class AddItemListener implements ActionListener, ItemListener
  {
    //Declaring necessary variables
    private JFileChooser fileChooser;
    private int chooserReturn;
    private File selectedPictureFile;
    JOptionPane pane;
    /**
     * This method implements the actionPerformed method so that every time
     * a value is changed, the GUI registers the change and stores the value.
     */
    public void actionPerformed(ActionEvent event)
    {
      //If the select image button is pressed, a dialog opens up that
      //allows the user to select any file. If the file is not a Java
      //supported image, then an error message is given. If the file is
      //supported, then the image status label is replaced with the image
      if (event.getSource() == selectImage)
      {
        System.out.println("Select Image button has been selected");
        fileChooser = new JFileChooser();
        chooserReturn = fileChooser.showOpenDialog(AddItem.this);
        if (chooserReturn == JFileChooser.APPROVE_OPTION)
        {
          selectedPictureFile = fileChooser.getSelectedFile();
          String error1 = "The selected file \"" + selectedPictureFile + "\""
                          + " is not a valid image. Please make sure the file"
                          + " is an image file.";
          //Try-block statement makes sure that selected file is an image.
          //Outputs appropriate error messages otherwise.
          try 
          {
            BufferedImage selectedPicture = ImageIO.read(selectedPictureFile);
            imagePanel.remove(imageStatus);
            imageStatus = new JLabel (new ImageIcon(selectedPicture));
            imagePanel.add(imageStatus);
            validate();
            repaint();
            pack();
            HomeInventoryManager.path = selectedPictureFile.getAbsolutePath();
            System.out.println("Opening File");
            System.out.println("Image has been read");
          } 
          catch (IOException error) 
          {
            System.out.println("Image not read.");
          }
          catch (IllegalArgumentException error)
          {
            System.out.println("Please Select a file to save");
          }
          catch (NullPointerException error)
          {
            JOptionPane.showMessageDialog(null, error1, "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.out.println("Selected File is Not an image");
          }
        }
      }
      // If confirmed is selected, all the values are updated appropriately
      // Error checking is also done in this portion.
      else if (event.getSource() == confirm)
      {
        HomeInventoryManager.confirmed = true;
        HomeInventoryManager.roomLocation = (String) 
                                            locationSelector.getSelectedItem();
        HomeInventoryManager.description = descriptionField.getText();
        HomeInventoryManager.cost = valueField.getText();
        int monthSelected = monthSelector.getSelectedIndex() + 1;
        HomeInventoryManager.month = Integer.toString(monthSelected);
        HomeInventoryManager.day = (String) daySelector.getSelectedItem();
        HomeInventoryManager.year = (String) yearSelector.getSelectedItem();
        String error1 = "Initialization";
        String error2 = "Initialization";
        String error3 = "Initialization";
        pane = new JOptionPane();
        try
        {
          error1 = "\"" + HomeInventoryManager.cost + "\" is not a valid" 
              + " input for value. Please input an integer or a " + 
              "number with 2 decimals.";
          error2 = "You did not input anything in description. Do you want to" 
              + " continue?";
          error3 = "Please input the day you purchased the item.";
          @SuppressWarnings("unused")
          double costTemp = Double.valueOf(HomeInventoryManager.cost);
          int indexOfDecimal = HomeInventoryManager.cost.indexOf('.');
          if (indexOfDecimal != -1)
          {
            String tempCost = 
                HomeInventoryManager.cost.substring(indexOfDecimal+1);
            if (tempCost.length() == 0)
            {
              HomeInventoryManager.cost = 
                  valueField.getText().substring(0, indexOfDecimal);
            }
            else if (tempCost.length() != 2)
            {
              throw new NumberFormatException();
            }
          }
          if ((Double.valueOf(HomeInventoryManager.cost) == 
              Math.floor(Double.valueOf(HomeInventoryManager.cost)))
              && indexOfDecimal == -1)
          {
            HomeInventoryManager.cost = HomeInventoryManager.cost + ".00";
          }
          else if ((Double.valueOf(HomeInventoryManager.cost) == 
              Math.floor(Double.valueOf(HomeInventoryManager.cost))
              && indexOfDecimal != -1 &&
              HomeInventoryManager.cost.substring(indexOfDecimal).isEmpty()))
          {
            System.out.println("This is a test");
            HomeInventoryManager.cost = HomeInventoryManager.cost + ".00";
          }
          if (HomeInventoryManager.day == null)
          {
            throw new NoSuchElementException();
          }
          if (HomeInventoryManager.description == null ||
              HomeInventoryManager.description.isEmpty())
          {
            throw new IllegalArgumentException();
          }
          System.out.println("Confirm has been selected");
          dispose();
        }
        catch (NumberFormatException exception)
        {
          System.out.println("Cost is not valid");
          JOptionPane.showMessageDialog(null, error1, "Error", 
              JOptionPane.ERROR_MESSAGE);
        }
        catch (NullPointerException exception)
        {
          System.out.println("Cost is not valid");
          JOptionPane.showMessageDialog(null, error1, "Error", 
              JOptionPane.ERROR_MESSAGE);
        }
        catch (NoSuchElementException exception)
        {
          JOptionPane.showMessageDialog(null, error3, "Error", 
              JOptionPane.ERROR_MESSAGE);
        }
        catch (IllegalArgumentException exception)
        {
          int result = JOptionPane.showConfirmDialog(null, error2, "Warning",
              JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
          if (result == JOptionPane.YES_OPTION)
          {
            System.out.println("Confirm has been Selected");
            dispose();
          }
        }
      }
      else if (event.getSource() == cancel)
      {
        HomeInventoryManager.confirmed = false;
        System.out.println("Cancel has been selected");
        dispose();
      }
    }
    /**
     * This method implements the itemStateChanged so that when a user changes
     * a value in the Combo Box, the GUI registers the change
     */
    public void itemStateChanged(ItemEvent event)
    {
      //Adjusts the day selector combo box based on the month of the year 
      int selectedValue = monthSelector.getSelectedIndex();
      //These months all have 31 days
      if (selectedValue == 0 || selectedValue == 2 || selectedValue == 4 ||
          selectedValue == 6 || selectedValue == 7 || selectedValue == 9 ||
          selectedValue == 11)
      {
        daySelector.setModel(days[0]);
      }
      //These months have 30 days
      else if (selectedValue == 3 || selectedValue == 5 || selectedValue == 8 || 
          selectedValue == 10)
      {
        daySelector.setModel(days[1]);
      }
      //February as 29 days
      else if (selectedValue == 1)
      {
        daySelector.setModel(days[2]);
      }
    }
  }
}

package eecs285.proj3.oghomesh;

public class Main
{
  public static void main (String [] args)
  {
    HomeInventoryManager inventory = 
        new HomeInventoryManager("Home Inventory System");
    inventory.pack();
    inventory.setVisible(true);
  }
}
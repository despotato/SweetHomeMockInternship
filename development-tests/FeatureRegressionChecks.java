import com.eteks.sweethome3d.io.*;
import com.eteks.sweethome3d.model.*;
import com.eteks.sweethome3d.swing.*;
import com.eteks.sweethome3d.viewcontroller.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;

public class FeatureRegressionChecks {
 static void check(boolean ok,String message) {if(!ok)throw new AssertionError(message);}
 static int checks=0;
 public static void main(String[] args)throws Exception {
  System.setProperty("java.awt.headless","true");System.setProperty("com.eteks.sweethome3d.no3D","true");Locale.setDefault(Locale.US);
  DefaultUserPreferences prefs=new DefaultUserPreferences();
  Home h=new Home();
  CatalogPieceOfFurniture cat=new CatalogPieceOfFurniture("small",null,null,2,3,4,true,false);
  HomePieceOfFurniture small=new HomePieceOfFurniture(cat),large=new HomePieceOfFurniture(new CatalogPieceOfFurniture("large",null,null,3,4,5,true,false));
  check(cat.getVolume()==24,"catalog volume"); checks++;
  h.addPieceOfFurniture(large);h.addPieceOfFurniture(small);
  FurnitureTable t=new FurnitureTable(h,prefs);
  h.setFurnitureSortedProperty(HomePieceOfFurniture.SortableProperty.VOLUME);
  check(t.getValueAt(0,0)==small,"ascending volume");checks++;
  h.setFurnitureDescendingSorted(true);check(t.getValueAt(0,0)==large,"descending volume");checks++;
  small.setWidth(10);check(t.getValueAt(0,0)==small,"resizing re-sorts volume");checks++;
  FurnitureController fc=new FurnitureController(h,prefs,new SwingViewFactory());
  fc.toggleFurnitureVisibleProperty(HomePieceOfFurniture.SortableProperty.VOLUME);
  fc.toggleFurnitureVisibleProperty(HomePieceOfFurniture.SortableProperty.VOLUME);
  check(h.getFurnitureVisibleProperties().contains(HomePieceOfFurniture.SortableProperty.VOLUME),"show/hide volume");checks++;
  for(int i=0;i<3;i++){Level l=new Level("L"+i,i*250,10,240);h.addLevel(l);Wall w=new Wall(0,0,200,0,10,240);w.setLevel(l);h.addWall(w);}
  h.getLevels().get(1).setViewable(false);Level original=h.getLevels().get(2);h.setSelectedLevel(original);
  PlanComponent plan=new PlanComponent(h,prefs,null);PageFormat pf=new PageFormat();BufferedImage im=new BufferedImage(800,1100,BufferedImage.TYPE_INT_RGB);Graphics2D g=im.createGraphics();
  check(plan.print(g,pf,1)==Printable.PAGE_EXISTS,"hidden levels skipped");checks++;
  check(plan.print(g,pf,2)==Printable.NO_SUCH_PAGE,"no extra hidden-level page");checks++;
  check(plan.print(g,pf,-1)==Printable.NO_SUCH_PAGE,"negative page");checks++;
  check(h.getSelectedLevel()==original,"nonsequential print restores selection");checks++;
  boolean failed=false;try{plan.print(null,pf,0);}catch(RuntimeException ex){failed=true;}
  check(failed && h.getSelectedLevel()==original,"render failure restores selection");checks++;
  Home empty=new Home();empty.addLevel(new Level("Empty",0,10,240));
  check(new PlanComponent(empty,prefs,null).print(g,pf,0)==Printable.PAGE_EXISTS,"empty level still has page");checks++;
  SwingUtilities.invokeAndWait(()->{
   FurnitureTable throwing=new FurnitureTable(h,prefs){public Printable getPrintable(JTable.PrintMode mode,java.text.MessageFormat header,java.text.MessageFormat footer){return (graphics,format,index)->{throw new PrinterException("deliberate test failure");};}};
   TableColumnModel columns=throwing.getColumnModel();Color grid=throwing.getGridColor();java.util.List<HomePieceOfFurniture.SortableProperty> visible=new ArrayList<>(h.getFurnitureVisibleProperties());
   try{throwing.print(g,pf,0);throw new AssertionError("expected print exception");}catch(PrinterException expected){}
   check(throwing.getColumnModel()==columns && throwing.getGridColor().equals(grid) && h.getFurnitureVisibleProperties().equals(visible),"table failure restores columns, grid, and visible properties");
  });checks++;
  g.dispose();System.out.println("Additional regression checks passed: "+checks);
 }
}

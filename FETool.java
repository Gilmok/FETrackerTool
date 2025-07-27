import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class KeyItem
{
	String name;
	Image pict;
	int index;
	boolean collected;
	boolean required;
	boolean used;
	Rectangle r;
	//int state;
	
	/*static final int SEED_RQUIRED = 1;
	static final int COLLECTED = 2;
	static final int USED = 4;*/
	
	public KeyItem(String data, Image img)
	{
		String[] pt = data.split(";");
		index = Integer.parseInt(pt[0]);
		index--;
		name = pt[1];
		pict = img;
		collected = false;
		required = false;
		int xx = index % 6;
		int yy = index / 6;
		r = new Rectangle(xx * 50 + 10, yy * 50, 50, 50);
	}
	
	
}

class Indicator
{
	String name;
	Image pict;
	byte state;
	Rectangle r;
	int index;
	public Color color;
	
	public Indicator(String data, Image img, int idx, Color c)
	{
		name = data;
		pict = img;
		state = 0;
		index = idx;
		int yy = index * 50;
		r = new Rectangle(6 * 50 + 10, yy, 50, 50);
		color = c;
	}
	
}

/*class KIPict
{
	String name;
	Image pict;
	byte state;
	Rectangle rect;
}*/

class KITopPanel extends JPanel implements MouseListener
{
	TrackerPanel parent;
	Indicator[] indics;
	KeyItem[] kpicts;
	boolean goMode;
	
	public KITopPanel(TrackerPanel par)
	{
		addMouseListener(this);
		parent = par;
		kpicts = FETool.kis;
		indics = FETool.indics;
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, 400, 400);
		for(int i = 0; i < kpicts.length; i++)
		{
			KeyItem ki = kpicts[i];
			if(ki.used)
				g.setColor(Color.blue);
			else if(ki.collected)
				g.setColor(Color.green);
			else if(ki.required)
				g.setColor(new Color(255, 255, 127, 127));
			else
				g.setColor(Color.black);
			g.fillRect(ki.r.x, ki.r.y, ki.r.width, ki.r.height);
			g.drawImage(ki.pict, ki.r.x + 7, ki.r.y + 7, null);
			
		}
		
		int ck = countKIs();
		if(ck < 10)
			g.setColor(Color.orange);
		else
			g.setColor(Color.green);
		
		g.drawString(ck + " / 17", 10, 175);
		
		g.setColor(Color.yellow);
		String obj = getObjectiveString();
		g.drawString(obj, 100, 175);
		if(goMode)
			indics[3].state = 1;
		
		for(int i = 0; i < indics.length; i++)
		{
			Indicator ii = indics[i];
			if(ii.state == 1)
				g.setColor(ii.color);
			else
				g.setColor(Color.black);
			g.fillRect(ii.r.x, ii.r.y, ii.r.width, ii.r.height);
			g.drawImage(ii.pict, ii.r.x + 7, ii.r.y + 7, null);
			
		}
		
		
		
	}
	
	private int countKIs()
	{
		int rv = 0;
		for(int i = 0; i < kpicts.length - 1; i++)  //skip the pass
			if(kpicts[i].collected == true)
				rv++;
		return rv;
	}
	
	public String getObjectiveString()
	{
		int open = 0;
		int complete = 0;
		int needed = FETool.reqObjCount;
		//open objectives
		if(FETool.seedObjectives != null)
		{
			for(int i = 0; i < FETool.seedObjectives.length; i++)
			{
				int oo = FETool.seedObjectives[i];
				if(oo == -1)  //cannot complete an unset objective
					continue;
				FEObjective obj = FETool.allObjectives[oo];
				if(obj.type == 32 && obj.name.contains("Key Items"))
				{
					int c = countKIs();
					if(c >= obj.qty)
					{
						open++;
						complete++;
						parent.parent.sop.oComplete[i].setSelected(true);
					}
				}
				if(FETool.seedLocations != null)
				{
					for(int j = 0; j < FETool.seedLocations.size(); j++)
					{
						Location ll = FETool.allLocations.get(FETool.seedLocations.get(j));
						if(ll.objectiveIndex == oo)
						{
							if(ll.found)
								open++;
							if(ll.visited)
							{
								complete++;
								parent.parent.sop.oComplete[i].setSelected(true);
							}
							break;
						}		
					}
				}
			}
		}
		
		if(open >= needed && needed > 0)
			goMode = true;
		else
			goMode = false;
		String rv = "Obj Req " + needed + "   Opn " + open + "   Cmp " + complete;
		return rv;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		int mx = e.getX();
		int my = e.getY();
		
		for(int i = 0; i < kpicts.length; i++)
		{
			if(kpicts[i].r.contains(mx, my))
			{
				//will be yellow if required for any objective in seed
				
				//mark the ki as collected if not collected (green)
				if(kpicts[i].collected == false)
				{
					kpicts[i].collected = true;
					parent.oList.collectKI(i);
				}
				else if(kpicts[i].collected == true && kpicts[i].used == false)  //mark as used (blue)
					kpicts[i].used = true;
				else if(kpicts[i].used == true)
				{
					kpicts[i].collected = false;
					kpicts[i].used = false;
					parent.oList.uncollectKI(i);
				}
				repaint();
				return;
			}
		}
		for(int i = 0; i < indics.length; i++)
		{
			if(indics[i].r.contains(mx, my))
			{
				indics[i].state ^= 1;
			}
		}
		repaint();
	}

	public void clearSeed() 
	{
		for(int i = 0; i < kpicts.length; i++)
		{
			kpicts[i].collected = false;
			kpicts[i].used = false;
			kpicts[i].required = false;
		}
		for(int i = 0; i < indics.length; i++)
		{
			indics[i].state = 0;
		}
		
	}
	
	
	
}

class KIListPanel extends JPanel implements ListSelectionListener, MouseListener
{
	class HilightRenderer extends JLabel implements ListCellRenderer
	{
		HilightRenderer()
		{
			super();
		}
		
		@Override
		public Component getListCellRendererComponent(JList arg0, Object value, int arg2, boolean arg3, boolean arg4) 
		{
			String s = (String) value;
			if(s.indexOf("OBJ") > -1)
				setForeground(Color.RED);
			else
				setForeground(Color.BLACK);
			setText(s);
			return this;
		}
		
	}
	
	JList<String> locDisplay;
	DefaultListModel<String> listModel;
	JScrollPane listScroll;
	
	IndexedList locList;
	
	ArrayList<Location> visited;
	//ArrayList<Location> seedLocations;
	
	int kiFound;
	
	TrackerPanel top;
	
	public KIListPanel(TrackerPanel tp)
	{
		top = tp;
		setLayout(new GridLayout(1,1));
		 // 1. Create a DefaultListModel
        listModel = new DefaultListModel<String>();

        // 2. Create a JList with the DefaultListModel
        locDisplay = new JList<String>(listModel);
        locDisplay.setCellRenderer(new HilightRenderer());
		locDisplay.addListSelectionListener(this);
		locDisplay.addMouseListener(this);
		
		//seedLocations = FETool.getSeedLocations();
		locList = new IndexedList();
		locList.allowDuplicates(true); //allow duplicates of priority
		
		collectKI(-1);
		
		/*for(int i = 0; i < FETool.seedLocations.size(); i++)
		{
			locList.add(seedLocations.get(i));
		}*/
		
		listScroll = new JScrollPane(locDisplay);
		add(listScroll);
		
		
	}

	public void uncollectKI(int ki) 
	{
		if(ki >= 0)
			kiFound ^= 1 << ki;
		locList.clear();
		
		for(int i = 0; i < FETool.seedLocations.size(); i++)
		{
			int j = FETool.seedLocations.get(i);
			Location ll = FETool.allLocations.get(j);
			if(ll.isInSeed == false)
			{
				System.out.println("This should not be here");
			}
			//if(ll.name.startsWith("Eblan"))
			//	System.out.println("a");
			//if(ll.found == true)
				//continue;
			if(ll.isUnlocked(kiFound))
			{
				ll.found = true;
				if(ll.isObjective())
					ll.priority = 1;
				locList.add(ll);
			}
			else
				ll.found = false;
		}
		//if(locList.size() != sz)
		//{
		updateList();
	}

	int remState;
	int selectedForRemove;
	
	public void clickMouse()
	{
		remState++;
		//System.out.println("Pressed Mouse state=" + remState + "  index=" + selectedForRemove);
		if(remState == 1)
		{
			Location ll = (Location) locList.get(selectedForRemove);
			ll.found = true;
			ll.visited = true;
			//int oi = ll.objectiveIndex; 
			
			locList.remove(selectedForRemove);
			listModel.remove(selectedForRemove);
			selectedForRemove = -1;
			remState = 0;
			top.repaint();
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent ev) 
	{
		// TODO Auto-generated method stub
		//if(ev.getSource() == locDisplay)
		//{
		//System.out.println("List value changed");
		selectedForRemove = locDisplay.getSelectedIndex();
		/*System.out.println(si + ", " + remState);
		if(selectedForRemove != si)
		{
			remState = 0;
			selectedForRemove = -1;
		}
		if(selectedForRemove == -1)
			selectedForRemove = si;
		else
		{
			remState++;
			if(remState == 3)
			{
				//locList
				//listModel
				listModel.remove(si);
				locList.remove(si);
				selectedForRemove = -1;
				remState = 0;
			}
		}*/
		remState = 0;
		//}
		/*listModel.remove(si);
		
		Location ll = (Location) locList.get(si);
		locList.remove(si);
		visited.add(ll);*/
		
	}
	
	

	

	public void collectKI(int ki) 
	{
		if(ki >= 0)
			kiFound |= 1 << ki;
		//int sz = locList.size();
		for(int i = 0; i < FETool.seedLocations.size(); i++)
		{
			int j = FETool.seedLocations.get(i);
			Location ll = FETool.allLocations.get(j);
			if(ll.isInSeed == false)
			{
				System.out.println("This should not be here");
			}
			//if(ll.name.startsWith("Eblan"))
			//	System.out.println("a");
			if(ll.found == true)
				continue;
			if(ll.isUnlocked(kiFound))
			{
				ll.found = true;
				if(ll.isObjective())
					ll.priority = 1;
				locList.add(ll);
			}
		}
		//if(locList.size() != sz)
		//{
		updateList();
		//}
		
	}
	
	public void updateList()
	{
		//update the JList
		listModel.clear();
		for(int i = 0; i < locList.size(); i++)
		{
			Location ll = (Location) locList.get(i);
			listModel.add(i, ll.getListString());
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		clickMouse();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void clearSeed() 
	{
		locList.clear();
		listModel.clear();
		kiFound = 0;
	}

}

class TrackerPanel extends JPanel
{
	KITopPanel picts;
	KIListPanel oList;
	ToolFrame parent;
	
	public TrackerPanel()
	{
		setLayout(new GridLayout(2, 1));
		picts = new KITopPanel(this);
		oList = new KIListPanel(this);
		
		add(picts);
		add(oList);
		//add(all);
	}
	
	public void setParent(ToolFrame fr)
	{
		parent = fr;
	}
	
	public void clearSeed()
	{
		picts.clearSeed();
		oList.clearSeed();
	}
	
}

class Location implements Indexed
{
	public boolean visited;
	public static final int BOSS = 1;
	public static final int CHARACTER = 2;
	public static final int KI = 4;
	public static final int MIAB = 8;
	public static final int OBJECTIVE = 16;
	public static final int SUMMON = 64;
	public static final int MOON = 32;
	
	public boolean found;
	String name;
	//String longName;
	int index;
	int type; // boss, key item, character
	int priority;
	int[] reqKI;
	int objectiveIndex;
	public boolean isInSeed;
	
	public Location(String lline) 
	{
		String[] info = lline.split(";");
		//String s = index + ";" + name + ";" + type + ";" + priority + ";" + objectiveIndex;
		index = toInt(info[0]);
		name = info[1];
		type = toInt(info[2]);
		priority = toInt(info[3]);
		objectiveIndex = toInt(info[4]);
		reqKI = new int[info.length - 5];
		for(int i = 5; i < info.length; i++)
			reqKI[i - 5] = toInt(info[i]);
	}
	
	private boolean isKI()
	{
		if(isType(Location.KI))
			return true;
		if(isType(Location.SUMMON))
			if(!this.isObjective())
				return true;
		return false;
	}
	
	public String getListString() 
	{
		String paren = " (";
		if(isObjective())
			paren += "OBJ,";
		if(isType(Location.BOSS))
			paren += "B,";
		if(isType(Location.CHARACTER))
			paren += "C,";
		if(isKI())
			paren += "KI,";
		paren = paren.substring(0, paren.length() - 1);
		if(paren.length() > 1)
			paren += ")";
		return name + paren;
	}

	public Location() 
	{
		name = "Unnamed location";
		reqKI = new int[1];
	}

	private int toInt(String in)
	{
		/*String n = "";
		for(int i = 0; i < in.length(); i++)
		{
			if(Character.isDigit(in.charAt(i)))
				n += in.charAt(i);
		}*/
		return Integer.parseInt(in);
	}

	@Override
	public long getIndex() 
	{
		// TODO Auto-generated method stub
		return priority;
	}
	
	public boolean isObjective() 
	{
		for(int i = 0; i < FETool.seedObjectives.length; i++)
			if(FETool.seedObjectives[i] == objectiveIndex && isType(Location.OBJECTIVE))
				return true;
		return false;
	}
	
	public boolean isType(int typeOf)
	{
		if((typeOf & type) > 0)
			return true;
		return false;
	}

	public boolean isUnlocked(int kiFound)
	{
		boolean rv = true;
		for(int i = 0; i < reqKI.length; i++)
		{
			if((reqKI[i] & kiFound) == reqKI[i])
				return true;
			else
				rv = false;
		}
		return rv;
	}

	public String getSaveString() 
	{
		
		String s = index + ";" + name + ";" + type + ";" + priority + ";" + objectiveIndex;
		for(int i = 0; i < reqKI.length; i++)
			s += ";" + reqKI[i];
		return s;
	}

	public boolean isBossOnly(int[] seedObjectives) 
	{
		int bossType = Location.OBJECTIVE | Location.BOSS;
		if((type & bossType) > 0)
			return false;
		if((type & Location.OBJECTIVE) == 0)
			return true;
		for(int i = 0; i < seedObjectives.length; i++)
		{
			if(seedObjectives[i] == -1)
				continue;
			if(FETool.allObjectives[seedObjectives[i]].index == objectiveIndex)
				return false;
		}
		return true;
	}

	public boolean isCharacterOnly(int[] seedObjectives) 
	{
		int bossType = Location.CHARACTER | Location.BOSS;
		if((type & bossType) > 0)
			return false;
		if((type & Location.OBJECTIVE) == 0)
			return true;
		for(int i = 0; i < seedObjectives.length; i++)
		{
			if(seedObjectives[i] == -1)
				continue;	
			if(FETool.allObjectives[seedObjectives[i]].index == objectiveIndex)
				return false;
		}
		return true;
	}
}

class KIPanel extends JPanel implements MouseListener
{
	KeyItem[] kis;
	int selected;
	
	public KIPanel(int sel)
	{
		kis = FETool.kis;
		selected = sel;
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(10, 0, 350, 300);
		for(int i = 0; i < kis.length; i++)
		{
			int k = 1 << i;
			if((k & selected) != 0)
				g.setColor(Color.magenta);
			else
				g.setColor(Color.black);
			Rectangle r = kis[i].r;
			g.fillRect(r.x, r.y, r.width, r.height);
			g.drawImage(kis[i].pict, r.x + 7, r.y + 7, null);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) 
	{
		int mx = me.getX();
		int my = me.getY();
		for(int i = 0; i < kis.length; i++)
			if(kis[i].r.contains(mx, my))
				selected ^= (1 << i);
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class KISelect extends JFrame implements ActionListener, ChangeListener, WindowListener
{
	//KIPanel kip;
	JButton rem;
	JButton ok;
	JTabbedPane multi;
	Location ll;
	LocationEditor le;
	
	KISelect(Location loc, LocationEditor le)
	{
		ll = loc;
		this.le = le;
		multi = new JTabbedPane();
		int max = Math.max(2,  loc.reqKI.length);
		for(int i = 0; i < max; i++)
		{
			KIPanel kip;
			String ss;
			if(loc.reqKI.length > i)
			{
				kip = new KIPanel(loc.reqKI[i]);
				ss = String.valueOf(i + 1);
			}
			else
			{
				kip = new KIPanel(0);
				ss = "+";
			}
			multi.add(kip, ss);
			
		}
		
		multi.addChangeListener(this);
		
		setLayout(new BorderLayout());
		add(multi, BorderLayout.CENTER);
		
		rem = new JButton("Remove");
		rem.addActionListener(this);
		ok = new JButton("OK");
		ok.addActionListener(this);
		
		JPanel okan = new JPanel(new GridLayout(1, 2));
		okan.add(rem);
		okan.add(ok);
		add(okan, BorderLayout.SOUTH);
		
		addWindowListener(this);
		
		setSize(400, 300);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) 
	{
		if(ev.getSource() == rem)
		{
			multi.remove(multi.getSelectedIndex());
		}
		else if(ev.getSource() == ok)
		{
			ArrayList<Integer> sets = new ArrayList<Integer>();
			for(int i = 0; i < multi.getTabCount(); i++)
			{
				KIPanel kip = (KIPanel) multi.getComponentAt(i);
				if(kip.selected > 0)
					sets.add(kip.selected);
			}
			int[] s = new int[sets.size()];
			for(int i = 0; i < s.length; i++)
				s[i] = sets.get(i);
			ll.reqKI = s;
			setVisible(false);
			le.kiWin = null;
		}
	}

	@Override
	public void stateChanged(ChangeEvent ce) 
	{
		if(multi.getSelectedIndex() == multi.getTabCount() - 1)
		{
			KIPanel kip = new KIPanel(0);
			multi.add(kip, "+");
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		le.kiWin = null;
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class LocationEditor extends JPanel implements ActionListener, ListSelectionListener
{
	InputBox locName;
	InputBox locLongName;
	JLabel indexLbl;
	JCheckBox[] attribs;
	JComboBox<String> objectiveList;
	/*JCheckBox boss;
	JCheckBox charaacter;
	JCheckBox keyItem;
	JCheckBox miab;
	JCheckBox objective;*/
	
	JSpinner priSpin;
	
	JButton reqKIs;
	
	JButton add;
	JButton save;
	JButton rem;
	
	Location currLoc;
	Location dummyLoc;
	ArrayList<Location> locs;
	
	JList<String> lst1;
	DefaultListModel<String> locList;
	
	KISelect kiWin;
	
	LocationEditor()
	{
		//locs = FETool.allLocations;
		
		JPanel top = new JPanel(new GridLayout(5, 1));
		
		
		locName = new InputBox("Location Name", "", 20);
		top.add(locName);
		//locLongName = new InputBox("Full Location Name", "");
		//top.add(locLongName);
		objectiveList = new JComboBox<String>(FETool.getObjectives());
		//objectiveList.addActionListener(this);
		top.add(objectiveList);
		String[] checks = {"Boss", "Character", "KI", "Miab", "Objective", "Moon", "Summon"};
		attribs = new JCheckBox[checks.length];
		JPanel p3 = new JPanel(new GridLayout(2, ((1 + checks.length) / 2)));
		indexLbl = new JLabel("#");
		p3.add(indexLbl);
		
		for(int i = 0; i < checks.length; i++)
		{
			attribs[i] = new JCheckBox(checks[i]);
			p3.add(attribs[i]);
		}
		top.add(p3);
		
		JPanel p4 = new JPanel(new GridLayout(1, 4));
		JPanel p4a = new JPanel(new GridLayout(2, 1));
		JLabel pri = new JLabel("Priority");
		p4a.add(pri);
		priSpin = new JSpinner();
		p4a.add(priSpin);
		p4.add(p4a);
		reqKIs = new JButton("Req KIs");
		reqKIs.addActionListener(this);
		p4.add(reqKIs);
		add = new JButton("Add");
		add.addActionListener(this);
		p4.add(add);
		rem = new JButton("Rem");
		rem.addActionListener(this);
		p4.add(rem);
		save = new JButton("Save");
		save.addActionListener(this);
		p4.add(save);
		
		top.add(p4);
		
		locList = new DefaultListModel<String>();
		lst1 = new JList<String>(locList);
		lst1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lst1.addListSelectionListener(this);
		
		
		setLayout(new GridLayout(2, 1));
		add(top);
		
		JScrollPane pne = new JScrollPane(lst1);
		add(pne);
		
		locs = FETool.allLocations;
		
		if(locs.size() == 0)
			selectLocation(new Location());
		else
		{
			for(int i = 0; i < locs.size(); i++)
				locList.add(i, locs.get(i).name);
			selectLocation(locs.get(0));
		}
	}
	
	public void selectLocation(Location ll)
	{
		editLoc();
		currLoc = ll;
		locName.setText(ll.name);
		objectiveList.setSelectedIndex(ll.objectiveIndex);
		//locLongName.setText(ll.longName);
		indexLbl.setText("#" + ll.index);
		for(int i = 0; i < attribs.length; i++)
		{
			int j = 1 << i;
			attribs[i].setSelected(false);
			if((ll.type & j) > 0)
				attribs[i].setSelected(true);
		}
		priSpin.setValue(ll.priority);
	}
	
	private boolean editingCurrent()
	{
		return locName.getText().equals(currLoc.name);
	}
	
	public void addLoc()
	{
		for(int i = 0; i < locs.size(); i++)
		{
			if(locs.get(i).name.equals(locName.getText()))
			{
				JOptionPane.showMessageDialog(this, "That location already exists.  Please make another.");
				return;
			}
		}
		Location ll = new Location();
		ll.name = locName.getText();
		ll.objectiveIndex = objectiveList.getSelectedIndex();
		//ll.longName = locLongName.getText();
		ll.index = locs.size();
		int tp = 0;
		for(int i = 0; i < attribs.length; i++)
		{
			int j = 1 << i;
			if(attribs[i].isSelected())
				tp |= j;
			/*attribs[i].setSelected(false);
			if((ll.type & j) > 0)
				attribs[i].setSelected(true);*/
		}
		ll.type = tp;
		ll.priority = (Integer) priSpin.getValue();
		if(dummyLoc != null)
		{
			ll.reqKI = dummyLoc.reqKI;
			dummyLoc = null;
		}
		locs.add(0, ll);
		locList.add(0, ll.name);
		repaint();
	}
	
	public void editLoc()
	{
		if(currLoc == null)
			return;
		if(!editingCurrent())
			return;
		Location ll = currLoc;
		ll.objectiveIndex = objectiveList.getSelectedIndex();
		//ll.longName = locLongName.getText();
		//ll.index = locs.size();
		int tp = 0;
		for(int i = 0; i < attribs.length; i++)
		{
			int j = 1 << i;
			if(attribs[i].isSelected())
				tp |= j;
			/*attribs[i].setSelected(false);
			if((ll.type & j) > 0)
				attribs[i].setSelected(true);*/
		}
		ll.type = tp;
		ll.priority = (Integer) priSpin.getValue();
		//updating kis is not necessary
	}
	
	public void remLoc(int loc)
	{
		locs.remove(loc);
		locList.removeElementAt(loc);
		repaint();
	}
	
	public void save(String fname)
	{
		String saveMe = "";
		for(int i = 0; i < locs.size(); i++)
		{
			Location ll = locs.get(i);
			//saveMe += ll.name + ";" + ll.objectiveIndex + ";" + ll.index + ";" + ll.type + ";" + ll.priority +  ";";
			//for(int j = 0; j < ll.reqKI.length; j++)
			//	saveMe += ll.reqKI[j] + ";";
			saveMe += ll.getSaveString();
			saveMe += "\r\n";
		}
		String path = System.getProperty("user.dir") + File.separator + fname + ".csv";
		File f = new File(path);
		try
		{
			if(!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(saveMe);
			bw.close();
			JOptionPane.showMessageDialog(this, path + " has been saved.", "File Saved", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) 
	{
		if(ev.getSource() == reqKIs && kiWin == null)
		{
			if(editingCurrent())
				kiWin = new KISelect(currLoc, this);
			else
			{
				if(dummyLoc == null)
					dummyLoc = new Location();
				kiWin = new KISelect(dummyLoc, this);
			}
		}
		else if(ev.getSource() == add)
		{
			addLoc();
		}
		else if(ev.getSource() == rem)
		{
			int sel = lst1.getSelectedIndex();
			remLoc(sel);
		}
		else if(ev.getSource() == save)
		{
			save("Locations");
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent ev) 
	{
		int sel = lst1.getSelectedIndex();
		selectLocation(locs.get(sel));
		
	}

	//@Override
	/*public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}*/
	
}

class SeedObjectivePanel extends JPanel implements ActionListener
{
	JLabel[] oLabels;
	JComboBox<String>[] objLists;
	boolean[] origRandom;
	ToolFrame homeFrame;
	
	JCheckBox[] oComplete;
	
	public SeedObjectivePanel(ToolFrame toolFrame)
	{
		homeFrame = toolFrame;
		if(FETool.seedObjectives == null)
		{
			setLayout(new GridLayout(9,1));
			JLabel b =  new JLabel("");
			JLabel lbl = new JLabel("Please input flags for new seed");
			JLabel lbl2 = new JLabel("before setting objectives");
			JLabel lbl3 = new JLabel("(File > New Seed)");
			JLabel[] all = {b, b, b, lbl, lbl2, lbl3, b, b, b};
			for(int i = 0; i < all.length; i++)
			{
				all[i].setHorizontalAlignment(SwingConstants.CENTER);
				add(all[i]);
			}
			
		}
		else
		{
			setupObjectives();
		}
		
	}
	
	public void setupObjectives()
	{
		setLayout(new GridLayout(11, 1));
		add(new JLabel(""));
		int[] objs = FETool.seedObjectives;
		if(objs == null)
			return;
		oLabels = new JLabel[objs.length];
		objLists = new JComboBox[objs.length];
		origRandom = new boolean[objs.length];
		oComplete = new JCheckBox[objs.length];
		String[] allObj = buildStringList();
		for(int i = 0; i < objs.length; i++)
		{
			if(objs[i] == -1)
			{
				origRandom[i] = true;
				objLists[i] = new JComboBox<String>();
				for(int j = 0; j < allObj.length; j++)
					objLists[i].addItem(allObj[j]);
				objLists[i].addActionListener(this);
				
				JPanel pan = new JPanel(new BorderLayout());
				oComplete[i] = new JCheckBox();
				pan.add(oComplete[i], BorderLayout.WEST);
				pan.add(objLists[i]);
				add(pan);
			}
			else
			{
				origRandom[i] = false;
				oLabels[i] = new JLabel(FETool.allObjectives[objs[i]].name);
				
				JPanel pan = new JPanel(new BorderLayout());
				oComplete[i] = new JCheckBox();
				pan.add(oComplete[i], BorderLayout.WEST);
				pan.add(oLabels[i]);
				add(pan);
			}
		}
	}
	
	
	
	private String[] buildStringList()
	{
		String[] all = new String[FETool.allObjectives.length + 1];
		all[0] = "<Set Random Objective>";
		for(int i = 1; i < all.length; i++)
			all[i] = FETool.allObjectives[i - 1].name;
		return all;
	}

	@Override
	public void actionPerformed(ActionEvent ev) 
	{
		for(int i = 0; i < objLists.length; i++)
		{
			if(ev.getSource() == objLists[i])
			{
				FETool.removeObjectiveFromSeed(FETool.seedObjectives[i]);
				int so = objLists[i].getSelectedIndex() - 1;
				
				if(so == -1)
				{
					FETool.seedObjectives[i] = so;
					return;
				}
				for(int j = 0; j < FETool.seedObjectives.length; j++)
				{	
					if(FETool.seedObjectives[j] == so)
					{
						JOptionPane.showMessageDialog(this, "That objective is already in the seed.", "Duplicate Objective", JOptionPane.INFORMATION_MESSAGE);
						FETool.seedObjectives[i] = -1;
						return;
					}
				}		
				
				FETool.seedObjectives[i] = so;
				FETool.addObjectiveToSeed(so, i);
				
				homeFrame.updateLocationList();
				return;
			}
		}
	}

	public void clearSeed() 
	{
		setupObjectives();
	}
}


class ToolFrame extends JFrame implements ActionListener
{
	JTabbedPane jtp;
	TrackerPanel tracker;
	SeedObjectivePanel sop;
	LocationEditor le;
	
	JMenuBar topMenu;
	JMenu fileMenu;
	JMenuItem newSeed;
	JMenuItem reset;
	JMenuItem exit;
	
	ToolFrame()
	{
		jtp = new JTabbedPane();
		tracker = new TrackerPanel();
		tracker.setParent(this);
		sop = new SeedObjectivePanel(this);
		le = new LocationEditor();
		
		jtp.add(tracker, "Tracker");
		jtp.add(sop, "Objectives");
		jtp.add(le, "Location Editor");
		
		//the menu
		topMenu = new JMenuBar();
		
		fileMenu = new JMenu("File");
		
		newSeed = new JMenuItem("New Seed...");
		newSeed.addActionListener(this);
		fileMenu.add(newSeed);
		reset = new JMenuItem("Reset Tracker");
		fileMenu.add(reset);
		reset.addActionListener(this);
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		fileMenu.add(exit);
		topMenu.add(fileMenu);
		setJMenuBar(topMenu);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(jtp);
		setSize(400, 600);
		setVisible(true);
		
	}
	
	public void updateLocationList()
	{
		tracker.oList.collectKI(-1);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newSeed)
		{
			JTextArea bigText = new JTextArea(8, 80);
			bigText.setLineWrap(true);
			bigText.setWrapStyleWord(true);
			int res = JOptionPane.showConfirmDialog(null, bigText, "Paste your flags here:", JOptionPane.OK_CANCEL_OPTION);
			if(res == JOptionPane.OK_OPTION)
			{
				String flags = bigText.getText();
				FETool.seedFlags = flags;
				FETool.processFlags(flags);
				invalidate();
				jtp.remove(sop);
				sop = new SeedObjectivePanel(this);
				jtp.add(sop, "Objectives", 1);
				jtp.setSelectedIndex(1);
				revalidate();
				repaint();
				
			}
		}
		else if(e.getSource() == reset)
		{
			///get seed locations and mark them as unfound
			//no KIs are collected
			//clear indicators
			clearSeed();
		}
		else if(e.getSource() == exit)
		{
			System.exit(0);
		}
	}

	public void clearSeed() 
	{
		tracker.clearSeed();
		sop.clearSeed();
		tracker.repaint();
	}
	
}

class FEObjective
{
	int index;
	String name;
	int type;
	String flag;
	int qty;
	boolean complete;
	
	public FEObjective(String in)
	{
		String[] all = in.split(";");
		index = Integer.parseInt(all[0]);
		name = all[1];
		type = Integer.parseInt(all[2]);
		String s = "";
		if(all.length == 4)
			s = all[3];
		flag = genFlag(s);
	}
	
	public void setQuantity(int qty)
	{
		if(type != 32)
			return;
		this.qty = qty;
		String qs = String.valueOf(qty);
		if(qty > 1000)
		{
			qs = "";
			while(true)
			{
				int qp = (qty % 1000);
				qs = qp + qs;
				qty /= 1000;
				if(qty == 0)
					break;
				if(qp < 100)
					qs = "0" + qs;
				if(qp < 10)
					qs = "0" + qs;
				qs = "," + qs;
			}
		}
		name = name.replaceFirst("###", qs);
	}
	
	private String genFlag(String in)
	{
		String charname = name.substring(name.indexOf(' ') + 1);
		String flagchar = "";
		for(int i = 0; i < charname.length(); i++)
		{
			char ch = Character.toLowerCase(charname.charAt(i));
			flagchar += ch;
		}
		if(type == 16) //char
		{
			flagchar = "char_" + flagchar;
			return flagchar;
		}
		else if(type == 8)
		{
			if(in.equals(""))
			{
				return "boss_" + flagchar;
			}
			else
				return "boss_" + in;
		}
		else
		{
			return "quest_" + in;
		}
	}
}


public class FETool 
{
	public static int reqObjCount;
	public static String seedFlags;
	public static FEObjective[] allObjectives;  //all possible objectives
	public static int[] seedObjectives;  //all seed objectives
	public static ArrayList<Location> allLocations;
	public static ArrayList<Integer> seedLocations;
	public static int reqObjectiveTypes;
	public static int kiLocationTypes;
	
	public static KeyItem[] kis;
	public static Indicator[] indics;
	
	public static ToolFrame tf;
	

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		loadObjectives();
		loadLocations();
		loadImages();
		tf = new ToolFrame();
	}
	
	public static ArrayList<Location> getSeedLocations() 
	{
		ArrayList<Location> rv = new ArrayList<Location>();
		for(int i = 0; i < seedLocations.size(); i++)
			rv.add(allLocations.get(seedLocations.get(i)));
		return rv;
	}

	public static String loadCSV(String csvFile)
	{
		String tf = csvFile;
		String currDir = System.getProperty("user.dir");
		String path = "";
		if(!tf.contains(File.separator))
			path = currDir + File.separator + tf;
		File f = new File(path);
		String rv = "";
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while(line != null)
			{
				rv += line + "\n";
				line = br.readLine();
			}
			br.close();
			return rv;
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return null;
		}
	}
	
	public static void loadObjectives()
	{
		String rv = loadCSV("Objectives.csv");
		if(rv == null)
		{
			JOptionPane.showMessageDialog(null, "Couldn\'t find data file Objectives.csv. Please make sure it is in the same directory as the executable.");
			System.exit(0);
		}
			
		String[] all = rv.split("\n");
		allObjectives = new FEObjective[all.length];
		for(int i = 0; i < all.length; i++)
		{
			allObjectives[i] = new FEObjective(all[i]);
		}
			
			//return rv;
		
		
	}
	
	public static BufferedImage grabImage(Image in, int index)
	{
		BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		int row = index / 4;
		int col = index % 4;
		int xx = col * 90;
		int yy = row * 34;
		Graphics g = bi.getGraphics();
		//destination then source
		g.drawImage(in, 0, 0, 32, 32, xx, yy, xx + 32, yy + 32, null);
		g.dispose();
		return bi;
	}
	
	public static void loadImages()
	{
		String imgFile = System.getProperty("user.dir") + File.separator + "Images.png";
		ImageIcon ico = new ImageIcon(imgFile);
		Image ii = ico.getImage();
		if(ii == null)
		{
			JOptionPane.showMessageDialog(null, "Couldn\'t find image file Images.png. Please make sure it is in the same directory as the executable.");
			System.exit(0);
		}
		String[] kis = {"Baron Key", "Luca Key", "Magma Key", "Tower Key", "Darkness Crystal", "Earth Crystal",
					    "Hook", "TwinHarp", "Package", "Pan", "SandRuby", "Rat Tail", "Adamant", "Legend Sword",
					    "Spoon", "Pink Tail", "Crystal", "Pass"};
		int[] indeces = {4, 10, 7, 8, 11, 6, 9, 5, 1, 14, 2, 12, 13, 3, 15, 16, 17, 18};
		String[] inames = {"Moonveil", "BS 6", "DMist", "Go"};
		Color[] iColors = {Color.BLUE, Color.RED, Color.RED, Color.GREEN};
		int[] indicPictIdx = {19, 16, 21, 20};
		FETool.kis = new KeyItem[18];
		FETool.indics = new Indicator[indicPictIdx.length];
		for(int n = 0; n < 22; n++)
		{
			BufferedImage bi = grabImage(ii, n);
			boolean found = false;
			for(int p = 0; p < indicPictIdx.length; p++)
			{
				if(n == indicPictIdx[p])
				{
					Indicator ind = new Indicator(inames[p], bi, p, iColors[p]);
					FETool.indics[p] = ind;
					found = true;
					break;
				}
			}
			if(!found)
			{
				int m = n;
				if(m > 16)
					m--;
				KeyItem ki = new KeyItem(indeces[m] + ";" + kis[m], bi);
				FETool.kis[m] = ki;
			}
		}
	}
	
	public static void loadLocations()
	{
		String rv = loadCSV("Locations.csv");
		seedLocations = new ArrayList<Integer>();
		if(rv == null)
		{
			allLocations = new ArrayList<Location>();
			return;
		}
		String[] all = rv.split("\n");
		allLocations = new ArrayList<Location>(all.length);
		//ArrayList<Location> temp = new ArrayList<Location>(all.length);
		for(int i = 0; i < all.length; i++)
		{
			allLocations.add(null);
		}
		for(int i = 0; i < all.length; i++)
		{
			Location ll = new Location(all[i]);
			allLocations.set(ll.index, ll);
			//allLocations.add(null);
		}
		
		
	}
	
	public static void saveLocations()
	{
		//String config = c1 + "\n" + c2 + "\n" + c3 + "\n";
		String currDir = System.getProperty("user.dir");
		String path = currDir + File.separator + "Locations.csv";
		File f = new File(path);
		try
		{
			if(!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			
			String out = "";
			for(int i = 0; i < allLocations.size(); i++)
				out += allLocations.get(i).getSaveString() + "\r\f";
			
			bw.write(out);
			bw.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	private static void addLocsOfType(int type)
	{
		System.out.println("Adding all of type " + type);  //4=KI, 8=CHARACTER
		for(int i = 0; i < allLocations.size(); i++)
		{
			Location ll = allLocations.get(i);
			//if(ll.name.equals("Antlion"))
			//	System.out.println("a");
			if((ll.type & type) != 0)
			{
				
				addLocationToSeed(ll);
			}
		}
		System.out.println("=======");
	}
	
	private static void removeLoc(String name)
	{
		for(int i = 0; i < allLocations.size(); i++)
		{
			//IndexedInteger ii = (IndexedInteger) list.get(i);
			Location ll = allLocations.get(i);
			if(ll.name.equals(name))
			{
				
				remLocationFromSeed(ll);
				return;
			}
		}
	}
	
	private static int findObjective(String flag)
	{
		for(int i = 0; i < allObjectives.length; i++)
		{
			if(allObjectives[i].flag.equals(flag))
				return i;
		}
		return -1;
	}
	
	public static void clearSeed()
	{
		for(int i = 0; i < allLocations.size(); i++)
		{
			Location ll = allLocations.get(i);
			ll.found = false;
			ll.visited = false;
			ll.isInSeed = false;
		}
		seedLocations.clear();
		seedObjectives = new int[0];
		tf.clearSeed();
	}
	
	public static void processFlags(String seedFlags)
	{
		clearSeed();
		seedFlags = seedFlags.trim();
		String[] allFlags = seedFlags.split(" ");
		//we care about objective, k, and char flags
		//IndexedList seedLocs = new IndexedList();
		//seedLocations = new ArrayList<Integer>();
		//seedLocations.allowDuplicates(false);
		
		
		for(int i = 0; i < allFlags.length; i++)
		{
			//objectives
			if(allFlags[i].startsWith("O"))
			{
				ArrayList<Integer> objIdx = new ArrayList<Integer>();
				String[] oFlags = allFlags[i].split("/");
				for(int j = 0; j < oFlags.length; j++)
				{
					if(oFlags[j].startsWith("random"))
					{
						int coloc = oFlags[j].indexOf(":");
						int randC = oFlags[j].charAt(coloc + 1) - '0';
						for(int k = 0; k < randC; k++)
							objIdx.add(-1);
					}
					if(oFlags[j].startsWith("req:"))
					{
						if(oFlags[j].endsWith("all"))
							FETool.reqObjCount = -1;
						else
						{
							int req = oFlags[j].charAt(4) - '0';
							FETool.reqObjCount = req;
						}
					}
					else if(oFlags[j].startsWith("Omode:"))
					{
						oFlags[j] = oFlags[j].substring(6);
						String[] poss = {"bosscollecter","fiends","classicforge","classicgiant"};   //ki[n] doesn't matter
						for(int k = 0; k < poss.length; k++)
						{
							if(oFlags[j].startsWith(poss[k]))
							{
								switch(k)
								{
								case 0:  case 1: 
									FETool.addLocsOfType(Location.BOSS);
									if(k == 1)  //fiends
									{
										String[] fiends = {"elements","milon", "mlonz", "kainazzo", "valvalis", "rubicant"};
										for(int m = 0; m < fiends.length; m++)
										{
											int ff = findObjective("boss_" + fiends[m]);
											if(ff != -1)
												objIdx.add(ff);
										}
									}
									break;
								case 2:
									int ff = findObjective("quest_forge");
									if(ff != -1)
										objIdx.add(ff);
									break;
								case 3:
									ff = findObjective("quest_giant");
									if(ff != -1)
										objIdx.add(ff);
									break;
									
								}
							}
						}
						//collection quests
						String[] qtyStyle = {"bosscollecter","goldhunter","dkmatter","ki"};
						for(int k = 0; k < qtyStyle.length; k++)
						{
							if(oFlags[j].startsWith(qtyStyle[k]))
							{
								int objIndex = 86 + k;
								String end = oFlags[j].substring(qtyStyle[k].length());
								int n = Integer.parseInt(end);
								if(k == 1)
									n *= 1000;
								allObjectives[objIndex].setQuantity(n);
								objIdx.add(objIndex);
							}
						}
					}
					else
					{
						if(oFlags[j].indexOf(':') >= 0)
							oFlags[j] = oFlags[j].substring(oFlags[j].indexOf(":") + 1);
						if(oFlags[j].startsWith("boss_"))
							addLocsOfType(Location.BOSS);
						else if(oFlags[j].startsWith("char_"))
							addLocsOfType(Location.CHARACTER);
						
						
						//oFlags[j] = oFlags[j].substring(oFlags[j].indexOf(":"));
						int ff = findObjective(oFlags[j]);
						if(ff != -1)
							objIdx.add(ff);
						//else
						//	JOptionPane.showMessageDialog(null, "Objective not found:" + oFlags[j]);
						
					}
				}
				
				seedObjectives = new int[objIdx.size()];
				for(int j = 0; j < objIdx.size(); j++)
				{
					int jj = objIdx.get(j);
					//seedObjectives[j] = jj;
					addObjectiveToSeed(jj, j);
				}
				if(FETool.reqObjCount == -1)
					FETool.reqObjCount = seedObjectives.length;
				
			}
			
			//K flag
			if(allFlags[i].startsWith("K"))
			{
				allFlags[i] = allFlags[i].substring(1);
				String[] kflags = allFlags[i].split("/");
				boolean miab = false;
				boolean moon = false;
				for(int j = 0; j < kflags.length; j++)
				{
					if(kflags[j].equals("main"))
					{
						FETool.addLocsOfType(Location.KI);
						//FETool.removeLoc("Cid Bed");
					}
					else if(kflags[j].equals("summon"))
						FETool.addLocsOfType(Location.SUMMON);
					else if(kflags[j].equals("moon"))
					{
						FETool.addLocsOfType(Location.MOON);
						moon = true;
					}
					else if(kflags[j].startsWith("miab"))
					{
						FETool.addLocsOfType(Location.MIAB);
						miab = true;
						if(kflags[j].endsWith("above"))
						{
							FETool.removeLoc("Sylph MIABS (7)");
							FETool.removeLoc("Lower Babil MIABS (4)");
							FETool.removeLoc("Warriors Chest");
						}
					}
					else if(kflags[j].startsWith("nofree"))
					{
						FETool.removeLoc("Bedward");
						if(kflags[j].endsWith("package"))
							addLoc("Village Mist");
						if(kflags[j].endsWith("Dwarf"))
							addLoc("Cid Bed");
					}	
				
				}
				if(miab && !moon)
					FETool.removeLoc("LST Miabs (9)");
			}
			
			//char flag
			if(allFlags[i].startsWith("C"))
			{
				allFlags[i] = allFlags[i].substring(1);
				String[] cflags = allFlags[i].split("/");
				for(int j = 0; j < cflags.length; j++)
				{
					if(cflags[j].equals("nofree"))
					{
						//remove character checks with no boss 
						for(int k = 0; k < seedLocations.size(); k++)
						{
							//IndexedInteger ii = (IndexedInteger) seedLocs.get(k);
							Location ll = FETool.allLocations.get(i);
							boolean remove = false;
							if((ll.type & Location.CHARACTER) > 0)
							{
								if(ll.reqKI == null)
									remove = true;
								if((ll.type & Location.BOSS) == 0)
									remove = true;
							}
							if(remove)
							{
								removeLoc(ll.name);
								k--;
							}
						}
					}
					else if(cflags[j].equals("noearned"))
					{
						//remove character checks with no boss 
						for(int k = 0; k < seedLocations.size(); k++)
						{
							//IndexedInteger ii = (IndexedInteger) seedLocs.get(k);
							Location ll = FETool.allLocations.get(i);
							boolean remove = false;
							if((ll.type & Location.CHARACTER) > 0)
							{
								if(ll.reqKI != null)
									remove = true;
								if((ll.type & Location.BOSS) > 0)
									remove = true;
							}
							if(remove)
							{
								removeLoc(ll.name);
								k--;
							}
						}
					}
				}
			}
		}
		
		tf.updateLocationList();
	}
	
	private static void resetReqKIs()
	{
		int reqki = 0;
		for(int i = 0; i < seedLocations.size(); i++)
		{
			Location ll = allLocations.get(seedLocations.get(i));
			if(ll.isObjective() == false)
				continue;
			for(int j = 0; j < ll.reqKI.length; j++)
				reqki |= ll.reqKI[j];
		}
		for(int i = 0; i < kis.length; i++)
		{
			int m = 1 << i;
			if((reqki & m) != 0)
				FETool.kis[i].required = true;
			else
				FETool.kis[i].required = false;
		}
	}
	
	private static void printReqKIs()
	{
		System.out.println("RKIs>> ");
		for(int i = 0; i < FETool.kis.length; i++)
			if(FETool.kis[i].required)
				System.out.print(FETool.kis[i].name + "  ");
		System.out.println("  <<");
	}
	
	private static void addLoc(String in)
	{
		for(int i = 0; i < allLocations.size(); i++)
		{
			Location ll = allLocations.get(i);
			if(ll.name.equals(in))
			{
				addLocationToSeed(ll);
				return;
			}
		}
	}
	
	private static void addLocationToSeed(Location ll)
	{
		System.out.println("Adding " + ll.name);
		if(!ll.isInSeed)
		{
			ll.isInSeed = true;
			seedLocations.add(ll.index);
		}
		if(ll.isObjective())  //always do this
		{
			for(int i = 0; i < ll.reqKI.length; i++)
			{
				int rq = ll.reqKI[i];
				for(int j = 0; j < kis.length; j++)
				{
					int m = 1 << j;
					if((rq & m) != 0)
						FETool.kis[j].required = true;
				}
			}
		}
		printReqKIs();
	}
	
	public static void remLocationFromSeed(Location ll)
	{
		System.out.println("removing " + ll.name);
		if(!ll.isInSeed)
			return;
		ll.isInSeed = false;
		for(int i = 0; i < seedLocations.size(); i++)
		{
			int ii = seedLocations.get(i);
			if(ii == ll.index)
			{
				seedLocations.remove(i);
				resetReqKIs();
				printReqKIs();
				return;
			}	
		}
	}
	
	public static void addObjectiveToSeed(int objIndex, int objLoc)
	{
		seedObjectives[objLoc] = objIndex;
		if(objIndex == -1)
			return;
		FEObjective obj = allObjectives[objIndex];
		if(obj.type <= 7)  
		{
			for(int i = 0; i < allLocations.size(); i++)
			{
				Location ll = allLocations.get(i);
				if(ll.objectiveIndex == obj.index && ll.isType(Location.OBJECTIVE))
					addLocationToSeed(ll);
			}
		}
		else if(obj.type == 8)  //boss
		{
			for(int i = 0; i < allLocations.size(); i++)
			{
				Location ll = allLocations.get(i);
				if(ll.isType(Location.BOSS))
					addLocationToSeed(ll);
			}
		}
		else if(obj.type == 16) //character
		{
			for(int i = 0; i < allLocations.size(); i++)
			{
				Location ll = allLocations.get(i);
				if(ll.isType(Location.CHARACTER))
					addLocationToSeed(ll);
			}
		}
		
		tf.updateLocationList();
	}
	
	public static void removeObjectiveFromSeed(int objIndex)
	{
		if(objIndex == -1)  //nothing to remove
			return;
		FEObjective obj = allObjectives[objIndex];
		//remove the objective
		for(int i = 0; i < seedObjectives.length; i++)
			if(seedObjectives[i] == objIndex)
				seedObjectives[i] = -1;
		
		if(obj.type > 7)  //character or boss objective (this branch should not be taken
		{
			int old = reqObjectiveTypes;
			reqObjectiveTypes = 0;
			for(int i = 0; i < seedObjectives.length; i++)
			{
				int idx = seedObjectives[i];
				if(allObjectives[idx].type > 7)
					reqObjectiveTypes |= allObjectives[idx].type;
			}
			if(reqObjectiveTypes == old)  //no change
				return;
			else
			{
				for(int i = 0; i < seedLocations.size(); i++)
				{
					//IndexedInteger ii = (IndexedInteger) seedLocations.get(i);
					int ii = seedLocations.get(i);
					Location ll = allLocations.get(ii);
					if((reqObjectiveTypes & 8) == 0)  //boss
					{
						//remove boss locations that are not an objective
						if(ll.isBossOnly(seedObjectives))
							remLocationFromSeed(ll);
					}
					else if((reqObjectiveTypes & 16) == 0)
					{
						if(ll.isCharacterOnly(seedObjectives))
							remLocationFromSeed(ll);
					}
				}
			}
		}
		for(int i = 0; i < seedLocations.size(); i++)
		{
			int ii = seedLocations.get(i);
			Location ll = allLocations.get(ii);
			if(ll.objectiveIndex == obj.index && ll.isType(Location.OBJECTIVE))
			{
				remLocationFromSeed(ll);
			}
		}
	}
	
	public static String[] getObjectives()
	{
		String[] rv = new String[allObjectives.length];
		for(int i = 0; i < rv.length; i++)
			rv[i] = allObjectives[i].name;
		return rv;
	}

}

interface Indexed
{
	public long getIndex();
}

class IndexedInteger implements Indexed, Comparable<IndexedInteger>
{
	int val;
	IndexedInteger(int value)
	{
		val = value;
	}
	
	public long getIndex()
	{
		return val;
	}
	
	public String toString(boolean isntSuper)
	{
		return String.valueOf(val);
	}

	@Override
	public int compareTo(IndexedInteger o) 
	{
		return (int) (getIndex() - o.getIndex());
	}

	
}

class IndexedList
{
	ArrayList<Indexed> list;
	boolean allowDuplicates;
	
	//String removeLog;
	
	public IndexedList()
	{
		list = new ArrayList<Indexed>();
		allowDuplicates = false;
	}
	
	public IndexedList(int initSize)
	{
		list = new ArrayList<Indexed>(initSize);
		allowDuplicates = false;
	}
	

	public IndexedList(IndexedList otherList)
	{
		populate(otherList);
		allowDuplicates = false;
	}
	
	public void allowDuplicates(boolean value)
	{
		allowDuplicates = value;
	}
	
	public void remove(int i) 
	{
		// TODO Auto-generated method stub
		list.remove(i);
	}
	
	public ArrayList<Indexed> getList()
	{
		return list;
	}

	private IndexedList(ArrayList<Indexed> l)  //do not make this public
	{
		list = l;
		allowDuplicates = false;
	}
	
	public IndexedList copy() 
	{
		ArrayList<Indexed> al = (ArrayList<Indexed>) list.clone();
		return new IndexedList(al);
	}

	public int size() 
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	public void clear() 
	{
		// TODO Auto-generated method stub
		list.clear();
	}
	
	public int addAll(IndexedList o)
	{
		int a = list.size();
		ArrayList<Indexed> nl = new ArrayList<Indexed>(a + o.size());
		int i = 0;
		int j = 0;
		long ii = 0;
		long jj = 0;
		while(true)  //simple merge
		{
			if(i == list.size())
			{
				nl.addAll(j, o.list);
				break;
			}
			else if(j == list.size())
			{
				nl.addAll(i, list);
				break;
			}
			else
			{
				ii = list.get(i).getIndex();
				jj = o.list.get(j).getIndex();
				if(ii < jj)
				{
					nl.add(list.get(i));
					i++;
				}
				else if(ii == jj)
				{
					if(allowDuplicates)
					{
						nl.add(list.get(i));
						i++;
					}
				}
				else
				{
					nl.add(o.list.get(j));
					j++;
				}
			}
		}
		list = nl;
		return nl.size();
	}
	
	public int add(Indexed newObj)
	{
		int idx = binSearch(newObj.getIndex(), 0, list.size() - 1);
		return internalAdd(idx, newObj);
	}
	
	public int append(Indexed newObj)
	{
		if(list.size() == 0 || newObj.getIndex() > list.get(list.size() - 1).getIndex())
		{
			list.add(newObj);
			return list.size() - 1;
		}
		else
		{
			int idx = binSearch(newObj.getIndex(), 0, list.size() - 1);
			return internalAdd(idx, newObj);
		}
	}
	
	public int addEarly(Indexed newObj)
	{
		int idx = expSearch(newObj.getIndex(), 0);
		return internalAdd(idx, newObj);
	}
	
	private int internalAdd(int idx, Indexed newObj)
	{
		if(list.size() == idx)
		{
			list.add(newObj);
			return idx;
		}
		if(list.get(idx).getIndex() != newObj.getIndex() || allowDuplicates)
		{
			list.add(idx, newObj);
			return idx;
		}
		return -1;
	}
	
	private int duplicateFind(Indexed newObj, int idx)
	{
		int i = idx;
		//removeLog += "\n Looking for " + newObj.toString() + "  index " + newObj.getIndex() + " going backwards from " + i;
		while(i >= 0)
		{
			//removeLog += "\n" + i + "=" + list.get(i).toString() + "  index is " + list.get(i).getIndex();
			if(list.get(i) == newObj)
				return i;
			if(list.get(i).getIndex() != newObj.getIndex())
				break;
			i--;
		}
		i = idx + 1;
		//removeLog += "\n going forward from " + i;
		while(i < list.size())
		{
			//removeLog += "\n" + i + "=" + list.get(i).toString() + "  index is " + list.get(i).getIndex();
			if(list.get(i) == newObj)
				return i;
			if(list.get(i).getIndex() != newObj.getIndex())
				break;
			i++;
		}
		return -1;
	}
	
	private int returnIndex(Indexed newObj, int idx)
	{
		if(idx >= list.size())
			return -1;
		if(list.get(idx).getIndex() == newObj.getIndex())
		{
			if(allowDuplicates)
				return duplicateFind(newObj, idx);
			else
				return idx;
		}
		return -1;
	}
	
	public int indexOf(Indexed newObj)
	{
		int idx = binSearch(newObj.getIndex(), 0, list.size() - 1);
		return returnIndex(newObj, idx);
	}
	
	public int indexOfExp(Indexed newObj)
	{
		int idx = expSearch(newObj.getIndex(), 0);
		return returnIndex(newObj, idx);
	}
	
	private int finalRemove(Indexed newObj, int idx)
	{
		//removeLog += "\n testing index " + list.get(idx).getIndex() + " against " + newObj.getIndex();
		if(idx >= list.size())
			return -1;
		if(list.get(idx).getIndex() == newObj.getIndex())
		{
			if(allowDuplicates)
			{
				int df = duplicateFind(newObj, idx);
				if(df != -1)
				{
					list.remove(df);
					return df;
				}
			}
			else
			{
				list.remove(idx);
				return idx;
			}
		}
		return -1;
	}
	
	public int remove(Indexed newObj)
	{
		//removeLog = "";
		int idx = binSearch(newObj.getIndex(), 0, list.size() - 1);
		//removeLog += "idx=" + idx + " out of " + list.size();
		return finalRemove(newObj, idx);
	}
	
	public int removeExp(Indexed newObj)
	{
		int idx = expSearch(newObj.getIndex(), 0);
		return finalRemove(newObj, idx);
	}
	
	public Indexed removeItemWithIndex(long index)
	{
		int loc = getItemLocWithIndexBin(index, 0);
		if(loc >= 0)
			return list.remove(loc);
		else
			return null;
	}
	
	public void removeIndexRange(long startIndex, long endIndex)
	{
		int idxS = getItemLocWithIndexBin(startIndex, 0);
		if(idxS < 0)
			idxS = (idxS + 1) * -1;
		
		int idxE = getItemLocWithIndexBin(endIndex, idxS);
		if(idxE < 0)
			idxE = (idxE + 1) * -1;
		
		list.subList(idxS, idxE).clear();
	}
		
		
	
	
	private void populate(IndexedList otherList)
	{
		list = new ArrayList<Indexed>(otherList.size());
		list.addAll(otherList.getList());
	}
	
	private int expSearch(long index, int startIndex)
	{
		switch(list.size() - startIndex)
		{
		case 0:
			return startIndex;
		case 1:
			if(list.get(startIndex).getIndex() >= index)
				return startIndex;
			else
				return 1 + startIndex;
		default:
			int start = 0;
			int end = 1;
			while(index > list.get(end + startIndex).getIndex())
			{
				start = end + 1;
				end *= 2;
				if(end + startIndex >= list.size())
				{
					end = list.size() - 1 - startIndex;
					break;
				}
			}
			int loc = binSearch(index, start + startIndex, end + startIndex);
			return loc;
		}
	}
	
	private int binSearch(long index, int start, int end)
	{
		long forValue = index;
		int mid = 0;
		long currVal = 0;
		while(start <= end)
		{
			mid = (start + end) >> 1;
			currVal = list.get(mid).getIndex();
			if(currVal == forValue)
				return mid;
			if(currVal > forValue)
				end = mid - 1;
			else
				start = mid + 1;
		}
		return start;  //start at this point is the first value after the one you did not find
	}
	
	public Indexed get(int i)
	{
		if(list.size() <= i)
			return null;
		return list.get(i);
	}
	
	public Indexed getLast()
	{
		if(list.size() == 0)
			return null;
		return list.get(list.size() - 1);
	}
	
	public Indexed findItemWithIndex(long index)
	{
		int idx = binSearch(index, 0, list.size() - 1);
		if(idx >= list.size())
			return null;
		Indexed iObj = list.get(idx);
		if(iObj.getIndex() == index)
			return iObj;
		else
			return null;
	}
	
	/*public Indexed[] findItemsBetween(long index1, long index2)
	{
		int i1 = binSearch(index1, 0, list.size() - 1);
		int i2 =
	}*/
	
	public Indexed findEarlyItemWithIndex(long index)
	{
		int idx = expSearch(index, 0);
		if(idx >= list.size())
			return null;
		Indexed iObj = list.get(idx);
		if(iObj.getIndex() == index)
			return iObj;
		else
			return null;
	}
	
	public Indexed findItemWithIndex(long index, int findAfterLoc)
	{
		int idx = expSearch(index, findAfterLoc);
		if(idx >= list.size())
			return null;
		Indexed iObj = list.get(idx);
		if(iObj.getIndex() == index)
			return iObj;
		else
			return null;
	}
	
	/* getItemLocWithIndexBin
	 * parameters: index - the index to find;  findAfterLoc - the index to begin searching after
	 * returns: the index of the found element, or a negative number equal to the index of the last element
	 * found before the given index - 1
	 */
	public int getItemLocWithIndexBin(long index, int findAfterLoc)
	{
		int idx = binSearch(index, findAfterLoc, list.size() - 1);
		if(idx >= list.size())
			return -1 * idx - 1;
		Indexed iObj = list.get(idx);
		if(iObj.getIndex() == index)
			return idx;
		else
			return -1 * idx - 1;
	}
	
	public int getItemLocWithIndexExp(long index, int findAfterLoc)
	{
		//System.out.println("Finding " + index + " beginning at loc " + findAfterLoc);
		int idx = expSearch(index, findAfterLoc);
		//System.out.println("Found at " + idx);
		if(idx >= list.size())
			return -1 * idx - 1;
		Indexed iObj = list.get(idx);
		if(iObj.getIndex() == index)
			return idx;
		else
			return -1 * idx - 1;
	}
	
	private ArrayList<Integer> intersection(IndexedList otherList)
	{
		ArrayList<Integer> rtnVal = new ArrayList<Integer>();
		int thisLoc = 0;
		int otherLoc = 0;
		long i1, i2;
		//int searchValue;// = Math.min(this.get(0).getIndex(), otherList.get(0).getIndex());
		//IndexedList searchList;
		i1 = this.get(thisLoc).getIndex();
		i2 = otherList.get(otherLoc).getIndex();
		while(true)
		{
			if(i1 > i2)
			{
				otherLoc = otherList.getItemLocWithIndexExp(i1, otherLoc);
				if(otherLoc < 0)  //not found
				{
					otherLoc = otherLoc * -1 - 1;
					if(otherLoc >= otherList.size())
						break;
					i2 = otherList.get(otherLoc).getIndex();
				}
				else //found
				{
					rtnVal.add(thisLoc);
					thisLoc++;
					otherLoc++;
					if(thisLoc >= this.size() || otherLoc >= otherList.size())
						break;
					i1 = this.get(thisLoc).getIndex();
					i2 = otherList.get(otherLoc).getIndex();
				}
			}
			else
			{
				thisLoc = getItemLocWithIndexExp(i2, thisLoc);
				if(thisLoc < 0)
				{
					thisLoc = thisLoc * -1 - 1;
					if(thisLoc >= this.size())
						break;
					i1 = this.get(thisLoc).getIndex();
				}
				else
				{
					rtnVal.add(thisLoc);
					thisLoc++;
					otherLoc++;
					if(thisLoc >= this.size() || otherLoc >= otherList.size())
						break;
					i1 = this.get(thisLoc).getIndex();
					i2 = otherList.get(otherLoc).getIndex();
				}
			}
		}
		return rtnVal;
	}
	
	public ArrayList<Indexed> getNotInList2(IndexedList otherList)
	{
		ArrayList<Integer> intIndeces = intersection(otherList);
		ArrayList<Indexed> rtnVal = new ArrayList<Indexed>(this.size());
		rtnVal.addAll(this.getList());
		for(int i = intIndeces.size() - 1; i >= 0; i--)
			rtnVal.remove((int) intIndeces.get(i));
		return rtnVal;
	}
	
	public ArrayList<Indexed> getInList2(IndexedList otherList)
	{
		ArrayList<Integer> intIndeces = intersection(otherList);
		ArrayList<Indexed> rtnVal = new ArrayList<Indexed>(intIndeces.size());
		for(int i = 0; i < intIndeces.size(); i++)
			rtnVal.add(this.get(intIndeces.get(i)));
		return rtnVal;
	}
	
	public void removeAllInList(IndexedList otherList)
	{
		int otherListLoc = 0;
		int i = 0;
		while(i < list.size())
		{
			long findIndex = list.get(i).getIndex();
			int otherLoc = otherList.getItemLocWithIndexExp(findIndex, otherListLoc);
			if(otherLoc >= 0)
			{
				//System.out.println("Item " + findIndex + " found; otherLoc=" + otherLoc);
				list.remove(i);
				i--;
				otherListLoc = otherLoc;
			}
			else
				otherListLoc = otherLoc * -1 - 1;
			i++;
			if(otherListLoc >= otherList.size())
				break;
		}
	}
	
	public ArrayList<Indexed> getNotInList(IndexedList otherList)
	{
		ArrayList<Indexed> rtnVal = new ArrayList<Indexed>();
		int otherListLoc = 0;
		int i = 0;
		while(i < list.size())
		{
			long findIndex = list.get(i).getIndex();
			int otherLoc = otherList.getItemLocWithIndexExp(findIndex, otherListLoc);
			if(otherLoc < 0)
			{
				//System.out.println("Item " + findIndex + " not found; otherLoc=" + otherLoc);
				rtnVal.add(list.get(i));
				otherListLoc = otherLoc * -1 - 1;
			}
			else
				otherListLoc = otherLoc;
			i++;
			if(otherListLoc >= otherList.size())
				break;
		}
		long lastValue = otherList.getLast().getIndex();
		while(i < list.size())
		{
			if(list.get(i).getIndex() > lastValue)
				break;
			i++;
		}
		while(i < list.size())
		{
			rtnVal.add(list.get(i));
			i++;
		}
		return rtnVal;
	}
	
	public ArrayList<Indexed> getInList(IndexedList otherList)
	{
		ArrayList<Indexed> rtnVal = new ArrayList<Indexed>();
		int otherListLoc = 0;
		int i = 0;
		while(i < list.size())
		{
			long findIndex = list.get(i).getIndex();
			int otherLoc = otherList.getItemLocWithIndexExp(findIndex, otherListLoc);
			if(otherLoc >= 0)
			{
				//System.out.println("Item " + findIndex + " found; otherLoc=" + otherLoc);
				rtnVal.add(list.get(i));
				otherListLoc = otherLoc;
			}
			else
				otherListLoc = otherLoc * -1 - 1;
			i++;
			if(otherListLoc >= otherList.size())
				break;
		}
		return rtnVal;
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	
	
	public static void intersectionTest()
	{
		int[] testCase1 = {2,6,8,9,11,12,15,16,17,18,21,25,26,30,32,34,35,36,37,38};
		int[] testCase2 = {0,1,2,4,5,6,7,11,17,18,19,20,21,22,23,29,31,32,33,34,36};
		
		IndexedList il = new IndexedList();
		il.clear();
		for(int i = 0; i < testCase1.length; i++)
			il.add(new IndexedInteger(testCase1[i]));
		
		IndexedList il2 = new IndexedList();
		il2.clear();
		for(int i = 0; i < testCase2.length; i++)
			il2.add(new IndexedInteger(testCase2[i]));
		
		int[] expected = {2,6,11,17,18,21,32,34,36};
		ArrayList<Indexed> output = il.getInList2(il2);
		if(output.size() != expected.length)
			System.out.println("Error - output size is " + output.size() + " when it should be " + expected.length);
		for(int i = 0; i < expected.length; i++)
		{
			boolean found = false;
			for(int j = 0; j < output.size(); j++)
			{
				if(output.get(j).getIndex() == expected[i])
					found = true;
			}
			if(!found)
				System.out.println("Error - didn't find " + expected[i]);
		}
	}
	
	public static void indexedListTest(int listSize, int nCases)
	{
		int listSize2 = 30;
		System.out.println("NCases = " + nCases);
		long time2 = 0;
		for(int k = 0; k < nCases; k++)
		{
		int[] testCase1 = null; //{2,6,8,9,11,12,15,16,17,18,21,25,26,30,32,34,35,36,37,38};
		int[] testCase2 = null;//{0,1,2,4,5,6,7,11,17,18,19,20,21,22,23,29,31,32,33,34,36};
		//int[] testCase1 = {1,1,1};
		//int[] testCase2 = {1,1,1};
		IndexedList il = new IndexedList();
		for(int i = 0; i < listSize; i++)
		{
			IndexedInteger ii = new IndexedInteger((int) (Math.random() * 40));
			il.add(ii);
		}
		if(testCase1 != null)
		{
			il.clear();
			for(int i = 0; i < testCase1.length; i++)
				il.add(new IndexedInteger(testCase1[i]));
		}
		/*System.out.print("List 1:");
		for(int i = 0; i < il.size(); i++)
			System.out.print(il.get(i).getIndex() + "  ");
		System.out.println();*/
		
		IndexedList il2 = new IndexedList();
		for(int i = 0; i < listSize2; i++)
		{
			IndexedInteger ii = new IndexedInteger((int) (Math.random() * 40));
			il2.add(ii);
		}
		if(testCase2 != null)
		{
			il2.clear();
			for(int i = 0; i < testCase2.length; i++)
				il2.add(new IndexedInteger(testCase2[i]));
		}
		/*System.out.print("List 2:");
		for(int i = 0; i < il2.size(); i++)
			System.out.print(il2.get(i).getIndex() + "  ");
		System.out.println();*/
		long time = System.nanoTime();
		ArrayList<Indexed> inList = il.getInList(il2);
		time = System.nanoTime() - time;
		ArrayList<Indexed> notInList1 = il.getNotInList(il2);
		
		ArrayList<Indexed> notInList2 = il2.getNotInList(il);
		
		time2 += time;
		for(int i = 0; i < il.size(); i++)
		{
			long val = il.get(i).getIndex();
			boolean found = false;
			for(int j = 0; j < il2.size(); j++)
			{
				if(il2.get(j).getIndex() == val)
				{
					found = true;
					break;
				}
			}
			boolean found2 = false;
			for(int j = 0; j < inList.size(); j++)
			{
				if(inList.get(j).getIndex() == val)
				{
					found2 = true;
					break;
				}
			}
			if(found && !found2)
				System.out.println("getInList() is missing a value");
			if(!found && found2)
				System.out.println("getInList() has an extra value");
			boolean found3 = false;
			for(int j = 0; j < notInList1.size(); j++)
			{
				if(notInList1.get(j).getIndex() == val)
				{
					found3 = true;
					break;
				}
			}
			if(found3 && found)
				System.out.println("l1.getNotInList(l2) has an extra value");
			if(!found2 && !found3)
				System.out.println("l1.getNotInList(l2) is missing a value");
		}
		/*System.out.print("In both lists:");
		for(int i = 0; i < inList.size(); i++)
			System.out.print(inList.get(i).getIndex() + "  ");
		System.out.println();*/
		
		
		/*System.out.print("Not in list 2:");
		for(int i = 0; i < notInList1.size(); i++)
			System.out.print(notInList1.get(i).getIndex() + "  ");
		System.out.println();*/
		
		for(int i = 0; i < il2.size(); i++)
		{
			long val = il2.get(i).getIndex();
			boolean found = false;
			for(int j = 0; j < il.size(); j++)
			{
				if(il.get(j).getIndex() == val)
				{
					found = true;
					break;
				}
			}
			boolean found3 = false;
			for(int j = 0; j < notInList2.size(); j++)
			{
				if(notInList2.get(j).getIndex() == val)
				{
					found3 = true;
					break;
				}
			}
			if(found && found3)
				System.out.println("l2.getNotInList(l1) has an extra value");
			if(!found && !found3)
				System.out.println("l2.getNotInList(l1) is missing a value");
		}
		
		/*System.out.print("Not in list 1:");
		for(int i = 0; i < notInList2.size(); i++)
			System.out.print(notInList2.get(i).getIndex() + "  ");
		System.out.println();*/
	}
		
		System.out.println(time2 + " ns");
	}
	
}


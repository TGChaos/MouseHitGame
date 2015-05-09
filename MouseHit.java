import java.text.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.regex.*;
import javax.swing.*;

public class MouseHit{
	public static void main(String[] args){
		new StartFrame();
	}
}

class PlayingFrame extends Frame{
	//URL sound = this.getClass().getResource("gargantuar_thump.wav");
	//AudioClip ac = Applet.newAudioClip(sound);
	MouseButton[] b = new MouseButton[9];
	Label title = new Label("打地鼠   V1.00");
	int timeleft;	//标记时间上限数值（不变）
	Label time;	//负责显示当前时间标签
	int totalscore = 0;	//总得分数值
	Label score;	//负责显示总得分的标签
	int maxLifes;	//标记最大生命上限
	int lifes;	//标记当前生命值
	Label currentLife;	//负责显示当前剩余生命值的标签
	Button start = new Button("开始");
	Button pause = new Button("暂停");
	Button resume = new Button("继续");
	Button restart = new Button("重置");
	Button back = new Button("返回");
	TimeController tmc; //负责控制时间的线程
	Panel mousePanel = new Panel(new GridLayout(3,0));
	Panel manuPanel = new Panel(new GridLayout(0,1));
	boolean startCount = false;	//标记是否已经开始了游戏，true表示开始计分
	int currentMouse = 0;	//用来标记当前已经出现的地鼠总数
	int speed;	//标记游戏速度
	
	PlayingFrame(int o,int j,int k){
		setVal(o,j,k);	//设定游戏的时间o，生命值j，游戏速度k
		time = new Label("剩余时间："+timeleft);
		score = new Label("总分数："+totalscore);
		currentLife = new Label("剩余生命："+lifes);
		int i;
		for(i=0;i<b.length;i++){
			b[i] = new MouseButton();
			mousePanel.add(b[i]);
			b[i].addActionListener(b[i].mc);
			b[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					MouseButton mb = (MouseButton)a.getSource();
					Color c = mb.getBackground();
					//ac.stop();
					//ac.play();
					if(startCount){
						if(c.equals(Color.GREEN)){	//绿色计10分
							totalscore+=10;
							score.setText("总分数："+totalscore);
						}else if(c.equals(Color.BLUE)){	//蓝色计5分
							totalscore+=5;
							score.setText("总分数："+totalscore);
						}else if(c.equals(Color.RED)){	//红色扣掉1点生命值，同时判断生命值是否为0
							lifes--;
							currentLife.setText("剩余生命："+lifes);
							if(lifes==0){	//若生命值为0表示游戏结束
								gameStop();	//游戏停止
								JOptionPane.showMessageDialog(null,"生命值为零\n游戏结束");
								JOptionPane.showMessageDialog(null,"你的得分是"+totalscore);
								startCount = false;
								askForSave();	//询问是否保存
							}
						}
					}
				}
			});
		}
		tmc = new TimeController();
		start.addActionListener(tmc);
		
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				for(int i=0;i<b.length;i++){
					if(b[i].mc!=null && b[i].mc.t!=null){
						b[i].cutThread();	//所有格子的线程都停止
					}
				}
				if(tmc.t!=null){
					tmc.t.interrupt();	//同时把控制时间的线程也停止
				}
				totalscore = 0;		//总分清零
				lifes = maxLifes;	//生命值恢复
				currentLife.setText("剩余生命："+lifes);
				score.setText("总分数："+totalscore);
				time.setText("剩余时间："+timeleft);
				startCount = false;	//开关关闭
			}
		});
		
		pause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				startCount = false;
				for(int i=0;i<b.length;i++) {
					b[i].setFlag(false);
				}
				//JOptionPane.showMessageDialog(null,"这个有待开发，哈哈！");	//有待开发，初步打算为所有线程睡眠
			}
		});
		
		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				startCount = true;
				for(int i=0;i<b.length;i++) {
					b[i].setFlag(true);
				}
				//JOptionPane.showMessageDialog(null,"这个有待开发，哈哈！");	//有待开发，初步打算为所有线程从睡眠中恢复执行
			}
		});
		
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				if(startCount){
					JOptionPane.showMessageDialog(null,"游戏中，不能返回！");	//负责返回上一个界面
					return;
				}
				setVisible(false);
				new StartFrame();
			}
		});
		
		setBounds(450,150,300,330);
		setLayout(new BorderLayout());
		manuPanel.add(time);
		manuPanel.add(score);
		manuPanel.add(currentLife);
		manuPanel.add(restart);
		manuPanel.add(start);
		manuPanel.add(pause);
		manuPanel.add(resume);
		manuPanel.add(back);
		manuPanel.setSize(50,300);
		mousePanel.setSize(200,200);
		add(manuPanel,BorderLayout.EAST);
		add(title,BorderLayout.NORTH);
		add(mousePanel,BorderLayout.CENTER);
		
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w){
				setVisible(false);
				System.exit(0);
			}
		});
		setVisible(true);
	}
	
	private void setVal(int time,int life,int _speed){
		timeleft = time;
		speed = _speed;
		maxLifes = life;
		lifes = maxLifes;
	}
	
	private void gameStop(){	//游戏停止，所有的线程都停止
		for(int i=0;i<b.length;i++){
			if(b[i].mc.t!=null){
				b[i].cutThread();
			}
		}
		tmc.t.interrupt();
	}
	
	private void askForSave(){	//询问保存分数
		int t = JOptionPane.showConfirmDialog(null,"你的分数是"+totalscore+"\n是否要保存？");
		if(t==0){
			String[] tempstring = new String[5];
			String temp;
			String name = JOptionPane.showInputDialog("请输入姓名（英文）");
			while(name.length()>5 ||!name.matches("\\w+")){
				JOptionPane.showMessageDialog(null,"名字最大长度为5！");
				name = JOptionPane.showInputDialog("请输入姓名（英文）");
			}
			name = getSpace(name);
			int i = 0;
			int maxIndex = -1;
			int currentMaxNumber = totalscore;
			Pattern p = Pattern.compile("######\\d+######");
			Matcher m;
			BufferedReader br = null;	//从已有的记录中读取数据，根据分数来判断当前要保存的分数排在第几
			BufferedWriter bw = null;	//再将重新排好的数据再次写回硬盘
			try{
				File f = new File("F:\\highscore.tg");
				if(!f.exists()){	//用于判断是否是第一次玩，第一次玩则自动创建一个新的空记录
					f.createNewFile();
				}
				br = new BufferedReader(new FileReader(f));
				temp = br.readLine();
				if(temp==null){	//判断记录是否为空，为空则直接将分数写入
					br.close();
					bw = new BufferedWriter(new FileWriter(f));
					bw.write(name+"######"+totalscore+"######"+getDate()+"\r\n");
					bw.close();		
					return;
				}
				while(temp!=null && i<=4){	//如果记录非空，则每次读入一条记录，比较该记录与当前要保存的分数的大小，并用
											//另外一个标识当前要保存的分数所处的位置。
					tempstring[i] = temp;
					m = p.matcher(temp);
					if(m.find()){
						int _currentMaxNumber = Integer.parseInt((m.group()).replaceAll("#", ""));
						if(_currentMaxNumber>currentMaxNumber){
							maxIndex = i;
						}
					}
					i++;
					temp = br.readLine();
				}
				br.close();
				bw = new BufferedWriter(new FileWriter(f));
				int count = 1;
				for(i=0;count<=5 &&i < 5;i++){	//写回硬盘
					if(i==maxIndex+1){
						bw.write(name+"######"+totalscore+"######"+getDate()+"\r\n");
						count++;
					}
					if(tempstring[i]!=null){
						bw.write(tempstring[i]+"\r\n");
						count++;
					}
				}
				bw.close();
				JOptionPane.showMessageDialog(null,"记录已经保存！");
			}catch(IOException e){
				try {
					if(br!=null){
						br.close();
					}
					if(bw!=null){
						bw.close();
					}
					JOptionPane.showMessageDialog(null,"出错了！");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,"出错了！");
				}
			}
		}
	}
	
	private String getSpace(String s){	//用于统一格式，对于输入字数比较少的字符串自动用空格补满
										//不过感觉补出来的效果不好。
		int i = 2*(5-s.length()+1);
		for(int j=1;j<=i;j++){
			s+=" ";
		}
		return s;
	}
	
	private String getDate(){			//获得当前时间，游戏记录中包括了游戏的时间
		TimeZone tz = TimeZone.getTimeZone("GMT+8");
		SimpleDateFormat chinaFormatter = new SimpleDateFormat("yyyy年MM月dd日   HH时mm分");
		chinaFormatter.setTimeZone(tz);
		String s=chinaFormatter.format(new Date());
		return s;
	}
	
	
	class TimeController implements ActionListener, Runnable{	//控制时间流逝的线程类
		Thread t;
		boolean over = false;
		boolean count = false;
		TimeController(){
			for(int i=0;i<b.length;i++){
				b[i].mc.t = new Thread(b[i]);
				b[i].setFlag(false);
				b[i].mc.t.start();
			}
		}
		public void run(){
			if(totalscore>0 || lifes<maxLifes){
				JOptionPane.showMessageDialog(null,"请点击重置");
				return;
			}
			int temp = timeleft;
			while(!over){
				if(count){	//线程每执行一次就睡眠1秒钟，每一次将时间减少一秒
					temp--;
					time.setText("剩余时间："+temp);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
					if(temp==0){	
						JOptionPane.showMessageDialog(null,"时间到!");
						gameStop();
						JOptionPane.showMessageDialog(null,"你的得分是："+ totalscore);
						startCount = false;
						askForSave();
						break;
					}
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
		public void actionPerformed(ActionEvent a){
			if(t == null)
				t = new Thread(new TimeController());
			t.start();
			if(timeleft==0){
				JOptionPane.showMessageDialog(null,"请点击重置");
				return;
			}
			this.count = true;
			for(int i=0;i<b.length;i++){
				b[i].setFlag(true);
			}
			startCount = true;
		}
		
	}

	class MouseButton extends Button implements Runnable{	//方格类，自身带有控制颜色变化的线程类
		boolean flag = false;	//标记方格能否变色的标记符
		boolean over = false;
		int index = 0;
		int[] activateInt = {1,4,7};	//方格变色的条件为随机数是1，4，7
		int sleepTime;
		
		MouseController mc = new MouseController();
		MouseButton(){
			switch(speed){	//根据设定的游戏速度来设置颜色变化时间
				case 1:sleepTime = 1500;break;
				case 2:sleepTime = 1200;break;
				case 3:sleepTime = 1000;break;
				case 4:sleepTime = 800;break;
				case 5:sleepTime = 500;break;
			}
			setBackground(Color.GRAY);	//默认颜色为灰色
		}
		public void run(){
			System.out.println("The run method is start!");
			while(!over){
					if(flag){
						index = (int)(Math.random()*9+1);	//颜色变化通过随机数的变化来实现
						if(numbersIn(activateInt,index)){
							index = (int)(Math.random()*15+1);
							try {
								if(index>=1 && index<=3){
									setBackground(Color.GREEN);
								}else if(index<=6){
									setBackground(Color.RED);
								}else if(index<=9){
									setBackground(Color.BLUE);
								}else {
									setBackground(Color.GRAY);
								}
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
								setBackground(Color.GRAY);
								System.out.println("set color exp");
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e1) {
									System.out.println("sleep exp");
								}
							}
						}
					}else{
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
			}
			System.out.println("The run method is over!");
		}
		
		private boolean numbersIn(int[] a,int n){	//判断数字是否在一个数组中
			for(int i=0;i<a.length;i++){
				if(a[i]==n){
					return true;
				}
			}
			return false;
		}
		
		public void cutThread(){	//中断线程
			flag = false;
			//over = true;
			this.setBackground(Color.GRAY);
		}
		
		public void setFlag(boolean f){	//设置标记符
			flag = f;
		}
		
		public boolean getFlag(){	//获得标记符
			return flag;
		}

		class MouseController implements ActionListener{	//方格按钮的监听器
			Thread t;
			public void actionPerformed(ActionEvent a){
				if(t==null){
					return;
				}
				MouseButton mb = (MouseButton)(a.getSource());
				mb.mc.t.interrupt();
			}
			
		}
		
	}
	
}

class StartFrame extends Frame{		//开始时的窗口
	Button gameStart = new Button("游戏开始");
	Button highestScore = new Button("最高分");
	Button about = new Button("关于游戏");
	Button gameOut = new Button("游戏结束");
	Label lb2 = new Label("打地鼠   V1.00",Label.CENTER);
	StartFrame(){
		setLayout(new GridLayout(0,1));
		add(lb2);
		add(gameStart);
		add(new Label());
		add(highestScore);
		add(new Label());
		add(about);
		add(new Label());
		add(gameOut);
		setBounds(450,150,200,300);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w){
				setVisible(false);
				System.exit(0);
			}
		});
		
		gameOut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				JOptionPane.showMessageDialog(null,"谢谢游戏！");
				setVisible(false);
				System.exit(0);
			}
		});
		
		gameStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				setVisible(false);
				new OptionFrame();
			}
		});
		
		highestScore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				setVisible(false);
				new HighScoreFrame();
			}
		});
		
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				setVisible(false);
				new AboutFrame();
			}
		});
		setVisible(true);
	}
	
	class AboutFrame extends Frame{		//关于游戏的窗口
		int currentPage = 1;
		Label pages = new Label(currentPage+"/3",Label.CENTER);	//页数下标
		Button leftButton = new Button("前一页");
		Button rightButton = new Button("后一页");
		Button back = new Button("返回");
		MyPanel showPassage = new MyPanel();
		Panel p1 = new Panel(new GridLayout(1,3));
		Panel p2 = new Panel(new GridLayout(1,3));
		String[] passage = new String[3];
		
		
		AboutFrame(){
			setLayout(new BorderLayout());
			passage[0] = "打地鼠是一个可以考验反应的游戏";
			passage[1] = "游戏玩法很简单     地鼠(砖块)颜色分为绿，蓝，红三种，打中绿色加10分，打中蓝色加5分，" +
						"打中红色扣一次生命值，生命值为0则宣布游戏结束。游戏速度1是最慢，5是最快";
			passage[2] = "V1.00版有部分功能尚未开发，如果遇到什么问题请联系我啦！Powered By LIN";
			showPassage.setText(passage[0]);
			p2.add(new Label("游戏说明：",Label.LEFT),BorderLayout.NORTH);
			p2.add(back);
			p1.add(leftButton);
			p1.add(pages);
			p1.add(rightButton);
			add(p2,BorderLayout.NORTH);
			add(p1,BorderLayout.SOUTH);
			add(showPassage,BorderLayout.CENTER);
			
			leftButton.addActionListener(new ActionListener(){	//控制前一页的监听器
				public void actionPerformed(ActionEvent a){
					if(currentPage==1){
						return;
					}
					currentPage--;
					pages.setText(currentPage+"/3");
					showPassage.setText(passage[currentPage-1]);
				}
			});
			
			rightButton.addActionListener(new ActionListener(){	//控制后一页的监听器
				public void actionPerformed(ActionEvent a){
					if(currentPage==3){
						return;
					}
					currentPage++;
					pages.setText(currentPage+"/3");
					showPassage.setText(passage[currentPage-1]);
				}
			});
			
			back.addActionListener(new ActionListener(){	//返回上一窗口
				public void actionPerformed(ActionEvent a){
					setVisible(false);
					new StartFrame();
				}
			});
			
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent w){
					setVisible(false);
					System.exit(0);
				}
			});
			
			setBounds(450,150,200,300);
			setVisible(true);
		}
		
		class MyPanel extends Panel{	//用标签来显示字符串，每个标签中字符个数为10
			ArrayList<Label> stringLabel = new ArrayList<Label>();
			int count;
			MyPanel(){
				super();
				Label tempLB;
				setLayout(new GridLayout(0,1));
				for(int i=0;i<10;i++){
					tempLB = new Label("",Label.CENTER);
					stringLabel.add(tempLB);
					add(tempLB);
				}
			}
			
			public void setText(String s){	//将
				String temp = "";
				count=0;
				int i;
				if(s.length()<=10){
					stringLabel.get(count).setText(s);
					for(i=count+1;i<10;i++){
						stringLabel.get(i).setText("");
					}
					return;
				}
				while(s.length()>10){
					temp = s.substring(0, 10);
					stringLabel.get(count).setText(temp);
					s = s.substring(10,s.length());
					count++;
				}
				stringLabel.get(count).setText(s);
				for(i=count+1;i<10;i++){
					stringLabel.get(i).setText("");
				}
			}
			
		}
		
	}
	
	class OptionFrame extends Frame{	//游戏设置窗口
		Label[] labelList = new Label[3];
		Label[] textList = new Label[3];
		Label[] labelList2 = new Label[3];
		Panel p1 = new Panel(new GridLayout(1,0));
		Panel p2 = new Panel(new GridLayout(1,0));
		Panel p3 = new Panel(new GridLayout(1,0));
		Panel p4 = new Panel(new GridLayout(1,0));
		Button ok = new Button("确定");
		Button auto = new Button("默认");
		Button back = new Button("返回");
		Button left1 = new Button("<");
		Button left2 = new Button("<");
		Button left3 = new Button("<");
		Button right1 = new Button(">");
		Button right2 = new Button(">");
		Button right3 = new Button(">");
		OptionFrame(){
			setLayout(new GridLayout(0,1));
			labelList[0] = new Label("时间设置：");
			labelList[1] = new Label("生命值设置：");
			labelList[2] = new Label("游戏速度：");
			
			textList[0] = new Label("60",Label.CENTER);
			textList[1] = new Label("5",Label.CENTER);
			textList[2] = new Label("3",Label.CENTER);
			
			labelList2[0] = new Label("(30-99)");
			labelList2[1] = new Label("(3-9)");
			labelList2[2] = new Label("(1-5)");
			
			p1.add(labelList[0]);p1.add(left1);p1.add(textList[0]);p1.add(right1);
			p2.add(labelList[1]);p2.add(left2);p2.add(textList[1]);p2.add(right2);
			p3.add(labelList[2]);p3.add(left3);p3.add(textList[2]);p3.add(right3);
			
			p4.add(auto);
			p4.add(ok);
			p4.add(back);
			
			add(p1);
			add(p2);
			add(p3);
			add(p4);
			
			
			auto.addActionListener(new ActionListener(){	//将数值设置为默认值
				public void actionPerformed(ActionEvent a){
					textList[0].setText("60");
					textList[1].setText("5");
					textList[2].setText("3");
				}
			});
			
			ok.addActionListener(new ActionListener(){		//开始游戏
				public void actionPerformed(ActionEvent a){
					int i = Integer.parseInt(textList[0].getText());
					int j = Integer.parseInt(textList[1].getText());
					int k = Integer.parseInt(textList[2].getText());
					setVisible(false);
					new PlayingFrame(i,j,k);
				}
			});
			
			back.addActionListener(new ActionListener(){	//返回上一窗口
				public void actionPerformed(ActionEvent a){
					setVisible(false);
					new StartFrame();
				}
			});
			
			left1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[0].getText());
					if(temp==30){
						return;
					}
					temp-=10;
					textList[0].setText(""+temp);
				}
			});
			
			left2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[1].getText());
					if(temp==1){
						return;
					}
					temp-=1;
					textList[1].setText(""+temp);
				}
			});
			
			left3.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[2].getText());
					if(temp==1){
						return;
					}
					temp-=1;
					textList[2].setText(""+temp);
				}
			});
			
			right1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[0].getText());
					if(temp==90){
						return;
					}
					temp+=10;
					textList[0].setText(""+temp);
				}
			});
			
			right2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[1].getText());
					if(temp==9){
						return;
					}
					temp+=1;
					textList[1].setText(""+temp);
				}
			});
			
			right3.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					int temp = Integer.parseInt(textList[2].getText());
					if(temp==5){
						return;
					}
					temp+=1;
					textList[2].setText(""+temp);
				}
			});
			
			
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent w){
					setVisible(false);
					System.exit(0);
				}
			});
			setBounds(450,150,300,150);
			setVisible(true);
		}
		
	}
	
	class HighScoreFrame extends Frame{		//最高分窗口，列出分数从高到低的5位
		BufferedReader bf;
		Button clear = new Button("清空记录");
		Button back = new Button("返回");
		Label title = new Label("玩家姓名       得分                          时间                                       ",Label.CENTER);
		Label[] resultList = new Label[5];
		Panel p1 = new Panel(new GridLayout(1,0));
		String[] tempresult;
		HighScoreFrame(){
			int i;
			String temp;
			for(i=0;i<resultList.length;i++){
				resultList[i] = new Label("",Label.CENTER);
			}
			try {
				File f = new File("F:\\highscore.tg");
				if(!f.exists()){
					f.createNewFile();
				}
				i=0;
				bf = new BufferedReader(new FileReader(f));
				while((temp = bf.readLine())!=null && i<=4){	//从记录文件中一条条地读出记录
					tempresult = temp.split("######");
					temp = "";
					for(int j=0;j<tempresult.length;j++){
						temp+=tempresult[j]+"            ";
					}
					resultList[i].setText(temp);
					i++;
				}
				bf.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,"出错了！");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,"出错了！");
			}
			
			setLayout(new GridLayout(0,1));
			
			p1.add(clear);
			p1.add(back);
			add(title);
			for(i=0;i<resultList.length;i++){
				add(resultList[i]);
			}
			add(p1);
			
			back.addActionListener(new ActionListener(){	//返回上一窗口
				public void actionPerformed(ActionEvent a){
					setVisible(false);
					new StartFrame();
				}
			});
			
			clear.addActionListener(new ActionListener(){	//将最高分记录清空
				public void actionPerformed(ActionEvent a){
					try {
						int temp = JOptionPane.showConfirmDialog(null,"是否要删除？");
						if(temp==0){
							BufferedWriter bw = new BufferedWriter(new FileWriter("F:\\MouseHit\\highscore.tg"));
							bw.write("");
							bw.close();
							JOptionPane.showMessageDialog(null,"记录已经清空！");
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null,"出错了！");
					}
				}
			});
			
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent w){
					setVisible(false);
					System.exit(0);
				}
			});
			
			setBounds(450,150,350,250);
			setVisible(true);
		}
		
	}
	
}

/*一个叫打地鼠小游戏，刚开始打算做的时候是周一（26号）的晚上的想法了。初初想的时候觉得还是挺简单的，但是做了起来才发现
 * 里面有很多需要注意的细节地方。
 *
 * 做的思路很简单，设置9个按钮来代表地鼠坑，设置开始，重置，暂停等按钮，然后对于每一个按钮来说用一个线程来控制它颜色的
 * 变化，再另外设置一个线程来控制时间的流逝。
 * 
 * 在做的过程中我发现，由于一开始线程的设置不合理，将控制任务类和被控制的类合并到了一起，结果导致相互之间的逻辑变得很混
 * 乱，这是涉及多线程的情况。一定要注意控制和对象分离。
 * 
 * 另外在做的过程中，感觉比较棘手的地方，一个是分数的保存，一个是对于线程的控制。
 * 
 * 先说分数的保存，最初的设计思路是对的，就是用一个文本文档将分数保存起来，在玩完一局游戏之后要保存时，再用IO类把记录一
 * 条一条地取出来，然后用正则表达式去提取出当中的分数，并且与当前需要保存的分数进行比较，通过比对来找到当前要保存的分数
 * 在前五名中排第几。确认好之后，再把调整好的顺序重新写回硬盘，从而达到了一个保存的效果。关于IO方面的问题其实不大，但是
 * 在分数显示的时候却让我很犯难。我当初打算用Label标签类来显示分数，但是发现标签类不能自动换行，于是改用panel加多个标签
 * 类结合的方法来显示分数。还有一点，我需要显示的是玩家姓名，分数和保存时间，但是由于姓名长度不一样，每次出来的字符串的
 * 长度就不一样，最后在label上显示的结果也不一样，所以不得不通过增补空格来修正字符串长度，但是又不知道为什么，增补空格之
 * 后，显示出来的结果还是有问题，于是就放下不管了，希望在下一个版本中会解决。
 * 
 * 再说说线程的控制问题，由于设计上的不足导致了线程和对象绑定在了一起，虽然在运行上不会有太大的问题，但是在更新维护上，特
 * 别是更换另一个线程的时候就会很麻烦，就不得不把大部分相关代码都要重写一遍。也正是将线程和对象绑定到了一起，导致在一开始
 * 调试的时候程序怎么都跑不起来，后来才发现是因为新建线程并没有以对象本身来创建，说白了就是创建了一个根本不是在控制我需要
 * 控制的对象的线程，然后调试了很久才发现这一点。
 * 
 * 至于一些具体的实现问题就不多说了。													
 * 																										2012.3.30
 * */

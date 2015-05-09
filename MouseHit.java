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
	Label title = new Label("�����   V1.00");
	int timeleft;	//���ʱ��������ֵ�����䣩
	Label time;	//������ʾ��ǰʱ���ǩ
	int totalscore = 0;	//�ܵ÷���ֵ
	Label score;	//������ʾ�ܵ÷ֵı�ǩ
	int maxLifes;	//��������������
	int lifes;	//��ǵ�ǰ����ֵ
	Label currentLife;	//������ʾ��ǰʣ������ֵ�ı�ǩ
	Button start = new Button("��ʼ");
	Button pause = new Button("��ͣ");
	Button resume = new Button("����");
	Button restart = new Button("����");
	Button back = new Button("����");
	TimeController tmc; //�������ʱ����߳�
	Panel mousePanel = new Panel(new GridLayout(3,0));
	Panel manuPanel = new Panel(new GridLayout(0,1));
	boolean startCount = false;	//����Ƿ��Ѿ���ʼ����Ϸ��true��ʾ��ʼ�Ʒ�
	int currentMouse = 0;	//������ǵ�ǰ�Ѿ����ֵĵ�������
	int speed;	//�����Ϸ�ٶ�
	
	PlayingFrame(int o,int j,int k){
		setVal(o,j,k);	//�趨��Ϸ��ʱ��o������ֵj����Ϸ�ٶ�k
		time = new Label("ʣ��ʱ�䣺"+timeleft);
		score = new Label("�ܷ�����"+totalscore);
		currentLife = new Label("ʣ��������"+lifes);
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
						if(c.equals(Color.GREEN)){	//��ɫ��10��
							totalscore+=10;
							score.setText("�ܷ�����"+totalscore);
						}else if(c.equals(Color.BLUE)){	//��ɫ��5��
							totalscore+=5;
							score.setText("�ܷ�����"+totalscore);
						}else if(c.equals(Color.RED)){	//��ɫ�۵�1������ֵ��ͬʱ�ж�����ֵ�Ƿ�Ϊ0
							lifes--;
							currentLife.setText("ʣ��������"+lifes);
							if(lifes==0){	//������ֵΪ0��ʾ��Ϸ����
								gameStop();	//��Ϸֹͣ
								JOptionPane.showMessageDialog(null,"����ֵΪ��\n��Ϸ����");
								JOptionPane.showMessageDialog(null,"��ĵ÷���"+totalscore);
								startCount = false;
								askForSave();	//ѯ���Ƿ񱣴�
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
						b[i].cutThread();	//���и��ӵ��̶߳�ֹͣ
					}
				}
				if(tmc.t!=null){
					tmc.t.interrupt();	//ͬʱ�ѿ���ʱ����߳�Ҳֹͣ
				}
				totalscore = 0;		//�ܷ�����
				lifes = maxLifes;	//����ֵ�ָ�
				currentLife.setText("ʣ��������"+lifes);
				score.setText("�ܷ�����"+totalscore);
				time.setText("ʣ��ʱ�䣺"+timeleft);
				startCount = false;	//���عر�
			}
		});
		
		pause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				startCount = false;
				for(int i=0;i<b.length;i++) {
					b[i].setFlag(false);
				}
				//JOptionPane.showMessageDialog(null,"����д�������������");	//�д���������������Ϊ�����߳�˯��
			}
		});
		
		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				startCount = true;
				for(int i=0;i<b.length;i++) {
					b[i].setFlag(true);
				}
				//JOptionPane.showMessageDialog(null,"����д�������������");	//�д���������������Ϊ�����̴߳�˯���лָ�ִ��
			}
		});
		
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				if(startCount){
					JOptionPane.showMessageDialog(null,"��Ϸ�У����ܷ��أ�");	//���𷵻���һ������
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
	
	private void gameStop(){	//��Ϸֹͣ�����е��̶߳�ֹͣ
		for(int i=0;i<b.length;i++){
			if(b[i].mc.t!=null){
				b[i].cutThread();
			}
		}
		tmc.t.interrupt();
	}
	
	private void askForSave(){	//ѯ�ʱ������
		int t = JOptionPane.showConfirmDialog(null,"��ķ�����"+totalscore+"\n�Ƿ�Ҫ���棿");
		if(t==0){
			String[] tempstring = new String[5];
			String temp;
			String name = JOptionPane.showInputDialog("������������Ӣ�ģ�");
			while(name.length()>5 ||!name.matches("\\w+")){
				JOptionPane.showMessageDialog(null,"������󳤶�Ϊ5��");
				name = JOptionPane.showInputDialog("������������Ӣ�ģ�");
			}
			name = getSpace(name);
			int i = 0;
			int maxIndex = -1;
			int currentMaxNumber = totalscore;
			Pattern p = Pattern.compile("######\\d+######");
			Matcher m;
			BufferedReader br = null;	//�����еļ�¼�ж�ȡ���ݣ����ݷ������жϵ�ǰҪ����ķ������ڵڼ�
			BufferedWriter bw = null;	//�ٽ������źõ������ٴ�д��Ӳ��
			try{
				File f = new File("F:\\highscore.tg");
				if(!f.exists()){	//�����ж��Ƿ��ǵ�һ���棬��һ�������Զ�����һ���µĿռ�¼
					f.createNewFile();
				}
				br = new BufferedReader(new FileReader(f));
				temp = br.readLine();
				if(temp==null){	//�жϼ�¼�Ƿ�Ϊ�գ�Ϊ����ֱ�ӽ�����д��
					br.close();
					bw = new BufferedWriter(new FileWriter(f));
					bw.write(name+"######"+totalscore+"######"+getDate()+"\r\n");
					bw.close();		
					return;
				}
				while(temp!=null && i<=4){	//�����¼�ǿգ���ÿ�ζ���һ����¼���Ƚϸü�¼�뵱ǰҪ����ķ����Ĵ�С������
											//����һ����ʶ��ǰҪ����ķ���������λ�á�
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
				for(i=0;count<=5 &&i < 5;i++){	//д��Ӳ��
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
				JOptionPane.showMessageDialog(null,"��¼�Ѿ����棡");
			}catch(IOException e){
				try {
					if(br!=null){
						br.close();
					}
					if(bw!=null){
						bw.close();
					}
					JOptionPane.showMessageDialog(null,"�����ˣ�");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,"�����ˣ�");
				}
			}
		}
	}
	
	private String getSpace(String s){	//����ͳһ��ʽ���������������Ƚ��ٵ��ַ����Զ��ÿո���
										//�����о���������Ч�����á�
		int i = 2*(5-s.length()+1);
		for(int j=1;j<=i;j++){
			s+=" ";
		}
		return s;
	}
	
	private String getDate(){			//��õ�ǰʱ�䣬��Ϸ��¼�а�������Ϸ��ʱ��
		TimeZone tz = TimeZone.getTimeZone("GMT+8");
		SimpleDateFormat chinaFormatter = new SimpleDateFormat("yyyy��MM��dd��   HHʱmm��");
		chinaFormatter.setTimeZone(tz);
		String s=chinaFormatter.format(new Date());
		return s;
	}
	
	
	class TimeController implements ActionListener, Runnable{	//����ʱ�����ŵ��߳���
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
				JOptionPane.showMessageDialog(null,"��������");
				return;
			}
			int temp = timeleft;
			while(!over){
				if(count){	//�߳�ÿִ��һ�ξ�˯��1���ӣ�ÿһ�ν�ʱ�����һ��
					temp--;
					time.setText("ʣ��ʱ�䣺"+temp);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
					if(temp==0){	
						JOptionPane.showMessageDialog(null,"ʱ�䵽!");
						gameStop();
						JOptionPane.showMessageDialog(null,"��ĵ÷��ǣ�"+ totalscore);
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
				JOptionPane.showMessageDialog(null,"��������");
				return;
			}
			this.count = true;
			for(int i=0;i<b.length;i++){
				b[i].setFlag(true);
			}
			startCount = true;
		}
		
	}

	class MouseButton extends Button implements Runnable{	//�����࣬������п�����ɫ�仯���߳���
		boolean flag = false;	//��Ƿ����ܷ��ɫ�ı�Ƿ�
		boolean over = false;
		int index = 0;
		int[] activateInt = {1,4,7};	//�����ɫ������Ϊ�������1��4��7
		int sleepTime;
		
		MouseController mc = new MouseController();
		MouseButton(){
			switch(speed){	//�����趨����Ϸ�ٶ���������ɫ�仯ʱ��
				case 1:sleepTime = 1500;break;
				case 2:sleepTime = 1200;break;
				case 3:sleepTime = 1000;break;
				case 4:sleepTime = 800;break;
				case 5:sleepTime = 500;break;
			}
			setBackground(Color.GRAY);	//Ĭ����ɫΪ��ɫ
		}
		public void run(){
			System.out.println("The run method is start!");
			while(!over){
					if(flag){
						index = (int)(Math.random()*9+1);	//��ɫ�仯ͨ��������ı仯��ʵ��
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
		
		private boolean numbersIn(int[] a,int n){	//�ж������Ƿ���һ��������
			for(int i=0;i<a.length;i++){
				if(a[i]==n){
					return true;
				}
			}
			return false;
		}
		
		public void cutThread(){	//�ж��߳�
			flag = false;
			//over = true;
			this.setBackground(Color.GRAY);
		}
		
		public void setFlag(boolean f){	//���ñ�Ƿ�
			flag = f;
		}
		
		public boolean getFlag(){	//��ñ�Ƿ�
			return flag;
		}

		class MouseController implements ActionListener{	//����ť�ļ�����
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

class StartFrame extends Frame{		//��ʼʱ�Ĵ���
	Button gameStart = new Button("��Ϸ��ʼ");
	Button highestScore = new Button("��߷�");
	Button about = new Button("������Ϸ");
	Button gameOut = new Button("��Ϸ����");
	Label lb2 = new Label("�����   V1.00",Label.CENTER);
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
				JOptionPane.showMessageDialog(null,"лл��Ϸ��");
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
	
	class AboutFrame extends Frame{		//������Ϸ�Ĵ���
		int currentPage = 1;
		Label pages = new Label(currentPage+"/3",Label.CENTER);	//ҳ���±�
		Button leftButton = new Button("ǰһҳ");
		Button rightButton = new Button("��һҳ");
		Button back = new Button("����");
		MyPanel showPassage = new MyPanel();
		Panel p1 = new Panel(new GridLayout(1,3));
		Panel p2 = new Panel(new GridLayout(1,3));
		String[] passage = new String[3];
		
		
		AboutFrame(){
			setLayout(new BorderLayout());
			passage[0] = "�������һ�����Կ��鷴Ӧ����Ϸ";
			passage[1] = "��Ϸ�淨�ܼ�     ����(ש��)��ɫ��Ϊ�̣����������֣�������ɫ��10�֣�������ɫ��5�֣�" +
						"���к�ɫ��һ������ֵ������ֵΪ0��������Ϸ��������Ϸ�ٶ�1��������5�����";
			passage[2] = "V1.00���в��ֹ�����δ�������������ʲô��������ϵ������Powered By LIN";
			showPassage.setText(passage[0]);
			p2.add(new Label("��Ϸ˵����",Label.LEFT),BorderLayout.NORTH);
			p2.add(back);
			p1.add(leftButton);
			p1.add(pages);
			p1.add(rightButton);
			add(p2,BorderLayout.NORTH);
			add(p1,BorderLayout.SOUTH);
			add(showPassage,BorderLayout.CENTER);
			
			leftButton.addActionListener(new ActionListener(){	//����ǰһҳ�ļ�����
				public void actionPerformed(ActionEvent a){
					if(currentPage==1){
						return;
					}
					currentPage--;
					pages.setText(currentPage+"/3");
					showPassage.setText(passage[currentPage-1]);
				}
			});
			
			rightButton.addActionListener(new ActionListener(){	//���ƺ�һҳ�ļ�����
				public void actionPerformed(ActionEvent a){
					if(currentPage==3){
						return;
					}
					currentPage++;
					pages.setText(currentPage+"/3");
					showPassage.setText(passage[currentPage-1]);
				}
			});
			
			back.addActionListener(new ActionListener(){	//������һ����
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
		
		class MyPanel extends Panel{	//�ñ�ǩ����ʾ�ַ�����ÿ����ǩ���ַ�����Ϊ10
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
			
			public void setText(String s){	//��
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
	
	class OptionFrame extends Frame{	//��Ϸ���ô���
		Label[] labelList = new Label[3];
		Label[] textList = new Label[3];
		Label[] labelList2 = new Label[3];
		Panel p1 = new Panel(new GridLayout(1,0));
		Panel p2 = new Panel(new GridLayout(1,0));
		Panel p3 = new Panel(new GridLayout(1,0));
		Panel p4 = new Panel(new GridLayout(1,0));
		Button ok = new Button("ȷ��");
		Button auto = new Button("Ĭ��");
		Button back = new Button("����");
		Button left1 = new Button("<");
		Button left2 = new Button("<");
		Button left3 = new Button("<");
		Button right1 = new Button(">");
		Button right2 = new Button(">");
		Button right3 = new Button(">");
		OptionFrame(){
			setLayout(new GridLayout(0,1));
			labelList[0] = new Label("ʱ�����ã�");
			labelList[1] = new Label("����ֵ���ã�");
			labelList[2] = new Label("��Ϸ�ٶȣ�");
			
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
			
			
			auto.addActionListener(new ActionListener(){	//����ֵ����ΪĬ��ֵ
				public void actionPerformed(ActionEvent a){
					textList[0].setText("60");
					textList[1].setText("5");
					textList[2].setText("3");
				}
			});
			
			ok.addActionListener(new ActionListener(){		//��ʼ��Ϸ
				public void actionPerformed(ActionEvent a){
					int i = Integer.parseInt(textList[0].getText());
					int j = Integer.parseInt(textList[1].getText());
					int k = Integer.parseInt(textList[2].getText());
					setVisible(false);
					new PlayingFrame(i,j,k);
				}
			});
			
			back.addActionListener(new ActionListener(){	//������һ����
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
	
	class HighScoreFrame extends Frame{		//��߷ִ��ڣ��г������Ӹߵ��͵�5λ
		BufferedReader bf;
		Button clear = new Button("��ռ�¼");
		Button back = new Button("����");
		Label title = new Label("�������       �÷�                          ʱ��                                       ",Label.CENTER);
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
				while((temp = bf.readLine())!=null && i<=4){	//�Ӽ�¼�ļ���һ�����ض�����¼
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
				JOptionPane.showMessageDialog(null,"�����ˣ�");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,"�����ˣ�");
			}
			
			setLayout(new GridLayout(0,1));
			
			p1.add(clear);
			p1.add(back);
			add(title);
			for(i=0;i<resultList.length;i++){
				add(resultList[i]);
			}
			add(p1);
			
			back.addActionListener(new ActionListener(){	//������һ����
				public void actionPerformed(ActionEvent a){
					setVisible(false);
					new StartFrame();
				}
			});
			
			clear.addActionListener(new ActionListener(){	//����߷ּ�¼���
				public void actionPerformed(ActionEvent a){
					try {
						int temp = JOptionPane.showConfirmDialog(null,"�Ƿ�Ҫɾ����");
						if(temp==0){
							BufferedWriter bw = new BufferedWriter(new FileWriter("F:\\MouseHit\\highscore.tg"));
							bw.write("");
							bw.close();
							JOptionPane.showMessageDialog(null,"��¼�Ѿ���գ�");
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null,"�����ˣ�");
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

/*һ���д����С��Ϸ���տ�ʼ��������ʱ������һ��26�ţ������ϵ��뷨�ˡ��������ʱ����û���ͦ�򵥵ģ��������������ŷ���
 * �����кܶ���Ҫע���ϸ�ڵط���
 *
 * ����˼·�ܼ򵥣�����9����ť���������ӣ����ÿ�ʼ�����ã���ͣ�Ȱ�ť��Ȼ�����ÿһ����ť��˵��һ���߳�����������ɫ��
 * �仯������������һ���߳�������ʱ������š�
 * 
 * �����Ĺ������ҷ��֣�����һ��ʼ�̵߳����ò�����������������ͱ����Ƶ���ϲ�����һ�𣬽�������໥֮����߼���úܻ�
 * �ң������漰���̵߳������һ��Ҫע����ƺͶ�����롣
 * 
 * ���������Ĺ����У��о��Ƚϼ��ֵĵط���һ���Ƿ����ı��棬һ���Ƕ����̵߳Ŀ��ơ�
 * 
 * ��˵�����ı��棬��������˼·�ǶԵģ�������һ���ı��ĵ�����������������������һ����Ϸ֮��Ҫ����ʱ������IO��Ѽ�¼һ
 * ��һ����ȡ������Ȼ����������ʽȥ��ȡ�����еķ����������뵱ǰ��Ҫ����ķ������бȽϣ�ͨ���ȶ����ҵ���ǰҪ����ķ���
 * ��ǰ�������ŵڼ���ȷ�Ϻ�֮���ٰѵ����õ�˳������д��Ӳ�̣��Ӷ��ﵽ��һ�������Ч��������IO�����������ʵ���󣬵���
 * �ڷ�����ʾ��ʱ��ȴ���Һܷ��ѡ��ҵ���������Label��ǩ������ʾ���������Ƿ��ֱ�ǩ�಻���Զ����У����Ǹ���panel�Ӷ����ǩ
 * ���ϵķ�������ʾ����������һ�㣬����Ҫ��ʾ������������������ͱ���ʱ�䣬���������������Ȳ�һ����ÿ�γ������ַ�����
 * ���ȾͲ�һ���������label����ʾ�Ľ��Ҳ��һ�������Բ��ò�ͨ�������ո��������ַ������ȣ������ֲ�֪��Ϊʲô�������ո�֮
 * ����ʾ�����Ľ�����������⣬���Ǿͷ��²����ˣ�ϣ������һ���汾�л�����
 * 
 * ��˵˵�̵߳Ŀ������⣬��������ϵĲ��㵼�����̺߳Ͷ��������һ����Ȼ�������ϲ�����̫������⣬�����ڸ���ά���ϣ���
 * ���Ǹ�����һ���̵߳�ʱ��ͻ���鷳���Ͳ��ò��Ѵ󲿷���ش��붼Ҫ��дһ�顣Ҳ���ǽ��̺߳Ͷ���󶨵���һ�𣬵�����һ��ʼ
 * ���Ե�ʱ�������ô���ܲ������������ŷ�������Ϊ�½��̲߳�û���Զ�������������˵���˾��Ǵ�����һ�����������ڿ�������Ҫ
 * ���ƵĶ�����̣߳�Ȼ������˺ܾòŷ�����һ�㡣
 * 
 * ����һЩ�����ʵ������Ͳ���˵�ˡ�													
 * 																										2012.3.30
 * */

package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import entity.Group;
import entity.Msg;
import entity.User;
import service.ServiceFactory;

/**
 * 
 *���ܣ��������촰�ڣ�ͬʱ�ֿ��Ը���ΪȺ�Ĵ��ڣ������ڸý�����Һ��ѻ�Ⱥ����������µ����촰�ڣ���Ϣ���ղ���ΪJScrollPane�������û��Ժ��ѵ�һϵ�в���
 *���ԣ�type���������ô������ڵ��Ļ���Ⱥ��	receiver�����ô��ڵ���Ϣ���շ�	self�����ô��ڵ���Ϣ���ͷ�	client������ǰ��¼�û���socket
 *map�������濪����ǰ���ڵĽ��շ��͸������Ϣ������Ϣ���չ��ܵ��߳�	list�������浱ǰ�����������Ĵ���ʵ��������	isfirst�������û���¼�������Ƿ��һ�δ򿪺͸ý���������Ĵ��ڣ������ж��Ƿ���Ҫ��ȡΪ��ȡ��Ϣ����������ظ�
 *count�������浱ǰ���ڲ鿴�����¼��ʱ�򣬴����ݿ������ȡ���ļ�¼�ۼ��������ظ�ÿ�δ����ݿ������ȡ�������ظ�������	
 */
public class MsgView extends JFrame{
	private final static int color = 0xcccccc;
	private  String type;
	private  User receiver=null;
	private User self=null;
	private final Socket client;
	private static final long serialVersionUID = 1L;
	private final static HashMap<User,Thread> map = new HashMap<>();
	private final static HashMap<User,MsgView> list = new HashMap<>();
	private final static HashMap<String,Boolean> isfirst = new HashMap<>();
	private final static HashMap<String,Integer> count = new HashMap<>();
	private final static HashMap<User,Thread> groupMap = new HashMap<>();
	public MsgView(String name,String type,Socket client){
		super(name);
		this.type=type;
		this.client=client;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public static MsgView createMsgView(String name,String type,String  chataccount,final Socket client,String useraccount){
		System.out.println("MsgView of "+type+":"+Thread.currentThread());
		final MsgView mv = new MsgView(name,type,client);
//		�ж������ڸ���������滹��Ⱥ�Ľ���
		if(type.equals("friend")){
			mv.receiver = (User) ServiceFactory.getService("user").findByCondition(chataccount);
		}else{
			Group group =(Group) ServiceFactory.getService("group").findByCondition(chataccount);
			User user = new User();
			user.setId(group.getId());
			user.setAccount(group.getAccount());
			user.setIcon(group.getIcon());
			user.setLabel(group.getLabel());
			user.setLevel(group.getLevel());
			user.setName(group.getName());
			mv.receiver=user;
		}
		mv.self=(User) ServiceFactory.getService("user").findByCondition(useraccount);
		ImageIcon ii = new ImageIcon(System.getProperty("user.dir")+"\\src\\imgs\\msg5.jpg");
		JLabel jl = new JLabel(ii);
		jl.setBounds(0, 0, ii.getIconWidth(), ii.getIconHeight());
		mv.getLayeredPane().setLayout(null);
		mv.getLayeredPane().add(jl, new Integer(Integer.MIN_VALUE));
		
		JPanel jpcontent = (JPanel) mv.getContentPane();
//		������������
		final JPanel jpmsgcontent = new JPanel();
		final JScrollPane jps = new JScrollPane(jpmsgcontent);
		jps.setVisible(true);
		jps.setBounds(0,70, 550, 400);
		jps.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jps.setOpaque(false);     
		jps.setBorder(new MyBorder(0xeeeeee));
		JViewport test =(JViewport) jps.getComponent(0);
		test.setOpaque(false);
		
		final JPanel jpsendmsg = new JPanel();
		jpcontent.setOpaque(false);
		jpcontent.setLayout(null);
		jpcontent.add(createTopInfo(mv));
		
		createMsgContent(jpmsgcontent,mv,jps);
		jpcontent.add(jps);
		jpcontent.add(createMsgRight(mv));
		jpcontent.add(createSendMsg(jpsendmsg,mv,jpmsgcontent,jps));
		jpcontent.add(createEnd());
		mv.setBounds(600, 200, 800, 700);
		
		mv.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mv.addWindowListener(new WindowAdapter() {

			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 * ���ܣ��˴����������Ĺر��¼����ر��¼���ʱ��û�н������������Ľ�����Ϣ�̣߳���Ӱ�쵽�����߳̽�����Ϣ������ͬһ�����������촰�ڶ�δ���֮�󣬹ر�֮�����һ���رյ�
			 * ���������Ϣ���߳��ǿ����ߵģ�Ӱ����Ϣ���´ν��գ����ҿ��ܻ�����Ϣ��״̬����ĸı��ˡ�
			 * ����ʵ�֣������߳��������Ϣ�������ж���ʵ���̵߳Ŀɿ��Թرգ��˴�����������͹ر�ָ���������ת������������ʱ���ܻ����ʧ�ܣ������������߳̽��գ�������Ҫ��ѭ�������͹ر�ָ��
			 * ��ѭ�����ж��������Ǹý����̵߳�״̬�����߳�����ʱ��ֹͣ���͹ر�ָ���ʱ���������ܽ��յ��˶����ر�ָ��������߳̽��չر�ָ��ʱ��Ҫ�Ǳ�ָ������������߳�������ʵ�ֹر��̵߳�
			 * ׼ȷ�ԣ�����෢����Ϣ���ᱻ�����̺߳��Ե���
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				while(true){
					Iterator<Entry<User,Thread>> it = map.entrySet().iterator();
					System.out.println(map.size());
					boolean isEnd = true;
					while(it.hasNext()){
						Entry<User,Thread> entry = it.next();
//						�ҳ���ǰָ�������ߴ��ڵĽ�����Ϣ�̣߳��жϴ��״̬�����͹ر�ָ��������߳�����ǣ�������Ϣ��ʽΪת����Ϣ�����ɷ�����ת����������
						if(entry.getKey().getAccount().equals(mv.receiver.getAccount())){
							System.out.println(entry.getValue().getName()+":"+entry.getValue().isAlive());
							if(entry.getValue().isAlive()){
//								��ǰû���̴߳��ʱ��isEndΪ�棬��ʱֹͣ���͹ر�ָ�
								System.out.println(isEnd);
								isEnd=false;
								try {
									BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
									bw.write("5:"+mv.receiver.getAccount()+":�ر�ָ��:"+entry.getValue().getName()+"\n");
									System.out.println("5:"+mv.receiver.getAccount()+":�ر�ָ��:"+entry.getValue().getName());
									bw.flush();
									Thread.currentThread().yield();
								} catch (IOException e1) {
									System.out.println("���͹ر�ָ��ʧ��");
									e1.printStackTrace();
								}
								continue;
							}
						}
					}
//					�ý����߳ɹ��رս�����Ϣ�̣߳����ڶ��δ���ý�������ͬ�����촰��ʱ��isfirst����ʾΪfalse�����ڿ���ʱ�����ȡ���ݿ���������Ϣ��
					if(isEnd){
						isfirst.remove(mv.receiver.getAccount());
						isfirst.put(mv.receiver.getAccount(), false);
						break;
					}
				}
			}

		});
		mv.setVisible(true);
		mv.setResizable(false);
		mv.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				mv.validate();
				mv.repaint();
			}
		});
		/*
		 * ���ܣ����½��濪��ʱ����ر���ͬ�����ߵ�֮��Ľ��棬���ѭ�����������ظ����͹ر�ָ����������ر���һ������ͬ�����ߵ����촰�ڵĽ�����Ϣ�̡߳�����ر�ָ��ᱻ����������Ϣ
		 * �̺߳��ԡ�ʵ�ֿ����µ����촰��ʱ�ض��ر���һ��������ͬ�����ߵ����촰�ڣ�����Ҳ������Ⱥ�ġ�
		 */
		while(true){
			Iterator<Entry<User,Thread>> it = map.entrySet().iterator();
			System.out.println(map.size());
			boolean isEnd = true;
			while(it.hasNext()){
				Entry<User,Thread> entry = it.next();
				if(entry.getKey().getAccount().equals(mv.receiver.getAccount())){
					System.out.println(entry.getValue().getName()+":"+entry.getValue().isAlive());
					if(entry.getValue().isAlive()){
						System.out.println(isEnd);
						isEnd=false;
						try {
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
							bw.write("5:"+mv.receiver.getAccount()+":�ر�ָ��:"+entry.getValue().getName()+"\n");
							System.out.println("5:"+mv.receiver.getAccount()+":�ر�ָ��:"+entry.getValue().getName());
							bw.flush();
							Thread.currentThread().yield();
						} catch (IOException e1) {
							System.out.println("���͹ر�ָ��ʧ��");
							e1.printStackTrace();
						}
						continue;
					}
				}
			}
			if(isEnd){
				break;
			}
		}
		/*
		 * ���ܣ����߳���������촰�ڵ�����Ҫ�������ơ������շ����������ͻ����ĸ�����֤��Ϣ��������Ϣ�����ָ��ӵ���Ϣ���պ����
		 * ����ĵĹ��ܣ��жϸ����촰�ڽ�����Ϣ���߳̽��յ�����Ϣ�Ƿ������ڸô��ڽ��ܵ���Ϣ��������֤receiver��account��
		 * ��ƥ�䣬���������������ת����Ϣ�����������յ���ϢͷΪ5��ת����Ϣ���ٴ�����û��Ķ��������Ϣ�̷߳������ݣ���������
		 * Ϊ��ȷ���С�����ת���������ȷ���߳̽�����Ϣ�󣬽�����Ϣ������ʾ��Ϣ���˹�����ȫ������Ⱥ�ġ�
		 */
		 Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(mv.client.getInputStream()));
					JLabel error = (JLabel)jpsendmsg.getComponent(1);
//					rightReceiver���ڼ�¼�ô��������Ľ����ߣ�������Ϣ���մ���֮���ת����������isFirst��ȷ������ȷ�Ľ�����
					
					String rightReceiver = null;
					boolean isFirst = true;
					while(true){
						String msgreceive = null;
						msgreceive = br.readLine();
						System.out.println(Thread.currentThread().getName());
						System.out.println(mv.self.getAccount()+"�յ���Ϣ��"+msgreceive);
						String[] msgarr = msgreceive.split(":");
//						�����յ��ر�ָ��鿴�Ƿ�ƥ�䵽���̣߳�δƥ������Ը�ָ��
						if(msgarr[0].equals("4")&&msgarr[2].equals("�ر�ָ��")&&!msgarr[3].equals(Thread.currentThread().getName())){
							continue;
						}
//						��ϢΪ4���������һ�����˴�Ϊɾѡ��������Ϣ���ų������Ϣ��
						if(msgarr[0].equals("4")){
//							δƥ��ý��մ�����ת����Ϣ��
							if(!msgarr[1].equals(mv.receiver.getAccount())){
								if(isFirst){
									rightReceiver=msgarr[1];
									isFirst=false;
								}
								System.out.println("���ճ��������ش��źţ�");
								BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(mv.client.getOutputStream()));
								if(msgarr[2].equals("���ѵ�ǰ������,תΪ���߷���")){
									bw.write("5:"+msgarr[1]+":"+msgarr[2]+"\n");
								}else{
									bw.write("5:"+rightReceiver+":"+msgarr[2]+":"+msgarr[3]+"\n");
								}
								bw.flush();
								continue;
							}
//							�ر�ָ��ɹ��������õص㣬��ʱ�����ý����̡߳�ͬʱ�رոô���
							if(msgarr[2].equals("�ر�ָ��")&&msgarr[3].equals(Thread.currentThread().getName())){
								System.out.println("�رմ���:"+msgarr[1]+"    "+"�ر��߳� :"+Thread.currentThread().getName());
								mv.setVisible(false);;
								break;
							}
//							�˴������´��ڽ���ʱ���ɴ��ڹر�ʱϵͳ�����˹���Ĺر�ָ�����ͨ����ǰ�����֤����ʱ��Ӧ��������Ϣ���������Թ��˵�
							if(msgarr[2].equals("�ر�ָ��")&&!msgarr[3].equals(Thread.currentThread().getName())){
								continue;
							}
//							���Ѳ������Ƿ�����Ϣ������ʾ������һ����ʾ��������Ϊ���������صĽ��������Ҫͨ������ش���������ȷ���иô��ڣ������װ���
							if(msgarr[2].equals("���ѵ�ǰ������,תΪ���߷���")){
								error.setVisible(true);
								error.setText(msgarr[2]);
								error.setForeground(Color.red);
								continue;
							}
							Msg testmsg =(Msg) ServiceFactory.getService("msg").findById(Integer.valueOf(msgarr[3]));
							System.out.println(testmsg.getStatus());
							if(testmsg.getStatus()==2&&mv.type.equals("friend")){
								Thread.currentThread().yield();
								continue;
							}
							JPanel newMsg = new JPanel();
							int count = jpmsgcontent.getComponentCount();
							jpmsgcontent.add(createSingleMsgBlock(newMsg,65*(count-1),0xeeeeee,1,mv.receiver.getIcon(),mv.receiver.getName(),msgarr[2],new SimpleDateFormat("EEE  HH:mm:ss").format(testmsg.getDate())));
							jpmsgcontent.setPreferredSize(new Dimension(530,65*count+35));
							jpmsgcontent.setBounds(0, 70, 530, 65*count+35);
							jps.getViewport().setViewPosition(new Point(0,65*(jpmsgcontent.getComponentCount())));
							mv.validate();
							mv.repaint();
//							���������յ�����Ϣ�������ݿⶼ��״̬Ϊ1��δ��״̬����ʱ�Ѿ���ʾ�ڽ����ϣ����Ը�Ϊ�Ѷ�
							int flag = ServiceFactory.getService("msg").updateByConditions("2",""+testmsg.getId());
							if(flag>0){
								System.out.println("�Ѿ�������Ϣ(id="+testmsg.getId()+")״̬�޸�Ϊ�Ѷ���");
							}
							
						}else{
//							if(!msgreceive.equals("���ͳɹ�")&&!msgreceive.equals("�ش��ɹ�")){
//								error.setVisible(true);
//								error.setText(msgreceive);
//								error.setForeground(Color.red);
//								continue;
//							}else{
//								error.setVisible(false);
//							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}							
			}
		});
		 thread.start();
		 map.put(mv.receiver,thread);
		 list.put(mv.receiver,mv);
		 count.put(mv.receiver.getAccount(),0);
//		 Ҫ�����ڵ�һ�ν���ʱ�ı�ʶ����Ϊ�棬���ڵ�һ�ν����ý����ߴ���ʱ��������Ϣ��ȡ����Ϣ�������
		 if(isfirst.get(mv.receiver.getAccount())==null){
			 isfirst.put(mv.receiver.getAccount(), true);
		 }
		 Thread.currentThread().yield();
//		 ����Ϊ��һ�δ򿪸ý����ߵ����촰�ڣ����ȡ������Ϣ�����ǻ��е�С���⡣����Ҳ����ظ���
		 if(isfirst.get(mv.receiver.getAccount())){
			 List<Msg> listmsg = ServiceFactory.getService("msg").listPart(""+mv.self.getId(),"3","1",""+mv.receiver.getId());
			 HashMap<Long,Msg> desc = new HashMap<>();
			 for(int i=0;i<listmsg.size();i++){
				 desc.put(Long.valueOf(listmsg.get(i).getDate().getTime()), listmsg.get(i));
			 }
			 Iterator<Entry<Long,Msg>> it = desc.entrySet().iterator();
			 while(it.hasNext()){
				 Entry<Long,Msg> msg = it.next();
				 System.out.println(msg.getValue().getContent()+":"+msg.getValue().getId()+":"+msg.getValue().getStatus());
			 	JPanel newMsg = new JPanel();
				int count = jpmsgcontent.getComponentCount();
				jpmsgcontent.add(createSingleMsgBlock(newMsg,65*(count-1),0xeeeeee,1,mv.receiver.getIcon(),mv.receiver.getName(),msg.getValue().getContent(),(new SimpleDateFormat("EEE  HH:mm:ss").format(msg.getValue().getDate()))));
				jpmsgcontent.setPreferredSize(new Dimension(530,65*count+35));
				jpmsgcontent.setBounds(0, 70, 530, 65*count+35);
				jps.getViewport().setViewPosition(new Point(0,65*(jpmsgcontent.getComponentCount())));
				int flag = ServiceFactory.getService("msg").updateByConditions("2",""+msg.getValue().getId());
				if(flag>0){
					System.out.println("�Ѿ�������Ϣ(id="+msg.getValue().getId()+")״̬�޸�Ϊ�Ѷ���");
				}
				mv.validate();
				mv.repaint();
			}
		 }
		return mv;
	}
	public static void main(String[] args) throws UnknownHostException, IOException {
		MsgView.createMsgView("������","friend","����(111111)",new Socket("127.0.0.1",1995),"111112");
	}
	public static JPanel createTopInfo(MsgView mv){
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBounds(0, 0, 200, 70);
		
		jp.add(createIconLabel(10,15,50,50,color,mv));
		jp.add(createNameLabel(70, 15, 200,25, mv.receiver.getName()+"("+mv.receiver.getAccount()+")", color,mv.receiver.getLabel()));
		return jp;
	}
	public static  JLabel createIconLabel(int left,int top,int width,int height,int bgcolor,MsgView mv){
//		ImageIcon ii = new ImageIcon("C:\\Users\\fsc\\Desktop\\icon.png");
		ImageIcon ii = new ImageIcon(System.getProperty("user.dir")+"\\src\\imgs\\xc.jpg");
		ii.setImage(ii.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
		JLabel jl = new JLabel(ii);
		jl.setBounds(left,top , width,height);
		return jl;
	}
	public  static JPanel createNameLabel(int left,int top,int width,int height,String name,int bgcolor,String label){
		JPanel jp = new JPanel();
		jp.setVisible(true);
		jp.setBounds(left, top, 300, height*2);
		jp.setLayout(null);
		jp.setOpaque(false);
		
		JLabel jl = new JLabel();
		jl.setText(name);
		jl.setFont(new Font("����",Font.BOLD,15));
		jl.setBounds(0, 0, width, height);
		
		JLabel jllabel = new JLabel();
		jllabel.setText(label);
		jllabel.setFont(new Font("����",Font.ITALIC,15));
		jllabel.setBounds(0, height, 300, height);
		
		jp.add(jl);
		jp.add(jllabel);
		return jp;
	}
	public static JPanel createMsgContent(final JPanel jp,final MsgView mv,final JScrollPane jsp){
		jp.setLayout(null);
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setBounds(0, 70, 530, 380);
		jp.setPreferredSize(new Dimension(530, 380));
		
		JButton lastMsg = new JButton("�鿴������Ϣ");
		lastMsg.setLayout(null);
		lastMsg.setVisible(true);
		lastMsg.setBackground(Color.white);
		lastMsg.setBounds(210, 5, 150, 30);
		lastMsg.setBorder(null);
		lastMsg.setFont(new Font("����",Font.ITALIC,13));
		lastMsg.setFocusable(false);
		lastMsg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int oldcount = count.get(mv.receiver.getAccount()).intValue();
				System.out.println("oldcount:"+oldcount);
				@SuppressWarnings("unchecked")
				List<Msg> list = ServiceFactory.getService("msg").listPart(""+mv.self.getId(),""+mv.receiver.getId(),"3",""+oldcount,""+(oldcount+5));
				count.remove(mv.receiver.getAccount());
				count.put(mv.receiver.getAccount(), oldcount+5);
				Iterator<Msg> it = list.iterator();
				System.out.println(list.size());
				
				while(it.hasNext()){
					Msg msg = it.next();
					JPanel newMsg = new JPanel();
					int count = jp.getComponentCount();
					int type = 0;
					if(msg.getUserId()==mv.self.getId()){
						type = 2;
					}else{
						type=1;
					}
					jp.add(createSingleMsgBlock(newMsg,65*(count-1),0xeeeeee,type,mv.receiver.getIcon(),mv.receiver.getName(),msg.getContent(),(new SimpleDateFormat("EEE  HH:mm:ss").format(msg.getDate()))),0);
					jp.setPreferredSize(new Dimension(530,65*count+35));
					jp.setBounds(0, 70, 530, 65*count+35);
					jsp.getViewport().setViewPosition(new Point(0,65*(jp.getComponentCount())));
				}
			}
		});
		
		jp.add(lastMsg);
		
		return jp;
	}
	private static JPanel createSingleMsgBlock(JPanel jp,int top,int color,int type,String icon,String name,String content,String time) {
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBounds(0, top+35, 530, 65);
		jp.setLayout(null);
		
		ImageIcon ii = new ImageIcon(System.getProperty("user.dir")+icon);
		ii.setImage(ii.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
		JLabel jicon = new JLabel(ii);
		jicon.setBounds(10, 10, 40, 40);
		jicon.setVisible(true);
		
		JLabel jlname = new JLabel();
		jlname.setVisible(true);
		jlname.setText(name+"��"+time);
		jlname.setBounds(55, 5, 420, 30);
		jlname.setFont(new Font("����",Font.ITALIC,14));
		
		JLabel jlmsg = new JLabel();
		jlmsg.setVisible(true);
		jlmsg.setText(content);
		jlmsg.setBounds(60, 30, 410, 30);
		jlmsg.setFont(new Font("����",Font.ITALIC,13));
		if(type==2){
			jlname.setHorizontalAlignment(SwingConstants.RIGHT);
			jlmsg.setHorizontalAlignment(SwingConstants.RIGHT);
			jicon.setBounds(480, 10, 40, 40);
			
		}
		jp.add(jicon);
		jp.add(jlname);
		jp.add(jlmsg);
		return jp;
	}
	public static JPanel createMsgRight(final MsgView mv){
		JPanel jp = new JPanel();
		JPanel jpset = new JPanel();
		JPanel jpgroup = new JPanel();
		final JPanel jpresult = new JPanel();
		jp.setLayout(null);
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setBounds(550, 70, 250, 350);
		
		final JTextField jtf = new JTextField();
		jtf.setText("���Һ���(�ǳƻ��˺�)");
		jtf.setFont(new Font("����",Font.ITALIC,13));
		jtf.setBounds(10,10,150,30);
		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(jtf.getText().equals("���Һ���(�ǳƻ��˺�)")){
					jtf.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(jtf.getText().equals("")){
					jtf.setText("���Һ���(�ǳƻ��˺�)");
				}
			}
		});
		
		JButton jb = new JButton("����");
		jb.setBounds(160,10,60,30);
		jb.setBackground(new Color(0x3CC3F5));
		jb.setForeground(new Color(0xffffff));
		jb.setBorderPainted(false);
		jb.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if(jtf.getText().equals("���Һ���(�ǳƻ��˺�)")){
					return;
				}
				List<User> users =ServiceFactory.getService("user").listPart(""+mv.self.getId(),mv.self.getAccount(),jtf.getText(),jtf.getText());
				if(users.size()>0){
					jpresult.setVisible(true);
					JPanel jp = (JPanel)jpresult.getComponent(1);
					JLabel jlname = (JLabel)jp.getComponent(0);
					JLabel jllabel = (JLabel)jp.getComponent(1);
					jlname.setText(users.get(0).getName());
					jllabel.setText(users.get(0).getLabel());
					
					JLabel icon = (JLabel)jpresult.getComponent(0);
					ImageIcon ii = new ImageIcon(System.getProperty("user.dir")+users.get(0).getIcon());
					ii.setImage(ii.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
					icon.setIcon(ii);
					
					JLabel jlaccount = (JLabel)jpresult.getComponent(3);
					jlaccount.setText(users.get(0).getAccount());
					
				}else{
					jpresult.setVisible(false);
				}
			}
		});
		
		createGroupList(jpgroup);
		createSetBlock(jpset);
		jp.add(jb);
		jp.add(jtf);
		jp.add(jpset);
		jp.add(jpgroup);
		jp.add(createSearchResult(mv,jpresult));
		if(mv.type.equals("friend")){
			jpset.setVisible(true);
			jpgroup.setVisible(false);
		}else{
			jpset.setVisible(false);
			jpgroup.setVisible(true);
		}
		return jp;
	}
	public static JPanel createSearchResult(final MsgView mv,JPanel jp){
		jp.setLayout(null);
		jp.setBounds(0, 50, 250, 100);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setOpaque(false);
		jp.setVisible(false);
		
		JLabel jlicon = createIconLabel(40, 10, 40, 40, 0xffffff,mv);
		JPanel jpname = createNameLabel(90, 10, 180,20, "fsc", 0xffffff, "hello");
		
		final JLabel jl = new JLabel();
		jl.setText("test");
		
		JButton jb = new JButton("����Ự");
		jb.setVisible(true);
		jb.setBackground(Color.white);
		jb.setBorder(new MyBorder(0xeeeeee));
		jb.setForeground(Color.black);
		jb.setBounds(40, 60, 100, 30);
		jb.setFont(new Font("����",Font.ITALIC,14));
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						MsgView.createMsgView("QQ","friend",jl.getText(),mv.client,mv.self.getAccount());
					}
				}).start();;
			}
		});
		
		jp.add(jlicon);
		jp.add(jpname);
		jp.add(jb);
		jp.add(jl);
		return jp;
	}
	public static JPanel createGroupList(JPanel jp){
		jp.setLayout(null);
		jp.setBounds(0, 150, 250, 200);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setOpaque(false);
		jp.setVisible(false);
		
		JTree tree = null;
		DefaultMutableTreeNode group = new DefaultMutableTreeNode("û�д���(4/5)");
		DefaultMutableTreeNode group1 = new DefaultMutableTreeNode("tkg");
		DefaultMutableTreeNode group2 = new DefaultMutableTreeNode("hbl");
		DefaultMutableTreeNode group3 = new DefaultMutableTreeNode("fty");
		DefaultMutableTreeNode group4 = new DefaultMutableTreeNode("lchika");
		group.add(group1);
		group.add(group2);
		group.add(group3);
		group.add(group4);
		tree=new JTree(group);
		tree.setBounds(10, 10, 180, 180);
		tree.setOpaque(false);
		tree.setFont(new Font("����",Font.BOLD,15));
		
		jp.add(tree);
		return jp;
	}
	public static JPanel createSetBlock(JPanel jp){
		jp.setLayout(null);
		jp.setBounds(0, 150, 250, 200);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setOpaque(false);
		jp.setVisible(true);
		
		JButton jb = new JButton("ɾ��TA");
		jb.setVisible(true);
		jb.setBackground(Color.white);
		jb.setBorder(new MyBorder(0xeeeeee));
		jb.setForeground(Color.black);
		jb.setBounds(120, 20, 100, 30);
		jb.setFont(new Font("����",Font.ITALIC,14));
		
		JButton jb1 = new JButton("����TA");
		jb1.setVisible(true);
		jb1.setBackground(Color.white);
		jb1.setBorder(new MyBorder(0xeeeeee));
		jb1.setForeground(Color.black);
		jb1.setBounds(120, 60, 100, 30);
		jb1.setFont(new Font("����",Font.ITALIC,14));
		
		JButton jb2 = new JButton("��עTA");
		jb2.setVisible(true);
		jb2.setBackground(Color.white);
		jb2.setBorder(new MyBorder(0xeeeeee));
		jb2.setForeground(Color.black);
		jb2.setBounds(120, 100, 100, 30);
		jb2.setFont(new Font("����",Font.ITALIC,14));
		
		JButton jb3 = new JButton("�ٱ�TA");
		jb3.setVisible(true);
		jb3.setBackground(Color.white);
		jb3.setBorder(new MyBorder(0xeeeeee));
		jb3.setForeground(Color.black);
		jb3.setBounds(120, 140, 100, 30);
		jb3.setFont(new Font("����",Font.ITALIC,14));
		
		jp.add(jb);
		jp.add(jb1);
		jp.add(jb2);
		jp.add(jb3);
		return jp;
	}
	public static JPanel createEnd(){
		JPanel jp  = new JPanel();
		jp.setLayout(null);
		jp.setBounds(550, 420, 250, 280);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setOpaque(false);
		jp.setVisible(true);
		
		JLabel jl = new JLabel("��ʷ��Ϣ");
		jl.setBackground(new Color(0xcccccc));
		jl.setFont(new Font("����",Font.BOLD,12));
		jl.setForeground(Color.white);
		jl.setOpaque(true);
		jl.setBounds(0, 10, 250, 30);
		jp.add(jl);
		
		JTextArea jta= new JTextArea("hello 2018-12-19 \n hello everyone !");
		jta.setBounds(10,40,250,50);
		jta.setEditable(false);
		jp.add(jta);
		
		JTextArea jta1= new JTextArea("hello 2018-12-19 \n hello everyone !");
		jta1.setBounds(10,90,250,50);
		jta1.setEditable(false);
		jp.add(jta1);
		return jp;
	}
	private static JPanel createSendMsg(JPanel jp,final MsgView mv,final JPanel jpmsgblock,final JScrollPane jsp){
		jp.setLayout(null);
		jp.setBounds(0, 470, 551, 230);
		jp.setBorder(new MyBorder(0xcccccc));
		jp.setOpaque(false);
		jp.setVisible(true);
		
		final JTextArea jtf = new JTextArea("�˴�������Ϣ");
		jtf.setBounds(10, 10, 530, 130);
		jtf.setFont(new Font("����",Font.ITALIC,15));
		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(jtf.getText().equals("�˴�������Ϣ")){
					jtf.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(jtf.getText().equals("")){
					jtf.setText("�˴�������Ϣ");
				}
			}
		});
		
		JButton jbcancle= new JButton("����");
		JButton jbsend = new JButton("����");
		final JLabel jlerror = new JLabel();
		jlerror.setVisible(false);
		jlerror.setBounds(150, 140, 200, 30);
		jlerror.setOpaque(false);
		jlerror.setFont(new Font("����",Font.ITALIC,13));
		jbcancle.setBackground(new Color(0x3CC3F5));
		jbcancle.setForeground(Color.WHITE);
		jbcancle.setBounds(350, 140, 80, 30);
		jbcancle.setBorderPainted(false);
		jbcancle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jtf.setText("�˴�������Ϣ");
			}
		});
		
		jbsend.setBackground(new Color(0x3CC3F5));
		jbsend.setForeground(Color.WHITE);
		jbsend.setBounds(440, 140, 80, 30);
		jbsend.setBorderPainted(false);
		jbsend.addActionListener(new ActionListener() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 * ���ܣ�������Ϣ������������Ϣ��ʽ3Ϊ��ʶ����������Ϊ�ָ�����ͬʱ���������ϴ�ӡ��Ϣ
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				String content = jtf.getText().trim();
				if(content.equals("�˴�������Ϣ")){
					return;
				}
				Msg msg = new Msg();
				msg.setContent(content);
				msg.setReceiver(mv.receiver.getId());
				msg.setUserId(mv.self.getId());
				msg.setStatus(1);
				msg.setType(3);
				String sendmsg = "3:"+mv.receiver.getAccount()+":"+content+"\n";
				try {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(mv.client.getOutputStream()));
					bw.write(sendmsg);
					bw.flush();
//					bw.close();
					System.out.println(mv.self.getAccount()+"������Ϣ��"+sendmsg);
					JPanel ownMsg = new JPanel();
					jpmsgblock.add(createSingleMsgBlock(ownMsg, 65*(jpmsgblock.getComponentCount()-1), 0xffffff, 2,mv.self.getIcon(),mv.self.getName(),content,new SimpleDateFormat("EEE  HH:mm:ss").format(new Date())));
					jpmsgblock.setPreferredSize(new Dimension(530,65*jpmsgblock.getComponentCount()+35));
					jpmsgblock.setBounds(0, 70, 530, 65*jpmsgblock.getComponentCount()+35);
					jsp.getViewport().setViewPosition(new Point(0,65*(jpmsgblock.getComponentCount())));
					mv.repaint();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jp.add(jtf);
		jp.add(jlerror);
		jp.add(jbcancle);
		jp.add(jbsend);
		return jp;
	}
}

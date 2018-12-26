package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.security.Provider.Service;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

import entity.FriendGroup;
import entity.FriendGroupUser;
import entity.Msg;
import entity.User;
import service.ServiceFactory;
import util.MsgUtils;
/**
 *
 *���ԣ�user�����õ�¼�û���ʵ��
 *���ܣ���Ҫ���ܣ��������ݿ��е������û���������Ӻ������󣬶Է����ܻ�ܾ��󶼻ᷢ����Ϣ���ͣ��ж��Ƿ���ӳɹ���
 *�����������Լ�����������û���ģ����ѯ��������account��name��ѯ
 *�Ѿ���������Ϣ�����ٴβ�����
 */
@SuppressWarnings("unchecked")
public class SetView extends JFrame{

	private static final long serialVersionUID = 1L;
	private static  User user;
	public SetView(String name){
		super(name);
	}
	public static SetView createSetView(String name,String type,String account){
		System.out.println(Thread.currentThread().getName());
		User user = (User) ServiceFactory.getService("user").findByCondition(account);
		SetView.user=user;
		SetView sv = new SetView(name);
		JPanel jpset = new JPanel();
		JPanel jpsearch = new JPanel();
		
		JPanel jpcontent = (JPanel) sv.getContentPane();
		jpcontent.setOpaque(false);
		jpcontent.setLayout(null);
		jpcontent.add(createTop(jpset,jpsearch));
		jpcontent.add(createBottomSet(jpset));
		jpcontent.add(createBottomAdd(jpsearch));
		if(type.equals("set")){
			jpset.setVisible(true);
			jpsearch.setVisible(false);
		}else{
			jpset.setVisible(false);
			jpsearch.setVisible(true);
		}
		
		sv.setBounds(600, 200, 800, 550);
		sv.setVisible(true);
		sv.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		sv.getLayeredPane().setLayout(null);
		ImageIcon ii = new ImageIcon(System.getProperty("user.dir")+"\\src\\imgs\\msg4.jpg");
		JLabel jlicon = new JLabel(ii);
		jlicon.setBounds(0, 0, ii.getIconWidth(), ii.getIconHeight());
		sv.getLayeredPane().add(jlicon,new Integer(Integer.MIN_VALUE));
		sv.setResizable(false);
		return sv ;
	}
	public static JPanel createTop(final JPanel jpset,final JPanel jpsearch){
		JPanel jp  = new JPanel();
		
		final JButton jl1 = new JButton();
		jl1.setBounds(250, 0, 150, 50);
		jl1.setText("ϵͳ����");
		jl1.setFont(new Font("����",Font.ITALIC,15));
		jl1.setForeground(new Color(0x225599));
		jl1.setBackground(Color.white);
		jl1.setBorderPainted(false);
		jl1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jpsearch.setVisible(false);
				jpset.setVisible(true);
			}
		});
		jl1.addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()>10&&e.getX()<140&&e.getY()>10&&e.getY()<40){
					jl1.setBackground(new Color(0xeeeeee));
				}else{
					jl1.setBackground(new Color(0xffffff));
				}
			}
		});
		
		
		final JButton jl2 = new JButton();
		jl2.setBounds(400, 0, 150, 50);
		jl2.setText("���ѹ���");
		jl2.setFont(new Font("����",Font.ITALIC,15));
		jl2.setForeground(new Color(0x225599));
		jl2.setBackground(Color.white);
		jl2.setBorderPainted(false);
		jl2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jpset.setVisible(false);
				jpsearch.setVisible(true);
			}
		});
		jl2.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()>10&&e.getX()<140&&e.getY()>10&&e.getY()<40){
					jl2.setBackground(new Color(0xeeeeee));
				}else{
					jl2.setBackground(new Color(0xffffff));
				}
			}
		});
		jp.add(jl1);
		jp.add(jl2);
		jp.setLayout(null);
		jp.setBounds(0, 0, 800,50);
		jp.setVisible(true);
		jp.setOpaque(false);
		return jp;
	}
	public static JPanel createBottomAdd(JPanel jp){
		JPanel jprightuser = new JPanel();
		JPanel jprightsystem = new JPanel();
		JPanel jpacceptmsg = new JPanel();
		JPanel jpleft = new JPanel();
		JPanel jp1= new JPanel();
		JPanel jp2= new JPanel();
		JPanel jp3= new JPanel();
		JPanel jp4= new JPanel();
		JPanel[] jparr1 = new JPanel[4];
		jparr1[0]=jp1;
		jparr1[1]=jp2;
		jparr1[2]=jp3;
		jparr1[3]=jp4;
		JPanel jp5= new JPanel();
		JPanel jp6= new JPanel();
		JPanel jp7= new JPanel();
		JPanel jp8= new JPanel();
		JPanel[] jparr2 = new JPanel[4];
		jparr2[0]=jp5;
		jparr2[1]=jp6;
		jparr2[2]=jp7;
		jparr2[3]=jp8;
		JButton jbleftuser = new JButton("��Ӻ���");
		JButton jbleftsystem = new JButton("���Ⱥ");
		JButton jbleftacceptmsg = new JButton("�鿴��Ӻ�������");
		JTextField jtfaddfriend = new JTextField();
		JTextField jtfaddgroup = new JTextField();
		JButton jbfriend = new JButton("����");
		JButton jbgroup = new JButton("����");
		JPanel jpsearchContentuser = new JPanel();
		JPanel jpsearchContentgroup = new JPanel();
		JPanel jpacceptmsgblock = new JPanel();
		
		JScrollPane jsp = new JScrollPane(jpacceptmsgblock);
		jp.setLayout(null);
		jp.setVisible(false);
		jp.setOpaque(false);
		jp.setBounds(0, 70, 800, 650);
		jp.setBorder(new MyBorder(0xcccccc));
		
		jp.add(createLeft(jpleft,jbleftuser,jbleftsystem,jbleftacceptmsg,jprightuser,jprightsystem,jpacceptmsg));
		jp.add(createRightSet(jprightuser));
		jp.add(createRightSet(jprightsystem));
		jp.add(createRightSet(jpacceptmsg));

		jprightuser.add(createTextField(jtfaddfriend,"����QQ�Ż��ǳ�(ģ����ѯ)"));
		jprightuser.add(createSearchBtn(jbfriend,"friend",jtfaddfriend,jparr1));
		jprightuser.add(createSearchContentALL(jpsearchContentuser));
		createSearchContent(jp1,"user",jpsearchContentuser, 60, 80, 225, 150,0xffffff);
		createSearchContent(jp2,"user",jpsearchContentuser, 285, 80, 225, 150,0xffffff);
		createSearchContent(jp3,"user",jpsearchContentuser, 60, 230, 225, 150,0xffffff);
		createSearchContent(jp4,"user",jpsearchContentuser, 285, 230, 225, 150,0xffffff);
		
		jprightuser.setVisible(true);
		
		jprightsystem.add(createTextField(jtfaddgroup,"����QQȺ�Ż�����(ģ����ѯ)"));
		jprightsystem.add(createSearchBtn(jbgroup,"group",jtfaddgroup,jparr2));
		jprightsystem.add(createSearchContentALL(jpsearchContentgroup));
		createSearchContent(jp5,"group",jpsearchContentgroup, 60, 80, 225, 150,0xffffff);
		createSearchContent(jp6,"group",jpsearchContentgroup, 285, 80, 225, 150,0xffffff);
		createSearchContent(jp7,"group",jpsearchContentgroup, 60, 230, 225, 150,0xffffff);
		createSearchContent(jp8,"group",jpsearchContentgroup, 285, 230, 225, 150,0xffffff);
		
		jpacceptmsg.add(createAcceptMsgBlock(jsp,jpacceptmsgblock));
		
		return jp;
	}
	/**
	 * @param jsp	��Ϣ���չ������
	 * @param jp	��Ϣ�������
	 * @return
	 * ���ܣ���ʾ������֤��Ϣ
	 */
	private static JScrollPane createAcceptMsgBlock(JScrollPane jsp ,JPanel jp) {
		jsp.setVisible(true);
		jsp.setBounds(0,0, 600, 600);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setOpaque(false);     
		jsp.setBorder(new MyBorder(0xeeeeee));
		JViewport test =(JViewport) jsp.getComponent(0);
		test.setOpaque(false);
		
		jp.setVisible(true);
		jp.setBounds(0, 0, 600, 600);
		jp.setOpaque(false);
		jp.setLayout(null);
		jp.setPreferredSize(new Dimension(600, 500));
		
		JLabel jl = new JLabel();
		jl.setText("������Ϣ�б�");
		jl.setBounds(200, 0, 600, 80);
		jl.setOpaque(false);
		jl.setVisible(true);
		jl.setFont(new Font("����",Font.ITALIC,20));
		
		List<Msg> list = ServiceFactory.getService("msg").listPart(String.valueOf(user.getId()));
		if(list.size()>0){
			for(int i=0,j=0;i<list.size();i++){
				if(list.get(i).getType()==3){
					continue;
				}
				JPanel jpnew = new JPanel();
				if(j%2==0){
					createAcceptMsgItem(jpnew,55*(j+1),0xeeeeee,list.get(i));
				}else{
					createAcceptMsgItem(jpnew, 55*(j+1), 0xffffff,list.get(i));
				}
				jp.add(jpnew);
				jp.setPreferredSize(new Dimension(600, 80+55*(j+1)));
				j++;
			}
		}
//		JPanel jpmsg = new JPanel();
//		JPanel jpmsg1 = new JPanel();
//		JPanel jpmsg2= new JPanel();
//		createAcceptMsgItem(jpmsg,55,0xeeeeee);
//		createAcceptMsgItem(jpmsg1,55*2,0xffffff);
//		createAcceptMsgItem(jpmsg2,55*3,0xeeeeee);
		jp.add(jl);
//		jp.add(jpmsg);
//		jp.add(jpmsg1);
//		jp.add(jpmsg2);
		return jsp;
	}
	public static JPanel createAcceptMsgItem(JPanel jpmsg,int top,int color,final Msg msg){
		jpmsg.setVisible(true);
		jpmsg.setBounds(0, 25+top, 600, 55);
		jpmsg.setBackground(new Color(color));
		jpmsg.setLayout(null);
		JLabel jlmsg = new JLabel();
		jlmsg.setText(msg.getContent());
		jlmsg.setVisible(true);
		jlmsg.setFont(new Font("����",Font.ITALIC,13));
		jlmsg.setBounds(10,5,400,20);
		JTextField jltime = new JTextField();
		jltime.setText(new SimpleDateFormat("yyyy-MM-dd    HH:mm:ss").format(msg.getDate()));
		jltime.setEditable(false);
		jltime.setOpaque(false);
		jltime.setVisible(true);
		jltime.setBorder(null);
		jltime.setFont(new Font("����",Font.ITALIC,13));
		jltime.setBounds(10,30,200,20);
		final JButton jbaccept =new JButton("�� ��");
		final JButton jbaccept1 =new JButton("�� ��");
		if(msg.getStatus()==2){
			jbaccept.setText("�����");
		}
		if(msg.getStatus()==3){
			jbaccept1.setText("�Ѿܾ�");
		}
		jbaccept.setVisible(true);
		jbaccept.setBackground(Color.white);
		jbaccept.setBorder(new MyBorder(0xeeeeee));
		jbaccept.setForeground(Color.black);
		jbaccept.setBounds(450, 5, 100, 20);
		jbaccept.setFont(new Font("����",Font.ITALIC,12));
		jbaccept.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!jbaccept.getText().equals("�� ��")||!jbaccept1.getText().equals("�� ��")){
					return;
				}
				//������Ӻ��ѣ������������˼��뵽�ҵĺ��ѷ���
				List<FriendGroupUser> fgu = ServiceFactory.getService("friendGroupUser").listPart(String.valueOf(user.getId()));
				for(int i=0;i<fgu.size();i++){
					FriendGroup fg = (FriendGroup) ServiceFactory.getService("friendGroup").findById(fgu.get(i).getFriendGroupId());
					List<FriendGroupUser> list1 = ServiceFactory.getService("friendGroupUser").listPart("1",String.valueOf(fg.getId()));
					for(int item=0;item<list1.size();item++){
						if(msg.getUserId()==list1.get(item).getUserId()){
							jbaccept.setText("�Ѿ��Ǻ���");
							int flag1 = ServiceFactory.getService("msg").updateByConditions(""+2,""+msg.getId());
							if(flag1>0){
								System.out.println("�޸ı��Ϊ "+msg.getId()+" ��״̬Ϊ�Ѷ�");
							}
							return;
						}
					}
					if(fg.getName().split(" ")[0].equals("�ҵĺ���")&&fg.getName().split(" ")[1].equals(user.getAccount())){
						int flag = ServiceFactory.getService("friendGroupUser").updateByConditions(String.valueOf(fg.getId()),String.valueOf(msg.getUserId()));
						if(flag>0){
							User userback = (User) ServiceFactory.getService("user").findById(msg.getReceiver());
							if(MsgUtils.sendMsgBack(2, msg.getUserId(), msg.getReceiver(),userback.getName()+"��ͬ����ĺ�������("+userback.getAccount()+")")>0){
								System.out.println("�ѷ��ͳɹ������Ϣ����");
							}
							User user = (User) ServiceFactory.getService("user").findById(msg.getUserId());
							System.out.println(user.getName()+" ��Ӻ��� "+user.getName()+"("+user.getAccount()+")");
							//������Ӻ���
							List<FriendGroupUser> fgu1 = ServiceFactory.getService("friendGroupUser").listPart(String.valueOf(msg.getUserId()));
							for(int j=0;j<fgu1.size();j++){
								FriendGroup fg1 = (FriendGroup) ServiceFactory.getService("friendGroup").findById(fgu1.get(j).getFriendGroupId());
								User user1 = (User) ServiceFactory.getService("user").findById(msg.getUserId());
								if(fg1.getName().split(" ")[0].equals("�ҵĺ���")&&fg1.getName().split(" ")[1].equals(user1.getAccount())){
									int flag2 = ServiceFactory.getService("friendGroupUser").updateByConditions(String.valueOf(fg1.getId()),String.valueOf(SetView.user.getId()));
									if(flag2>0){
										System.out.println("������Ӻ��ѳɹ�");
									}
								}
							}
							int flag1 = ServiceFactory.getService("msg").updateByConditions(""+2,""+msg.getId());
							if(flag1>0){
								System.out.println("�޸ı��Ϊ "+msg.getId()+" ��״̬Ϊ�Ѷ�");
							}
							jbaccept.setText("�����");
						}
					}
				}
			}
		});

		jbaccept1.setVisible(true);
		jbaccept1.setBackground(Color.white);
		jbaccept1.setBorder(new MyBorder(0xeeeeee));
		jbaccept1.setForeground(Color.black);
		jbaccept1.setBounds(450, 30, 100, 20);
		jbaccept1.setFont(new Font("����",Font.ITALIC,12));
		jbaccept1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!jbaccept.getText().equals("�� ��")||!jbaccept1.getText().equals("�� ��")){
					return;
				}
				Msg msg1 = (Msg) ServiceFactory.getService("msg").findById(msg.getId());
				if(msg1.getStatus()==1){
					int flag1 = ServiceFactory.getService("msg").updateByConditions(""+3,""+msg.getId());
					if(flag1>0){
						System.out.println("�޸ı��Ϊ "+msg.getId()+" ��״̬Ϊ������");
						jbaccept1.setText("�Ѿܾ�");
						User userback = (User) ServiceFactory.getService("user").findById(msg.getReceiver());
						if(MsgUtils.sendMsgBack(2, msg.getUserId(), msg.getReceiver(),userback.getName()+"�Ѿܾ���ĺ�������("+userback.getAccount()+")")>0){
							System.out.println("�ѷ��;ܾ������Ϣ����");
						}
					}
				}else if(msg1.getStatus()==2){
					jbaccept.setText("�����");
				}else{
					jbaccept.setText("�Ѿ��Ǻ���");
				}
				
			}
		});
		if(msg.getType()==2){
			jbaccept.setVisible(false);
			jbaccept1.setVisible(false);
		}
		jpmsg.add(jbaccept);
		jpmsg.add(jbaccept1);
		jpmsg.add(jlmsg);
		jpmsg.add(jltime);
		return jpmsg;
	}
	public static JPanel createSearchContentALL(JPanel jp){
		jp.setVisible(true);
		jp.setBounds(0, 0, 600, 600);
		jp.setOpaque(false);
		jp.setLayout(null);
		return jp;
	}
	public static void createSearchContent(JPanel jp,String type,JPanel jpall,int left,int top,int width,int height,int bgcolor){
		final JLabel jlname = new JLabel();
		JLabel jlsex = new JLabel();
		jp.setVisible(true);
		jp.setBounds(left, top, width, height);
		jp.setLayout(null);
		jp.setBackground(new Color(bgcolor));
		jp.setBorder(new MyBorder(0xeeeeee));
		jp.setOpaque(false);
		
		ImageIcon ii = new ImageIcon("C:\\Users\\fsc\\Desktop\\icon.png");
		ii.setImage(ii.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT));
		JLabel jlicon = new JLabel(ii);
		jlicon.setBounds(20, 10, ii.getIconWidth(), ii.getIconHeight());
		jlicon.setVisible(true);
		if(type.equals("user")){
			jlname.setText("��֮��:");
			jlname.setBounds(100, 10, 100, 25);
			jlname.setFont(new Font("����",Font.ITALIC,13));
			jlname.setForeground(new Color(0x6C829F));
			jlname.setVisible(true);
		}else{
			jlname.setText("Ⱥ֮��:");
			jlname.setBounds(100, 10, 100, 25);
			jlname.setFont(new Font("����",Font.ITALIC,13));
			jlname.setForeground(new Color(0x6C829F));
			jlname.setVisible(true);
			
		}
		if(type.equals("user")){
			jlsex.setText("�ԡ���:");
			jlsex.setBounds(100, 60, 100, 25);
			jlsex.setFont(new Font("����",Font.ITALIC,13));
			jlsex.setForeground(new Color(0x6C829F));
			jlsex.setVisible(true);
		}else{
			jlsex.setText("");
			jlsex.setBounds(100, 60, 100, 25);
			jlsex.setFont(new Font("����",Font.ITALIC,13));
			jlsex.setForeground(new Color(0x6C829F));
			jlsex.setVisible(true);
		}
		
		JLabel jllevel = new JLabel();
		jllevel.setText("�ȡ���:");
		jllevel.setBounds(100, 35, 100, 25);
		jllevel.setFont(new Font("����",Font.ITALIC,13));
		jllevel.setForeground(new Color(0x6C829F));
		jllevel.setVisible(true);
		
		JTextArea jllabel = new JTextArea();
		jllabel.setText("���:");
		jllabel.setBounds(20, 85, 190, 30);
		jllabel.setFont(new Font("����",Font.ITALIC,13));
		jllabel.setForeground(new Color(0x000000));
		jllabel.setVisible(true);
		jllabel.setOpaque(false);
		jllabel.setEditable(false);
		
		final JLabel jlaccount = new JLabel();
		jlaccount.setText("no");
		jlaccount.setVisible(false);
		
		JButton jbsendmsg = new JButton("��������");
		jbsendmsg.setVisible(true);
		jbsendmsg.setBounds(110, 115, 100, 25);
		jbsendmsg.setBackground(new Color(0x3CC3F5));
		jbsendmsg.setBorderPainted(false);
		jbsendmsg.setForeground(Color.white);
		jbsendmsg.setFont(new Font("����",Font.ITALIC,12));
		jbsendmsg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(jlaccount.getText().equals("no")){
					return ;
				}
				Msg msg = new Msg();
				msg.setContent(user.getName()+"�����Ϊ���ѣ�("+user.getAccount()+")");
				msg.setReceiver(Integer.parseInt(jlaccount.getText()));
				msg.setUserId(user.getId());
				msg.setStatus(1);
				msg.setType(1);
				int flag = ServiceFactory.getService("msg").update(msg);
				if(flag>0){
					System.out.println(user.getName()+" �� "+jlname.getText().substring(4)+" �����˺�������");
				}
			}
		});
		jp.add(jlicon);
		jp.add(jlname);
		jp.add(jlsex);
		jp.add(jllevel);
		jp.add(jllabel);
		jp.add(jbsendmsg);
		jp.add(jlaccount);
		
		jpall.add(jp);
	}
	public static JTextField createTextField(final JTextField jtfaddfriend,final String text){
		jtfaddfriend.setBounds(70, 30, 200, 40);
		jtfaddfriend.setFont(new Font("����",Font.ITALIC,14));
		jtfaddfriend.setText(text);
		jtfaddfriend.setBorder(new MyBorder(0xcccccc));
		jtfaddfriend.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(jtfaddfriend.getText().equals(text)){
					jtfaddfriend.setText("");
				}
					
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(jtfaddfriend.getText().equals("")){
					jtfaddfriend.setText(text);
				}
			}
		});
		jtfaddfriend.setVisible(true);
		return jtfaddfriend;
	}
	public static JButton createSearchBtn(JButton jb,final String type,final JTextField account,final JPanel[] arr){
		jb.setBounds(270, 30, 70, 40);
		jb.setVisible(true);
		jb.setFont(new Font("����",Font.ITALIC,15));
		jb.setForeground(new Color(0x008CFF));
		jb.setBackground(Color.white);
		jb.setBorder(new MyBorder(0xcccccc));
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(type.equals("friend")){
					if(account.getText().equals("����QQ�Ż��ǳ�(ģ����ѯ)")){
						return;
					}
					List<User> list = ServiceFactory.getService("user").listPart(account.getText(),account.getText(),user.getAccount());
					if(list.size()>0){
						for(int i=0;i<4;i++){
							arr[i].setVisible(false);
						}
						for(int i=0;i<(list.size()<=4?list.size():4);i++){
							JLabel jlname = (JLabel) arr[i].getComponent(1);
							JLabel jlsex = (JLabel) arr[i].getComponent(2);
							JLabel jllevel = (JLabel) arr[i].getComponent(3);
							JTextArea jllabel = (JTextArea) arr[i].getComponent(4);
							JLabel jlaccount = (JLabel) arr[i].getComponent(6);
							jlname.setText("��֮��:"+list.get(i).getName());
							jlsex.setText("�ԡ���:"+list.get(i).getSex());
							jllevel.setText("�ȼ�:"+list.get(i).getLevel());
							jllabel.setText("���:"+list.get(i).getLabel());
							jlaccount.setText(""+list.get(i).getId());
							arr[i].setVisible(true);
							System.out.println("�������˺�:"+list.get(i).getAccount());
						}
					}else{
						for(int i=0;i<4;i++){
							arr[i].setVisible(false);
						}
						
					}
				}else{
				
				}
			}
		});
		return jb;
	}
	public static JPanel createBottomSet(JPanel jp){
		JPanel jprightuser = new JPanel();
		JPanel jprightsystem = new JPanel();
		JPanel jpleft = new JPanel();
		JPanel jp1= new JPanel();
		JButton jbleftuser = new JButton("��ͨ�û�");
		JButton jbleftsystem = new JButton("����Ա�û�");
		JButton jbleft1 = new JButton("����");
		jp.setLayout(null);
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBounds(0, 70, 800, 650);
		jp.setBorder(new MyBorder(0xcccccc));
		
		jp.add(createLeft(jpleft,jbleftuser,jbleftsystem,jbleft1,jprightuser,jprightsystem,jp1));
		jp.add(createRightSet(jprightuser));
		jp.add(createRightSet(jprightsystem));

		JCheckBox jcbuser = new JCheckBox("�û�����");
		jcbuser.setBounds(50, 50, 200, 30);
		jcbuser.setVisible(true);
		jcbuser.setOpaque(false);
		jprightuser.add(jcbuser);
		jprightuser.setVisible(true);
		
		JCheckBox jcbsystem = new JCheckBox("��ϵͳ����");
		jcbsystem.setBounds(50, 50, 200, 30);
		jcbsystem.setVisible(true);
		jcbsystem.setOpaque(false);
		jprightsystem.add(jcbsystem);
		
		return jp;
	}

	public static JPanel createRightSet(JPanel jp){
		jp.setLayout(null);
		jp.setVisible(false);
		jp.setOpaque(false);
		jp.setBounds(200, 0, 600, 650);
		
		
		return jp;
	}
	public static JPanel createLeft(JPanel jp,final JButton jb1 , final JButton jb2,final JButton jb3,final JPanel jprightuser,final JPanel jprightsystem,final JPanel jpacceptmsg){
		jp.setLayout(null);
		jp.setVisible(true);
		jp.setOpaque(false);
		jp.setBounds(0, 0, 200, 650);
		jp.setBorder(new MyBorder(0xcccccc));
		
		jb1.setBounds(0, 0, 200, 50);
		jb1.setBackground(new Color(0x3CC3F5));
		jb1.setBorderPainted(false);
		jb1.setForeground(Color.white);
		jb1.setFont(new Font("����",Font.ITALIC,15));
		jb1.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()<190&&e.getX()>10&&e.getY()<45&&e.getY()>5){
					jb1.setForeground(Color.green);
				}else{
					jb1.setForeground(Color.white);
				}
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
		jb1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jprightsystem.setVisible(false);
				jprightuser.setVisible(true);
				jpacceptmsg.setVisible(false);
			}
		});
		
		jb2.setBounds(0, 52, 200, 50);
		jb2.setBackground(new Color(0x3CC3F5));
		jb2.setBorderPainted(false);
		jb2.setForeground(Color.white);
		jb2.setFont(new Font("����",Font.ITALIC,15));
		jb2.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()<190&&e.getX()>10&&e.getY()<45&&e.getY()>5){
					jb2.setForeground(Color.green);
				}else{
					jb2.setForeground(Color.white);
				}
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
		jb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jprightsystem.setVisible(true);
				jprightuser.setVisible(false);
				jpacceptmsg.setVisible(false);
			}
		});
		
		jb3.setBounds(0, 104, 200, 50);
		jb3.setBackground(new Color(0x3CC3F5));
		jb3.setBorderPainted(false);
		jb3.setForeground(Color.white);
		jb3.setVisible(true);
		jb3.setFont(new Font("����",Font.ITALIC,15));
		jb3.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()<190&&e.getX()>10&&e.getY()<45&&e.getY()>5){
					jb3.setForeground(Color.green);
				}else{
					jb3.setForeground(Color.white);
				}
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
		jb3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jprightsystem.setVisible(false);
				jprightuser.setVisible(false);
				jpacceptmsg.setVisible(true);
			}
		});
		
		JButton jb4 = new JButton("����");
		jb4.setBounds(0, 156, 200, 50);
		jb4.setBackground(new Color(0x3CC3F5));
		jb4.setBorderPainted(false);
		jb4.setForeground(Color.white);
		jb4.setVisible(true);
		jb4.setFont(new Font("����",Font.ITALIC,15));
		
		JButton jb5 = new JButton("����");
		jb5.setBounds(0, 208, 200, 50);
		jb5.setBackground(new Color(0x3CC3F5));
		jb5.setBorderPainted(false);
		jb5.setForeground(Color.white);
		jb5.setVisible(true);
		jb5.setFont(new Font("����",Font.ITALIC,15));
		
		
		jp.add(jb1);
		jp.add(jb2);
		jp.add(jb3);
		jp.add(jb4);
		jp.add(jb5);
		return jp;
	}
	public static void main(String[] args) {
		SetView.createSetView("���úͲ���ҳ��","set","111112");
	}
}

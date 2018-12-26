package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import entity.User;
import service.ServiceFactory;

/**
 * 
 *clinet �Ǵ���QQClient���͹����Ĳ�����
 *���ܣ�������¼���棬��¼��ע�ᣬ�м��кܶ����֤
 */
public class Login extends JFrame{
	
	private final Socket client ;;
	private static final long serialVersionUID = 1L;
	public Login(String name,Socket client){
		super(name);
		this.client = client;
	}
	public static Login createLoginView(String name,Socket client){
		Login login = new Login(name,client);
		
		ImageIcon background = new ImageIcon(System.getProperty("user.dir")+"\\src\\imgs\\qq.jpg");
		JLabel jl = new JLabel(background);
		jl.setBounds(0, 0, background.getIconWidth(), background.getIconHeight());
		JPanel jp = (JPanel) login.getContentPane();
		jp.setOpaque(false);
		jp.setLayout(null);
		JPanel userInfo = createJPanel(background,login);
		jp.add(userInfo);
		
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setBounds(700, 250, 500, 500);
		login.getLayeredPane().setLayout(null);
		login.getLayeredPane().add(jl, new Integer(Integer.MIN_VALUE));
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setSize(background.getIconWidth(), background.getIconHeight()+220);
		login.setResizable(false);
		login.setVisible(true);
		return login;
	}
	private static JPanel createJPanel(ImageIcon background,Login jp) {
		JPanel userInfo = new JPanel();
		final JButton jb = new JButton("��ȫ��¼");
		final JButton registerbtn = new JButton("�л���ע��");
		final JTextField jtfusername = new JTextField();
		final JTextField jtfuserpassword = new JTextField();
		JCheckBox jcb1 = new JCheckBox("��ס����");
		JCheckBox jcb2 = new JCheckBox("�Զ���¼");
		JTextArea jlerror = new JTextArea();
		
		userInfo.setLayout(null);
		userInfo.setBounds(0,background.getIconHeight(), background.getIconWidth(), background.getIconHeight());
		userInfo.setBackground(Color.white);
		JLabel icon = new JLabel();
//		ImageIcon usericon  = new ImageIcon("C:\\Users\\fsc\\Desktop\\icon.png");
		ImageIcon usericon  = new ImageIcon(System.getProperty("user.dir")+"\\src\\imgs\\xc.jpg");
		icon.setIcon(usericon);
		icon.setBounds(50, 10, usericon.getIconWidth(), usericon.getIconHeight());
		
		createTextFieldUser(usericon, registerbtn,jtfusername);
		createRegisterBtn(jb, usericon, registerbtn,jtfusername,jtfuserpassword,jlerror);
		createTextFieldPassword(usericon,registerbtn, jtfuserpassword);
		createCheckBox(usericon, jcb1,70);
		createCheckBox(usericon, jcb2,191);
		createLoginBtn(jb,registerbtn, usericon,jtfusername,jtfuserpassword,jlerror,jp);
		createErrorBlock(jlerror,usericon);
		
		userInfo.add(icon);
		userInfo.add(jtfusername);
		userInfo.add(registerbtn);
		userInfo.add(jtfuserpassword);
		userInfo.add(jcb1);
		userInfo.add(jcb2);
		userInfo.add(jb);
		userInfo.add(jlerror);
		return userInfo;
	}
	private static JTextArea createErrorBlock(JTextArea jlerror,ImageIcon icon) {
		jlerror.setVisible(false);
		jlerror.setEditable(false);
		jlerror.setText("text\nhello");
		jlerror.setFont(new Font("����",Font.ITALIC,12));
		jlerror.setBackground(new Color(0xffffff));
		jlerror.setBounds(icon.getIconWidth()+320, 110, 200, 44);
		return jlerror;
	}
	private static void createLoginBtn(final JButton jb,final JButton jb1, ImageIcon usericon,final JTextField username,
			final JTextField userpassword,final JTextArea jlerror,final Login jp) {
		jb.setBounds(usericon.getIconWidth()+70,116,243,38);
		jb.setBackground(new Color(0x3CC3F5));
		jb.setForeground(new Color(0xffffff));
		jb.setBorderPainted(false);
		jb.setFont(new Font("����",Font.PLAIN,14));
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = username.getText();
				String password = userpassword.getText();
				String type = null;
				if(jb1.getText().equals("�л���ע��")){
					type="2";
				}else{
					type="1";
				}
				System.out.println(name+":"+password);
				if(name.startsWith("��")||name.startsWith("ע")){
					name="";
				}
				if(password.startsWith("��")||password.startsWith("��")){
					password="";
				}
				if(name.length()<6||password.length()<6){
					jlerror.setText("QQ�ʺŻ��������ʽ\n���ԣ�����������");
					jlerror.setForeground(Color.red);
					jlerror.setVisible(true);
					return;
				}else{
					jlerror.setVisible(false);
				}
				BufferedWriter bw = null;
				BufferedReader br = null;
				try {
					bw = new BufferedWriter(new OutputStreamWriter(jp.client.getOutputStream()));
					bw.write(type+":"+name+":"+password+"\n");
					bw.flush();
						br = new BufferedReader(new InputStreamReader(jp.client.getInputStream()));
						String result = br.readLine();
						System.out.println(result);
						if(result.startsWith("��ӭ:")){
							jlerror.setVisible(true);
							jlerror.setText("����ɹ���\n2�����ת");
							jlerror.setForeground(Color.GREEN);
							jp.setVisible(false);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							System.out.println(Thread.currentThread().getName());
							final User user = (User) ServiceFactory.getService("user").findByCondition(result.split(":")[1]);
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									
									try {
//										�����û�ע�����棬���Ұ�socket��������
										UserView.createUserView("QQ",0xffffff,jp.client,user.getAccount());
									} catch (Exception e) {
										e.printStackTrace();
									}									
								}
							}).start();;
							jp.dispose();
							
						}else{
							String[] res = result.split(",");
							result = "";
							System.out.println(res.length);
							for(int i=0;i<res.length;i++){
								result+=res[i]+"\n";
							}
							jlerror.setVisible(true);
							jlerror.setText(result);
							jlerror.setForeground(Color.red);
						}
				} catch (IOException e1) {
					System.out.println("ע����¼ʧ�ܣ�");
				}
			}
		});
	}
	private static void createCheckBox(ImageIcon usericon, JCheckBox jcb1,int left) {
		jcb1.setFont(new Font("����",Font.PLAIN,13));
		jcb1.setBounds(usericon.getIconWidth()+left,86,121,30);
		jcb1.setForeground(new Color(0x000000));
		jcb1.setBackground(Color.WHITE);
	}
	private static void createTextFieldPassword(ImageIcon usericon,final JButton tab,
			final JTextField jtfuserpassword) {
		jtfuserpassword.setBounds(usericon.getIconWidth()+70, 46, 243, 38);
		jtfuserpassword.setText("����������");
//		jtfuserpassword.setText("111111");
		jtfuserpassword.setFont(new Font("����",Font.ITALIC,14));
		jtfuserpassword.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(jtfuserpassword.getText().equals("����������")||jtfuserpassword.getText().equals("����(6λ�ַ�)"))
				jtfuserpassword.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(jtfuserpassword.getText().equals("")){
					if(tab.getText().equals("�л���ע��")){
						jtfuserpassword.setText("����������");
					}else{
						jtfuserpassword.setText("����(6λ�ַ�)");
					}
					
				}
			}
			
		});
	}
	private static void createRegisterBtn(final JButton jb, ImageIcon usericon,final JButton registerbtn,
			final JTextField username,final JTextField userpassword,final JTextArea jlerror) {
		registerbtn.setVisible(true);
		registerbtn.setBounds(usericon.getIconWidth()+313, 10, 110, 30);
		registerbtn.setBorderPainted(false);
		registerbtn.setFont(new Font("����",Font.ITALIC,14));
		registerbtn.setBackground(Color.white);
		registerbtn.setForeground(new Color(0x2685E3));
		registerbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jlerror.setVisible(false);
				if(registerbtn.getText().equals("�л���ע��")){
					registerbtn.setText("�л�����¼");
					jb.setText("��ȫע��");
					username.setText("ע���QQ��(6λ��������)");
					userpassword.setText("����(6λ�ַ�)");
				}else{
					registerbtn.setText("�л���ע��");
					jb.setText("��ȫ��¼");
					username.setText("������QQ��");
					userpassword.setText("����������");
				}
				
			}
		});
	}
	private static void createTextFieldUser(ImageIcon usericon,final JButton tab,final JTextField jtfusername) {
		jtfusername.setBounds(usericon.getIconWidth()+70, 10, 243, 38);
		jtfusername.setText("������QQ��");
//		jtfusername.setText("111111");
		jtfusername.setFont(new Font("����",Font.ITALIC,14));
		jtfusername.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if(jtfusername.getText().equals("������QQ��")||jtfusername.getText().equals("ע���QQ��(6λ��������)"))
				jtfusername.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(tab.getText().equals("�л���ע��")){
					if(jtfusername.getText().equals("")){
						jtfusername.setText("������QQ��");
					}
				}else{
					if(jtfusername.getText().equals("")){
						jtfusername.setText("ע���QQ��(6λ��������)");
					}
				}
				
			}
			
		});
	}
}
class MyBorder implements Border{
	private int color;
	public MyBorder(int color){
		this.color=color;
	}
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		g.setColor(new Color(color));
		g.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 0, 0);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0,0,0,0);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}
	
}

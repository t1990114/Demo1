package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import entity.Group;
import entity.Msg;
import entity.User;
import service.ServiceFactory;

/**
 * ����qq�����
 *���������Ǹ���Ŀ����ĵĲ��֣��е���������Ϣ�Ĵ���ת���洢���û��ĵ�¼ע����֤�����е����ݿ��������֮�йأ�����ʵ������ӵ���Ϣת�������ش����ơ����������û���������Ϣ����Ⱥ��������Ϣ�����������û���socket
 *���ܣ�ת���ͻ��˵���Ϣ���ÿͻ��˿�������������Ҳʵ����Ⱥ�Ĺ��ܣ��ײ��ǻ�����Ϣ��־�����ش������ӶȽϸߡ�
 *���ԣ�users���������û���¼��Ϣ�����������ܵ��ͻ��˵�socket���Ӻ�õ���socket���û���¼��ע���¼����û���Ϣ��������ʵ����Ϣ��׼ȷ��Ӧ���͡�
 *groups�����û���¼ʱ�������Ⱥ�б��е�Ⱥ��ÿ��Ⱥ����Ӧ���û���socket���Ӷ���ʵ��Ⱥ��Ϣת��ʱ������groups��������Ⱥ��Ա��socket����Ⱥ��Ϣ����ʱҲ�ᾭ�������ش����ƣ����ͻ��˽���Ⱥ�Ľ�����ȫ������
 *���Ľ�������й��ܣ����Կ��Ա�������ͨ�û����������
 */
public class QQServer {

	public static HashMap<User,Socket> users = new HashMap<>();
	public static HashMap<Group,Socket> groups = new HashMap<>();
	
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(1995);
			System.out.println("����������,�ȴ��ͻ�������");
			while(true){
				Socket client = server.accept();
//				System.out.println(client.getInetAddress()+":"+client.getPort());
				System.out.println("�û���¼!");
				User newUser = new User();
				new Thread(new ServerTask(client,newUser)).start();
				users.put(newUser,client);
				System.out.println("��ǰ����������"+users.size());
			}
			
		} catch (IOException e) {
			System.out.println("��ȡ��������socketʧ��!");
		}
	}
}
/**
 *
 *���ܣ�ÿ����һ���û��ɹ���Ժ��ڸ÷����������ɵ�һ��socket���뵽���̣߳����Ҵ������û���������ã���ʱΪ�ն��󣬵�¼ʱ��ȫ�û���Ϣ��
 *ÿ���ͻ��˳������к󶼻��ø������ʼֻ�ǿյ�user���ú�socket��Ӧ����ʱ����¼֮�󣬾��õ�¼����û�������socket���Ӧ���Դ�ʵ��
 *��ͬ�û�֮����Ϣ����ʱ��socketѡ�񣬴Ӷ���ȷ�����Ե�socket������Ϣ��ÿ���û���Ӧһ����������ÿ���ͻ��˽����߳����Ӧ��
 *ͬʱȺ��Ҳ�������������Ⱥ�ĳ�����Ϣ���ͷ�ʽ��ͬ�������һ�¡�
 *�����������е���Ϣ���ܣ���������Ϣͷ���ж���Ϣ����
 *��Ϣͷ��1.ע����Ϣ	2.��¼��Ϣ	3.�������û�������Ϣ	4.δ����	5.ת����Ϣ	6.�رմ���ָ��Ĵ���
 *
 */
class ServerTask implements Runnable{
	private Socket socket;
	private final User user;
	private final List<Group> groups = new ArrayList<>();
	public ServerTask(Socket socket,User user){
		this.socket=socket;
		this.user=user;
	}
	@Override
	public void run() {
		BufferedReader br = null;
		BufferedWriter bw = null;
		String content = null;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while(true){
				if(socket.isClosed()){
					System.out.println("�����ѹر�");
					return;
				}
				
				content = br.readLine();
				System.out.println((user.getAccount()!=null?user.getAccount():"����")+" receive msg:"+content);
				System.out.println((user.getAccount()!=null?user.getAccount():"����")+" socket port:"+socket.getPort());
				String[] msg = content.split(":");
				String result = null;
				if(msg[0].equals("1")){
					result = doRegister(msg)+"\n";
				}
				if(msg[0].equals("2")){
					result = doLogin(msg)+"\n";
				}
				if(msg[0].equals("3")){
					result  = doSendMsg(msg)+"\n";
				}
				if(msg[0].equals("4")){
					result = doAcceptMsg(msg)+"\n";
				}
				if(msg[0].equals("5")){
					result = doRetrainsmit(msg)+"\n";
				}
				if(msg[0].equals("6")){
					result = doClose(msg);
				}
//				else{
//					bw.write(content+"\n");
//					bw.flush();
//				}
				bw.write(result);
				bw.flush();
			}
		} catch (IOException e) {
//			���û��Ͽ�����ʱ�����ر�userview����ʱ����Ͽ�socket���ӣ���ʱ�жϸ��û��Ѿ����ߣ���users����ȥ���û�������Ϣ��ͬʱȥ�����û���Ⱥ��Ϣ����ȫȥ���˺͸��û�ͨ�ŵ�socket��
//			��ֹ��Ϣ���͵Ĵ���
			System.out.println("��������ȡ�ͻ���������ʧ�ܣ�");
			System.out.println("�û���"+(user.getAccount()!=null?user.getAccount():"�ο�")+") �Ѿ�����!");
			QQServer.users.remove(user);
			for(int i=0;i<groups.size();i++){
				QQServer.groups.remove(groups.get(i));
			}
			System.out.println("�����û�������"+QQServer.users.size());
		}
		
	}
	private String doClose(String[] msg) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("4:"+msg[1]+":"+msg[2]+":"+msg[3]+"\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "�ش��ɹ�";
	}
//	��Ϣ�ش�����
	private String doRetrainsmit(String[] msg) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			if(msg[2].equals("�ر�ָ��")){
				bw.write("4:"+msg[1]+":"+msg[2]+":"+msg[3]+"\n");
			}else{
				bw.write("4:"+msg[1]+":"+msg[2]+":"+msg[3]+"\n");
			}
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "�ش��ɹ�";
	}
	private String doAcceptMsg(String[] msg) {
//		try {
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//			System.out.println("write");
//			bw.write(msg[1]+"\n");
//			bw.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return msg[1];
	}
	/**
	 * @param msg
	 * @return
	 * @throws IOException
	 * ��Ϣ���ͷ���������Ⱥ�ĺ͵��ġ�
	 */
	@SuppressWarnings("unchecked")
	private String doSendMsg(String[] msg) throws IOException {
		Group group = (Group) ServiceFactory.getService("group").findByCondition(msg[1]);
		User selfuser = (User) ServiceFactory.getService("user").findByCondition(user.getAccount());
//		Ⱥ����Ϣ���ͣ����շ�Ϊָ����Ⱥ�����������û���socket��groups�����ȡ��
		if(group!=null){
			HashMap<Group,Socket> temp = new HashMap<>(QQServer.groups);
			for(int i=0;i<groups.size();i++){
				temp.remove(groups.get(i));
			}
			Iterator<Entry<Group,Socket>> it = temp.entrySet().iterator();
			HashSet<Socket> groupReceivers = new HashSet<Socket>();
			System.out.println("groupuser count:"+QQServer.groups.size());
			while(it.hasNext()){
				Entry<Group,Socket> entry = it.next();
				if(entry.getKey().getAccount().equals(group.getAccount())){
					groupReceivers.add(entry.getValue());
				}
			}
			
			System.out.println("groupReceivers count:"+groupReceivers.size());
			Iterator<Socket> receivers = groupReceivers.iterator();
			Msg newmsg = new Msg();
			newmsg.setContent(msg[2]);
			newmsg.setReceiver(group.getId());
			newmsg.setUserId(selfuser.getId());
			newmsg.setStatus(1);
			newmsg.setType(3);
			int flag = ServiceFactory.getService("msg").update(newmsg);
			if(flag>0){
				System.out.println("�ɹ�������Ϣ��");
			}
			while(receivers.hasNext()){
				Socket s = receivers.next();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				bw.write("4:"+group.getAccount()+":"+msg[2]+":"+flag+"\n");
				bw.flush();
				System.out.println("ת�����û�"+user.getAccount()+" ��Ⱥ��Ϣ��4:"+group.getAccount()+":"+msg[2]+":"+flag);
			}
			return "���ͳɹ�";
//			���ĵ���Ϣ���ͻ��ơ�
		}else{
			String receiverAccount = msg[1];
			User receiveruser = (User) ServiceFactory.getService("user").findByCondition(msg[1]);
			
			Iterator<Entry<User, Socket>> it = QQServer.users.entrySet().iterator();
			while(it.hasNext()){
				User receiver = it.next().getKey();
//				ѡ����ȷ�Ľ����ߵ�socket
				if(receiver.getAccount().equals(receiverAccount)){
					Socket socket1 = QQServer.users.get(receiver);
					if(socket1.isClosed()){
						System.out.println("�ӿ��ѹرգ�");
						return "�ӿ��ѹر�";
					}
					Msg newmsg = new Msg();
					newmsg.setContent(msg[2]);
					newmsg.setReceiver(receiveruser.getId());
					newmsg.setUserId(selfuser.getId());
					newmsg.setStatus(1);
					newmsg.setType(3);
//					���յ��˾ʹ������ݿ⣬״̬Ϊδ����
					int flag = ServiceFactory.getService("msg").update(newmsg);
					if(flag>0){
						System.out.println("�ɹ�������Ϣ��");
					}
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
					bw.write("4:"+user.getAccount()+":"+msg[2]+":"+flag+"\n");
					bw.flush();
					System.out.println("���û�"+receiverAccount+" �����ˣ�4:"+user.getAccount()+":"+msg[2]+":"+flag);
					
					
					return "���ͳɹ�";
				}
			}
			Msg newmsg = new Msg();
			newmsg.setContent(msg[2]);
			newmsg.setReceiver(receiveruser.getId());
			newmsg.setUserId(selfuser.getId());
			newmsg.setStatus(1);
			newmsg.setType(3);
			int flag = ServiceFactory.getService("msg").update(newmsg);
			if(flag>0){
				System.out.println("�ɹ�������Ϣ��");
			}
			System.out.println("���ѵ�ǰ�����ߣ�תΪ���߷���");
			return "4:"+msg[1]+":���ѵ�ǰ������,תΪ���߷���";
		}
		
	}
	@SuppressWarnings("unchecked")
	private String doLogin(String[] msg) {
		User user = (User) ServiceFactory.getService("user").findByCondition(msg[1]);
		if(user==null){
			return "��QQ�Ų�����";
		}
		if(user.getPassword().equals(msg[2])){
			Iterator<User> it = QQServer.users.keySet().iterator();
			while(it.hasNext()){
				User u = it.next();
				if(u.getAccount()!=null){
					if(u.getAccount().equals(msg[1])){
						return "���û��ѵ�¼,�����ظ���¼";
					}
				}
			}
//			��¼�ɹ��󣬴����¼���û���Ϣ�����ں͸�socketƥ�䣬�����Ժ����Ϣ���ͽ���
			this.user.setAccount(user.getAccount());
			List<Group> usergroup = ServiceFactory.getService("group").listPart(""+user.getId());
//			��¼�󣬴����û���Ⱥ��Ϣ�����Ұ󶨸��û���socket�������յ�Ⱥ��Ϣʱ��ͨ��groups���������и�Ⱥ���û���socket�����������Ϣ��
			for(int i=0;i<usergroup.size();i++){
				groups.add(usergroup.get(i));
				QQServer.groups.put(usergroup.get(i), socket);
			}
			return "��ӭ:"+user.getAccount();
			
		}else{
			return "�û���������,��ƥ��";
		}
	}
	@SuppressWarnings("unchecked")
	private String doRegister(String[] msg) {
		User user =(User) ServiceFactory.getService("user").findByCondition(msg[1]);
		if(user!=null){
			return "QQ���ѱ�ע��";
		}else{
			User newUser = new User();
			newUser.setAccount(msg[1]);
			newUser.setPassword(msg[2]);
			System.out.println(msg[1]+msg[2]);
			int flag = ServiceFactory.getService("user").update(newUser);
			if(flag>0){
				System.out.println("�ɹ�ע��");
				this.user.setAccount(newUser.getAccount());
				List<Group> usergroup = ServiceFactory.getService("group").listPart(""+newUser.getId());
				for(int i=0;i<usergroup.size();i++){
					groups.add(usergroup.get(i));
					QQServer.groups.put(usergroup.get(i), socket);
				}
				return "��ӭ:"+newUser.getAccount();
			}
			return "ע��ʧ��!";
		}
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
}

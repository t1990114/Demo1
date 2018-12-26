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
 * 大腿qq服务端
 *描述：这是该项目最核心的部分，承担着所有消息的处理转发存储，用户的登录注册验证，所有的数据库操作都与之有关，而且实现了最复杂的消息转发错误重传机制。保存所有用户的在线信息，和群的在线信息。保存所有用户的socket
 *功能：转发客户端的信息，让客户端可以正常交流，也实现了群聊功能，底层是基于消息标志甄别和重传，复杂度较高。
 *属性：users——保存用户登录信息，服务器接受到客户端的socket连接后得到的socket与用户登录或注册登录后的用户信息绑定起来，实现消息的准确对应发送。
 *groups——用户登录时，会加载群列表中的群，每个群都对应该用户的socket，从而来实现群消息转发时，根据groups来向多个该群成员的socket发送群消息，此时也会经过错误重传机制，但客户端界面群聊界面完全复用了
 *单聊界面的所有功能，所以可以被当作普通用户聊天操作。
 */
public class QQServer {

	public static HashMap<User,Socket> users = new HashMap<>();
	public static HashMap<Group,Socket> groups = new HashMap<>();
	
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(1995);
			System.out.println("服务器启动,等待客户端连接");
			while(true){
				Socket client = server.accept();
//				System.out.println(client.getInetAddress()+":"+client.getPort());
				System.out.println("用户登录!");
				User newUser = new User();
				new Thread(new ServerTask(client,newUser)).start();
				users.put(newUser,client);
				System.out.println("当前在线人数："+users.size());
			}
			
		} catch (IOException e) {
			System.out.println("获取服务器端socket失败!");
		}
	}
}
/**
 *
 *功能：每当有一个用户成功配对后，在该服务器端生成的一个socket传入到该线程，并且传入了用户对象的引用，此时为空对象，登录时补全用户信息。
 *每个客户端程序运行后都会获得该任务，最开始只是空的user引用和socket对应，当时当登录之后，就让登录后的用户对象与socket相对应，以此实现
 *不同用户之间消息传送时的socket选择，从而正确的往对的socket发送信息。每个用户对应一个该任务，与每个客户端接受线程相对应。
 *同时群聊也被复用在了这里，群聊除了消息发送方式不同，其余均一致。
 *该任务处理所有的消息接受，并依据消息头来判断消息类型
 *消息头：1.注册信息	2.登录信息	3.向其他用户发送信息	4.未定义	5.转发信息	6.关闭窗口指令的处理
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
					System.out.println("连接已关闭");
					return;
				}
				
				content = br.readLine();
				System.out.println((user.getAccount()!=null?user.getAccount():"匿名")+" receive msg:"+content);
				System.out.println((user.getAccount()!=null?user.getAccount():"匿名")+" socket port:"+socket.getPort());
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
//			当用户断开连接时，即关闭userview界面时，会断开socket连接，此时判断该用户已经下线，从users里面去掉用户在线信息，同时去除该用户的群信息，完全去开了和该用户通信的socket，
//			防止消息发送的错误。
			System.out.println("服务器获取客户端输入流失败！");
			System.out.println("用户（"+(user.getAccount()!=null?user.getAccount():"游客")+") 已经下线!");
			QQServer.users.remove(user);
			for(int i=0;i<groups.size();i++){
				QQServer.groups.remove(groups.get(i));
			}
			System.out.println("在线用户数量："+QQServer.users.size());
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
		
		return "重传成功";
	}
//	消息重传机制
	private String doRetrainsmit(String[] msg) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			if(msg[2].equals("关闭指令")){
				bw.write("4:"+msg[1]+":"+msg[2]+":"+msg[3]+"\n");
			}else{
				bw.write("4:"+msg[1]+":"+msg[2]+":"+msg[3]+"\n");
			}
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "重传成功";
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
	 * 消息发送方法，包括群聊和单聊。
	 */
	@SuppressWarnings("unchecked")
	private String doSendMsg(String[] msg) throws IOException {
		Group group = (Group) ServiceFactory.getService("group").findByCondition(msg[1]);
		User selfuser = (User) ServiceFactory.getService("user").findByCondition(user.getAccount());
//		群聊消息发送，接收方为指定的群的所有在线用户，socket从groups里面获取。
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
				System.out.println("成功存入信息！");
			}
			while(receivers.hasNext()){
				Socket s = receivers.next();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				bw.write("4:"+group.getAccount()+":"+msg[2]+":"+flag+"\n");
				bw.flush();
				System.out.println("转发了用户"+user.getAccount()+" 的群消息：4:"+group.getAccount()+":"+msg[2]+":"+flag);
			}
			return "发送成功";
//			单聊的消息发送机制。
		}else{
			String receiverAccount = msg[1];
			User receiveruser = (User) ServiceFactory.getService("user").findByCondition(msg[1]);
			
			Iterator<Entry<User, Socket>> it = QQServer.users.entrySet().iterator();
			while(it.hasNext()){
				User receiver = it.next().getKey();
//				选出正确的接收者的socket
				if(receiver.getAccount().equals(receiverAccount)){
					Socket socket1 = QQServer.users.get(receiver);
					if(socket1.isClosed()){
						System.out.println("接口已关闭！");
						return "接口已关闭";
					}
					Msg newmsg = new Msg();
					newmsg.setContent(msg[2]);
					newmsg.setReceiver(receiveruser.getId());
					newmsg.setUserId(selfuser.getId());
					newmsg.setStatus(1);
					newmsg.setType(3);
//					接收到了就存入数据库，状态为未读。
					int flag = ServiceFactory.getService("msg").update(newmsg);
					if(flag>0){
						System.out.println("成功存入信息！");
					}
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
					bw.write("4:"+user.getAccount()+":"+msg[2]+":"+flag+"\n");
					bw.flush();
					System.out.println("向用户"+receiverAccount+" 发送了：4:"+user.getAccount()+":"+msg[2]+":"+flag);
					
					
					return "发送成功";
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
				System.out.println("成功存入信息！");
			}
			System.out.println("好友当前不在线，转为离线发送");
			return "4:"+msg[1]+":好友当前不在线,转为离线发送";
		}
		
	}
	@SuppressWarnings("unchecked")
	private String doLogin(String[] msg) {
		User user = (User) ServiceFactory.getService("user").findByCondition(msg[1]);
		if(user==null){
			return "该QQ号不存在";
		}
		if(user.getPassword().equals(msg[2])){
			Iterator<User> it = QQServer.users.keySet().iterator();
			while(it.hasNext()){
				User u = it.next();
				if(u.getAccount()!=null){
					if(u.getAccount().equals(msg[1])){
						return "该用户已登录,不能重复登录";
					}
				}
			}
//			登录成功后，存入登录的用户信息，用于和该socket匹配，用于以后的消息发送接受
			this.user.setAccount(user.getAccount());
			List<Group> usergroup = ServiceFactory.getService("group").listPart(""+user.getId());
//			登录后，存入用户的群信息，并且绑定该用户的socket，当接收到群消息时，通过groups来查找所有该群的用户的socket来逐个发送消息。
			for(int i=0;i<usergroup.size();i++){
				groups.add(usergroup.get(i));
				QQServer.groups.put(usergroup.get(i), socket);
			}
			return "欢迎:"+user.getAccount();
			
		}else{
			return "用户名和密码,不匹配";
		}
	}
	@SuppressWarnings("unchecked")
	private String doRegister(String[] msg) {
		User user =(User) ServiceFactory.getService("user").findByCondition(msg[1]);
		if(user!=null){
			return "QQ号已被注册";
		}else{
			User newUser = new User();
			newUser.setAccount(msg[1]);
			newUser.setPassword(msg[2]);
			System.out.println(msg[1]+msg[2]);
			int flag = ServiceFactory.getService("user").update(newUser);
			if(flag>0){
				System.out.println("成功注册");
				this.user.setAccount(newUser.getAccount());
				List<Group> usergroup = ServiceFactory.getService("group").listPart(""+newUser.getId());
				for(int i=0;i<usergroup.size();i++){
					groups.add(usergroup.get(i));
					QQServer.groups.put(usergroup.get(i), socket);
				}
				return "欢迎:"+newUser.getAccount();
			}
			return "注册失败!";
		}
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
}

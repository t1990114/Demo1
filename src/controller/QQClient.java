package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import view.Login;

/**
 * 
 *�ͻ��˵���ڣ����������ͻ��˳������ӷ����������ӳɹ��󴴽�һ��socket
 *����socket���뵽login.class
 */
public class QQClient {
	private Socket client = null;
	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("user.dir"));
			QQClient qqClient = new QQClient();
			qqClient.client = new Socket("127.0.0.1",1995);
			System.out.println("�ͻ��˳ɹ����ӷ�����");
			System.out.println(qqClient.client.getInetAddress()+":"+qqClient.client.getPort());
			Login.createLoginView("QQ",qqClient.client);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

/**
 * 
 * ���µĲ���Ϊ�������룬���ڳ��ڵĺͷ�������ͨ���ԣ�����ͨ������ɾ��
 * ����Ϊ�����������ͻ����������ڽ��շ����������ݺ���������������ݡ�
 *
 */
class ClientSendMsg implements Runnable{
	private Socket socket = null;
	public ClientSendMsg(Socket s){
		this.socket = s;
	}
	@Override
	public void run() {
		BufferedWriter bw = null;
		String content = "hello world";
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(content+"\n");
			bw.flush();
			
		} catch (IOException e) {
			System.out.println("�ͻ���д��ʧ��");
		}
			
	}
	
}
class ClientAcceptMsg implements Runnable{
	private Socket socket = null;
	public ClientAcceptMsg(Socket s){
		this.socket = s;
	}
	@Override
	public void run() {
		BufferedReader br = null;
		String content = null;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				System.out.println("hello");
				content = br.readLine();
				System.out.println("������Ϣ:"+content);
			}
			
		} catch (IOException e) {
			System.out.println("������Ϣʧ�ܣ�");
		}
	}
	
}
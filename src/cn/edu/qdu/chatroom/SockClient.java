package cn.edu.qdu.chatroom;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class SockClient {
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
//	private static boolean flag=true;

	@SuppressWarnings("resource")
	public void init(){
		String str = null;
		Scanner input = new Scanner(System.in);
		try {
			// �ͻ������ӷ�����
			socket = new Socket("127.0.0.1", 8888);
			System.out.println("�ͻ��������ӣ�");
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			// �����ӳɹ��������߳�ʵ��ѭ�����շ���˷��͵���Ϣ�Ĺ��ܡ�
			// ��Ϊֻ��Ҫ����һ���߳̾Ϳ����������շ�������Ϣ�����������������ڲ��ࡣ
			new Thread() {
				@Override
				public void run() {
					String s = null;
					try {
						while (true) {
							if ((s = dis.readUTF()) != null)
								System.out.println(s);
						}
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}.start();
			// �����߳���ʵ�ִӿ���̨�������ݲ��ҷ��͵��������Ĺ��ܡ�
			do {
				str = input.next();
				dos.writeUTF(str);
			} while (!str.equals("88"));
			System.out.println("client over!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// �ر�����
			try {
				dis.close();
				dos.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	
	public static void main(String[] args) {
		SockClient tc = new SockClient();
		tc.init();
	}
}

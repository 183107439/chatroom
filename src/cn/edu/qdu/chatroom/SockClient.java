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
			// 客户端连接服务器
			socket = new Socket("127.0.0.1", 8888);
			System.out.println("客户端已连接！");
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			// 当连接成功后开启子线程实现循环接收服务端发送的信息的功能。
			// 因为只需要另起一个线程就可以用来接收服务器信息，所以这里用匿名内部类。
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
			// 在主线程中实现从控制台接收数据并且发送到服务器的功能。
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
			// 关闭连接
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

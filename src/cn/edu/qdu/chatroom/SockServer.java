package cn.edu.qdu.chatroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SockServer {
	static List<Client> clientList = new ArrayList<Client>();

	public void init() {
		ServerSocket server = null;
		Socket socket = null;
		try {
			// �򿪶˿ڣ��ȴ����������ӡ�
			server = new ServerSocket(8888);
			System.out.println("�������ѿ�����");
			// �����ѭ�������������пͻ������ӣ�����˽��ܲ��Ҵ���һ���̣߳�������ͨ��socket������߳��У��������̡߳�
			while (true) {
				socket = server.accept();
				Client c = new SockServer().new Client(socket);//ClientΪ�ڲ���
				clientList.add(c);// �������ӵĿͻ��˴���list�У��Ա��ں������������Ϣ��
				c.start();
				//�����û��б���Ϣuserlist
				updateUserList();
			}

		} catch (IOException e) {
			System.out.println("server over!");
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		SockServer ts = new SockServer();
		ts.init();
	}

	//���չ�������õ��ַ���  ����,aa,bb,cc,dd
	public String getNameStr(){
		String nameStr=",";
		for (int i = 0; i < clientList.size(); i++) {
			nameStr+=clientList.get(i).name+",";
		}
		return nameStr;
	}
	
	//�����пͻ��˷��͸��º���û��б�by getNameStr()
	public void updateUserList(){
		String userList=getNameStr();
		for (int i = 0; i < clientList.size(); i++) {
			try {
				new DataOutputStream(clientList.get(i).socket.getOutputStream()).writeUTF(userList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ��Ϊ��Ҫ���ö���߳�����ɿͻ��˵����ӣ���������ʹ���ڲ��ࡣ
	class Client extends Thread {
		Socket socket;
		String name;
		DataOutputStream dos;
		DataInputStream dis;

		// ʹ�ô������Ĺ��췽������ͨ�������߳��С�
		public Client(Socket socket) {
			this.socket = socket;
			name="�û�"+socket.getPort();
		}

		@Override
		public void run() {
			String str = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
				// ѭ��������������ĳ���ͻ��˴�������Ϣ�����ҽ���Щ��Ϣ�������͸������ͻ��ˡ�
				while (true) {
					if ((str = dis.readUTF()) != null) {
						//�ж����ݵĸ�ʽ��������#��ʼ�������Լ���name���ԣ�����ѭ�������Ϣ
						if(str.startsWith("#")){
							this.name=str.substring(1);
							updateUserList();//���������û��б�
						}else{
							// ѭ����������Ϣ���͸����пͻ��ˡ�
							for (int i = 0; i < clientList.size(); i++) {
									new DataOutputStream(clientList.get(i).socket.getOutputStream())
								.writeUTF(name+ ":" + str);	
								}
							}
						}
					}
//			}
			} catch (IOException e) {
				System.out.println("�ͻ���" + socket.getPort() + "�˳�");
				clientList.remove(this);
				updateUserList();//�����û��б���Ϣ
				// e.printStackTrace();
			} finally {
				// �رո�����
				try {
					dis.close();
					dos.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	
}

package cn.edu.qdu.chatroom;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class MainUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton send;
	private JTextArea showMsg;
	private JTextField inputMsg;
	private JLabel nameLable;
	private JList<String> userList;
	private JLabel onLineUserLable;
	private JButton changeNameButton;
	

	
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private JTextField inputName;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init(){		
			// 客户端连接服务器
			try {
				socket = new Socket("127.0.0.1", 8888);
				System.out.println("客户端已连接！");
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 当连接成功后开启子线程实现循环接收服务端发送的信息的功能。
			// 因为只需要另起一个线程就可以用来接收服务器信息，所以这里用匿名内部类。
			new Thread() {
				@Override
				public void run() {
					String s = null;
					try {
						while (true) {
							if ((s = dis.readUTF()) != null)
								if (s.startsWith(",")) {
									addOnlineUserList(s);//更新用户列表
								} else {
									showMsg.append(s+"\r\n");//更新聊天内容
								}
						}
					} catch (IOException e) {
						
					}
				}
			}.start();			
	}
	
	//更新用户列表
	public void addOnlineUserList(String userName){
		String onlineUserList[]=userName.split(",");
		DefaultListModel<String> dlm=new DefaultListModel<String>();
		for (int i = 0; i < onlineUserList.length; i++) {
			dlm.addElement(onlineUserList[i]);
		}
		userList.setModel(dlm);
	}
	
	public void sendMsg(){
		//向服务器发送信息
		try {
			dos.writeUTF(inputMsg.getText());
			inputMsg.setText("");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * Create the frame.
	 */
	public MainUI() {
		//监听窗口关闭事件
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				// 关闭连接
				try {
					dis.close();
					dos.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		setTitle("\u804A\u5929\u5BA4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 489, 472);
		contentPane = new JPanel();
		contentPane.setForeground(Color.CYAN);
		contentPane.setBackground(Color.BLUE);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		
		send = new JButton("\u53D1\u9001");
		send.setAutoscrolls(true);
		send.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//向服务器发送信息
				sendMsg();
			}
		});
		
		send.setForeground(Color.BLACK);
		send.setFont(new Font("仿宋", Font.BOLD, 13));
		send.setBackground(Color.CYAN);
		
		showMsg = new JTextArea();
		showMsg.setEditable(false);
		
		inputMsg = new JTextField();
		inputMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//向服务器发送信息
				sendMsg();
			}
		});
		inputMsg.setColumns(10);
		
		nameLable = new JLabel("\u6635\u79F0\uFF1A");
		nameLable.setFont(new Font("宋体", Font.PLAIN, 15));
		nameLable.setBackground(Color.CYAN);
		
		inputName = new JTextField();
		inputName.setColumns(10);
		
		changeNameButton = new JButton("\u63D0\u4EA4");
		//提交呢称
		changeNameButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//向服务器提交昵称
				try {
					dos.writeUTF("#"+inputName.getText());
//					inputMsg.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		changeNameButton.setFont(new Font("隶书", Font.BOLD, 12));
		changeNameButton.setForeground(Color.BLACK);
		
		userList = new JList<String>();
		//用户列表监听事件
		userList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//点击选项后所作的事
				//比如私聊
				if (!e.getValueIsAdjusting()) {  //避免被执行多次
					
				} else {

				}
			}
		});
		userList.setValueIsAdjusting(true);
		userList.setVisibleRowCount(18);
		
		onLineUserLable = new JLabel("\u5728\u7EBF\u7528\u6237\uFF1A");
		onLineUserLable.setFont(new Font("方正姚体", Font.BOLD, 12));
		onLineUserLable.setForeground(Color.RED);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
							.addComponent(inputMsg)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(nameLable)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(inputName, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)))
						.addComponent(showMsg, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(onLineUserLable, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
						.addComponent(changeNameButton, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
						.addComponent(userList, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
						.addComponent(send, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
					.addGap(33))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(22)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(nameLable, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(changeNameButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(inputName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(onLineUserLable, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(userList, GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE))
						.addComponent(showMsg, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE))
					.addGap(35)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(send)
						.addComponent(inputMsg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
		//连接服务端
		init();		
	}
}

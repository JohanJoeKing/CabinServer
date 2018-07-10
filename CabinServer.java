import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * TODO Project information
 * Project : CabinServer
 * Filename : CabinServer.java
 * Author : Liu Chang
 * Student ID : 20153178[SIST.STDU]
 * Date : 2018/7/8
 * Version : 1.0
 * Description : A screen server use by train administrater
 * 				 to launch information of the train.
 * 
 */

public class CabinServer extends JFrame{

	// TODO Attributes
	private String nextStop;
	private String arriveTime;
	private String train;
	@SuppressWarnings("unused")
	private String routine;
	private String terminal;
	private String terminus;
	private int stops;         // amount of stops
	private int cstop = 0;     // current stop
	private int stateStop = 0; // state stop after launching
	private final int MAX_STOP = 50;  // max stops of a routine
	private String[] stop;     // stops
	private String[] time;     // arrive times
	private String[] off;      // people who will get off of one cabin
	private String[] on;       // people who will get on of one cabin
	
	// TODO Views
	private Container con;      // container
	private JPanel pan;         // panel for panels
	private JPanel pan1;        // panel for labels
	private JPanel pan2;        // panel for buttons
	private JLabel sNextStop;   // next stop
	private JLabel sArriveTime; // arrive time
	private JLabel sTrain;      // train
	private JLabel sRoutine;    // routine
	private JButton bLastStop;  // last stop
	private JButton bNextStop;  // next stop
	private JButton bReady;     // be ready
	private JButton bLaunch;    // launch information
	private JButton bReload;    // reload train information
	private JButton bReset;     // reset the printing tips
	
	// TODO supports
	private Color PAN_COLOR = Color.GRAY;      // color of 'pan'
	private Color PAN1_COLOR = Color.WHITE;    // color of 'pan1' and 'pan2'
	private Color WORD_COLOR = Color.BLUE;     // color of words
	private final String INTERVAL = "         ";   // Interval between string and board
	private final String IP = "192.168.43.44";     // client IP
	
	private static final long serialVersionUID = 1L;

	/**
	 * Function name : TODO CabinServer
	 * Description : construction method
	 * Variables : void
	 */
	CabinServer(){
		
		// initial frame
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(50,50,500,400);
		this.setTitle("Train Information Platform - Uganda Railway Bureau");
		con = this.getContentPane();
		
		// initial views
		// initial panels
		pan = new JPanel();
		pan.setLayout(new GridLayout(2,1,0,10));
		pan.setBackground(PAN_COLOR);
		
		pan1 = new JPanel();
		pan1.setLayout(new GridLayout(4,1));
		pan1.setBackground(PAN1_COLOR);
		
		pan2 = new JPanel();
		pan2.setLayout(new GridLayout(3,2));
		pan2.setBackground(PAN1_COLOR);
		
		// initial labels
		sNextStop = new JLabel(INTERVAL + "Next stop:");
		sArriveTime = new JLabel(INTERVAL + "Arrive time:");
		sTrain = new JLabel(INTERVAL + "Train");
		sRoutine = new JLabel(INTERVAL + "Routine:");
		sNextStop.setFont(new Font("Arial",0,20));
		sArriveTime.setFont(new Font("Arial",0,20));
		sTrain.setFont(new Font("Arial",0,20));
		sRoutine.setFont(new Font("Arial",0,20));
		sNextStop.setForeground(WORD_COLOR);
		sArriveTime.setForeground(WORD_COLOR);
		sTrain.setForeground(WORD_COLOR);
		sRoutine.setForeground(WORD_COLOR);
		
		// initial buttons
		bLastStop = new JButton("LAST STOP");
		bNextStop = new JButton("NEXT STOP");
		bReady = new JButton("BE READY");
		bLaunch = new JButton("LAUNCH");
		bReload = new JButton("RELOAD");
		bReset = new JButton("RESET");
		bLastStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_lastStop();
			}
		});
		bNextStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_nextStop();
			}
		});
		bReady.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_ready();
			}
		});
		bLaunch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_launch();
			}
		});
		bReload.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_reload();
			}
		});
		bReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				button_reset();
			}
		});
		
		// add views to panels
		// pan1
		pan1.add(sNextStop);
		pan1.add(sArriveTime);
		pan1.add(sTrain);
		pan1.add(sRoutine);
		
		// pan2
		pan2.add(bLastStop);
		pan2.add(bNextStop);
		pan2.add(bReady);
		pan2.add(bLaunch);
		pan2.add(bReload);
		pan2.add(bReset);
		
		// pan and container
		pan.add(pan1);
		pan.add(pan2);
		con.add(pan);
		
		// let the frame visible
		this.setVisible(true);
		
		// initial attributes
		stop = new String[MAX_STOP];
		time = new String[MAX_STOP];
		off = new String[MAX_STOP];
		on = new String[MAX_STOP];
		
	}
	
	/**
	 * Function name : TODO showInformation
	 * Description : show current train's information
	 * Variables : void
	 */
	private void showInformation(){
		sNextStop.setText(INTERVAL + "Next stop:" + INTERVAL + stop[cstop]);
		nextStop = stop[cstop];
		sArriveTime.setText(INTERVAL + "Arrive time:" + INTERVAL + time[cstop]);
		arriveTime = time[cstop];
		sTrain.setText(INTERVAL + "Train:" + INTERVAL + train);
		sRoutine.setText(INTERVAL + "Routine:" + INTERVAL + stop[0] + " - " + stop[stops - 1]);
		terminal = stop[0];
		terminus = stop[stops - 1];
	}
	
	/**
	 * Function name : TODO button_lastStop
	 * Description : button method
	 * Variables : void
	 */
	private void button_lastStop(){
		
		// dec 'cstop'
		cstop--;
		
		// show the first stop
		showInformation();
		
		// close and open some views
		bNextStop.setEnabled(true);
		if(cstop == 0){
			bLastStop.setEnabled(false);
		}
		
	}

	/**
	 * Function name : TODO button_nextStop
	 * Description : button method
	 * Variables : void
	 */
	private void button_nextStop(){
		
		// inc 'cstop'
		cstop++;
				
		// show the first stop
		showInformation();
				
		// close and open some views
		bLastStop.setEnabled(true);
		if(cstop == stops - 1){
			bNextStop.setEnabled(false);
		}
				
	}
	
	/**
	 * Function name : TODO button_ready
	 * Description : button method
	 * Variables : void
	 */
	private void button_ready(){
		
		// launch a twinkle signal to client
		launchData(8, "", 3460);
		
	}
	
	/**
	 * Function name : TODO button_launch
	 * Description : button method
	 * Variables : void
	 */
	private void button_launch(){
		
		// save the current stop
		stateStop = cstop;
		
		// launch next stop
		for(int i = 0;i < 5;i++){
			launchData(1, nextStop, 3461);
			launchData(2, arriveTime, 3462);
			launchData(3, off[cstop], 3463);
			launchData(4, on[cstop], 3464);
			launchData(5, train, 3465);
			launchData(6, terminal, 3466);
			launchData(7, terminus, 3467);
			launchData(7, "3", 3450);
		}
		
		
	}
	
	/**
	 * Function name : TODO button_reload
	 * Description : button method
	 * Variables : void
	 */
	private void button_reload(){
		
		try{
			// open file
			String pathname = "H:\\stop.txt";
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            @SuppressWarnings("resource")
            
            // read file
			BufferedReader br = new BufferedReader(reader);
            String line = "";
            int c = 0;  // read counter
            cstop = 0;
            
            // get stops and train
            line = br.readLine(); 
            stops = Integer.parseInt(line);
            line = br.readLine();
            train = line;
            
            // read stops' information
            while (line != null) {  
                line = br.readLine();
                if(c == 0){
                	c++;
                	stop[cstop] = line;
                }
                else if(c == 1){
                	c++;
                	time[cstop] = line;
                }
                else if(c == 2){
                	c++;
                	off[cstop] = line;
                }
                else if(c == 3){
                	c = 0;
                	on[cstop] = line;
                	cstop++;
                }
            }
		}
		catch(Exception e){
			@SuppressWarnings("unused")
			int c = JOptionPane.showConfirmDialog(null, "Can not open the aim file.", "Error", JOptionPane.YES_OPTION);
		}
		
		// close some views
		bLastStop.setEnabled(false);
		
		// show the first stop
		cstop = 0;
		showInformation();
		
	}
	
	/**
	 * Function name : TODO button_reset
	 * Description : button method
	 * Variables : void
	 */
	private void button_reset(){
		
		// return to state stop
		cstop = stateStop;
		
		// show
		showInformation();
		
	}
	
	/**
	 * Function name : TODO launchData
	 * Description : launch data to client
	 * Variables : int command, String msg
	 */
	private void launchData(int command, String msg, int port){
		/*
		 * The first character of a string replaces the number
		 * of one kind of label, such as 1 means 'nextStation'.
		 * 
		 * List:
		 * 1 - nextStation
		 * 2 - arriveTime
		 * 3 - getOff
		 * 4 - getOn
		 * 5 - train
		 * 6 - terminal
		 * 7 - terminus
		 * 8 - twinkle
		 * 9 - cabin
		 * 
		 */
		try {
			// open a socket
			String str = command + msg;
			DatagramSocket socket;
			socket = new DatagramSocket();
			
			// generate a packet
			byte[] buf = str.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(IP), port);
			
			// send data
			System.out.println("Send[" + IP + "|" + port + "] : " + str);
			socket.send(packet);
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Function name : TODO main
	 * Description : main method
	 * Variables : String[] args
	 */
	public static void main(String[] args) {
		
		@SuppressWarnings("unused")
		CabinServer CS = new CabinServer();
		
	}

}

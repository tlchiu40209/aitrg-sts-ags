import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WEcAGSED {
	private JFrame frmAgsPerformanceTest;
	private JTextField txtTimes;
	private JTextField txtElapse;
	private JLabel lblStatus;
	private JComboBox cbxAESK;
	private JTextField txtFilesize;
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WEcAGSED window = new WEcAGSED();
					window.frmAgsPerformanceTest.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WEcAGSED() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAgsPerformanceTest = new JFrame();
		frmAgsPerformanceTest.setTitle("AGS Performance Test");
		frmAgsPerformanceTest.setBounds(100, 100, 450, 300);
		frmAgsPerformanceTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmAgsPerformanceTest.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblAes = new JLabel("AES:");
		lblAes.setBounds(22, 78, 46, 15);
		panel.add(lblAes);
		
		cbxAESK = new JComboBox();
		cbxAESK.setModel(new DefaultComboBoxModel(new String[] {"SIV_128", "SIV_256", "GCM_128", "GCM_256"}));
		cbxAESK.setBounds(114, 75, 213, 21);
		panel.add(cbxAESK);
		
		JLabel lblTimes = new JLabel("Times");
		lblTimes.setBounds(22, 128, 46, 15);
		panel.add(lblTimes);
		
		txtTimes = new JTextField();
		txtTimes.setBounds(114, 125, 213, 21);
		panel.add(txtTimes);
		txtTimes.setColumns(10);
		
		JButton btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread execute = new Thread() {
					public void run() {
						try {
							lblStatus.setText("Running");
							SecureRandom sr = new SecureRandom();
							int fileSize = Integer.parseInt(txtFilesize.getText());
							int testTimes = Integer.parseInt(txtTimes.getText());
							int selectedKeySpec = cbxAESK.getSelectedIndex();
							int keySize;
							if (selectedKeySpec == 1 || selectedKeySpec ==3) {
								keySize = 256;
							} else {
								keySize = 128;
							}
							EasyAES eaes = new EasyAES(keySize);
							
							byte[] encrypt;
							byte[] decrypt;
							byte[] original = new byte[fileSize];
							long msbefore;
							long msafter;
							
							long encTotalElapse = 0;
							long decTotalElapse = 0;
							
							if (selectedKeySpec < 2) {
								for (int i = 0; i < testTimes; i++) {
									msbefore = getCurrentTime();
									encrypt = eaes.SIV_encrypt(original);
									msafter = getCurrentTime();
									encTotalElapse = encTotalElapse + (msafter - msbefore);
									msbefore = getCurrentTime();
									decrypt = eaes.SIV_decrypt(encrypt);
									msafter = getCurrentTime();
									decTotalElapse = decTotalElapse + (msafter - msbefore);
								}
							} else {
								encrypt = eaes.GCM_encrypt(original);
								for (int i = 0; i < testTimes; i++) {
									encTotalElapse = encTotalElapse + eaes.GCM_encrypt_test(original);
									decTotalElapse = decTotalElapse + eaes.GCM_decrypt_test(encrypt);
								}
							}
							float encResult = (float)encTotalElapse / (float)testTimes;
							float decResult = (float)decTotalElapse / (float)testTimes;
							String displayString = "AVENC: " + encResult + " ms / AVDEC: " + decResult + " ms";
							txtElapse.setText(displayString);
							lblStatus.setText("Ready");
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				execute.start();
				
			}
		});
		btnExecute.setBounds(337, 228, 87, 23);
		panel.add(btnExecute);
		
		JLabel lblElapse = new JLabel("Elapse");
		lblElapse.setBounds(22, 176, 46, 15);
		panel.add(lblElapse);
		
		txtElapse = new JTextField();
		txtElapse.setBounds(114, 173, 213, 21);
		panel.add(txtElapse);
		txtElapse.setColumns(10);
		
		lblStatus = new JLabel("Ready");
		lblStatus.setBounds(22, 232, 46, 15);
		panel.add(lblStatus);
		
		JLabel lblFilesize = new JLabel("Filesize");
		lblFilesize.setBounds(22, 32, 46, 15);
		panel.add(lblFilesize);
		
		txtFilesize = new JTextField();
		txtFilesize.setBounds(114, 29, 213, 21);
		panel.add(txtFilesize);
		txtFilesize.setColumns(10);
		
		JLabel lblBytes = new JLabel("Bytes");
		lblBytes.setBounds(358, 32, 46, 15);
		panel.add(lblBytes);
	}
	
	public static long getCurrentTime() {
		Date today = new Date();
		return today.getTime();
	}

}

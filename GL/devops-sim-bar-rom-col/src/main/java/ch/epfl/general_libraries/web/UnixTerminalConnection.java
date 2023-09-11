package ch.epfl.general_libraries.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ch.epfl.general_libraries.clazzes.ParamName;

public class UnixTerminalConnection {
	
	public static void main(String[] args) {
		UnixTerminalConnection connection = new UnixTerminalConnection("128.59.65.188", "minyee", "SimPhoenix");
		try {
			String[] resp = connection.sendCommandAndWaitForPrompt("cd test_seb");
			System.out.print(resp[0]);
			System.out.println(resp[1]);
			connection.uploadFile("/home/minyee/test_seb", "sst-test.py");
			resp = connection.sendCommandAndWaitForPrompt("sst sst-test.py");
			System.out.print(resp[0]);
			System.out.println(resp[1]);			
			connection.exitShell();
			connection.terminate();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private long TIMEOUTNS = 3000000000l;
	
	public void terminate() {
		this.session.disconnect();
	}

	public void exitShell() {
		this.channelShell.disconnect();
	}

	// config - input
	private String ip;
	private String user;
	private String password;
	
	// state variables
	private String prompt;
	private ChannelShell channelShell;
	private Session session;
	
	public UnixTerminalConnection(
			@ParamName(name="Machine IP", default_="192.168.1.103") String ip,
			@ParamName(name="user", default_="sebastien") String user,
			@ParamName(name="password") String pass) {
		this.ip = ip;
		this.user = user;
		this.password = pass;
	}
	
	public void uploadFile(String workingDir, String pathToFileToTransfer) {
		try {
			if (channelShell != null) {
				channelShell.disconnect();
			}
			if (session == null) {
				createSession();
			}
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;
			channelSftp.cd(workingDir);

			File f1 = new File(pathToFileToTransfer);
			channelSftp.put(new FileInputStream(f1), f1.getName(), ChannelSftp.OVERWRITE);

			channelSftp.exit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String[] sendCommandAndWaitForPrompt(String command) throws JSchException, IOException, InterruptedException {
		if (prompt == null || channelShell == null || !channelShell.isConnected()) {
			connectAndIdentifyPrompt();
		}
		if (channelShell == null) throw new IOException("Channel inexistant, problem somewhere");
		OutputStreamWriter osw = new OutputStreamWriter(channelShell.getOutputStream());
		InputStream is = channelShell.getInputStream();
		osw.write(command);
		osw.flush();
	//	Thread.sleep(100);
		depleteSimple(is, command);	
		osw.write("\n");
		osw.flush();
		return depleteConsole(is);
	}
	
	private void depleteSimple(InputStream in, String end) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		byte[] tmp=new byte[1024];
		long time = System.nanoTime();
		while(System.nanoTime() - time < TIMEOUTNS){
			while(in.available()>0){
				int i=in.read(tmp, 0, 1024);
				if(i<0)break;
				sb.append(new String(tmp, 0, i));
			}
			if(sb.toString().replaceAll(" \\r","").endsWith(end)){
				System.out.print(sb.toString());
				return;
			}
			try{Thread.sleep(50);}catch(Exception ee){}
		}

	}
	
	private String[] depleteConsole(InputStream inReader) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		StringBuilder line = new StringBuilder();
		int skip = 0;
		while(true) {
			if (inReader.available() == 0) {
				Thread.sleep(50);
			}
			
			int b = inReader.read();
			if (b >= 0) {
				if (skip == 1 && b == 75) {
				//	char cc = (char)b;
				//	System.out.println(cc);
					skip++;
				}
				if (skip > 0) {
					skip--;
					continue;
				}
				if (b == 27) {
					skip = 2;
					continue;
				}
				char c = (char)b;
				if (c == '$') {
					line.append(c);
					if (line.toString().startsWith(prompt)) {
						return new String[]{sb.toString(), line.toString()};
					}
				} else if (c == '\r') {
					sb.append(line);
					sb.append(c);
					line = new StringBuilder();
				} else if (c == '\n') {
					sb.append(c);
				} else {
					line.append(c);
				}			
			}
		}		
	//	System.out.print("Should not be there");
	//	return sb.toString();

	}	
	
	private void createSession() throws JSchException {
		JSch jsch = new JSch();
		session = jsch.getSession(user, ip);
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		session.setPassword(password);
		session.connect(1000);		
	}
	
	public void connectAndIdentifyPrompt() throws JSchException, IOException, InterruptedException {
		if (session == null) createSession();

		channelShell = (ChannelShell) session.openChannel("shell");
		channelShell.setPtySize(600, 600, 4800, 4800);
		OutputStreamWriter osw = new OutputStreamWriter(channelShell.getOutputStream());
		InputStreamReader inReader = new InputStreamReader(channelShell.getInputStream());
		channelShell.connect();	

		while(channelShell.isConnected() == false) {}


		findPrompt(inReader, osw);
	}
	
	private void findPrompt(InputStreamReader inReader, OutputStreamWriter outputStream) throws IOException, InterruptedException {
		outputStream.write("\n");
		outputStream.flush();
		while(inReader.ready() == false) {
			Thread.sleep(100);
		}
		StringBuilder sb = new StringBuilder();
		long time = System.nanoTime();
		long timeOut = 30000000000l; //  3sec
		while (this.prompt == null && (System.nanoTime() - time) < timeOut) {
			while(inReader.ready()) {
				char c = (char)inReader.read();
				if (c == '\r') {
					prompt = analyzeLine(sb.toString());
					if (prompt != null) {
						return;
					} else {
						sb = new StringBuilder();
					}
				} else {
					sb.append(c);
				}
			}
		}
	}
	
	private static Pattern unixPrompt = Pattern.compile("(\\n)([^@]+)@([^:]+):([^\\$]*)\\$"); 

	private String analyzeLine(String string) {
		Matcher m = unixPrompt.matcher(string);
		boolean found = m.find();
		if (found) {
			int groups = m.groupCount();
			String[] gr = new String[groups];
			for (int i = 0 ; i < groups ; i++) {
				gr[i] = m.group(i);
			}
			return gr[2] + "@" + gr[3] + ":";
		} else {
			return null;
		}
	}
	
	public File downloadFile(String pre, String rfile, String localFile) throws JSchException, IOException {
		String prefix=null;
		if(new File(rfile).isDirectory()){
			prefix=rfile+File.separator;
		}
		
		// exec 'scp -f rfile' remotely
		String command="scp -f "+ pre + rfile;
		Channel channel=session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();

		channel.connect();

		byte[] buf=new byte[1024];

		// send '\0'
		buf[0]=0; out.write(buf, 0, 1); out.flush();
		File localF = null;

		while(true){
			int c=checkAck(in);
			if(c!='C'){
				break;
			}

			// read '0644 '
			in.read(buf, 0, 5);

			long filesize=0L;
			while(true){
				if(in.read(buf, 0, 1)<0){
					// error
					break; 
				}
				if(buf[0]==' ')break;
				filesize=filesize*10L+(long)(buf[0]-'0');
			}

			String file=null;
			for(int i=0;;i++){
				in.read(buf, i, 1);
				if(buf[i]==(byte)0x0a){
					file=new String(buf, 0, i);
					break;
				}
			}

			//System.out.println("filesize="+filesize+", file="+file);

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();

			// read a content of lfile
			localF = new File(localFile);
			FileOutputStream fos=new FileOutputStream(localF);
			int foo;
			while(true){
				if(buf.length<filesize) foo=buf.length;
				else foo=(int)filesize;
				foo=in.read(buf, 0, foo);
				if(foo<0){
					// error 
					break;
				}
				fos.write(buf, 0, foo);
				filesize-=foo;
				if(filesize==0L) break;
			}
			fos.close();
			fos=null;

			if(checkAck(in)!=0){
				throw new IllegalStateException("Problem in file download");
			}

			// send '\0'
			buf[0]=0; out.write(buf, 0, 1); out.flush();
		}

		session.disconnect();
		return localF;
	}

	static int checkAck(InputStream in) throws IOException{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

}

package woogle.cky;

import java.io.*;

public class MyFileReader {
	
	private BufferedReader in;
	public int lineno;
	private String next;
	private File file;
	
	public MyFileReader(File file) {
		try {
			this.file = file;
			System.out.println("���ļ�:" + file.getAbsolutePath());
        	in = new BufferedReader(new FileReader(file));
        	next = in.readLine();
        	lineno = 0;
		} catch (Exception e) {
			System.err.println("�޷���:" + file.getAbsolutePath());
            e.printStackTrace();
        }
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.in.close();
		super.finalize();
	}

	public String nextLine() {
		String current = next;
		try {
			next = in.readLine();
			lineno ++;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("��ȡ������:" + lineno);
		}
		return current;
	}

	public boolean hasNext() {
		return next != null;
	}
	
	public void close() {
		try {
			System.out.println("�ر��ļ�:" + this.file.toString());
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

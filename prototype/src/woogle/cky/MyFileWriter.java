package woogle.cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class MyFileWriter {
	private StringBuffer sb;
	private PrintStream writer;
	private int senno;
	public static int THRESHOLD = 1000;
	private File file;
	
	public MyFileWriter(File file) {
		try {
			this.file = file;
			System.out.println("���ļ�:" + file.toString());
			this.writer = new PrintStream(file);
			this.sb = new StringBuffer();
			this.senno = 0;
		} catch (FileNotFoundException e) {
			System.err.println("�޷���:" + file.toString());
            e.printStackTrace();
		}
	}
	
	public void print(Object o) {
		this.sb.append(o.toString());
		this.senno ++;
		if (senno % THRESHOLD == 0) {
			this.writer.print(this.sb);
			this.sb = new StringBuffer();
		}
	}
	
	public void println(Object o) {
		this.print(o.toString() + "\n");
	}
	
	public void println() {
		this.print("\n");
	}
	
	public void close() {
		if (sb.length() != 0)
			this.writer.print(sb);
		System.out.println("�ر��ļ�:" + this.file.toString());
		this.writer.close();
	}
}

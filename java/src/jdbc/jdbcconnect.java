package jdbc;

import java.sql.*;

public class jdbcconnect {
	public static void main(String[] args){
		Godown godown = new Godown(50); 
        Consumer c1 = new Consumer(1,50, godown); 
        Consumer c2 = new Consumer(2,20, godown); 
        Consumer c3 = new Consumer(3,30, godown); 
        Producer p1 = new Producer(4,10, godown); 
        Producer p2 = new Producer(5,10, godown); 
        Producer p3 = new Producer(6,10, godown); 
        Producer p4 = new Producer(7,10, godown); 
        Producer p5 = new Producer(8,10, godown); 
        Producer p6 = new Producer(9,10, godown); 
        Producer p7 = new Producer(10,80, godown); 

        c1.start(); 
        c2.start(); 
        c3.start(); 
        p1.start(); 
        p2.start(); 
        p3.start(); 
        p4.start(); 
        p5.start(); 
        p6.start(); 
        p7.start(); 
	}
}

class Godown { 
    public static final int max_size = 100; //������� 
    public int curnum;     //��ǰ����� 
    Godown() { 
    } 
    Godown(int curnum) {
    	Connection conn = null;
    	try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("�޷���������");
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/procons?characterEncoding=utf8&useSSL=false","root","123456");
		} catch (SQLException e) {
			System.out.println("�޷��������ݿ�");
			e.printStackTrace();
		}
        try {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate("insert into storage (id,num,sum) values (0,0," + curnum + ")");
    		stmt.close();
    		conn.close();
    	} catch (SQLException e) {
   			System.out.println("�޷������ʼ������");
   			e.printStackTrace();
   		}
        System.out.println(0 + ".Ŀǰ���Ϊ:" + curnum);
        this.curnum = curnum;
    }
    
    public synchronized void produce(int id, int neednum) { 
            //�����Ƿ���Ҫ���� 
        while (neednum + curnum > max_size) { 
            System.out.println("Ҫ�����Ĳ�Ʒ����" + neednum + "����ʣ������" + (max_size - curnum) + "����ʱ����ִ����������!"); 
            try {  
                wait(); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        } 
      
        //�����������������������������򵥵ĸ��ĵ�ǰ����� 
        curnum += neednum; 
        Connection conn = null;
      	try {
    		Class.forName("org.gjt.mm.mysql.Driver");
    	} catch (ClassNotFoundException e) {
   		System.out.println("�޷���������");
    		e.printStackTrace();
    	}
    	try {
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/procons?characterEncoding=utf8&useSSL=false","root","123456");
   		} catch (SQLException e) {
    		System.out.println("�޷��������ݿ�");
    		e.printStackTrace();
    	}
        try {
       		Statement stmt = conn.createStatement();
       		stmt.executeUpdate("insert into storage(id,num,sum) values("+ id +","+ neednum +","+ curnum +")");
       		stmt.close();
       		conn.close();
       	} catch (SQLException e) {
       		System.out.println("�޷�������������");
       	}
        System.out.println(id + ".�Ѿ�������" + neednum + "����Ʒ���ֲִ���Ϊ" + curnum);
        //�����ڴ˶���������ϵȴ��������߳� 
        notifyAll(); 
    } 
    public synchronized void consume(int id, int neednum) { 
            //�����Ƿ������ 
        while (curnum < neednum) { 
            try { 
                           //��ǰ�������̵߳ȴ� 
                wait(); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        } 

            //����������������������ѣ�����򵥵ĸ��ĵ�ǰ����� 
        curnum -= neednum; 
        Connection conn = null;
       	try {
    		Class.forName("org.gjt.mm.mysql.Driver");
    	} catch (ClassNotFoundException e) {
    		System.out.println("�޷���������");
    		e.printStackTrace();
    	}
    	try {
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/procons?characterEncoding=utf8&useSSL=false","root","123456");
    	} catch (SQLException e) {
    		System.out.println("�޷��������ݿ�");
    		e.printStackTrace();
    	}
        try {
        	Statement stmt = conn.createStatement();
        	stmt.executeUpdate("insert into storage (id,num,sum) values (" + id + "," + (-neednum) + "," + curnum + ")");
        	stmt.close();
        	conn.close();
        } catch (SQLException e) {
       		System.out.println("�޷�������������");
       	}
        System.out.println(id + ".�Ѿ�������" + neednum + "����Ʒ���ֲִ���Ϊ" + curnum); 
            //�����ڴ˶���������ϵȴ��������߳� 
        notifyAll(); 
    } 
} 

class Producer extends Thread {
	private int id;
    private int neednum;                //������Ʒ������ 
    private Godown godown;            //�ֿ� 

    Producer(int id, int neednum, Godown godown) {
    	this.id = id;
        this.neednum = neednum; 
        this.godown = godown; 
    } 

    public void run() { 
            //����ָ�������Ĳ�Ʒ 
            godown.produce(id,neednum); 
    } 
} 

class Consumer extends Thread { 
	private int id;
    private int neednum;                //������Ʒ������ 
    private Godown godown;            //�ֿ� 

    Consumer(int id, int neednum, Godown godown) { 
    	this.id = id;
        this.neednum = neednum; 
        this.godown = godown; 
    } 

    public void run() { 
            //����ָ�������Ĳ�Ʒ 
            godown.consume(id,neednum); 
    } 
}

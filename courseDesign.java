package test1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.Scanner;

import RSA.RSA;

public class courseDesign {

	public static void main(String[] args) throws IOException {
		RSA rsa=new RSA(512,10);
		Scanner cin=new Scanner(System.in);
		System.out.println("������Ҫ���ܵ��ļ�·��:");
		String s=cin.next();
		System.out.println("������ܽ����ŵ��ļ���·��:");
		String ss=cin.next();
		FileInputStream fis=new FileInputStream(new File(s));
		FileOutputStream fos=new FileOutputStream(new File(ss));
		System.out.println("������...");
		byte[]tmp=new byte[32];
		int num;
		BigInteger data;
		while((num=fis.read(tmp))!=-1){
			data=BinaryToDecimal(ByteToBinary(tmp,num));
			data=rsa.encrypt(data);
			String t=data.toString();
			byte []b=new byte[t.length()];
			for(int i=0;i<t.length();i++){
				b[i]=(byte) (t.charAt(i));
			}
			
			fos.write(b, 0, t.length());
			fos.write(13);
			fos.write(10);
		}
		System.out.println("�������!");
		fos.close();
		fis.close();
		
		System.out.println("������ܵ��ļ�·��:");
		String p=cin.next();
		System.out.println("������ܽ����ŵ��ļ�·��:");
		String pp=cin.next();
		System.out.println("������...");
		FileOutputStream fts=new FileOutputStream(new File(pp));
		
		BufferedReader br=new BufferedReader(new FileReader(new File(p)));
		String buf;
		while((buf=br.readLine())!=null){
			data=new BigInteger(buf);
			deal(rsa.decrypt(data),fts);
		}
		System.out.println("�������!");
		br.close();
		fts.close();
		
	}
	
	/*
	 * ��������:���ļ����ܺ󣬶Եõ������ݽ��д���������ļ���
	 * �������:������s,�ļ������fos
	 * �������:��
	 */
	public static void deal(BigInteger s,FileOutputStream fos) throws IOException{
		BigInteger two=BigInteger.ONE.add(BigInteger.ONE);
		int tmp=0;
		int factor=1;
		BigInteger t=s;
		int num=0,pos=0;
		byte[]b=new byte[32];
		while(!t.equals(BigInteger.ZERO)){
			if(t.mod(two).equals(BigInteger.ONE))tmp+=factor;
			factor*=2;
			t=t.divide(two);
			pos++;
			if(pos==8){
				b[num++]=(byte)tmp;
				tmp=0;
				factor=1;
				pos=0;
			}
		}
		if(pos!=8&&pos!=0)b[num++]=(byte)tmp;
		byte bb[]=new byte[32];
		for(int i=0;i<num;i++){
			bb[i]=b[num-1-i];
		}
		fos.write(bb, 0, num);
	}
	
	/*
	 * ��������:��������תΪʮ����
	 * �������:�����Ƶ��ַ�����ʽ
	 * �������:��Ӧ��ʮ������
	 */
	public static BigInteger BinaryToDecimal(String str){
		BigInteger two=BigInteger.ONE.add(BigInteger.ONE);
		BigInteger result=BigInteger.ZERO;
		BigInteger factor=BigInteger.ONE;
		for(int i=str.length()-1;i>=0;i--){
			if(str.charAt(i)=='1')result=result.add(factor);
			factor=factor.multiply(two);
		}
		//System.out.println("����ǰ�Ľ��:"+result);
		return result;
	}
	
	/*
	 * ��������:��byte�ö�������ʽ��ʾ
	 * �������:byte�����Ҫת����byte�ĸ���
	 * �������:byte�Ķ�������ʽ
	 */
	public static String ByteToBinary(byte[]b,int len){
		String str="";
		int a[]=new int[len];
		for(int i=0;i<len;i++){
			if(b[i]>=0)a[i]=b[i];
			else a[i]=(int)b[i]+256;
		}
		for(int i=0;i<len;i++){
			char c[]=new char[8];
			int pos=0;
			int t=a[i];
			while(t!=0){
				if(t%2==1)c[7-pos]='1';
				else c[7-pos]='0';
				t=(byte) (t/2);
				pos++;
			}
			while(pos!=8){
				c[7-pos]='0';
				pos++;
			}
			str+=new String(c);
		}
		return str;
	}
	
}

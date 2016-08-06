package RSA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class RSA {
	//˽Կ,p,qΪ����������,rm mod fpq=1��gcd(r,fpq)=1
	private BigInteger p,q,m;
	//��Կ
	public BigInteger r,n;
	//�ñ���Ϊ��p-1)*(q-1)
	private BigInteger fpq;
	
	BigInteger two=BigInteger.ONE.add(BigInteger.ONE);
	
	public RSA(int bitLength,int time)throws IOException{
		System.out.println("����1ѡ��������Կ���м���\n����2����������Կ���м���");
		Scanner cin=new Scanner(System.in);
		int choice=cin.nextInt();
		String pub,pri;
		cin.nextLine();
		if(choice==1){
			System.out.println("���빫Կ�����ļ�:");
			pub=cin.nextLine();
			System.out.println("����˽Կ�����ļ�:");
			pri=cin.nextLine();
			getKey(pub,pri);
		}else{
			System.out.println("�������ɹ�˽Կ...");
			generate_key(bitLength,time);
			System.out.println("���ɳɹ���");
			System.out.println("���빫Կ����λ��:");
			pub=cin.nextLine();
			System.out.println("����˽Կ����λ��:");
			pri=cin.nextLine();
			outputToFile(pub,pri);
		}
	}
	
	/*
	 * �������ܣ����ɹ�˽Կ
	 * �����������Կ�Ķ����Ƴ���,����һ�����Ƿ��������Ĵ���
	 * �����������
	 * �޸ĵı���:p,q,n,fpq,r,m
	 */
	public void generate_key(int bitLength,int time) throws IOException{
		p=generatePrime(bitLength,time);
		q=generatePrime(bitLength,time);
		while(p.equals(q)||!isPrime(q,time)){
			q=generatePrime(bitLength,time);
		}
		n=p.multiply(q);
		fpq=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		r=bigRandom(20);
		while(!r.gcd(fpq).equals(BigInteger.ONE)){
			r=bigRandom(20);
		}
		m=inverse(r,fpq);
	}
	/*
	 * ��������:���ļ��ж�ȡ��˽Կ
	 * �������:��Ź�˽Կ���ļ���
	 * �������:��
	 */
	public void getKey(String public_key,String private_key) throws IOException{
		BufferedReader br_public=new BufferedReader(new FileReader(new File(public_key)));
		BigInteger n=new BigInteger(br_public.readLine());
		BigInteger r=new BigInteger(br_public.readLine());
		br_public.close();
		setPublicKey(n,r);
		BufferedReader br_private=new BufferedReader(new FileReader(new File(private_key)));
		BigInteger p=new BigInteger(br_private.readLine());
		BigInteger q=new BigInteger(br_private.readLine());
		BigInteger m=new BigInteger(br_private.readLine());
		br_private.close();
		setPrivateKey(p,q,m);
	}
	/*
	 *��������:����Կ��˽Կ�ֱ𱣴浽�����ļ���ȥ
	 *�������:�����ļ����ļ���
	 *�������:�� 
	 */
	public void outputToFile(String public_key,String private_key) throws IOException{
		BufferedWriter bw_public=new BufferedWriter(new FileWriter(new File(public_key),true));
		BufferedWriter bw_private=new BufferedWriter(new FileWriter(new File(private_key)));
		bw_public.write(n.toString()+"\r\n");
		bw_public.write(r.toString()+"\r\n");
		bw_private.write(p.toString()+"\r\n");
		bw_private.write(q.toString()+"\r\n");
		bw_private.write(m.toString()+"\r\n");
		bw_public.close();
		bw_private.close();
	}
	/*
	 * �������ܣ�����������
	 * ��������������Ķ����Ƴ���,����һ�����Ƿ��������Ĵ���
	 * ��������������Ĵ�����
	 */
	public BigInteger generatePrime(int bitLength,int time){
		BigInteger p=bigRandom(bitLength);
		while(!isPrime(p,time)){
			p=bigRandom(bitLength);
		}
		return p;
	}
	
	/*
	 * �������ܣ�����һ���������
	 * ���������������Ķ�����λ��
	 * ��������������Ĵ������
	 */
	public BigInteger bigRandom(int bitLength){
		String s="";
		int randomNumber=1;
		s+=randomNumber+"";
		for(int i=1;i<bitLength;i++){
			randomNumber=(int)(Math.random()*2);
			s+=randomNumber+"";
		}
		BigInteger factor=BigInteger.ONE;
		BigInteger result=BigInteger.ZERO;
		for(int i=s.length()-1;i>=0;i--){
			if(s.charAt(i)=='1')result=result.add(factor);
			factor=factor.multiply(two);
		}
		return result;
	}
	
	/*
	 * �������ܣ�ʹ��Miller-Rabin�㷨�ж�һ�����Ƿ�������
	 * ������������жϵ���
	 * �������������������1�����Ƿ���0
	 */
	public boolean isPrime(BigInteger num,int time){
		int count=0;
		for(int i=0;i<time;i++){
			BigInteger t=num.subtract(BigInteger.ONE);
			BigInteger k=BigInteger.ZERO;
			BigInteger a;
			while(t.mod(two).equals(BigInteger.ZERO)){
				t=t.divide(two);
				k=k.add(BigInteger.ONE);
			}
			Random rn=new Random();
			while(true){
				int tmp=rn.nextInt();
				a=new BigInteger(tmp+"");
				if(a.compareTo(BigInteger.ZERO)==-1)continue;
				if(a.compareTo(num)==-1)break;
			}
			BigInteger b=quickMod(a,t,num);
			if(b.mod(num).equals(BigInteger.ONE)){
				count++;
				continue;
			}
			while(!k.equals(BigInteger.ZERO)){
				BigInteger b1,b2,b3;
				b1=b.mod(num);
				b2=BigInteger.ZERO.subtract(BigInteger.ONE);//-1
				b3=num.subtract(BigInteger.ONE);//n-1
				if(b1.equals(b2)||b1.equals(b3)){
					count++;
					break;
				}
				b=b.multiply(b).mod(num);
				k=k.subtract(BigInteger.ONE);
			}
		}
		if(time==count)return true;
		return false;
	}
	
	
	
	/*
	 * �������ܣ�ʵ��ģ�ظ�ƽ���㷨,��ָ������Ϊ10240λ����
	 * �������������a,��ָ��b��ģ��n
	 * ���������a^bmod n
	 */
	public BigInteger quickMod(BigInteger a,BigInteger b,BigInteger n){
		BigInteger tmp=b;
		BigInteger factor=a;
		BigInteger result=BigInteger.ONE;
		while(!tmp.equals(BigInteger.ZERO)){
			if(tmp.remainder(two).equals(BigInteger.ONE)){
				result=result.multiply(factor).mod(n);
			}
			factor=factor.multiply(factor).mod(n);
			tmp=tmp.divide(two);
		}
		return result;
	}
	
	/*
	 * ��������:��һ��������Ԫ
	 * �������:b,a(��Ҫ��b^-1moda)
	 * �������:��õ���Ԫ���粻������Ԫ����0
	 */
	public BigInteger inverse(BigInteger b,BigInteger a){
		BigInteger a0,b0,t0,t,q,r;
		a0=a;
		b0=b;
		t0=BigInteger.ZERO;
		t=BigInteger.ONE;
		q=a0.divide(b0);
		r=a0.subtract(q.multiply(b0));
		while(r.compareTo(BigInteger.ZERO)==1){
			BigInteger temp=t0.subtract(q.multiply(t)).mod(a);
			t0=t;
			t=temp;
			a0=b0;
			b0=r;
			q=a0.divide(b0);
			r=a0.subtract(q.multiply(b0));
		}
		if(!b0.equals(BigInteger.ONE))return BigInteger.ZERO;
		else return t;
	}
	/*
	 * �������ܣ������ݽ��м���
	 * �������������������
	 * ������������ܽ��
	 */
	public BigInteger encrypt(BigInteger data){
		return quickMod(data,r,n);
	}
	
	/*
	 * �������ܣ������ݽ��н���
	 * �������������
	 * �������������
	 */
	public BigInteger decrypt(BigInteger data){
		return quickMod(data,m,n);
	}
	
	/*
	 * �������ܣ�����˽Կ
	 * ���������������ָ��p,q�ͽ���ָ��m
	 * �����������
	 */
	private void setPrivateKey(BigInteger _p,BigInteger _q,BigInteger _m){
		p=_p;
		q=_q;
		m=_m;
	}
	/*
	 * �������ܣ����ù�Կ
	 * ���������n�ͼ���ָ��r
	 * �����������
	 */
	private void setPublicKey(BigInteger _n,BigInteger _r){
		n=_n;
		r=_r;
	}
}

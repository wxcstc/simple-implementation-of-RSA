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
	//私钥,p,q为两个大素数,rm mod fpq=1且gcd(r,fpq)=1
	private BigInteger p,q,m;
	//公钥
	public BigInteger r,n;
	//该变量为（p-1)*(q-1)
	private BigInteger fpq;
	
	BigInteger two=BigInteger.ONE.add(BigInteger.ONE);
	
	public RSA(int bitLength,int time)throws IOException{
		System.out.println("输入1选择现有密钥进行加密\n输入2现在生成密钥进行加密");
		Scanner cin=new Scanner(System.in);
		int choice=cin.nextInt();
		String pub,pri;
		cin.nextLine();
		if(choice==1){
			System.out.println("输入公钥所在文件:");
			pub=cin.nextLine();
			System.out.println("输入私钥所在文件:");
			pri=cin.nextLine();
			getKey(pub,pri);
		}else{
			System.out.println("正在生成公私钥...");
			generate_key(bitLength,time);
			System.out.println("生成成功！");
			System.out.println("输入公钥保存位置:");
			pub=cin.nextLine();
			System.out.println("输入私钥保存位置:");
			pri=cin.nextLine();
			outputToFile(pub,pri);
		}
	}
	
	/*
	 * 函数功能：生成公私钥
	 * 输入参数：密钥的二进制长度,检验一个数是否是素数的次数
	 * 输出参数：无
	 * 修改的变量:p,q,n,fpq,r,m
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
	 * 函数功能:从文件中读取公私钥
	 * 输入参数:存放公私钥的文件名
	 * 输出参数:无
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
	 *函数功能:将公钥和私钥分别保存到两个文件中去
	 *输入参数:两个文件的文件名
	 *输出参数:无 
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
	 * 函数功能：产生大素数
	 * 输入参数：素数的二进制长度,检验一个数是否是素数的次数
	 * 输出参数：产生的大素数
	 */
	public BigInteger generatePrime(int bitLength,int time){
		BigInteger p=bigRandom(bitLength);
		while(!isPrime(p,time)){
			p=bigRandom(bitLength);
		}
		return p;
	}
	
	/*
	 * 函数功能：产生一个大随机数
	 * 输入参数：随机数的二进制位数
	 * 输出参数：产生的大随机数
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
	 * 函数功能：使用Miller-Rabin算法判断一个数是否是素数
	 * 输入参数：待判断的数
	 * 输出参数：是素数返回1，不是返回0
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
	 * 函数功能：实现模重复平方算法,幂指数最大可为10240位数据
	 * 输入参数：底数a,幂指数b和模数n
	 * 输出参数：a^bmod n
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
	 * 函数功能:求一个数的逆元
	 * 输入参数:b,a(需要求b^-1moda)
	 * 输出参数:求得的逆元，如不存在逆元返回0
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
	 * 函数功能：对数据进行加密
	 * 输入参数：待加密数据
	 * 输出参数：加密结果
	 */
	public BigInteger encrypt(BigInteger data){
		return quickMod(data,r,n);
	}
	
	/*
	 * 函数功能：对数据进行解密
	 * 输入参数：密文
	 * 输出参数：明文
	 */
	public BigInteger decrypt(BigInteger data){
		return quickMod(data,m,n);
	}
	
	/*
	 * 函数功能：设置私钥
	 * 输入参数：两个大指数p,q和解密指数m
	 * 输出参数：无
	 */
	private void setPrivateKey(BigInteger _p,BigInteger _q,BigInteger _m){
		p=_p;
		q=_q;
		m=_m;
	}
	/*
	 * 函数功能：设置公钥
	 * 输入参数：n和加密指数r
	 * 输出参数：无
	 */
	private void setPublicKey(BigInteger _n,BigInteger _r){
		n=_n;
		r=_r;
	}
}

import java.io.IOException;
import java.text.DecimalFormat;

public class hits1432 {
	static int edges;
	static int vertices;
	static int[][] adjMtrx;
	//static int[][] trnsMtrx;
	static double[] hub;
	static double [] auth;
	static FileReader1432 fr;
	static int initialValue;
	static int iterations;
	static double[] prevHub;
	static double[] prevAuth;

	public static void main(String[] args) throws IOException {

		fr = new FileReader1432("l.txt");

		vertices = fr.getVerticeSize();
		adjMtrx = new int[vertices][vertices];

		edges = fr.getEdgeSize();
		
		//System.out.println("Edges: "+edges+" Vertices: "+vertices);

		adjMtrx = initMtrx(adjMtrx.length);

		initialValue = 1;
		iterations = 9;

		DecimalFormat numberFormat = new DecimalFormat("0.0000000");

		if (vertices > 10) {
			iterations = 0;
			initialValue = -1;

		}
		
		//printMtrx(adjMtrx);

		prevHub = prevAuth = new double[vertices];
		hub = auth = fillVector(vertices, initialValue, vertices);

		int i=0;
		while((iterations == 0 ? true : i!=iterations )) {	

			if(i == 0) {
				System.out.print("Base: 0 : ");
				for(int g=0; g<hub.length; g++) {
					System.out.print("A/H["+g+"] = "+numberFormat.format(Math.floor(auth[g] * 1e7)/1e7)+" / "
							+numberFormat.format(Math.floor(hub[g] * 1e7)/1e7)+" ");
				}
				System.out.println();
			}else {
				if(vertices < 11) {
					System.out.print("Iter: "+i+" :");
					for(int g=0; g<hub.length; g++) {
						System.out.print("A/H["+g+"] = "+numberFormat.format(Math.floor(auth[g] * 1e7)/1e7)+" / "
								+numberFormat.format(Math.floor(hub[g] * 1e7)/1e7)+" ");
						
					}
					System.out.println();
				}
			}

			prevAuth = auth;
			prevHub = hub;

			auth = computeAuth(auth, hub, adjMtrx);
			hub = computeHub(hub, auth, adjMtrx);		

			double[] dash = computeUVDash(hub,auth);//0=hub, 1=auth

			//System.out.println(dash[1]+"--"+dash[0]);

			auth = scaleMtrx(dash[1],auth);

			hub = scaleMtrx(dash[0],hub);

			if(vertices>10 && didItConverge((iterations == 0) ? 0 : i,prevAuth, prevHub, auth, hub)) break;
			i++;
		}

		if(vertices>10) {
			System.out.println("Iter: "+i+" :");
			for(int g=0; g<hub.length; g++) {
				System.out.println("A/H["+g+"] = "+numberFormat.format(Math.floor(auth[g] * 1e7)/1e7)+" / "
						+numberFormat.format(Math.floor(hub[g] * 1e7)/1e7)+" ");
			}
		}
		//printMtrx(trnsMtrx);
		fr.close();
		//printMtrx(adjMtrx);
	}

	static boolean didItConverge(int iterations, double[] prevA, double[] prevH, double[] au, double[] hu) {
		double errRate = 0;
		errRate = (iterations == 0)? 100000 : Math.pow(10, (iterations * -1));

		DecimalFormat numberFormat = new DecimalFormat("0.0000000");
		for (int i = 0; i < au.length; i++) {
			//System.out.println(au[i]+"--"+errRate+"--"+au[i]*errRate);
			//System.out.println((int)Math.floor(au[i] * errRate)+"--"+(int)Math.floor(prevA[i] * errRate));
			if ((int)Math.floor(au[i] * errRate) != (int)Math.floor(prevA[i] * errRate)) {
				return false;
			}
		}

		for (int i = 0; i < hu.length; i++) {
			//System.out.println((int)Math.floor(hu[i] * errRate)+"--"+(int)Math.floor(prevH[i] * errRate));
			if ((int)Math.floor(hu[i] * errRate) != (int)Math.floor(prevH[i] * errRate)) { 

				return false;
			}
		}

//		for(int g=0; g<hub.length; g++) {
//			System.out.println("A/H["+g+"] = "+numberFormat.format(Math.floor(auth[g] * 1e7)/1e7)+" / "
//					+numberFormat.format(Math.floor(hub[g] * 1e7)/1e7)+" ");
//		}
//		for(int g=0; g<prevAuth.length; g++) {
//			System.out.println("A/H["+g+"] = "+numberFormat.format(Math.floor(prevAuth[g] * 1e7)/1e7)+" / "
//					+numberFormat.format(Math.floor(prevHub[g] * 1e7)/1e7)+" ");
//		}
		//System.out.println("Converged!");
		return true;
	}

	static double[] computeHub(double[] mtrx, double[] mtrx2, int[][] adjMtrx) {
		double sum;
		double[] temp = new double[mtrx.length];
		for(int i=0; i <mtrx.length; i++) {
			//prevHub[i]=mtrx[i];
			sum = 0.0;
			for(int a=0; a<adjMtrx.length; a++) {
				if(adjMtrx[i][a] == 1)	{
					sum += mtrx2[a];
					//sum = Math.round((sum) * 10000000d) / 10000000d;
					//System.out.println("HUB>>>>>i>"+i+" sum>"+Math.round((sum) * 10000000d) / 10000000d+" a>"+a+" val>"+mtrx2[a]+" auth>"+auth[a]);
				}
			}
			temp[i] = sum;
		}	
		return temp;
	}

	static double[] computeAuth(double[] mtrx, double[] mtrx2, int[][] adjMtrx) {
		double sum;
		double[] temp = new double[mtrx.length];
		for(int i=0; i <mtrx.length; i++) {
			//prevAuth[i]=mtrx[i];
			sum = 0.0;
			for(int a=0; a<adjMtrx.length; a++) {
				if(adjMtrx[a][i] == 1) {
					sum += mtrx2[a];
					//sum = Math.round((sum) * 10000000d) / 10000000d;
					//System.out.println("AUTH>>>>i>"+i+" sum>"+Math.round((sum) * 10000000d) / 10000000d+" a>"+a+" val>"+mtrx2[a]+" auth>"+auth[a]);
				}
			}
			temp[i] =sum;
		}
		return temp;
	}



	static double[] scaleMtrx(double num, double[] mtrx) {
		int size = mtrx.length;
		double[] temp = new double[size];
		for(int i=0; i<size; i++) { 
			//System.out.println(mtrx[i]+"/"+num+"-->"+Math.round((mtrx[i] / num) * 10000000d) / 10000000d);
			//temp[i] = Math.round((mtrx[i] / num) * 10000000d) / 10000000d;
			temp[i] = mtrx[i] / num;
		}
		return temp;
	}

	static double[] computeUVDash(double[] u, double[] v) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		for(int i=0; i<u.length; i++) {
			sum1 += (double)Math.pow(u[i],2);
			//System.out.println("sum: "+sum1);
			sum2 += (v[i] * v[i]);
		}
		//System.out.println(sum1+" sum->"+Math.sqrt(sum1));
		return new double[] {Math.sqrt(sum1),Math.sqrt(sum2)};	
		//return new double[]{ Math.round((Math.sqrt(sum1)) * 10000000d) / 10000000d,
		//	Math.round((Math.sqrt(sum2)) * 10000000d) / 10000000d };
	}

	//	static int[][] transpose(int[][] mtrx, int size){
	//		int[][] trnsMtrx = new int[size][size];
	//		for(int i=0; i<size; i++) 
	//			for(int j=0; j<size; j++) 
	//				trnsMtrx[j][i] = mtrx[i][j];
	//		return trnsMtrx;
	//	}
	//
	//	static double[] mtrxMulti(double mtrx[], int mtrx2[][]) {
	//		int size = mtrx.length;
	//		double[] temp = new double[size];
	//		for(int i=0; i<size; i++) {
	//			int sum = 0;
	//			for(int j=0; j<size; j++) {
	//				sum += mtrx2[i][j] * mtrx[j];
	//			}
	//			temp[i] = sum;
	//		}	
	//		return temp;
	//	}

	static int[][] initMtrx(int size){
		//System.out.println(size);
		try {
			int i;
			int[][] mtrx = new int[size][size];
			while ( (i = fr.getNextValue()) != -1 ){ 
				int prev = i;
				//System.out.println(prev+"--"+i);
				if((i = fr.getNextValue()) != -1) {
					//System.out.println(prev+"--"+i);
					mtrx[prev][i] = 1;
					//mtrx[Integer.parseInt(((char)prev)+"")][Integer.parseInt((char)i+"")] = 1;
				}
				else break;
			}
			return mtrx;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static double[] fillVector(int size, int initValue, int vertices) {
		//System.out.println(initialValue);
		double temp = initValue;
		if(initValue == -1) {
			temp = (1/(double)vertices);
		}else if(initValue == -2) {
			temp = 1/(double)Math.sqrt(vertices);
		}
		double[] vec = new double[size];
		for(int j=0; j<size; j++) vec[j] = temp;
		return vec;
	}

	static void printMtrx(int[][] mtrx) {
		for(int l=0; l<mtrx.length; l++) {
			for(int y=0; y<mtrx[l].length; y++) 
				System.out.print(mtrx[l][y]);
			System.out.println();
		}
	}

}

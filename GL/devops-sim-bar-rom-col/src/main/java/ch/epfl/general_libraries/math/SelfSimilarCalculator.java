package ch.epfl.general_libraries.math;


import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.random.RandomSource;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

/**
 * Author : Laura Capdevila	-  copyright
 */
public class SelfSimilarCalculator {
	
	public static void main(String[] args) {
		
		double[] Hs = new double[]{0.6, 0.7, 0.8};
		double mean = 20;
		
		Execution ex = new Execution();
		for (double H : Hs) {
			SelfSimilarCalculator cal = new SelfSimilarCalculator(mean, H, 1024, 1024);
			
			DataPoint dp = new DataPoint();
			dp.addProperty("H", H);
			dp.addProperty("mean", mean);
			
			PRNStream stream = PRNStream.getDefaultStream(0);
			
			
			double[][] vals = cal.fftfgnTransformed(stream);
			int k = 0;
			for (int i = 0 ; i < vals.length ; i++) {
				for (int j = 0 ; j < vals[i].length ; j++) {
					DataPoint dp2 = dp.getDerivedDataPoint();
					dp2.addProperty("index", k);
					dp2.addResultProperty("self-sim sample value", vals[i][j]);
					ex.addDataPoint(dp2);
					k++;
				}
			}
		}
		
		SmartDataPointCollector col = new SmartDataPointCollector();
		col.addExecution(ex);
		
		DefaultResultDisplayingGUI.displayDefault_(col);
	}
	
	private double sigma, desiredRate;
	private double H;
	int n, N, M, force;
	private double co[];
	double [][]f;
	
	public SelfSimilarCalculator(double sigma, double desiredMeanRate, double H, int N, int M){
		this.H = getHcalibrated(H);
		this.M = M;
		this.N = N;
		this.n = 1;
		this.force = 1;
		this.desiredRate = desiredMeanRate;
		this.sigma = sigma;
		co = new double[2*M + 1]; 
	}
	
	public SelfSimilarCalculator(double desiredMeanRate, double H, int N, int M) {
		this.H = getHcalibrated(H);
		this.M = M;
		this.N = N;
		this.n = 2;
		this.force = 1;
		this.desiredRate = desiredMeanRate;
		this.sigma = getSigma(desiredMeanRate);
		co = new double[2*M + 1];	
	}
		
	public double getSigma(double desiredMeanRate){
	  	double factor = 0.6;
		double sigma_ = desiredMeanRate - factor*desiredMeanRate;	 //pendent ajustar factor!!!!!
	
		return sigma_;	
	}
	
	public double getDesiredRate() {
		return desiredRate;
	}
	
	
	private double getHcalibrated (double desiredH){
		return desiredH;
	}
	
	
	//fftfgnTransformed returns the fftfgn result (f) already transformed (calibrated with desiredMeanRate and absolute transformation)
	public double[][] fftfgnTransformed(RandomSource src){
				
		if(H == 0.5){   						
			f = mult(randn(n,M,src), sigma);
			
	 	}else if(/*(H <= 1) &&*/ (H > 0.5)){		
	 		f= new double [n][N];
	 		double []fi;
			double []fiP1;	  
				   	
		  	int nM = getnM(M, force);
		  	double[] c = getC(nM, sigma, M, H);		  	
			double[] ft = toRealVector((FourierCalculator.get(toComplex(c), nM, false)));
		  	
		  	double[] ft2 = linkVect(subList(ft, (int)(nM/2), nM),subList(ft, 0, (int)(nM/2)));
		  	double[][] randomMatrix = randn(n, 2*M+N, src);   

		  	for(int i = 0; i < randomMatrix.length; i++){
		  		fi = fftconv (randomMatrix[i], ft2, force);
		  		fiP1 = (double[])subList(fi,2*M-1,2*M+N-1);  
		  		for(int j=0; j< f[0].length; j++){
					f[i][j] = fiP1[j];
				}	
		  	}
		 	
	 	}else if((H < 0.5)&& (H > 0)){	  
	 		/*
	 		Complex[][] x = getXmatrix(n, N, src);
	 		double[][] y = getYmatrix(x, N);
	 		f = mult(y, pow(sigma*N,H)); 	*/
	 		System.out.println("Parameter H has to be: H > 0.5");
	 	}
	 	
	 	
	 //	f = sumMatrix(f,desiredRate);
	 //	f = transformedMatrix(f);
	 	return f;
	 	
	}
	
	
	
	private static double log2(int x){
		return Math.log10(x)/ Math.log10(2);
	}
	
	private static double pow (double num, double exp){
		return Math.pow(num, exp);
	}
	
	private static double abs (double num){
		return Math.abs(num);
	}
	
	public double[][] mult(double[][] v, double num){
		double[][] res = new double[v.length][v[0].length];
		for(int i = 0; i < v.length; i++){
			for(int j=0; j < v[0].length; j++){
				res[i][j] = num * v[i][j];
			}
		}
		return res;
	}
	
/*	private double[][] sumMatrix(double[][] c1, double num){  
		double[][] c3 = new double[c1.length][c1[0].length]; 
   		for(int i = 0; i < c1.length; i++){
			for(int j=0; j < c1[0].length; j++){
				c3[i][j] = c1[i][j] + num;
			}
   		}
   		return c3;
	}*/
	
	
	
/*	private Complex[][] minusMatrix(Complex[][] c1, Complex num){
		Complex[][] c3 = new Complex[c1.length][c1[0].length];
   		for(int i = 0; i < c1.length; i++){
			for(int j=0; j < c1[0].length; j++){
				c3[i][j] = c1[i][j].minus(num);
			}
   		}
   		return c3;
	}
	*/
	 
	//If X is a matrix, then diff(X) returns a matrix of row differences:  [X(2:m,:)-X(1:m-1,:)]
/*	private double[][] diff(double[][] x){
		double[][] res = new double [x.length-1][x[0].length];		
		for(int i = 1; i < x.length; i++){
			for(int j = 0; j < x[0].length; j++){
				res[i-1][j] = x[i][j] - x[i-1][j];
			}
		}
		return res;
	}
	*/
	
/*	private Complex[][] transMatrixComplex(Complex[][] x){
		Complex [][] t = new Complex[x[0].length][x.length];
  		for(int i = 0; i < x.length; ++i){
			for(int j = 0; j < x[0].length; ++j){
	  			t[j][i] = x[i][j];
			}
  		}
  	  	return t;
	}*/
	
/*	private double[][] transMatrixDouble(double[][] x){
		double [][] t = new double[x[0].length][x.length];
  		for(int i = 0; i < x.length; ++i){
			for(int j = 0; j < x[0].length; ++j){
	  			t[j][i] = x[i][j];
			}
  		}
  	  	return t;
	}*/
	
	
	private double[] getC(int nM,double sigma,int M, double H){
		//Obtention co
		double t[] = new double[2*M + 1];
		for(int i = 0; i < 2*M + 1 ; i++){
	 		t[i]= i - M;
	 		co[i] = 0.5 * pow(sigma,2) * (pow(abs(t[i]+1),2*H) + pow(abs(t[i]-1),2*H) - 2 * pow(abs(t[i]),2*H));  
		}
		//Obtention co_hat
		Complex [] co_hat = FourierCalculator.get(toComplex(co), nM, true); 

		//Obtention elements of c = abs(co_hat)^0.5			
		double[] c = new double[nM];
		for(int k = 0; k < nM; k++){	  	
		  		c[k] = pow(co_hat[k].abs(),0.5);
		} 
		return c;
	}
	
	//Obtention nM
	private int getnM(int M, int force){		
		if(force==1){ 
			return (int)pow(2, (int)(log2(2*(M+1)) + 1));  
		}else{
			return (2 * M) + 1;	
	   }
	}	
	
	//Obtention of S matrix (OK)
/*	private Complex[][] getSmatrix(int n, int N){
		Complex s[][] = new Complex[n][2*N];	
		double[] tmp = new double[2*N];
	 	
	 	tmp[0] = 1.0;
	 	for(int k = 0; k < N; k++){	   
	 		tmp[k+1]= 1 - pow((double)(k+1)/N,2*H);
	 		tmp[2*N-1-k] = tmp[k+1];
	 	}
	 	tmp[N] = 0.0;
	 	
	 	Complex m[] = FourierCalculator.get(toComplex(tmp),2*N,true);			
		for(int i=0; i< n; i++){
		 	for(int k = 0; k < 2*N; k++){	  	 
		   		s[i][k] = new Complex(pow(m[k].abs(),0.5),0.0);
		  	} 
		} 	
		return s;
	}*/
	
	
		
	
	//Obtention of a matrix [n][M] with Complex random values	
/*	private Complex[][] getComplexRandMatrix(int n, int N, RandomSource src){
		Complex[][] randMatrix = new Complex[n][N];
		for(int i=0; i< n; i++){
	 		for(int j=0; j<N; j++){
	 			double val = randn(n, N, src)[i][j]/Math.sqrt(2.0);
	 			double val2 = randn(n, N, src)[i][j]/Math.sqrt(2.0);
	 			randMatrix[i][j] = new Complex(val,val2);	
	 		}
	 	}
		return randMatrix;
	}*/
	
	
	//returns an row-by-col matrix of random entries (row -> row, col -> column), 
	//whose elements are normally distributed with mean 0 and standard desviation = 1
	private double[][] randn(int row, int col, RandomSource src){		 
	   	double[][] randValues = new double [row][col];
		for(int k = 0; k < row; k++){
			for(int i = 0; i < col; i++){	
				randValues[k][i] = (double)src.nextGaussian();
			}
		}
		return randValues;
	}
	
	
	//Creation of Complex[] from double[]
	private Complex[] toComplex(double[] v){
		Complex[] c = new Complex[v.length];
	  	for(int i = 0; i < v.length; i++){	  	
	  		c[i] = new Complex(v[i], 0);	 							
	  	}
	  	return c;
	}
	
	
	//Creation of double[] from the real part of the Complex[]
	private double[] toRealVector(Complex[] v){
		double[] c = new double[v.length];
	  	for(int i = 0; i < v.length; i++){	  	
	  		c[i] = v[i].re();	 							
	  	}
	  	return c;
	}
	
	
	//Creation sublist for Complex[] -> [origin, end)
	private double[] subList(double[] v, int origin, int end){
		double c2[] = new double[end - origin];
		for(int i = 0; i < end-origin; i++){
			c2[i] = v[i+origin];
		}
		return c2;
	} 
	
	
	//Link v1 and v2;   v = [v1 v2] 	
	private double[] linkVect(double v1[], double v2[]){
		int len = v1.length + v2.length;
		double[] newv = new double[len];
		for(int i=0; i < len; i++){
			if(i < v1.length){
				newv[i] = v1[i];
			}else{
				newv[i] = v2[i-v1.length];
			}
		}
		return newv;
	}	
	
	private double[] fftconv( double[] a, double[] b, int force){	 
		int n;
		if(force ==1){
			double tmp = log2(a.length + b.length);
			n = (int)pow(2,(int)(tmp + 1));
		}else{
			n = a.length + b.length;
		}		
		Complex A[] = FourierCalculator.get(toComplex(a), n, true);
		Complex B[] = FourierCalculator.get(toComplex(b), n, true);
		Complex MULT[] = new Complex[n];
	
		for(int k=0; k < n; k++){
			MULT[k] = A[k].times(B[k]);   		
		}
		
		return subList(toRealVector(FourierCalculator.get(MULT, n, false)), 0, a.length + b.length - 1);
	}
	
	public static double getMatrixMean(double[][] f){
		double m;
		double acum = 0;
		int cont = 0;
		for(int i=0; i < f.length; i++){
			for(int j=0; j < f[0].length; j++){
				acum = acum + f[i][j];
				cont++;
			}
		}
		m = acum/(double)cont;
		return m;
	}
	
}
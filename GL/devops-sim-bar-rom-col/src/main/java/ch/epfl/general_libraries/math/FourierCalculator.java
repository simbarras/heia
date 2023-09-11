package ch.epfl.general_libraries.math;


/*************************************************************************
 *  Limitations
 *  -----------
 *   -  assumes N is a power of 2
 *
 *   -  not the most memory efficient algorithm (because it uses
 *      an object type for representing complex numbers and because
 *      it re-allocates memory for the subarray, instead of doing
 *      in-place or reusing a single temporary array)
 * 
 *************************************************************************/


public class FourierCalculator {

	public FourierCalculator() {

	}


	//fftOrIfft = true(getFFT)   AND   fftOrIfft = false(getIFFT)
	public static Complex[] get(Complex[] x, int numPoints, boolean fftOrIfft){
		Complex[] newx = new Complex[numPoints];
		int N = x.length;

		if(N < numPoints){		//For to considerate the numPoints value
			for(int i=0; i < numPoints; i++){
				if(i < N){
					newx[i] = x[i];
				}else{
					newx[i] = new Complex (0.0,0.0);
				}
			}
		}else{
			for(int i=0; i < numPoints; i++){
				newx[i] = x[i];
			}
		}

		Complex[] res;

		if(fftOrIfft == true){
			res = fft(newx);
		}else{
			res = ifft(newx);
		}
		return res;
	}




	// compute the FFT of x[], assuming its length is a power of 2
	public static Complex[] fft(Complex[] x){

		int N = x.length;

		// base case
		if (N == 1) {
			return new Complex[] { x[0] };
		}

		// radix 2 Cooley-Tukey FFT
		if (N % 2 != 0) {
			throw new RuntimeException("N is not a power of 2");
		}

		// fft of even terms
		Complex[] even = new Complex[N/2];
		for (int k = 0; k < N/2; k++) {
			even[k] = x[2*k];
		}
		Complex[] q = fft(even);

		// fft of odd terms
		Complex[] odd  = even;  // reuse the array
		for (int k = 0; k < N/2; k++) {
			odd[k] = x[2*k + 1];
		}

		Complex[] r = fft(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N/2; k++) {
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k]       = q[k].plus(wk.times(r[k]));
			y[k + N/2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}



	// compute the inverse FFT of x[], assuming its length is a power of 2
	public static Complex[] ifft(Complex[] x){
		int N = x.length;
		Complex[] y = new Complex[N];

		// take conjugate
		for (int i = 0; i < N; i++) {
			y[i] = x[i].conjugate();
		}

		// compute forward FFT
		y = fft(y);

		// take conjugate again
		for (int i = 0; i < N; i++) {
			y[i] = y[i].conjugate();
		}

		// divide by N
		for (int i = 0; i < N; i++) {
			y[i] = y[i].times((1.0/N)) ;
		}

		return y;
	}


	//If x is a matrix, fft returns the Fourier transform of each column of the matrix.
	//So, xT must be the transposition of x
	public static Complex[][] get(Complex[][] xT, int numPoints, boolean fftOrIfft){
		Complex[][] c = new Complex[xT.length][xT[0].length];
		for(int i = 0; i < xT.length; ++i){
			c[i] = get(xT[i], numPoints, fftOrIfft);
		}
		return c;
	}





}



//info interessant FFT:  http://www.dspguide.com/ch12/2.htm
// http://www.cs.princeton.edu/introcs/97data/
//http://www.unilim.fr/pages_perso/jean.debord/math/fourier/fft.htm
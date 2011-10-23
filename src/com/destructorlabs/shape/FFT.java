package com.destructorlabs.shape;



public class FFT {
	int n, m;
	// Lookup tables.  Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;

	double[] window;

	public FFT(final int n) {
		this.n = n;
		this.m = (int)(Math.log(n) / Math.log(2));

		// Make sure n is a power of 2
		if(n != (1<<this.m)) {
			throw new RuntimeException("FFT length must be power of 2");
		}

		// precompute tables
		this.cos = new double[n/2];
		this.sin = new double[n/2];

		//     for(int i=0; i<n/4; i++) {
		//       cos[i] = Math.cos(-2*Math.PI*i/n);
		//       sin[n/4-i] = cos[i];
		//       cos[n/2-i] = -cos[i];
		//       sin[n/4+i] = cos[i];
		//       cos[n/2+i] = -cos[i];
		//       sin[n*3/4-i] = -cos[i];
		//       cos[n-i]   = cos[i];
		//       sin[n*3/4+i] = -cos[i];
		//     }

		for(int i=0; i<n/2; i++) {
			this.cos[i] = Math.cos(-2*Math.PI*i/n);
			this.sin[i] = Math.sin(-2*Math.PI*i/n);
		}

		this.makeWindow();
	}

	protected void makeWindow() {
		// Make a blackman window:
		// w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
		this.window = new double[this.n];
		for(int i = 0; i < this.window.length; i++) {
			this.window[i] = 0.42 - 0.5 * Math.cos(2*Math.PI*i/(this.n-1))
					+ 0.08 * Math.cos(4*Math.PI*i/(this.n-1));
		}
	}

	public double[] getWindow() {
		return this.window;
	}

	public double[] fft(final double[] x) {
		return this.fft(x, new double[x.length]);
	}

	public double[] fft(final double[] x, final double[] y)
	{
		int i,j,k,n1,n2,a;
		double c,s,t1,t2;


		// Bit-reverse
		j = 0;
		n2 = this.n/2;
		for (i=1; i < this.n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				j = j - n1;
				n1 = n1/2;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (i=0; i < this.m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;

			for (j=0; j < n1; j++) {
				c = this.cos[a];
				s = this.sin[a];
				a +=  1 << (this.m-i-1);

				for (k=j; k < this.n; k=k+n2) {
					t1 = c*x[k+n1] - s*y[k+n1];
					t2 = s*x[k+n1] + c*y[k+n1];
					x[k+n1] = x[k] - t1;
					y[k+n1] = y[k] - t2;
					x[k] = x[k] + t1;
					y[k] = y[k] + t2;
				}
			}
		}

		return x;
	}

	public static void makePlot(final double[] re) {
		System.out.print(" Y");
		for (int i=0; i<re.length; i++) {
			System.out.print(i);
		}
		System.out.println("\nX");

		for(int i=0; i<re.length; i++) {
			System.out.print(i + " ");
			for (int j=0; j<re[i]; j++) {
				System.out.print(" ");
			}
			System.out.println("x");
		}
	}

	protected static void beforeAfter(final FFT fft, final double[] re, final double[] im) {
		System.out.println("Before: ");
		FFT.printReIm(re, im);
		fft.fft(re, im);
		System.out.println("After: ");
		FFT.printReIm(re, im);
	}

	protected static void printReIm(final double[] re, final double[] im) {
		System.out.print("Re: [");
		for (double element : re) {
			System.out.print(((int)(element*1000)/1000.0) + " ");
		}

		System.out.print("]\nIm: [");
		for (double element : im) {
			System.out.print(((int)(element*1000)/1000.0) + " ");
		}

		System.out.println("]");
	}

	public static int getN(final int size) {
		int N=0;
		for (int i=0;; i++) {
			if (size<Math.pow(2, i)) {
				N=(int) Math.pow(2, i-1);
				break;
			}
		}
		return N;
	}
}

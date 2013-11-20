package variand.aplicacion;


import java.util.Vector;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.text.NumberFormat;

import tools.FFTbase;

//Clase que realiza los cálculos frecuenciales 
public class CalculosFrecuenciales extends Activity {

	//variable que devuelve la función power
	static double poweriband=0.0;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculosfrecuenciales);
		
		//Vector de la ventana hamming
		Vector<Double> hamming = new Vector<Double>();
		//Declarar elementos xml
		Button cerrar = (Button)findViewById(R.id.cerrar);
		//Dar formato a los float resultantes
		NumberFormat NF = NumberFormat.getInstance();
		NF.setMaximumFractionDigits(3); //3 decimales

		//Botón cerrar
		cerrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();				
			}
		});

		//vector de datos recien interpolados
		double[] data,data2,fftresult,spec_tmp,spec,freqs;
		double Fs=4.0;

		//Número de frames
		int ventana = Representar.interpolar.size()/256;
		//Contador del frame en el que nos encontramos
		int aux=0;
		Log.i("NUMERO DE FRAMES", String.valueOf(ventana));
		while(aux<ventana)
		{
			Log.i("VENTANA", String.valueOf(aux));
			int posicion=256*aux;
			// creamos data del tamano de interpolar
			data= new double [256];

			//Pasar datos a vector data
			for(int i=0;i<data.length;i++)
			{
				data[i]=Representar.interpolar.get(posicion);
				posicion++;
			}
			//Aplicar hamming
			for(int i=0;i<data.length;i++)
			{
				hamming.add(0.54-0.46*Math.cos((2*3.14159265359*data[i])/(data.length)));
				Log.i("sdsds", String.valueOf(hamming.get(i)));

			}

			//hamming=0.54-0.46*cos(2*pi*(0:(length(data)-1))/(length(data)-1))
			//# data = data*hamming
			//data = data-mean(data)
			
			//Multiplicamos los datos del vector data por hamming
			for(int i=0;i<data.length;i++)
			{
				data[i]=data[i]*hamming.get(i);
			}


			// Restamos la media del vector
			double sumvector = 0.0;

			for (int i=0;i<data.length;i++) {
				sumvector = sumvector + data[i];
			}
			for (int i=0;i<data.length;i++) {
				data[i] = data[i] - sumvector/data.length;
			}

			// Parte imaginaria vector de entrada
			data2 = new double[data.length];
			for (int i=0;i<data.length;i++) {
				data2[i] = 0.0;
			}

			//Cálculo de la FFT
			FFTbase result = new FFTbase();

			fftresult = new double[2*data.length];

			fftresult = result.fft(data,data2,true);
			// spec_tmp del tamaño de data
			spec_tmp = new double [data.length];		

			for (int i=0; i<10; i++) {
				double ff = Math.sqrt(fftresult[2*i]*fftresult[2*i]+fftresult[2*i+1]*fftresult[2*i+1])*16.0;
			}

			//Se mete en spec_tmp ff al cuadrado
			for(int i=0;i<spec_tmp.length;i++)
			{
				double ff = Math.sqrt(fftresult[2*i]*fftresult[2*i]+fftresult[2*i+1]*fftresult[2*i+1])*16.0;

				spec_tmp[i]=ff*ff;
			}
			//Creamos el vector spec con la mitad de tamaÃ±o que spec_tmp
			spec= new double [spec_tmp.length/2];
			//Rellenamos el vector spec
			for(int i=0;i<spec.length;i++)
			{
				spec[i]=spec_tmp[i];
			}

			//Creamos freqs del tamaño de spec
			freqs = new double [spec.length];
			
			double start =0.0;
			double stop=Fs/2.0;
			double n =spec.length;

			double step = (stop-start)/(n-1);
			//Rellenamos freqs con los valores adecuados
			for(int i = 0; i <= n-2; i++)
			{
				freqs[i]=start + (i * step);
			}
			
			freqs[127]=stop;
			
			//Captura de los resultados en variables
			String fft=NF.format(power(spec,freqs,0,Fs/2));
			String ulf=NF.format(power(spec,freqs,0.00,0.03));
			String vlf=NF.format(power(spec,freqs,0.03,0.05));
			double lf=power(spec,freqs,0.05,0.15);
			double hf=power(spec,freqs,0.15,0.40);
			String rango=NF.format(power(spec,freqs,0.40,Fs/2));
			double lfhf=lf/hf;
			//Switch en el que según el frame en el que estemos se le pasan unos valores u otros a la tabla
			switch (aux) {
			case 0:
				((TextView)findViewById(R.id.cero)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftcero)).setText(fft);
				((TextView)findViewById(R.id.ulfcero)).setText(ulf);
				((TextView)findViewById(R.id.vlfcero)).setText(vlf);
				((TextView)findViewById(R.id.lfcero)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfcero)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfcero)).setText(NF.format(lfhf));
				break;
			case 1:
				((TextView)findViewById(R.id.uno)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftuno)).setText(fft);
				((TextView)findViewById(R.id.ulfuno)).setText(ulf);
				((TextView)findViewById(R.id.vlfuno)).setText(vlf);
				((TextView)findViewById(R.id.lfuno)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfuno)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfuno)).setText(NF.format(lfhf));
				break;
			case 2:
				((TextView)findViewById(R.id.dos)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftdos)).setText(fft);
				((TextView)findViewById(R.id.ulfdos)).setText(ulf);
				((TextView)findViewById(R.id.vlfdos)).setText(vlf);
				((TextView)findViewById(R.id.lfdos)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfdos)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfdos)).setText(NF.format(lfhf));
				break;
			case 3:

				((TextView)findViewById(R.id.tres)).setText("Frame "+aux);
				((TextView)findViewById(R.id.ffttres)).setText(fft);
				((TextView)findViewById(R.id.ulftres)).setText(ulf);
				((TextView)findViewById(R.id.vlftres)).setText(vlf);
				((TextView)findViewById(R.id.lftres)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hftres)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhftres)).setText(NF.format(lfhf));
				break;
			case 4:
				((TextView)findViewById(R.id.cuatro)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftcuatro)).setText(fft);
				((TextView)findViewById(R.id.ulfcuatro)).setText(ulf);
				((TextView)findViewById(R.id.vlfcuatro)).setText(vlf);
				((TextView)findViewById(R.id.lfcuatro)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfcuatro)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfcuatro)).setText(NF.format(lfhf));
				break;
			case 5:
				((TextView)findViewById(R.id.cinco)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftcinco)).setText(fft);
				((TextView)findViewById(R.id.ulfcinco)).setText(ulf);
				((TextView)findViewById(R.id.vlfcinco)).setText(vlf);
				((TextView)findViewById(R.id.lfcinco)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfcinco)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfcinco)).setText(NF.format(lfhf));
				break;
			case 6:
				((TextView)findViewById(R.id.seis)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftseis)).setText(fft);
				((TextView)findViewById(R.id.ulfseis)).setText(ulf);
				((TextView)findViewById(R.id.vlfseis)).setText(vlf);
				((TextView)findViewById(R.id.lfseis)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfseis)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfseis)).setText(NF.format(lfhf));
				break;
			case 7:
				((TextView)findViewById(R.id.siete)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftsiete)).setText(fft);
				((TextView)findViewById(R.id.ulfsiete)).setText(ulf);
				((TextView)findViewById(R.id.vlfsiete)).setText(vlf);
				((TextView)findViewById(R.id.lfsiete)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfsiete)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfsiete)).setText(NF.format(lfhf));
				break;
			case 8:
				((TextView)findViewById(R.id.ocho)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftocho)).setText(fft);
				((TextView)findViewById(R.id.ulfocho)).setText(ulf);
				((TextView)findViewById(R.id.vlfocho)).setText(vlf);
				((TextView)findViewById(R.id.lfocho)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfocho)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfocho)).setText(NF.format(lfhf));
				break;
			case 9:
				((TextView)findViewById(R.id.nueve)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftnueve)).setText(fft);
				((TextView)findViewById(R.id.ulfnueve)).setText(ulf);
				((TextView)findViewById(R.id.vlfnueve)).setText(vlf);
				((TextView)findViewById(R.id.lfnueve)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfnueve)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfnueve)).setText(NF.format(lfhf));
				break;
			case 10:
				((TextView)findViewById(R.id.diez)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftdiez)).setText(fft);
				((TextView)findViewById(R.id.ulfdiez)).setText(ulf);
				((TextView)findViewById(R.id.vlfdiez)).setText(vlf);
				((TextView)findViewById(R.id.lfdiez)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfdiez)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfdiez)).setText(NF.format(lfhf));
				break;
			case 11:
				((TextView)findViewById(R.id.once)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftonce)).setText(fft);
				((TextView)findViewById(R.id.ulfonce)).setText(ulf);
				((TextView)findViewById(R.id.vlfonce)).setText(vlf);
				((TextView)findViewById(R.id.lfonce)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfonce)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfonce)).setText(NF.format(lfhf));
				break;
			case 12:
				((TextView)findViewById(R.id.doce)).setText("Frame "+aux);
				((TextView)findViewById(R.id.fftdoce)).setText(fft);
				((TextView)findViewById(R.id.ulfdoce)).setText(ulf);
				((TextView)findViewById(R.id.vlfdoce)).setText(vlf);
				((TextView)findViewById(R.id.lfdoce)).setText(NF.format(lf));
				((TextView)findViewById(R.id.hfdoce)).setText(NF.format(hf));
				((TextView)findViewById(R.id.lfhfdoce)).setText(NF.format(lfhf));
				break;

			default:
				break;
			}

			aux++;
			hamming.removeAllElements();

		}


	}
	
	//Función que calcula los parámetros frecuenciales deseados
	public static double power(double[] spec,double[] freq,double fmin,double fmax)
	{
		Vector<Double> band = new Vector<Double>();
		for(int i=0;i<spec.length;i++)
		{
			if(freq[i]>=fmin && freq[i]<=fmax)
			{
				band.add(spec[i]);

			}
		}

		for(int i=0;i<band.size();i++)
		{
			poweriband= poweriband+band.get(i);
		}
		System.out.println("tamaÃ±o"+band.size()+" samples");
		poweriband= poweriband/(2*Math.pow((spec.length),2));
		band.removeAllElements();
		return poweriband;
	}


}


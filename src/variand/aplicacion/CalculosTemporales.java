	package variand.aplicacion;


import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.text.NumberFormat;

//Clase que realiza los cálculos temporales
public class CalculosTemporales extends Activity {
	//Vectores necesarios
	Vector<Double> promedios = new Vector<Double>();
	Vector<Double> desviacionEstandar = new Vector<Double>();
	Vector<Double> diferencias = new Vector<Double>();
	ArrayList<Double> diferencias2 = new ArrayList<Double>();
	//variables necesarias
	double sumatorio,sumatorio1,sumatorio2,sumatorio3,sumatorio4;
	double mediaarit,sdnn,finalinterval,finalintervalmarcador,sdann,sdnnidx,contador,marcadorframe,frame,pnn50,diferencia,diferenciacuadrado,rmssd,madrr;
	int z,z2,z3;
	double radioframe=120.0;
	private ProgressDialog dialog;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculostemporales);
		//Declarar elementos xml
		final RadioGroup rg = (RadioGroup)findViewById(R.id.radiogrupo);
		final CheckBox checksdnn = (CheckBox)findViewById(R.id.checksdnn);
		final CheckBox checksdann = (CheckBox)findViewById(R.id.checksdann);
		final CheckBox checksdnnidx = (CheckBox)findViewById(R.id.checksdnnidx);
		final CheckBox checkpnn50 = (CheckBox)findViewById(R.id.checkpnn50);
		final CheckBox checkrmssd = (CheckBox)findViewById(R.id.checkrmssd);
		final CheckBox checkmadrr = (CheckBox)findViewById(R.id.checkmadrr);
		final Button calcular = (Button)findViewById(R.id.calcular);
		Button cerrar = (Button)findViewById(R.id.cerrar);
		final TextView resultado = (TextView)findViewById(R.id.resultado);

		rg.clearCheck();
		rg.check(R.id.radio2);




		//Botón para calcular parámetros
		calcular.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//Dar formato a los float
				NumberFormat NF = NumberFormat.getInstance();
				NF.setMaximumFractionDigits(3); //3 decimales

				int checkedRadioButton = rg.getCheckedRadioButtonId();

				//Clic en radiobuton
				switch (checkedRadioButton) {

				case R.id.radio1 : radioframe = 60;
				break;
				case R.id.radio2 : radioframe = 120;
				break;
				case R.id.radio3 : radioframe = 180;
				break;
				case R.id.radio4 : radioframe = 240;
				break;
				case R.id.radio5 : radioframe = 300;
				break;

				}

				resultado.setText("");

				//comprobar si está marcado para calcularlo
				if(checksdnn.isChecked())
				{
					resultado.append("SDNN:");

					sumatorio=0.0;
					sumatorio1=0.0;
					sumatorio2=0.0;
					sumatorio3=0.0;
					//recorrido RR completo
					for(int i=0;i<Representar.RR.size();i++)
					{
						sumatorio= sumatorio+Representar.RR.get(i); //sumo todos los valores
					}

					mediaarit=sumatorio/Representar.RR.size(); //promedio
					//calculos para la dsviacion estandar
					for(int i=0;i<Representar.RR.size();i++)
					{
						sumatorio1=(Representar.RR.get(i)-mediaarit); //numero menos media

						sumatorio2= pow(sumatorio1,2); // resultado al cuadrado

						sumatorio3=sumatorio3+sumatorio2; //sumatorio final


					}

					sdnn=sqrt(sumatorio3/Representar.RR.size()); // desviacion estandar

					String res=NF.format(sdnn);
					resultado.append(" "+res);
					resultado.append("\n");
					resultado.append("\n");

				}

				//comprobar si está marcado para calcularlo
				if(checksdann.isChecked())
				{
					resultado.append("SDANN:");

					sumatorio=0.0;
					sumatorio1=0.0;
					sumatorio2=0.0;
					sumatorio3=0.0;
					finalinterval=300.0;
					finalintervalmarcador=300.0;

					z=0;
					z3=0;
					// recorrer vector RR
					for(int j=0;j<Representar.RR.size();j++)
					{

						if(Representar.beatTime.get(j)<=finalinterval && Representar.RR.size()>j) 
						{

							z++;
							sumatorio=sumatorio+Representar.RR.get(j);  //seguir sumando


						}
						else
						{ 
							z3++;
							mediaarit=sumatorio/(z);  // hallar media

							promedios.add(mediaarit);  //guardar media en un vector

							sumatorio=0.0;
							z=0;
							finalinterval=finalinterval+finalintervalmarcador;

						}

					}




					for(int i=0;i<promedios.size();i++)
					{
						sumatorio=0.0;

						sumatorio= sumatorio+promedios.get(i); //sumo todos los valores


					}

					mediaarit=sumatorio/promedios.size(); //promedio

					//calculos para la desviacion estandar
					for(int i=0;i<promedios.size();i++)
					{
						sumatorio1=(promedios.get(i)-mediaarit); //numero menos media

						sumatorio2= pow(sumatorio1,2); // resultado al cuadrado

						sumatorio3=sumatorio3+sumatorio2; //sumatorio final


					}

					sdann=sqrt(sumatorio3/promedios.size()); // desviacion estandar
					String res=NF.format(sdann);
					promedios.removeAllElements();
					//Comprobar si hay suficientes datos para realizar el cálculo
					if(z3<2)
					{
						resultado.append(" "+"No hay suficientes datos");
					}
					else
					{
						resultado.append(" "+"Basado en "+z3+" "+"intervalos"+" = "+res+" ");
					}
					resultado.append("\n");
					resultado.append("\n");

				}


				//comprobar si está marcado para calcularlo
				if(checksdnnidx.isChecked())
				{
					resultado.append("SDNNIDX:");

					sumatorio=0.0;
					sumatorio1=0.0;
					sumatorio2=0.0;
					sumatorio3=0.0;
					finalinterval=300.0;
					finalintervalmarcador=300.0;
					contador=0;
					sdnnidx=0.0;
					z=0;
					z2=0;
					z3=0;
					desviacionEstandar.removeAllElements();

					// recorrer vector RR
					for(int j=0;j<Representar.RR.size();j++)
					{
						z3=j;


						if(Representar.beatTime.get(j)<=finalinterval && Representar.RR.size()>j) 
						{
							z++;
							sumatorio=sumatorio+Representar.RR.get(j);  //seguir sumando

						}
						else
						{ 
							z3++;
							mediaarit=sumatorio/(z);  // hallar media


							for(int i=z2;i<=j;i++)
							{
								sumatorio1=(Representar.RR.get(i)-mediaarit); //numero menos media

								sumatorio2= pow(sumatorio1,2); // resultado al cuadrado

								sumatorio3=sumatorio3+sumatorio2; //sumatorio final


							}

							sumatorio4=sqrt(sumatorio3/z); // desviacion estandar

							desviacionEstandar.add(sumatorio4);
							sumatorio=0.0;
							z2=j;
							z=0;
							finalinterval=finalinterval+finalintervalmarcador;

						}

					}


					for(int i=0;i<desviacionEstandar.size();i++)
					{

						contador=contador+desviacionEstandar.get(i);
					}

					sdnnidx=contador/desviacionEstandar.size();
					String res=NF.format(sdnnidx);
					//Comprobar si hay suficientes datos para realizar el cálculo
					if(desviacionEstandar.size()<2)
					{
						resultado.append(" "+"No hay suficientes datos");
					}
					else
					{
						resultado.append(" "+"Basado en "+desviacionEstandar.size()+" "+"intervalos"+" = "+res+" ");
					}
					resultado.append("\n");
					resultado.append("\n");


				}


				//comprobar si está marcado para calcularlo
				if(checkpnn50.isChecked())
				{
					resultado.append("pNN50:");

					sumatorio=0.0;
					marcadorframe=radioframe;
					frame=radioframe;
					sdnnidx=0.0;
					z=0;
					z2=0;
					z3=0;

					// recorrer vector RR
					for(int j=0;j<Representar.RR.size();j++)
					{

						if(Representar.beatTime.get(j)<=frame && Representar.RR.size()>j) 
						{
							z++;
							sumatorio=sumatorio+Representar.RR.get(j);  //seguir sumando

						}

						else
						{

							z3++;

							int aux=0;

							for(int i=z2;i<j;i++)
							{
								diferencia=Representar.RR.get(i+1)-Representar.RR.get(i);
								diferencia=abs(diferencia);
								Log.d(String.valueOf(i), String.valueOf(diferencia));

								if(diferencia>50.0)
								{
									aux++;
								}

							}

							pnn50=(aux*100.0)/z;
							String res=NF.format(pnn50);
							resultado.append(" "+"Frame"+" "+z3+"="+res+" ,"+" ");

							z2=j;
							z=0;
							sumatorio=0.0;
							frame=frame+marcadorframe;
						}

					}
					resultado.append("\n");
					resultado.append("\n");

				}


				//comprobar si está marcado para calcularlo
				if(checkrmssd.isChecked())
				{
					resultado.append("r-MSSD:");

					sumatorio=0.0;
					sumatorio1=0.0;
					marcadorframe=radioframe;
					frame=radioframe;
					sdnnidx=0.0;
					z=0;
					z2=0;
					z3=0;
					diferencia=0;
					diferenciacuadrado=0;
					mediaarit=0.0;

					// recorrer vector RR
					for(int j=0;j<Representar.RR.size();j++)
					{

						if(Representar.beatTime.get(j)<=frame && Representar.RR.size()>j) 
						{
							z++;
							sumatorio=sumatorio+Representar.RR.get(j);  //seguir sumando

						}

						else
						{
							z3++;

							for(int i=z2;i<j;i++)
							{
								diferencia=Representar.RR.get(i+1)-Representar.RR.get(i);
								diferenciacuadrado= pow(diferencia,2);
								diferencias.add(diferenciacuadrado);
							}

							for(int i=0;i<diferencias.size();i++)
							{
								sumatorio1=sumatorio1+diferencias.get(i);
							}
							mediaarit=sumatorio1/diferencias.size();

							rmssd=sqrt(mediaarit);
							String res=NF.format(rmssd);
							resultado.append(" "+"Frame"+" "+z3+"="+res+" ,"+" ");

							diferencias.removeAllElements();
							z2=j;
							z=0;
							sumatorio=0.0;
							sumatorio1=0.0;
							frame=frame+marcadorframe;

						}

					}


					resultado.append("\n");
					resultado.append("\n");

				}

				//comprobar si está marcado para calcularlo
				if(checkmadrr.isChecked())
				{

					resultado.append("MADRR:");

					diferencia=0;

					for(int i=0;i<Representar.RR.size()-1;i++)
					{
						diferencia=Representar.RR.get(i+1)-Representar.RR.get(i);
						diferencia=abs(diferencia);
						diferencias2.add(diferencia);

					}

					for (int i = 0; i < diferencias2.size(); i++)
					{

						for (int j = 0; j < diferencias2.size()- 1; j++)
						{

							if (diferencias2.get(j) > diferencias2.get(j+1)) 
							{
								double tmp = diferencias2.get(j+1);
								double tmp2 = diferencias2.get(j);

								diferencias2.remove(j);					
								diferencias2.add(j,tmp);
								diferencias2.remove(j+1);
								diferencias2.add(j+1,tmp2);

							}
						}
					}

					madrr=Median(diferencias2);
					String res=NF.format(madrr);
					resultado.append(" "+res);

					diferencias2.removeAll(diferencias2);

				}

			}

		});





		//Botón para cerrar la ventana de cálculos paramétricos
		cerrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});


	}


	//Función que calcula la mediana
	public  double Median(ArrayList<Double> values)
	{
		Collections.sort(values);

		if (values.size() % 2 == 1)
			return  values.get((values.size()+1)/2-1);
		else
		{
			double lower =  values.get(values.size()/2-1);
			double upper =  values.get(values.size()/2);

			return (lower + upper) / 2.0;
		}	
	}

}


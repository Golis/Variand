package variand.aplicacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import tools.FFTbase;
import static java.lang.Math.*;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import android.R.menu;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
//Esta clase permite Representar las señales y realizar muchos tratamientos sobre ellas
@SuppressLint("FloatMath")
public class Representar extends ListFicheros implements OnTouchListener{

	public XYPlot mySimpleXYPlot;

	File grafica;
	FileOutputStream fos;

	Vector<String> vector = new Vector<String>();  //para leer fichero
	static Vector<Double> beatTime = new Vector<Double>();  //vector que contiene los datos del fichero
	Vector<Double> niHR = new Vector<Double>();		//vector que contiene la frecuencia cardiaca
	static Vector<Double> RR = new Vector<Double>();		//vector que contiene nihr en milisegundos
	static Vector<Double> RR2 = new Vector<Double>();		//vector que contiene nihr en milisegundos
	Vector<Double> beatTime2 = new Vector<Double>();//copia del vector beat time
	Vector<Double> niHR2 = new Vector<Double>();	//copia del vector niHr
	Vector<Double> vectorAux1 = new Vector<Double>();  //vectores axiliares
	Vector<Double> vectorAux2 = new Vector<Double>();
	Vector<Double> vectorAux3 = new Vector<Double>();
	Vector<Double> promedios = new Vector<Double>();	//vector de promedios
	Vector<Double> desviacionEstandar = new Vector<Double>();	//vector para desviaciones estándar
	Vector<Double> diferencias = new Vector<Double>();
	ArrayList<Double> diferencias2 = new ArrayList<Double>();
	static Vector<Double> interpolar = new Vector<Double>();	//Vector que almacena los datos a interpolar
	double[] data;
	//Declaración de variables necesarias para trabajar con la señal
	double primero,segundo,restaFrec,frecuencia,actual,actual2,actual3;
	static final int NONE = 0;
	static final int ONE_FINGER_DRAG = 1;
	static final int TWO_FINGERS_DRAG = 2;
	int mode = NONE;
	PointF firstFinger;
	float lastScrolling;
	float distBetweenFingers;
	float lastZooming;
	static int color0=200;
	static int color1,color2;
	int color3;
	int color4,color5;
	int veces=0;
	int z=0;
	int z2=0;
	int z3=0;
	double frame=0.0;
	double marcadorframe=0.0;
	double finalintervalmarcador=0.0;
	double diferencia=0.0;
	double diferenciacuadrado=0.0;
	double contador=0;
	double finalinterval;
	String minimo,maximo,des,has;
	String nombrefil;
	double mini,maxi,desded,hastad=0;
	double M,miliseg,seg;
	double mediaarit,sdnn,sdann,pnn50,sdnnidx,rmssd,madrr,sumatorio,sumatorio1,sumatorio2,sumatorio3,sumatorio4,rrconvert;
	int incrementador=1;
	public PointF minXY;
	public PointF maxXY;

	double winlength=50;
	double last =13;
	double ulast =0;
	double minbpm=24;
	double maxbpm=198;
	double umean=0;
	double acumulador=0;
	int comprobacion=0;




	//comienzo ejecucion
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.representar);
		//Limpiar vectores
		beatTime.removeAllElements();
		niHR.removeAllElements();
		RR.removeAllElements();
		beatTime2.removeAllElements();
		niHR2.removeAllElements();



		vector.removeAllElements();
		//Recibir nombre del fichero
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{
			nombrefil = extras.getString("nombrefich");
		}



		//Acceso a la SDCard
		File f = new File(Environment.getExternalStorageDirectory(),
				"FC_aplication");
		//Si la carpeta no existe, se crea
		if (!f.exists()) {
			try {
				f.mkdirs();
			} catch (Exception ex) {
				Log.e("Carpeta", "Imposible crear carpeta");
			}
		}

		//Linkar con el txt
		File file = new File(f,nombrefil);

		//Leer texto del txt
		StringBuilder text = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {

				vector.add(line);
				text.append(line);
				text.append('\n');    

			}
		}
		catch (IOException e) {

		}

		//Inicializar nuestro Plot
		mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		mySimpleXYPlot.setOnTouchListener(this);


		//Rellenar vector beatTime y su complementario con los números convertidos a double
		for(int i =0;i<vector.size();i++)
		{
			beatTime.add(Double.parseDouble(vector.get(i)));

			beatTime2.add(Double.parseDouble(vector.get(i)));


		}

		//Introducir puntos en la grafica plot una vez convertidos de string a double
		primero=beatTime.get(0);
		segundo=beatTime.get(1);
		restaFrec=segundo-primero;
		frecuencia=(1/restaFrec)*60;
		niHR.add(frecuencia);
		niHR2.add(frecuencia);

		//Rellenar vector niHR y su complementario con las frecuencias cardíacas
		for(int i =0;i<beatTime.size()-1;i++)
		{
			primero=beatTime.get(i);
			segundo=beatTime.get(i+1);
			restaFrec=segundo-primero;
			frecuencia=(1/restaFrec)*60;
			niHR.add(frecuencia);
			niHR2.add(frecuencia);
		}

		//Rellenar vector RR y su complementario con las frecuencias cardíacas transformadas a milisegundos
		for(int i=0;i<niHR.size();i++)
		{
			rrconvert=(1.0/niHR.get(i))*1000.0*60.0;
			RR.add(rrconvert);
			RR2.add(rrconvert);

		}

		Dibujar(color0,color1,color2);

	}

	//Método que permite hacer touch con los dedos para el zoom y el scroll de la señal
	public boolean onTouch(View arg0, MotionEvent event) {

		Touchable();
		return true;
	}



	//Coger opciones menú
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menugeneral, menu);
		return true;
	}



	//Seleccionar las opciones del menú
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{


		case R.id.editar:
			comprobacion=0;
			//Lanzar Activy para editar
			Intent editar = new Intent(this, Editar.class);
			startActivityForResult(editar, 1);

			return true;

		case R.id.filtrom:
			comprobacion=0;
			//Lanzar Activy para filtrar manualmente
			Intent filtromanual = new Intent(this, FiltroManual.class);
			startActivityForResult(filtromanual, 2);

			return true;

		case R.id.filtroau:
			comprobacion=0;
			//Lanzar Activy para filtrar automáticamente
			FilterNIHR(winlength, last, minbpm, maxbpm);

			setContentView(R.layout.representar);

			mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
			//Habilitar la capacidad de hacer touch sobre el gráfico
			Touchable();

			mySimpleXYPlot.clear();
			//Seleccionar si se redibuja la señal con o sin los puntos que la forman
			SeleccionarModoRepintado();
			
			Toast.makeText(getApplicationContext(),"Señal filtrada correctamente", Toast.LENGTH_LONG).show();


			return true;



		case R.id.poincare:
			//Lanzar Activity para mostrar la gráfica de Poincaré
			Intent poincare = new Intent();
			poincare.setClass(getApplicationContext(), Poincare.class);
			startActivity(poincare);

			return true;
		case R.id.calculostemporales:
			//Lanzar activity para mostrar las opciones sobre cálculos paramétricos
			Intent calculos = new Intent();
			calculos.setClass(getApplicationContext(), CalculosTemporales.class);
			startActivity(calculos);
			return true;

		case R.id.interpolacion:
			//Calcular interpolación de la señal
			double frec=4;
			double fr = 1/frec;
			double frecuencia2=0.5;
			double y=0.0;
			interpolar.removeAllElements();

			interpolar.add(niHR.get(0));
			interpolar.add(niHR.get(0));


			for(int i=0;i<beatTime.size()-1;i++)
			{
				while(frecuencia2>=beatTime.get(i)&&frecuencia2<beatTime.get(i+1))
				{

					y= ((niHR.get(i)-niHR.get(i+1))/(beatTime.get(i)-beatTime.get(i+1)))*frecuencia2 + niHR.get(i)-((niHR.get(i)-niHR.get(i+1))/(beatTime.get(i)-beatTime.get(i+1)))*beatTime.get(i);
					interpolar.add(y);
					frecuencia2+=fr;			

				}

			}
			
			for(int i=0;i<interpolar.size();i++)
			{
				Log.i("interpolar"+i, String.valueOf(interpolar.get(i)));
			}

			comprobacion=1;

			Toast.makeText(getApplicationContext(),"Señal interpolada correctamente", Toast.LENGTH_LONG).show();

			return true;
		case R.id.calculosfrecuenciales:

			if(comprobacion==1)
			{
				Intent frecuencia = new Intent();
				frecuencia.setClass(getApplicationContext(), CalculosFrecuenciales.class);
				startActivity(frecuencia);
			}
			else if(comprobacion==0)
			{
				Intent comprobar = new Intent();
				comprobar.setClass(getApplicationContext(), BloquearOpcion.class);
				startActivity(comprobar);
			}



			return true;

		case R.id.exportarjpeg:
			//Lanzar activity para recoger el nombre con el que exportar la imagen de la señal
			Intent exportarjpeg = new Intent(this, Exportar.class);
			startActivityForResult(exportarjpeg, 3);

			return true;

		case R.id.exportarpng:
			//Lanzar activity para recoger el nombre con el que exportar la imagen de la señal
			Intent exportarpng = new Intent(this, Exportar.class);
			startActivityForResult(exportarpng, 4);

			return true;

		case R.id.restaurarimg:
			//Restaurar la señal a la posición inicial
			setContentView(R.layout.representar);

			mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

			//Habilitar la capacidad de hacer touch sobre el gráfico
			Touchable();

			//Seleccionar si se redibuja la señal con o sin los puntos que la forman
			SeleccionarModoRepintado();

			Toast.makeText(getApplicationContext(), "La señal ha sido restaurada correctamente", Toast.LENGTH_LONG).show();
			return true;

		case R.id.restaurarsen:
			comprobacion=0;
			//Restaurar la señal al estado inicial
			setContentView(R.layout.representar);

			mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

			//Habilitar la capacidad de hacer touch sobre el gráfico
			Touchable();
			//Borrar datos de los vectores
			beatTime.removeAllElements();
			niHR.removeAllElements();
			RR.removeAllElements();
			//Meter en los vectores originales los datos guardados en los vectores complementarios
			for(int i=0;i<beatTime2.size();i++)
			{
				beatTime.add(beatTime2.get(i));
				niHR.add(niHR2.get(i));
				RR.add(RR2.get(i));
			}

			//Seleccionar si se redibuja la señal con o sin los puntos que la forman
			SeleccionarModoRepintado();
			Toast.makeText(getApplicationContext(), "La señal ha sido restaurada correctamente", Toast.LENGTH_LONG).show();

			return true;

		case R.id.verdel:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=0;
				color1=200;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=0;
				color1=200;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;

		case R.id.azull:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=0;
				color1=0;
				color2=200;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=0;
				color1=0;
				color2=200;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;

		case R.id.rosal:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=200;
				color1=0;
				color2=100;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=200;
				color1=0;
				color2=100;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;
		case R.id.rojol:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=200;
				color1=0;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=200;
				color1=0;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;
		case R.id.amarillol:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=200;
				color1=200;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=200;
				color1=200;
				color2=0;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;
		case R.id.blancol:
			//Validaciones del color de la señal
			if(color3!=0||color4!=0||color5!=0)
			{
				color0=200;
				color1=200;
				color2=200;
				mySimpleXYPlot.clear();
				Dibujar2(color0,color1,color2,color3,color4,color5);
			}
			else
			{
				color0=200;
				color1=200;
				color2=200;
				mySimpleXYPlot.clear();
				Dibujar(color0, color1, color2);
			}
			return true;

		case R.id.verdep:
			//Validaciones del color de los puntos de la señal
			color3=0;
			color4=200;
			color5=0;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;

		case R.id.azulp:
			//Validaciones del color de los puntos de la señal
			color3=0;
			color4=0;
			color5=200;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;

		case R.id.rosap:
			//Validaciones del color de los puntos de la señal
			color3=200;
			color4=0;
			color5=100;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;
		case R.id.rojop:
			//Validaciones del color de los puntos de la señal
			color3=200;
			color4=0;
			color5=0;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;
		case R.id.amarillop:
			//Validaciones del color de los puntos de la señal
			color3=200;
			color4=200;
			color5=0;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;
		case R.id.blancop:
			//Validaciones del color de los puntos de la señal
			color3=200;
			color4=200;
			color5=200;
			mySimpleXYPlot.clear();
			Dibujar2(color0,color1,color2,color3,color4,color5);
			return true;


		case R.id.puntos:
			//desactivar puntos de la señal
			mySimpleXYPlot.clear();
			Dibujar(color0, color1, color2);
			color3=0;
			color4=0;
			color5=0;

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}



	// funcion hallar mediana
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

	//método para calcular el zoom
	public void zoom(float scale) {
		float domainSpan = maxXY.x	- minXY.x;
		float domainMidPoint = maxXY.x		- domainSpan / 2.0f;
		float offset = domainSpan * scale / 2.0f;
		minXY.x=domainMidPoint- offset;
		maxXY.x=domainMidPoint+offset;
	}

	//método para realizar scroll
	public void scroll(float pan) {
		float domainSpan = maxXY.x - minXY.x;
		float step = domainSpan / mySimpleXYPlot.getWidth();
		float offset = pan * step;
		minXY.x = minXY.x + offset;
		maxXY.x = maxXY.x + offset;
	}

	//funcion necesaria para zoom
	float x,y;
	public float spacing(MotionEvent event) {

		if(event.getPointerCount()>1)
		{
			x = event.getX(0) - event.getX( event.getPointerCount() -1  );
			y = event.getY(0) - event.getY( event.getPointerCount() -1 );
		}
		return FloatMath.sqrt(x * x + y * y);

	}

	// Método que calcula el filtro automático
	private void FilterNIHR(double winlength,double last,double minbpm, double maxbpm)
	{


		ulast=last;
		umean=1.5*ulast;
		vectorAux1.add(beatTime.get(0));
		vectorAux2.add(niHR.get(0));
		vectorAux3.add(RR.get(0));
		int index=1;
		while(index<niHR.size()-1)
		{

			for(int i=0;i<niHR.size();i++)
			{
				acumulador=acumulador+niHR.get(i);

			}

			M=acumulador/niHR.size();

			if ( ( (100*abs((niHR.get(index)-niHR.get(index-1))/niHR.get(index-1)) < ulast) ||
					(100*abs((niHR.get(index)-niHR.get(index+1))/niHR.get(index+1)) < ulast) ||
					(100*abs((niHR.get(index)-M)/M) < umean) )
					&& (niHR.get(index) > minbpm) && (niHR.get(index) < maxbpm))
			{
				vectorAux1.add(beatTime.get(index));
				vectorAux2.add(niHR.get(index));
				vectorAux3.add(RR.get(index));
				index += 1;
			}
			else
			{

				index += 1;

			}



		}
		vectorAux1.add(beatTime.get(beatTime.size()-1));
		vectorAux2.add(niHR.get(niHR.size()-1));
		vectorAux3.add(RR.get(RR.size()-1));
		beatTime.removeAllElements();
		niHR.removeAllElements();
		RR.removeAllElements();

		for(int i=0;i<vectorAux1.size();i++)
		{
			beatTime.add(vectorAux1.get(i));
			niHR.add(vectorAux2.get(i));
			RR.add(vectorAux3.get(i));

		}
		vectorAux1.removeAllElements();
		vectorAux2.removeAllElements();
		vectorAux3.removeAllElements();

	}
	//Método que establece la configuración inicial de la señal
	public void ConfigurarSeñal()
	{
		//Parámetros de configuración de la gráfica
		mySimpleXYPlot.setTicksPerRangeLabel(4);

		mySimpleXYPlot.disableAllMarkup();

		mySimpleXYPlot.getBackgroundPaint().setAlpha(0);
		mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setAlpha(0);
		mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setAlpha(0);  
		//Redibujar gráfica
		mySimpleXYPlot.redraw();

		//Configuración para boundaries
		mySimpleXYPlot.calculateMinMaxVals();
		minXY=new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),mySimpleXYPlot.getCalculatedMinY().floatValue());
		maxXY=new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),mySimpleXYPlot.getCalculatedMaxY().floatValue());
		mySimpleXYPlot.setScrollbarFadingEnabled(true);
		mySimpleXYPlot.setScrollContainer(true);

		mySimpleXYPlot.getLayoutManager()
		.remove(mySimpleXYPlot.getLegendWidget());
		//Etiqueta eje Y
		mySimpleXYPlot.setRangeLabel("Frecuencia cardíaca");
		//Etiqueta eje X
		mySimpleXYPlot.setDomainLabel("Tiempo en segundos");
		mySimpleXYPlot.getLayoutManager()
		.remove(mySimpleXYPlot.getTitleWidget());
		//Márgenes de la gráfica
		mySimpleXYPlot.setPlotMargins(10, 10, 10, 10);
		mySimpleXYPlot.setPlotPadding(10, 10, 10, 10);
	}

	//Método dibujar sin puntos
	public void Dibujar(int color0 , int color1 , int color2)
	{
		XYSeries series1 = new SimpleXYSeries(
				beatTime,          //Vector para representar en eje X
				niHR, //Vector para representar en eje Y
				"Variability heart");                             //Título de la gráfica

		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(color0,color1,color2),                  // Color de la línea
				null,                  // Color de los puntos
				null);              // Color de relleno


		mySimpleXYPlot.addSeries(series1, series1Format);
		ConfigurarSeñal();
	}

	//Método dibujar con puntos
	public void Dibujar2(int color0 , int color1 , int color2,int color3, int color4, int color5)
	{
		XYSeries series1 = new SimpleXYSeries(
				beatTime,          //Vector para representar en eje X
				niHR, //Vector para representar en eje Y
				"Variability heart");                             //Título de la gráfica

		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(color0,color1,color2),                  // Color de la línea
				Color.rgb(color3,color4,color5),                  // Color de los puntos
				null);              // Color de relleno


		mySimpleXYPlot.addSeries(series1, series1Format);
		ConfigurarSeñal();
	}
	//Método que permite realizar zoom y scroll sobre la señal haciendo touch sobre la pantalla
	public void Touchable()
	{
		mySimpleXYPlot.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {


				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: // Comienzo
					firstFinger = new PointF(event.getX(), event.getY());
					mode = ONE_FINGER_DRAG;
					break;
				case MotionEvent.ACTION_UP: 
				case MotionEvent.ACTION_POINTER_UP:
					//Comienzo del zoom y del scroll
					Timer t = new Timer();
					t.schedule(new TimerTask() {
						@Override
						public void run() {
							while(Math.abs(lastScrolling)>1f || Math.abs(lastZooming-1)<1.01){ 
								lastScrolling*=.8;
								scroll(lastScrolling);
								lastZooming+=(1-lastZooming)*.2;
								zoom(lastZooming);
								mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);

								try {
									mySimpleXYPlot.postRedraw();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}, 0);

				case MotionEvent.ACTION_POINTER_DOWN: // Segundo dedo
					distBetweenFingers = spacing(event);
					//Se trabaja con la distancia entre ambos dedos
					if (distBetweenFingers >5) {
						mode = TWO_FINGERS_DRAG;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == ONE_FINGER_DRAG) {
						PointF oldFirstFinger=firstFinger;
						firstFinger=new PointF(event.getX(), event.getY());
						lastScrolling=oldFirstFinger.x-firstFinger.x;
						scroll(lastScrolling);
						lastZooming=(firstFinger.y-oldFirstFinger.y)/mySimpleXYPlot.getHeight();
						if (lastZooming<0)
							lastZooming=1/(1-lastZooming);
						else
							lastZooming+=1;
						zoom(lastZooming);
						mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
						mySimpleXYPlot.redraw();

					} else if (mode == TWO_FINGERS_DRAG) {


						float oldDist =distBetweenFingers; 
						distBetweenFingers=spacing(event);
						lastZooming=oldDist/distBetweenFingers;
						zoom(lastZooming);
						mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
						mySimpleXYPlot.redraw();



					}
					break;
				}
				return true;

			}
		});
	}
	//Función que selecciona si se redibuja la señal con o sin los puntos que la conforman
	public void SeleccionarModoRepintado()
	{
		if(color3!=0||color4!=0||color5!=0)
		{
			Dibujar2(color0,color1,color2,color3,color4,color5);
		}
		else
		{
			Dibujar(color0, color1, color2);
		}
	}

	//Función a la que se vuelve tras recoger datos para filtrado y edición por ejemplo
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Si el caso es 1, es el caso de editar la señal
		if (requestCode == 1) {

			if(resultCode == RESULT_OK){  
				//Recibir datos
				String desde=data.getStringExtra("desde");  
				String hasta=data.getStringExtra("hasta");          

				desded= Double.parseDouble(desde);
				hastad= Double.parseDouble(hasta);
				vectorAux1.removeAllElements();
				vectorAux2.removeAllElements();
				vectorAux3.removeAllElements();
				//Rellenar vectores auxiliares
				for(int i =0;i<beatTime.size();i++)
				{

					actual=niHR.get(i);
					actual2=beatTime.get(i);
					actual3=RR.get(i);



					if(actual2<=desded || actual2>=hastad)
					{
						vectorAux1.add(actual);
						vectorAux2.add(actual2);
						vectorAux3.add(actual3);


					}


				}
				//Borrar contenido vectores iniciales
				niHR.removeAllElements();
				beatTime.removeAllElements();
				RR.removeAllElements();
				//Dar la vuelta al contenido de los vectores
				for(int i=0;i<vectorAux1.size();i++)
				{
					actual= vectorAux1.get(i);
					actual2= vectorAux2.get(i);
					actual3= vectorAux3.get(i);

					niHR.add(actual);
					beatTime.add(actual2);
					RR.add(actual3);


				}
				//Borrar contenido de los vectores auxiliares
				vectorAux1.removeAllElements();
				vectorAux2.removeAllElements();
				vectorAux3.removeAllElements();

				//Lanzar vista de la representación gráfica
				setContentView(R.layout.representar);

				mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
				//Permite el zoom y scroll de la señal haciendo touch en la pantalla
				Touchable();

				mySimpleXYPlot.clear();
				//Repintar la señal con o sin los puntos que la forman
				SeleccionarModoRepintado();
				
				Toast.makeText(getApplicationContext(),"Señal editada correctamente", Toast.LENGTH_LONG).show();


			}
		}
		//Si el caso es 2, es el modo de filtrado manual
		else if (requestCode == 2) {

			if(resultCode == RESULT_OK)
			{  
				//Recogida de datos
				String min=data.getStringExtra("min");  
				String max=data.getStringExtra("max");          

				mini= Double.parseDouble(min);
				maxi= Double.parseDouble(max);
				//Rellenar vectores auxiliares
				for(int i =0;i<niHR.size();i++)
				{

					actual=niHR.get(i);
					actual2=beatTime.get(i);
					actual3=RR.get(i);

					if(actual>mini && actual<maxi)
					{
						vectorAux1.add(actual);
						vectorAux2.add(actual2);
						vectorAux3.add(actual3);
					}
				}
				niHR.removeAllElements();
				beatTime.removeAllElements();
				RR.removeAllElements();
				//Cambiar contenido a vectores originales
				for(int i=0;i<vectorAux1.size();i++)
				{
					actual= vectorAux1.get(i);
					actual2= vectorAux2.get(i);
					actual3= vectorAux3.get(i);

					niHR.add(actual);
					beatTime.add(actual2);
					RR.add(actual3);
				}
				//Borrar contenido de los vectores auxiliares
				vectorAux1.removeAllElements();
				vectorAux2.removeAllElements();
				vectorAux3.removeAllElements();

				//Llamar a la vista de presentar la gráfica
				setContentView(R.layout.representar);

				mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
				//Permite el zoom y scroll de la señal haciendo touch en la pantalla
				Touchable();

				mySimpleXYPlot.clear();
				//Repintar la señal con o sin los puntos que la forman
				SeleccionarModoRepintado();
				Toast.makeText(getApplicationContext(),"Señal filtrada correctamente", Toast.LENGTH_LONG).show();

			}	

		}

		//Si el caso es 3 es el modo de exportar imagen en formato JPEG
		else if (requestCode == 3) {

			if(resultCode == RESULT_OK)
			{  
				//Conseguir el nombre
				String nombre=data.getStringExtra("nombre");  


				mySimpleXYPlot.setDrawingCacheEnabled(true);
				int width = mySimpleXYPlot.getWidth();
				int height = mySimpleXYPlot.getHeight();
				mySimpleXYPlot.measure(width, height);
				Bitmap bmp = Bitmap.createBitmap(mySimpleXYPlot.getDrawingCache());

				//Acceso a la SDCard
				String path = Environment.getExternalStorageDirectory().toString();
				OutputStream fOut = null;
				File file = new File(path+"/FC_aplication/", nombre+".JPEG");
				try{
					fOut = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					fOut.flush();
					fOut.close();
					MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

					Toast.makeText(Representar.this, "Señal exportada en formato JPEG", Toast.LENGTH_LONG).show();

				}
				catch (Exception e) {
					Log.e("Error", "Fallo al exportar en formato JPEG");
				}


			}
		}

		//Si el caso es 4
		else if (requestCode == 4) {

			if(resultCode == RESULT_OK)
			{  
				String nombre=data.getStringExtra("nombre");  


				mySimpleXYPlot.setDrawingCacheEnabled(true);
				int width1 = mySimpleXYPlot.getWidth();
				int height1 = mySimpleXYPlot.getHeight();
				mySimpleXYPlot.measure(width1, height1);
				Bitmap bmp1 = Bitmap.createBitmap(mySimpleXYPlot.getDrawingCache());
				String path1 = Environment.getExternalStorageDirectory().toString();
				OutputStream fOut1 = null;
				File file1 = new File(path1+"/FC_aplication/", nombre+".PNG");
				try{
					fOut1 = new FileOutputStream(file1);
					bmp1.compress(Bitmap.CompressFormat.PNG, 100, fOut1);
					fOut1.flush();
					fOut1.close();
					MediaStore.Images.Media.insertImage(getContentResolver(),file1.getAbsolutePath(),file1.getName(),file1.getName());
					Toast.makeText(Representar.this, "Señal exportada en formato PNG", Toast.LENGTH_LONG).show();

				}
				catch (Exception e) {
					Log.e("Error", "Fallo al exportar en formato PNG");

				}


			}
		}


	}







}
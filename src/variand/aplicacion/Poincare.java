package variand.aplicacion;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class Poincare extends Activity{

	Vector<Double> RR1 = new Vector<Double>();		//vector que contiene nihr en milisegundos una unidad adelantado
	Vector<Double> RRaux = new Vector<Double>();		//vector RR con un parámetro de menos

	public static XYPlot mySimpleXYPlot;
	public static PointF minXY;
	public static PointF maxXY;




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poincare);


		//Rellenar vector RR1
		for(int i=1;i<Representar.RR.size();i++)
		{
			RR1.add(Representar.RR.get(i));
		}
		//Rellenar vector RRaux
		for(int i=0;i<Representar.RR.size()-1;i++)
		{
			RRaux.add(Representar.RR.get(i));

			Log.i("RRaux", String.valueOf(Representar.RR.get(i)));
		}


		// Declarar el Plot
		mySimpleXYPlot = (XYPlot) findViewById(R.id.plotPoincare);

		//Configurar el Plot
		XYSeries series1 = new SimpleXYSeries(
				RRaux,          // Vector a representar en el eje X
				RR1, // Vector a representar en el eje Y
				"Poincaré");                            //Etiqueta


		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.TRANSPARENT,                  // Color de la línea
				Color.rgb(Representar.color0,Representar.color1,Representar.color2),        //Color de los puntos
				null);              // Color de relleno

		//Propiedades para configurar el Plot
		mySimpleXYPlot.addSeries(series1, series1Format);
		mySimpleXYPlot.setTicksPerRangeLabel(4);
		mySimpleXYPlot.disableAllMarkup();
		mySimpleXYPlot.getBackgroundPaint().setAlpha(0);
		mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setAlpha(0);
		mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setAlpha(0);  
		//Redibujar el Plot
		mySimpleXYPlot.redraw();

		//Configuración boundaries
		mySimpleXYPlot.calculateMinMaxVals();
		//Cálculo del valor mínimo y máximo
		minXY=new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),mySimpleXYPlot.getCalculatedMinY().floatValue());
		maxXY=new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),mySimpleXYPlot.getCalculatedMaxY().floatValue());
		mySimpleXYPlot.setScrollbarFadingEnabled(true);
		mySimpleXYPlot.setScrollContainer(true);

		mySimpleXYPlot.getLayoutManager()
		.remove(mySimpleXYPlot.getLegendWidget());
		//Etiquetas vertical y horizontal
		mySimpleXYPlot.setRangeLabel("RR N+1");
		mySimpleXYPlot.setDomainLabel("RR N");
		mySimpleXYPlot.getLayoutManager()
		.remove(mySimpleXYPlot.getTitleWidget());
		//Márgenes del Plot
		mySimpleXYPlot.setPlotMargins(10, 10, 10, 10);
		mySimpleXYPlot.setPlotPadding(10, 10, 10, 10);
		//Comienzo y fin del plot
		mySimpleXYPlot.setDomainBoundaries(mySimpleXYPlot.getCalculatedMinX().floatValue()-10.0,mySimpleXYPlot.getCalculatedMaxX().floatValue()+10.0, BoundaryMode.FIXED);
		mySimpleXYPlot.setRangeBoundaries(mySimpleXYPlot.getCalculatedMinY().floatValue()-10.0,mySimpleXYPlot.getCalculatedMaxY().floatValue()+10.0, BoundaryMode.FIXED);

	}



	//Coger opciones menú
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menupoincare, menu);
		return true;
	}



	//Opción seleccionada
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		//Exportar en formato JPEG
		case R.id.exportarjpeg:

			Intent exportarjpeg = new Intent(this, Exportar.class);
			startActivityForResult(exportarjpeg, 0);

			return true;
			//Exportar en formato PNG
		case R.id.exportarpng:

			Intent exportarpng = new Intent(this, Exportar.class);
			startActivityForResult(exportarpng, 1);

			return true;

		}
		return false;
	}

	//Función a la que se salta después de la introducción de datos en otros Activities
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//Si el código es cero, se trata de exportar la imagen en formato JPEG
		if (requestCode == 0) {

			if(resultCode == RESULT_OK)
			{  
				String nombre=data.getStringExtra("nombre");  


				mySimpleXYPlot.setDrawingCacheEnabled(true);
				int width = mySimpleXYPlot.getWidth();
				int height = mySimpleXYPlot.getHeight();
				mySimpleXYPlot.measure(width, height);
				Bitmap bmp = Bitmap.createBitmap(mySimpleXYPlot.getDrawingCache());
				//Acceso a SDCard
				String path = Environment.getExternalStorageDirectory().toString();
				OutputStream fOut = null;
				File file = new File(path+"/FC_aplication/", nombre+".JPEG");
				try{
					fOut = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					fOut.flush();
					fOut.close();
					MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

					Toast.makeText(Poincare.this, "Señal exportada en formato JPEG", Toast.LENGTH_LONG).show();

				}
				catch (Exception e) {}


			}
		}

		//Si el código es cero, se trata de exportar la imagen en formato PNG
		else if (requestCode == 1) {

			if(resultCode == RESULT_OK)
			{  
				String nombre=data.getStringExtra("nombre");  


				mySimpleXYPlot.setDrawingCacheEnabled(true);
				int width1 = mySimpleXYPlot.getWidth();
				int height1 = mySimpleXYPlot.getHeight();
				mySimpleXYPlot.measure(width1, height1);
				//Acceso a SDCard
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
					Toast.makeText(Poincare.this, "Señal exportada en formato PNG", Toast.LENGTH_LONG).show();

				}
				catch (Exception e) {}


			}
		}


	}


}


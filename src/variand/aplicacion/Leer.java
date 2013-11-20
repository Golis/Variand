package variand.aplicacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//Clase que permite visualizar el contenido de un archivo, ya sea de imagen o de texto
@SuppressLint("SdCardPath")
public class Leer extends Activity {


	String nombrefil;

	int i =0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leer);
		//Declaración elementos xml
		TextView mostrar = (TextView)findViewById(R.id.mostrar);
		Button cerrar = (Button)findViewById(R.id.cerrar);
		ImageView imagen=(ImageView)findViewById(R.id.imagen);

		//Botón cerrar ventana
		cerrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});
		//Recibir nombre del fichero
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{
			nombrefil = extras.getString("nombre");
		}
		//Si el nombre del fichero contiene la extensión txt
		if(nombrefil.contains("txt"))
		{

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
			//Leemos línea por línea
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {

					text.append(line);
					text.append('\n');
					i++;
				}
			}
			catch (IOException e) {

			}

			mostrar.setText(text);

		}
		// Si el fichero es de imagen
		else if(nombrefil.contains("PNG") || nombrefil.contains("JPEG"))
		{
		Bitmap bMap = BitmapFactory.decodeFile("/sdcard/FC_aplication/"+nombrefil);


		imagen.setImageBitmap(bMap);
		
		}
	}



}



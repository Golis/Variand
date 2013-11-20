package variand.aplicacion;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.dropbox.client2.DropboxAPI.Entry;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//Clase que permite subir archivos a Dropbox
public class ListSubida extends Activity {
	//Vector que almacena los archivos
	final ArrayList<ItemGhrv> items = new ArrayList<ItemGhrv>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listsubida);

		
		//Acceso a la SDCad
		File f = new File(Environment.getExternalStorageDirectory(),
				"FC_aplication");
		//Comprobar tamaño del path de la SDCard 
				String tamano=Environment.getExternalStorageDirectory().toString();
				//Le sumamos 15 caracteres del nombre de nuestra carpeta
				int numCaracteres=tamano.length()+15;
		//Si la carpeta no existe, se crea
		if (!f.exists()) {
			try {
				f.mkdirs();
			} catch (Exception ex) {
				Log.e("Carpeta", "Imposible crear carpeta");
			}
		}

		File[] files=f.listFiles();
		//Se añaden al vector items los nombres de los archivos
		for(int i=0; i<files.length; i++)
		{
			File file = files[i];
			/*It's assumed that all file in the path are in supported type*/
			String filePath = file.getPath();
			if(filePath.endsWith(".txt")||filePath.endsWith(".PNG")||filePath.endsWith(".JPEG")) // Condition to check .txt file extension

			{
				items.add(new ItemGhrv(i, filePath.substring(numCaracteres), filePath.substring(numCaracteres), "drawable/flechasubidalist"));

			}

		}
		//Se crea el listview
		ListView lv = (ListView) findViewById(R.id.mylist);
		//Se genera nuestro adaptador
		final MiAdapter adapter2 = new MiAdapter(this, items);
		//Se asignta un adaptador a un listview
		lv.setAdapter(adapter2);  
		//listener del listview
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, final int posicion,
					long id) 
			{
				//Si posición mayor que cero
				if(posicion>=0)
				{

					//Se lanza un ProgressDialog mientras carga
					final ProgressDialog pd2 = ProgressDialog.show(ListSubida.this,"Subiendo archivo...","Espere, por favor",true, false);

					new Thread(new Runnable(){
						public void run(){

							FileInputStream inputStream = null;
							try {
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
								File file = new File(f,items.get(posicion).getNombre());
								inputStream = new FileInputStream(file);
								Entry newEntry = Dropbox.mDBApi.putFile(items.get(posicion).getNombre(), inputStream,
										file.length(), null, null);
								//Lanzar mensaje informativo correcto
								Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
								Intent bien= new Intent(getApplicationContext(),SubidoCorrecto.class);
								startActivity(bien);
								finish();
								pd2.dismiss();
							}
							catch (Exception e) {
								//Lanzar mensaje informativo erróneo
								Log.e("Error", "Algo ha fallado al subir el archivo a Dropbox");
								Intent volver= new Intent(getApplicationContext(),SubidoErroneo.class);
								startActivity(volver);
								finish();
								pd2.dismiss();

							}
							finally {
								if (inputStream != null) {
									try {
										inputStream.close();
									} catch (IOException e) {}
								}
							}


						}
					}).start();	


				}	



			}


		});




	}


}
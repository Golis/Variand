package variand.aplicacion;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
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

//Clase que permite descargar archivos de Dropbox
public class ListBajada extends Activity {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listbajada);

		//Declarar list view
		ListView lv = (ListView) findViewById(R.id.mylist);
		
		final MiAdapter adapter2 = new MiAdapter(this, Dropbox.items);
		//linkar listview con el adaptador
		lv.setAdapter(adapter2);  

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, final int posicion,
					long id) 
			{



				//Si posición es mayor que cero
				if(posicion>=0)
				{



					//Declarar pogress dialog
					final ProgressDialog pd2 = ProgressDialog.show(ListBajada.this,"Descargando archivo...","Espere, por favor",true, false);
					//hilo para descargar archivos de Dropbox
					new Thread(new Runnable(){
						public void run(){


							FileOutputStream outputStream = null;
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
								File file = new File(f,Dropbox.items.get(posicion).getNombre());
								outputStream = new FileOutputStream(file);
								DropboxFileInfo info = Dropbox.mDBApi.getFile(Dropbox.items.get(posicion).getNombre(), null, outputStream, null);
								Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
								//Lanzar mensaje informativo descargado correcto
								Intent volver= new Intent(getApplicationContext(),DescargadoCorrecto.class);
								startActivity(volver);
								finish();
								pd2.dismiss();
							}	catch (Exception e) {
								//Lanzar mensaje informativo erróneo
								Log.e("Error", "Algo ha fallado al descargar el archivo desde Dropbox");
								Intent volver= new Intent(getApplicationContext(),DescargadoErroneo.class);
								startActivity(volver);
								finish();
								pd2.dismiss();

							} 
							finally {
								if (outputStream != null) {
									try {
										outputStream.close();
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
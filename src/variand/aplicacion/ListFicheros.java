package variand.aplicacion;


import java.io.File;
import java.util.ArrayList;
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

public class ListFicheros extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listficheros);
		//Vector que almacena los ficheros
		final ArrayList<ItemGhrv> items = new ArrayList<ItemGhrv>();

		//Acceso a la SDCard
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
		//Añadir todos los ficheros con extensión txt al vector items
		for(int i=0; i<files.length; i++)
		{
			File file = files[i];
			String filePath = file.getPath();
			if(filePath.endsWith(".txt")) //Se comprueba que sea extensión txt
			{
				items.add(new ItemGhrv(i, filePath.substring(numCaracteres), filePath.substring(numCaracteres), "drawable/cardiogramalist"));
			}
		}


		ListView lv = (ListView) findViewById(R.id.mylist);

		//Linkar el adaptador a un listview
		final MiAdapter adapter2 = new MiAdapter(this, items);

		lv.setAdapter(adapter2);  
		//listener del listview
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, final int posicion,
					long id) 
			{


				//si posicion del listview es mayor que cero

				if(posicion>=0)
				{


					final ProgressDialog pd2 = ProgressDialog.show(ListFicheros.this,"Cargando...","Espere, por favor",true, false);

					new Thread(new Runnable(){
						public void run(){
							Intent representar= new Intent(getApplicationContext(),Representar.class);
							representar.putExtra("nombrefich",items.get(posicion).getNombre());
							try{
							Thread.sleep(3000);
							startActivity(representar);
							pd2.dismiss();
							}
							catch (Exception e) {
								pd2.dismiss();
							}
						}
					}).start();	

				}	



			}


		});




	}
}
package variand.aplicacion;




import java.io.File;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//Clase que permite la gestión de la SDCard
@SuppressLint("SdCardPath")
public class GestionSdcard extends Activity {
	//Array que almacena los ficheros
	final ArrayList<ItemGhrv> items = new ArrayList<ItemGhrv>();
	static String nombreAntiguo;
	private static final int EDIT_NAME= Menu.FIRST;
	private static final int DELETE_FILE= Menu.FIRST+1;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestionsdcard);

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
		//Meter todos los archivos en el vector items
		for(int i=0; i<files.length; i++)
		{
			File file = files[i];
			String filePath = file.getPath();
			if(filePath.endsWith(".txt")||filePath.endsWith(".PNG")||filePath.endsWith(".JPEG")) // Condition to check .txt file extension

			{
				items.add(new ItemGhrv(i, filePath.substring(numCaracteres), filePath.substring(numCaracteres), "drawable/sdcardlist"));

			}

		}


		//Declarar listview
		ListView lv = (ListView) findViewById(R.id.mylist);

		registerForContextMenu(lv);

		final MiAdapter adapter2 = new MiAdapter(this, items);
		//Linkar el adaptador con el listview
		lv.setAdapter(adapter2);  

		//Listener del listview, para clic simple sobre archivo
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, final int posicion,
					long id) 
			{



				//Si posición mayor que cero
				if(posicion>=0)
				{
					//Llamada a la clase que muestra el contenido
					Intent leer = new Intent();
					leer.putExtra("nombre", items.get(posicion).getNombre());
					leer.setClass(getApplicationContext(), Leer.class);
					startActivity(leer);

				}	



			}


		});



	}

	//Método que permite retornar después de la llamada a editar o eliminar
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Si los datos son nulos
		if(data==null)
		{
			Log.i(String.valueOf(requestCode), "cancelado");
		}
		//Caso en el que el código sea 1 y los datos no sean nulos
		if (requestCode == 1 && data!=null)
		{
			String nombre=data.getStringExtra("nombre");  



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

			File from = new File(f,nombreAntiguo);
			//Si es un txt
			if(nombreAntiguo.contains("txt"))
			{
				File to = new File(f,nombre+".txt");
				from.renameTo(to);

				Toast.makeText(getApplicationContext(), "El fichero ha sido editado correctamente", Toast.LENGTH_LONG).show();
			}
			//Si es un PNG
			else if(nombreAntiguo.contains("PNG"))
			{
				File to = new File(f,nombre+".PNG");
				from.renameTo(to);
				Toast.makeText(getApplicationContext(), "El fichero ha sido editado correctamente", Toast.LENGTH_LONG).show();

			}
			//Si es un JPEG
			else if(nombreAntiguo.contains("JPEG"))
			{
				File to = new File(f,nombre+".JPEG");
				from.renameTo(to);
				Toast.makeText(getApplicationContext(), "El fichero ha sido editado correctamente", Toast.LENGTH_LONG).show();

			}
			finish();
			Intent renovar = new Intent();
			renovar.setClass(getApplicationContext(), GestionSdcard.class);
			startActivity(renovar);




		}
	}


	//Método que permite generar una ventana de confirmación de borrado
	private Dialog crearDialogoConfirmacion(String name,final int posicion)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// builder.setTitle("Confirmacion");
		builder.setMessage("¿Desea borrar el fichero "+name +"?");
		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				File file = new File("/sdcard/FC_aplication/"+items.get(posicion).getNombre());
				file.delete();	        dialog.cancel();
				finish();
				Intent renovar = new Intent();
				renovar.setClass(getApplicationContext(), GestionSdcard.class);
				startActivity(renovar);
				Toast.makeText(getApplicationContext(), "El fichero ha sido eliminado correctamente", Toast.LENGTH_LONG).show();

			}
		});
		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("Dialogos", "Confirmacion Cancelada.");
				dialog.cancel();
			}
		});

		return builder.create();
	}



	//Crea los items del menú contextual, es deicr, aquel que sale al hacer un clic largo
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_NAME, 0, "Cambiar el nombre del fichero");
		menu.add(0, DELETE_FILE, 0,  "Borrar el fichero de la SDCARD");
	}

	//Casos posibles del menú contextual
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		//Caso editar nombre
		case EDIT_NAME:
			//Se lanza la ventana de recogida de datos
			nombreAntiguo=items.get(info.position).getNombre();
			Intent cambiar = new Intent(this, CambiarNombre.class);
			cambiar.putExtra("nombreantiguo", nombreAntiguo);
			startActivityForResult(cambiar, 1);
			return true;
			//Caso de eliminar un fichero
		case DELETE_FILE:
			//Se lanza la ventana de confirmación de borrado
			int posicion=info.position;
			crearDialogoConfirmacion(items.get(info.position).getNombre(),posicion).show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}




}


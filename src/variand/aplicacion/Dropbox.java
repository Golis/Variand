package variand.aplicacion;



import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

//Clase que permite la sincronización con Dropbox
public class Dropbox extends Activity {
	//Array ficheros
	final static ArrayList<ItemGhrv> items = new ArrayList<ItemGhrv>();
	//Claves
	public static String appKey = "wtg6sxf30wrj80q";
	public static String appSecret = "2fk0dwp49tpu7bj";
	//Tipo de acceso
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	//Sesión
	public static DropboxAPI<AndroidAuthSession> mDBApi;
	//Variable contador de accesos a sesión
	int cont=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropbox);
		//Declaración elementos xml
		ImageButton upload = (ImageButton)findViewById(R.id.upload);
		ImageButton download = (ImageButton)findViewById(R.id.download);

		//Inicialización de la sesión con las claves
		AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		mDBApi.getSession().startAuthentication(Dropbox.this);

		//Botón subir archivo
		upload.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				//Cargar lista de archivos para subir
				Intent subir = new Intent();
				subir.setClass(getApplicationContext(), ListSubida.class);
				startActivity(subir);
			}
		});
		//Botón descargar archivo
		download.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {


				final ProgressDialog pd2 = ProgressDialog.show(Dropbox.this,"Cargando...","Espere, por favor",true, false);

				new Thread(new Runnable(){
					public void run(){
						//Borrar elementos del vector items
						if(!items.isEmpty())
						{
							items.removeAll(items);
						}
						Entry contact = null;

						try {
							contact = mDBApi.metadata("/", 0, null, true, null);
						} catch (DropboxException e) {
							Log.e("Error", "Fallo en la conexión a Internet");
						}
						// Listar archivos de Dropbox
						List<Entry> CFolder = contact.contents;
						for (Entry entry : CFolder) {
							Log.i("DbExampleLog", "Filename: " + entry.fileName());
							items.add(new ItemGhrv(0, entry.fileName(), entry.fileName(), "drawable/flechabajadalist"));
						}


						//Lanzar vista con la lista de archivos de Dropbox
						Intent bajar = new Intent();
						bajar.setClass(getApplicationContext(), ListBajada.class);
						startActivity(bajar);
						pd2.dismiss();

					}
				}).start();	





			}

		});


	}


	protected void onResume() {
		super.onResume();

		//Comprobar autenticación en Dropbox
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {

				mDBApi.getSession().finishAuthentication();
				cont=1;

			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
		else
		{
			if(cont!=0)
			{
				finish();
			}
			cont=1;
		}
		
	}


	

}


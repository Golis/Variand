package variand.aplicacion;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Clase inicial
public class Main extends Activity {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Declaración elementos xml
		ImageButton sd = (ImageButton)findViewById(R.id.sd);
		ImageButton dropbox = (ImageButton)findViewById(R.id.dropbox);
		ImageButton sdc = (ImageButton)findViewById(R.id.sdc);
		//Botón para representar ficheros
		sd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent  sd = new Intent();
				sd.setClass(getApplicationContext(), ListFicheros.class);
				startActivity(sd);

			}
		});
		//Botón para sincronizar con Dropbox
		dropbox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent dropbox = new Intent();
				dropbox.setClass(getApplicationContext(), Dropbox.class);
				startActivity(dropbox);

			}
		});
		//Botón para Gestionar SD Card
		sdc.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent sdcard = new Intent();
				sdcard.setClass(getApplicationContext(), GestionSdcard.class);
				startActivity(sdcard);

			}
		});
		
	}
	
	

}
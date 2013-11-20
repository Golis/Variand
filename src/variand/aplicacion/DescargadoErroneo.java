package variand.aplicacion;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//Clase que informa mediante un cartel de si se produjeron problemas al descargar un archivo
public class DescargadoErroneo extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descargadoerroneo);

		//Declaraci�n elementos xml
		Button aceptar = (Button)findViewById(R.id.aceptar);
		
		//Bot�n aceptar
		aceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Lanzar mensaje informativo
				finish();
				Intent aceptar = new Intent();
				aceptar.setClass(getApplicationContext(), Main.class);
				startActivity(aceptar);
			}
		});
		
	
		
	}


}


package variand.aplicacion;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//Clase que informa de si las subidas de archivos han tenido alguna anomalía
public class SubidoErroneo extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subidoerroneo);

		//Declaración elementos xml
		Button aceptar = (Button)findViewById(R.id.aceptar);
		//Botón aceptar
		aceptar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//lanzar mensaje informativo
				finish();
				Intent aceptar = new Intent();
				aceptar.setClass(getApplicationContext(), Main.class);
				startActivity(aceptar);
			}
		});
		
	
		
	}


}


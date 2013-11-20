package variand.aplicacion;




import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

//Clase que informa mediante un cartel de cuándo se debe inteprolar antes de calcular parámetros frecuenciales
public class BloquearOpcion extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descargadocorrecto);

		//Declaración elementos xml
		Button aceptar = (Button)findViewById(R.id.aceptar);
		TextView descargado = (TextView)findViewById(R.id.descargado);

		descargado.setText("Debe interpolar la señal antes de realizar los cálculos frecuenciales");

		//Botón aceptar
		aceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});



	}


}


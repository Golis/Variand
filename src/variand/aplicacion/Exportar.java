package variand.aplicacion;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//Clase que recoge el nombre con el que se va a exportar la captura de pantalla
public class Exportar extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exportar);

		//Declaración elementos xml
		Button aplicar = (Button)findViewById(R.id.aplicar);
		Button cancelar = (Button)findViewById(R.id.cancelar);
		final EditText nombre = (EditText)findViewById(R.id.insertnombre);
		//Botón aplicar
		aplicar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!nombre.getText().toString().isEmpty())
				{
					//Lanzar vista para recogida de datos
					Intent returnIntent = new Intent();

					returnIntent.putExtra("nombre",nombre.getText().toString());
					setResult(RESULT_OK,returnIntent);     
					finish();
									

				}
				else if(nombre.getText().toString().isEmpty())
				{
					nombre.requestFocus();
				}
				
			}
		});
		//Botón cancelar
		cancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});



	}


}


package variand.aplicacion;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


//Esta clase captura los datos en segundos para aplicar al filtro manual de la aplicación
public class FiltroManual extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filtromanual);

		//Declaración de elementos xml
		Button aplicar = (Button)findViewById(R.id.aplicar);
		Button cancelar = (Button)findViewById(R.id.cancelar);
		final EditText insertmin= (EditText)findViewById(R.id.insertmin);
		final EditText insertmax= (EditText)findViewById(R.id.insertmax);
		//Botón para aplicar filtro
		aplicar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!insertmin.getText().toString().isEmpty() && !insertmax.getText().toString().isEmpty())
				{
					//Recogida de datos de los campos de texto
					Intent volver = new Intent();
					volver.putExtra("min",insertmin.getText().toString());
					volver.putExtra("max",insertmax.getText().toString());
					 setResult(RESULT_OK,volver);     
					 finish();
									

				}
				//Validaciones de relleno correcto de todos los campos de texto necesarios
				else if(insertmin.getText().toString().isEmpty())
				{
					insertmin.requestFocus();
				}
				else if(insertmax.getText().toString().isEmpty())
				{
					insertmax.requestFocus();
				}
				else
				{
					insertmin.requestFocus();

				}

				
				
				
			}
		});
		//Botón que cancela la acción
		cancelar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
	
		
	}


}


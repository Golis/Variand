package variand.aplicacion;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//Clase que recoge los datos en segundos que se van a aplicar a la edici�n de la se�al
public class Editar extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar);

		//Declaraci�n elementos xml
		Button aplicar = (Button)findViewById(R.id.apply);
		Button cancelar = (Button)findViewById(R.id.cancel);
		final EditText desde = (EditText)findViewById(R.id.desde);
		final EditText hasta = (EditText)findViewById(R.id.hasta);
		//Bot�n aplicar
		aplicar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!desde.getText().toString().isEmpty() && !hasta.getText().toString().isEmpty())
				{
					//Lanzar ventana recogida de datos
					Intent returnIntent = new Intent();

					returnIntent.putExtra("desde",desde.getText().toString());
					returnIntent.putExtra("hasta",hasta.getText().toString());

					setResult(RESULT_OK,returnIntent);     
					finish();
									

				}
				//Validaciones de relleno de los campos de texto
				else if(desde.getText().toString().isEmpty())
				{
					desde.requestFocus();
				}
				else if(hasta.getText().toString().isEmpty())
				{
					hasta.requestFocus();
				}
				else
				{
					desde.requestFocus();

				}


			}
		});
		//Bot�n cancelar
		cancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});



	}


}


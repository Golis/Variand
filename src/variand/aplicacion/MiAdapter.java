package variand.aplicacion;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Clase en la que se genera nuestro propio objeto Adapter
public class MiAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ItemGhrv> items;
	//Constructor
	public MiAdapter(Activity activity, ArrayList<ItemGhrv> items) {
		this.activity = activity;
		this.items = items;
	}
	//Métodos get
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}
	//Método para inflar el objeto
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;

		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.milist, null);
		}

		ItemGhrv item = items.get(position);

		ImageView image = (ImageView) vi.findViewById(R.id.cardiogramalist);
	    int imageResource = activity.getResources().getIdentifier(item.getRutaImagen(), null, activity.getPackageName());
	    image.setImageDrawable(activity.getResources().getDrawable(imageResource));
		 

		TextView nombre = (TextView) vi.findViewById(R.id.nombre);
		
	
		//Variación de colores según la posición de la fila
		if(position %2 == 0)
			nombre.setBackgroundColor(Color.GRAY);
		else
			nombre.setBackgroundColor(Color.WHITE);
		
		nombre.setText(item.getNombre());




		return vi;
	}
}
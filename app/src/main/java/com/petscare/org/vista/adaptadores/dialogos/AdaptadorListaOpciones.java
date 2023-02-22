
package com.petscare.org.vista.adaptadores.dialogos;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.petscare.org.modelo.objetos.Item1;

import java.util.ArrayList;
public abstract class AdaptadorListaOpciones {

        public static ListAdapter getAdaptador(final Activity actividad, final ArrayList<Item1> items) {

            //Adaptador para los items del dialogo

            return new ArrayAdapter<Item1>(actividad, android.R.layout.select_dialog_item, android.R.id.text1, items) {
                public View getView(int position, View convertView, ViewGroup parent) {

                    //Usar la superclase para personalizar el textview
                    View item_view = super.getView(position, convertView, parent);
                    TextView txt_item = (TextView) item_view.findViewById(android.R.id.text1);
                    txt_item.setTextSize(20);

                    //Mostrar imagen en un textview
                    txt_item.setCompoundDrawablesWithIntrinsicBounds(items.get(position).getIcon(), 0, 0, 0);

                    //Agregar margen en la imagen y en el texto de los items
                    int dp5 = (int) (15 * actividad.getResources().getDisplayMetrics().density + 0.5f);
                    txt_item.setCompoundDrawablePadding(dp5);

                    //retornar la vista de los items
                    return item_view;
                }
            };
        }
    }


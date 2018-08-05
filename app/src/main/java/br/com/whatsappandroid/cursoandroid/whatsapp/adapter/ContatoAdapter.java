package br.com.whatsappandroid.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;

public class ContatoAdapter extends ArrayAdapter<Contato> {
    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(@NonNull Context c, @NonNull ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.contatos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;
        //Verifica se a lista esta vazia
        if(contatos != null){
            //Inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Monta a View a partir do XML
            view = inflater.inflate(R.layout.lista_contato,parent,false);

            //Recupera elementos para exibição
            TextView nomeContato = (TextView)view.findViewById(R.id.tv_nome);
            TextView email = (TextView)view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nomeContato.setText(contato.getNome());
            email.setText(contato.getEmail());


        }

        return view;
    }
}

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
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;

public class ConversaAdapter extends ArrayAdapter<Conversa> {
    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c,0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //Verifica se a lista esta vazia
        if(conversas != null){
            //Inicializar objeto pra montagem da view
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Monta View a partir do XML
            view = inflater.inflate(R.layout.lista_conversa, parent, false);

            //Recupera elementos para exibicao
            TextView nomeDestinatario = (TextView)view.findViewById(R.id.tv_nome_destinatario);
            TextView ultimaMensagem = (TextView)view.findViewById(R.id.tv_ultima_mensagem);

            Conversa conversa = conversas.get(position);
            nomeDestinatario.setText(conversa.getNome());
            ultimaMensagem.setText(conversa.getMensagem());

        }

        return view;
    }
}

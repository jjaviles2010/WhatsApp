package br.com.whatsappandroid.cursoandroid.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.activity.ConversaActivity;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.ConversaAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listViewConversas;
    private ArrayAdapter adapterConversas;
    private ArrayList<Conversa> listConversas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConveras;
    private ValueEventListener valueEventListenerContato;
    private String identUsuario = "";
    private  Contato contato;

    public ConversasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConveras);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConveras);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listConversas = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        listViewConversas =(ListView)view.findViewById(R.id.lv_conversas);

        adapterConversas = new ConversaAdapter(getActivity(),listConversas);

        listViewConversas.setAdapter(adapterConversas);

        //Recuperar Conversas do Firebase
        Preferencias preferences = new Preferencias(getActivity());
        identUsuario = preferences.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase().child("conversas").child(identUsuario);

        //Listener para recuperar Conversas
        valueEventListenerConveras = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpa a lista de conversas
                listConversas.clear();

                for (DataSnapshot dato: dataSnapshot.getChildren()){
                    Conversa conversa = dato.getValue(Conversa.class);
                    listConversas.add(conversa);
                }

                adapterConversas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listViewConversas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //Recupera dados a serem pasados
                Conversa conversa = listConversas.get(position);
                String idUsuario = conversa.getIdUsuario();
                String emailUsuario = Base64Custom.decodificarBase64(idUsuario);


                //enviando dados para ConversaActivity
                intent.putExtra("nome",conversa.getNome());
                intent.putExtra("email",emailUsuario);
                startActivity(intent);
            }
        });

        return view;
    }

}

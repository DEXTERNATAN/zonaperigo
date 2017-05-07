package dominando.android.ex01_hello;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import static android.R.attr.onClick;
import static dominando.android.ex01_hello.Conexao.postDados;

public class RegistrarOcorrecia extends AppCompatActivity {

    EditText data;
    EditText hora;
    EditText latitude;
    EditText longitude;
    EditText descricao;
    Button adicionaBanco;
    Button cancelar;
    String marcacoes;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_ocorrecia);

        data = (EditText) findViewById(R.id.editText2);
        hora = (EditText) findViewById(R.id.editText3);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        descricao = (EditText) findViewById(R.id.editText4);
        adicionaBanco = (Button) findViewById(R.id.Banco);
        cancelar = (Button) findViewById(R.id.Cancelar);

        Intent it = getIntent();

        IniciaMarcadores iniciaMarcadores = (IniciaMarcadores) it.getSerializableExtra("localizacao");
        latitude.setText(iniciaMarcadores.latitude);
        longitude.setText(iniciaMarcadores.longitude);


        adicionaBanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String marcacoes1 = latitude.getText().toString();
                String marcacoes2 = longitude.getText().toString();
                String Pegadata = data.getText().toString();
                String pegahora = hora.getText().toString();
                String pegaDescricao = descricao.getText().toString();

                if(Pegadata.isEmpty() || pegahora.isEmpty() || pegaDescricao.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Data e Hora não podem ser vazios",Toast.LENGTH_LONG).show();
                }else{

                    url = "http://192.168.43.250:80/registroMarcadores.php";

                    marcacoes = "latitude=" + marcacoes1 + "&longitude=" + marcacoes2 + "&data=" + Pegadata +" &horario=" +
                            pegahora + "&descricao=" + pegaDescricao ;

                    new SolicitaDados().execute(url);

                    Intent MarcadorAdicionado = new Intent(RegistrarOcorrecia.this,MapaActivity.class);
                    RegistrarOcorrecia.this.startActivity(MarcadorAdicionado);
                    RegistrarOcorrecia.this.finish();
                    Toast.makeText(RegistrarOcorrecia.this,"Marcador cadastrado",Toast.LENGTH_LONG).show();


                }



            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltar = new Intent(RegistrarOcorrecia.this,MapaActivity.class);
                RegistrarOcorrecia.this.startActivity(voltar);
                RegistrarOcorrecia.this.finish();
            }
        });


    }






    class SolicitaDados extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            return postDados(urls[0], marcacoes);

        }


    }
}

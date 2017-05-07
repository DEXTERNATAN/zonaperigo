package dominando.android.ex01_hello;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

import static dominando.android.ex01_hello.Conexao.postDados;
import static dominando.android.ex01_hello.R.id.add;
import static dominando.android.ex01_hello.R.id.map;


public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    String url;
    String marcacoes;
    LocalizacaoTask mTask;
    List<IniciaMarcadores> mLocalizacao;
    private AlertDialog alerta;
    Button mapa1;
    Button mapa2;
    Button mapa3;
    String data;
    String horario;
    String descricao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        mLocalizacao = new ArrayList<IniciaMarcadores>();
        mapa1 = (Button) findViewById(R.id.terreno);
        mapa2 = (Button) findViewById(R.id.hibrido);
        mapa3 = (Button) findViewById(R.id.normal);
        mapa1.setOnClickListener(this);
        mapa2.setOnClickListener(this);
        mapa3.setOnClickListener(this);


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.terreno:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:

        }
    }


    class LocalizacaoTask extends AsyncTask<Void, Void, List<IniciaMarcadores>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<IniciaMarcadores> doInBackground(Void... strings) {
            return CarregaMarcadores.carregarLocalizacaoJson();
        }

        @Override
        protected void onPostExecute(List<IniciaMarcadores> localizacoes) {
            super.onPostExecute(localizacoes);

            if (localizacoes != null) {
                mLocalizacao.clear();
                mLocalizacao.addAll(localizacoes);
                for (int i = 0; i < mLocalizacao.size(); i++) {

                    IniciaMarcadores pegandoDados = mLocalizacao.get(i);
                    Double recebeLatitude = Double.parseDouble(pegandoDados.latitude);
                    Double rebebeLongitude = Double.parseDouble(pegandoDados.longitude);
                    String recebeData = pegandoDados.data;
                    String recebehorario = pegandoDados.horario;
                    String recebeDescricao = pegandoDados.descricao;
                    LatLng latLng = new LatLng(recebeLatitude, rebebeLongitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Descricão:" + recebeDescricao).
                            snippet("Dia da ocorrência:" + recebeData).icon(BitmapDescriptorFactory.fromResource(R.drawable.femenine)));


                }


            }
        }
    }


    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;


        if (location != null || !location.equals("")) {

            Geocoder geocoder = new Geocoder(this);

            try {


                addressList = geocoder.getFromLocationName(location, 1);



            } catch (IOException e) {
                e.printStackTrace();
            }


            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }else {
            Toast.makeText(MapaActivity.this,"Campo não pode estar vazio",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mTask = new LocalizacaoTask();
        mTask.execute();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-15.8134, -48.1044), 16));
        LatLng brasilia = new LatLng(-15.8134, -48.1044);
        mMap.addMarker(new MarkerOptions().position(brasilia).title("Ceilândia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(brasilia));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng latLng) {


                if (mMap.addMarker(new MarkerOptions().position(latLng)) != null) {


                    try {

                        mMap.addMarker(new MarkerOptions().position(latLng));

                        String marcacoes1 = latLng.toString().replaceAll("lat/lng:", "").replaceAll("[(]", "").
                                replaceAll("[)]", "").replaceAll(",", "").substring(0, 17);

                        String marcacoes2 = latLng.toString().replaceAll("lat/lng:", "").replaceAll("[(]", "").
                                replaceAll("[)]", "").replaceAll(",", "").substring(20, 38);


                        // url = "http://192.168.15.7:8090/registroMarcadores.php";

                        // marcacoes = "latitude=" + marcacoes1 + "&longitude=" + marcacoes2;

                        //new SolicitaDados().execute(url);

                        Intent its = new Intent(MapaActivity.this, RegistrarOcorrecia.class);
                        its.putExtra("localizacao", new IniciaMarcadores(marcacoes1, marcacoes2, data, horario, descricao));
                        MapaActivity.this.startActivity(its);
                        MapaActivity.this.finish();

                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }


                }


            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    class SolicitaDados extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            return postDados(urls[0], marcacoes);

        }


        @Override
        protected void onPostExecute(String resultado) {

            //login.setText(resultado);

            //if(resultado.contains("login_ok")){
            //Intent TelaLogado = new Intent(LoginActivity.this,MapaActivity.class);
            //LoginActivity.this.startActivity(TelaLogado);
            //LoginActivity.this.finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}









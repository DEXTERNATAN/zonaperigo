package dominando.android.ex01_hello;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scalified.fab.ActionButton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

import static dominando.android.ex01_hello.Conexao.postDados;
import static dominando.android.ex01_hello.R.id.map;


public class Mapa_visualizacao extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, View.OnClickListener{

    public LocalizacaoTask mTask;
    public Button mapa1, mapa2, mapa3, mapa4, mapa5;
    public String url, marcacoes, data, horario;
    private EditText lcOrigem;
    private EditText lcDestino;
    public List<IniciaMarcadores> mLocalizacao;
    private AlertDialog alerta;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_visualizacao);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        mLocalizacao = new ArrayList<IniciaMarcadores>();
        mapa1 = (Button) findViewById(R.id.terreno);
        mapa2 = (Button) findViewById(R.id.hibrido);
        mapa3 = (Button) findViewById(R.id.normal);
        mapa4 = (Button) findViewById(R.id.BotaoRegistrar);
        mapa5 = (Button) findViewById(R.id.BotaoLogin);

        lcOrigem = (EditText) findViewById(R.id.lcOrigem);
        lcDestino = (EditText) findViewById(R.id.lcDestino);
        //ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        mapa1.setOnClickListener(this);
        mapa2.setOnClickListener(this);
        mapa3.setOnClickListener(this);
        mapa4.setOnClickListener(this);
        mapa5.setOnClickListener(this);

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
            case R.id.BotaoRegistrar:
                Intent Registro = new Intent(Mapa_visualizacao.this,RegisterActivity.class);
                Mapa_visualizacao.this.startActivity(Registro);
                Mapa_visualizacao.this.finish();
                break;
            case R.id.BotaoLogin:
                Intent Login = new Intent(Mapa_visualizacao.this,LoginActivity.class);
                Mapa_visualizacao.this.startActivity(Login);
                Mapa_visualizacao.this.finish();
                break;
            default:

        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Por favor! Aguarde.",
                "Procurando rotas..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
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

        String origem = lcOrigem.getText().toString();
        String destino = lcDestino.getText().toString();

        //EditText locationSearch = (EditText) findViewById(R.id.editText);
        //String location = locationSearch.getText().toString();
        //List<Address> addressList = null;

        if(origem.isEmpty()){
            Toast.makeText(this,"Por favor informe o local de partida!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(destino.isEmpty()){
            Toast.makeText(this,"Por favor informe o local de destino!",Toast.LENGTH_SHORT).show();
            return;
        }

        /*if (location != null || !location.equals("")) {
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
        }*/

        try {
            new DirectionFinder(this, origem, destino).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mTask = new LocalizacaoTask();
        mTask.execute();

        LatLng brasilia = new LatLng(-15.8134, -48.1044);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasilia, 18));
        //mMap.addMarker(new MarkerOptions().position(brasilia).title("Ceilândia"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(brasilia));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
        .title("Ceilândia")
        .position(brasilia)
        ));

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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




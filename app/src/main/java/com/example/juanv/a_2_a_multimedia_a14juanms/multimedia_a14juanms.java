package com.example.juanv.a_2_a_multimedia_a14juanms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;

public class multimedia_a14juanms extends Activity {

    Spinner spAudio;
    Button btnReproducir, btnGravar, btnFoto;
    ArrayList<String> canciones;
    MediaPlayer mediaplayer = new MediaPlayer();//creamos o obxeto Mediaplayer
    MediaRecorder mediaRecorder = new MediaRecorder();//creamos o obxeto MediaRecorder
    String cancionSeleccionada;
    private boolean pause;
    private static final int REQUEST_CODE_FOTO_OK = 5;
    ImageView img;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia_a14juanms);
        spAudio = (Spinner) findViewById(R.id.spAudio);
        btnFoto = (Button) findViewById(R.id.btnFoto);
        btnReproducir = (Button) findViewById(R.id.btnReproducir);
        btnGravar = (Button) findViewById(R.id.btnGravar);
        img = (ImageView) findViewById(R.id.imgFoto);

        Log.i("carpeta por defecto", Environment.getExternalStorageDirectory().toString());


        File faudios = new File(Environment.getExternalStorageDirectory() + "/UD-A2A/MUSICA/");
        Log.i("route", (Environment.getExternalStorageDirectory() + "/UD-A2A/MUSICA/"));
        // Fonte de datos
        canciones = new ArrayList<String>();
        String[] audios = faudios.list();
        for (int i = 0; i < audios.length; i++) canciones.add(audios[i]);

        // Enlace do adaptador cos datos
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canciones);

        // Opcional: layout usuado para representar os datos no Spinner
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Enlace do adaptador co Spinner do Layout.
        spAudio.setAdapter(adaptador);

        // Escoitador
        spAudio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Toast.makeText(getBaseContext(), "Seleccionaches: " + ((TextView) view).getText(), Toast.LENGTH_LONG).show();
                cancionSeleccionada = canciones.get(pos).toString();
                Log.i("cancion", cancionSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        }); // Fin da clase anónima

        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UD-A2A/MUSICA/" + cancionSeleccionada;
                    Log.i("ruta", path);
                    //Uri uri = Uri.parse(Uri.encode(path));
                    mediaplayer.reset();
                    mediaplayer.setDataSource(path);
                    mediaplayer.prepare();
                    mediaplayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button btnParar = (Button) findViewById(R.id.btnPara);
        btnParar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mediaplayer.isPlaying())
                    mediaplayer.stop();
                pause = false;
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intento, REQUEST_CODE_FOTO_OK);
            }
        });



        btnGravar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mediaRecorder = new MediaRecorder();
                File arquivoGravar = new File(Environment.getExternalStorageDirectory()+"/UD-A2A/MUSICA/" + "record.3gp");
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setMaxDuration(10000);
                mediaRecorder.setAudioEncodingBitRate(32768);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setOutputFile(arquivoGravar.toString());
                try {
                    mediaRecorder.prepare();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    mediaRecorder.reset();
                }
                mediaRecorder.start();
                abrirDialogo("GRAVAR");
                adaptador.notifyDataSetChanged();

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaplayer.isPlaying()) {
            mediaplayer.pause();
            pause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pause) {
            mediaplayer.start();
            pause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaplayer.isPlaying()) mediaplayer.stop();

        if (mediaplayer != null) mediaplayer.release();
        mediaplayer = null;

    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        estado.putBoolean("MEDIAPLAYER_PAUSE", pause);
        super.onSaveInstanceState(estado);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("MEDIAPLAYER_PAUSE", false);
        pause = savedInstanceState.getBoolean("MEDIAPLAYER_PAUSE");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOTO_OK) {
            Bitmap bitMap = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bitMap);
        }
    }

    private void abrirDialogo(String tipo) {
        if (tipo == "GRAVAR") {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setMessage("GRAVANDO").setPositiveButton(
                            "PREME PARA PARAR",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    mediaRecorder.stop();
                                    mediaRecorder.release();
                                    mediaRecorder = null;
                                }
                            });
            dialog.show();
        }
    }
}

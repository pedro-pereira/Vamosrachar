package br.ufc.smd.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class VamosRacharActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, TextToSpeech.OnInitListener {

    private EditText edtValor;
    private EditText edtQuantidadePessoas;
    private TextView txtResultado;
    private FloatingActionButton fbtCompartilhar;
    private FloatingActionButton fbtAudio;
    private TextToSpeech tts;
    private DecimalFormat formatador;
    private Double resultado;
    private Locale loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtValor             = (EditText) findViewById(R.id.edtValor);
        edtQuantidadePessoas = (EditText) findViewById(R.id.edtQuantidadePessoas);
        txtResultado         = (TextView) findViewById(R.id.txtResultado);
        fbtCompartilhar      = (FloatingActionButton) findViewById(R.id.fbtCompartilhar);
        fbtAudio             = (FloatingActionButton) findViewById(R.id.fbtAudio);

        edtValor.addTextChangedListener(this);
        edtQuantidadePessoas.addTextChangedListener(this);

        fbtAudio.setOnClickListener(this);
        fbtCompartilhar.setOnClickListener(this);

        formatador = new DecimalFormat("0,00");

        loc = new Locale("pt", "BR");
        tts = new TextToSpeech (this, this);
        tts.setLanguage(Locale.UK);
    }

    private boolean campoEstaVazio(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            double valor = 0;
            if(!campoEstaVazio(edtValor) && !campoEstaVazio(edtQuantidadePessoas)) {
                Double resultado = calculaValor(edtValor.getText().toString(), edtQuantidadePessoas.getText().toString());
                Log.i("TESTE - Resultado", formatador.format(resultado * 100));
                txtResultado.setText("R$ " + formatador.format(resultado * 100));
            } else {
                if(campoEstaVazio(edtValor)) {
                    txtResultado.setText("Informe o valor da conta...");
                }
                if(campoEstaVazio(edtQuantidadePessoas)) {
                    txtResultado.setText("Informe a quantidade de pessoas para dividir a conta...");
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onClick(View v) {

        if(v == fbtAudio) {
            tts.speak(txtResultado.getText().toString(), TextToSpeech.QUEUE_ADD, null, null);
            Log.i("TESTE TTS", "cliquei no botao de audio");
        }


        if (v == fbtCompartilhar) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "O valor para cada pessoa é R$ " + resultado);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            // To Choose language of speech
            tts.setLanguage(Locale.UK);
        }
    }

    public Double calculaValor(String valor, String quantidadePessoas) {
        double conta = Double.valueOf(valor);
        int pessoas = Integer.valueOf(quantidadePessoas);

        if(pessoas < 1)
            pessoas = 1;

        double resultado = conta / pessoas;
        this.resultado = resultado;
        return resultado;
    }
}
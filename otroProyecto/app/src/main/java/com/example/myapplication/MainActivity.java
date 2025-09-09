package com.example.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    private String mensajeEscribir = "Hola estas NFC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            mTextView.setText("NFC no soportado en este dispositivo");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            writeNfcMessage(tag, mensajeEscribir);
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            readNfcMessage(intent);
        }
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        IntentFilter[] intentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    private void writeNfcMessage(Tag tag, String message) {
        NdefRecord record = NdefRecord.createTextRecord("en", message);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (ndef.isWritable()) {
                    ndef.writeNdefMessage(ndefMessage);
                    Toast.makeText(this, "Mensaje NFC escrito correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Etiqueta NFC no es escribible", Toast.LENGTH_SHORT).show();
                }
                ndef.close();
            } else {
                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    formatable.connect();
                    formatable.format(ndefMessage);
                    Toast.makeText(this, "Etiqueta NFC formateada y escrita", Toast.LENGTH_SHORT).show();
                    formatable.close();
                } else {
                    Toast.makeText(this, "Etiqueta NFC no soportada", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException | NullPointerException e) {
            Toast.makeText(this, "Error al escribir NFC", Toast.LENGTH_SHORT).show();
        } catch (FormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void readNfcMessage(Intent intent) {
        NdefMessage[] messages = (NdefMessage[]) intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (messages != null && messages.length > 0) {
            String message = new String(messages[0].getRecords()[0].getPayload(), Charset.forName("UTF-8"));
            mTextView.setText("Mensaje NFC recibido: " + message);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "Mensaje desde otro dispositivo NFC";
        NdefRecord record = NdefRecord.createTextRecord("en", message);
        return new NdefMessage(new NdefRecord[]{record});
    }
}

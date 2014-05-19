package com.chachat.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;
import com.silva.managers.UserManager;
import com.silva.objects.User;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private EditText edtUsername;
    private Button btnEnter;

    private SocketIOClient mSocket;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        edtUsername = (EditText) findViewById(R.id.main_username);
        btnEnter = (Button) findViewById(R.id.main_enter_btn);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edtUsername.getText().toString();

                if (mSocket != null) {
                    mSocket.disconnect();
                    Log.d("SOCKET SERVER LOG", "PREVIOUS SOCKET DISCONNECTED");
                }

                getSocketIoInstance(username);

            }
        });

    }

    private void getSocketIoInstance(final String name) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setTitle("ChitChat");
        pd.setMessage("Connecting to server...");
        pd.show();

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://128.199.225.219:3000", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient socketIOClient) {

                mSocket = socketIOClient;

                pd.dismiss();

                if (ex != null) {
                    ex.printStackTrace();
                    Log.d("SOCKET IO SERVER EXCEPTION", "" + "Error Connecting to server!");
                    return;
                }

                if (socketIOClient.isConnected()) {
                    join(name, socketIOClient);

                    User user = new User();
                    user.setName(name);
                    UserManager.saveUser(user, mContext);

                } else {
                    Log.d("SOCKET IO JOIN GROUP", "" + "Not Connected");
                }

                socketIOClient.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        System.out.println(string);
                        Log.d("SOCKET IO STRING CALLBACK", "" + string);
                    }
                });
                socketIOClient.on("event", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + argument.toString());
                        Log.d("SOCKET IO SOME EVENTS", "" + argument.toString());
                    }
                });
                socketIOClient.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject jsonObject, Acknowledge acknowledge) {
                        System.out.println("json: " + jsonObject.toString());
                        Log.d("SOCKET IO JSON CALLBACK", "" + jsonObject.toString());
                    }
                });

                Intent intent = new Intent(mContext, ChatActivity.class);
                startActivity(intent);

            }
        });
    }

    private void join(String nickname, SocketIOClient socket) {


        Gson gson = new Gson();
        String jsonObject = gson.toJson(nickname);
        socket.emit(jsonObject);

        Log.d("SOCKET IO USER MESSAGE", "" + jsonObject);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

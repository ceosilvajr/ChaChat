package com.chachat.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;
import com.silva.managers.UserManager;
import com.silva.objects.Message;
import com.silva.objects.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;


public class ChatActivity extends ActionBarActivity {

    private EditText mEditMessage;
    private Button mBtnSend;

    private Context mContext;

    private SocketIOClient mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = this;

        final User user = UserManager.getUser(mContext);

        mEditMessage = (EditText) findViewById(R.id.chat_message);
        mBtnSend = (Button) findViewById(R.id.chat_send_btn);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSocket != null) {
                    mSocket.disconnect();
                    mSocket = null;
                }

                String textMessage = mEditMessage.getText().toString();

                Message message = new Message();
                message.setCreatedDate(new Date(System.currentTimeMillis()));
                message.setUser(user);
                message.setText(textMessage);

                getSocketIoInstance(message);
            }
        });
    }

    private void getSocketIoInstance(final Message message) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setTitle("ChitChat");
        pd.setMessage("Sending message.");
        pd.show();

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://128.199.225.219:3000", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient socketIOClient) {

                pd.dismiss();

                if (ex != null) {
                    ex.printStackTrace();
                    // Toast.makeText(mContext, "Error connecting to server", Toast.LENGTH_LONG).show();
                    return;
                }

                mSocket = socketIOClient;

                if (socketIOClient.isConnected()) {
                    Log.d("SOCKET IO", "USER CONNECTED");

                    sendMessage(message, socketIOClient);

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

                socketIOClient.on("data", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + argument.toString());
                        Log.d("SOCKET IO SOME EVENTS USER RECEIVED MESSAGE", "" + argument.toString());
                    }
                });

                socketIOClient.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject jsonObject, Acknowledge acknowledge) {
                        System.out.println("json: " + jsonObject.toString());
                        Log.d("SOCKET IO JSON CALLBACK", "" + jsonObject.toString());
                    }
                });

            }
        });
    }

    private void sendMessage(Message message, SocketIOClient socket) {

        Gson gson = new Gson();
        String jsonObject = gson.toJson(message);
        socket.emit(jsonObject);
        Log.d("SOCKET IO USER MESSAGE", "" + jsonObject);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
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

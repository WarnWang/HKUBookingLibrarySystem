package hk.hku.cs.warn.hkubookinglibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author：warn on 11/13/15 23:03
 * Email：wangyouan@gmail.com
 */

public class LibrarySystem {

    private Context context;
    private BookingSeat bookingSeat;
    private HttpMethod httpCtrl;

    public LibrarySystem(Context context, BookingSeat bookingSeat) {
        this.context = context;
        this.bookingSeat = bookingSeat;
        httpCtrl = new HttpMethod();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean login() throws IOException {
        boolean loginResult = false;

        try {
            // Get the login info
            httpCtrl.get(context.getResources().getString(R.string.booking_url), new HashMap<String, String>());

            // Post the user info
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("userid", context.getString(R.string.username));
            userInfo.put("password", context.getString(R.string.password));
            userInfo.put("x", "0");
            userInfo.put("y", "0");
            userInfo.put("uri", context.getString(R.string.login_uri));
            userInfo.put("formaction", "auth");
            HttpContent postContent = httpCtrl.post(context.getString(R.string.login_url),
                    userInfo);

            List<String> location = (List<String>) postContent.getHeaders().get(context
                    .getString(R.string.location_header));

            httpCtrl.get(location.get(0), new HashMap<String, String>());
            loginResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            loginResult = false;
        }
        return loginResult;
    }

    private void getSingleStudyRoom() {

    }

    private void getStudyTable() {

    }

    public AlertDialog createAlertDialog(String message, String title,
                                         DialogInterface.OnClickListener positiveAction,
                                         DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(bookingSeat)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, positiveAction)
                .setNegativeButton(android.R.string.cancel, negativeAction);
        return builder.create();
    }

    public boolean bookASeat() throws IOException {

        boolean bookResult = false;

        if (this.checkNetworkConnection()) {

            // this means this mobile phone has a network connection
            if (this.login()) {
                this.getSingleStudyRoom();
                this.getStudyTable();
            } else {
                AlertDialog loginFail = createAlertDialog(context.getResources()
                                .getString(R.string.login_fail),
                        context.getResources().getString(R.string.error_info),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                loginFail.show();
            }
        } else {
            this.createAlertDialog(context.getResources().getString(R.string.no_network_connection),
                    context.getResources().getString(R.string.error_info),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
        }
        return bookResult;
    }
}

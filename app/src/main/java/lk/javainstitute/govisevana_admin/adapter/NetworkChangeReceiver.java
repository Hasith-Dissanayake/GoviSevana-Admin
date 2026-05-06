package lk.javainstitute.govisevana_admin.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static NetworkChangeListener networkChangeListener;

    public static void setNetworkChangeListener(NetworkChangeListener listener) {
        networkChangeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (networkChangeListener != null) {
            networkChangeListener.onNetworkChange(isConnected(context));
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }
}

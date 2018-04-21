package be.ugent.zeus.hydra.minerva.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ChannelCreator;
import com.google.android.gms.auth.api.accounttransfer.AccountTransfer;
import com.google.android.gms.auth.api.accounttransfer.AccountTransferClient;
import com.google.android.gms.auth.api.accounttransfer.AuthenticatorTransferCompletionStatus;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Responsible for doing the actual account transfers.
 *
 * @author Niko Strijbol
 * @see <a href="https://developer.android.com/guide/topics/data/account-transfer.html">The API Guide</a>
 */
public class AccountTransferService extends IntentService {

    private static final String TAG = "AccountTransferService";
    private static final int NOTIFICATION_ID = 1;
    private static final int ACCOUNT_TRANSFER_PROTOCOL_VERSION = 1;

    public AccountTransferService() {
        super(TAG);
    }

    public static Intent getIntent(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(context, AccountTransferService.class);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create a notification if necessary, to keep the service alive.
        this.createNotification();
    }

    /**
     * Create a notification if necessary, to keep the service alive.
     */
    private void createNotification() {
        // Create the channel if needed.
        ChannelCreator channelCreator = ChannelCreator.getInstance(this);
        channelCreator.createMinervaAccountChannel();
        Notification notification = new NotificationCompat.Builder(this, ChannelCreator.MINERVA_ACCOUNT_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.minerva_exporting_account))
                .setContentTitle(getString(R.string.minerva_exporting_account))
                .setContentText(getText(R.string.minerva_exporting_account_desc))
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.w(TAG, "onHandleIntent: action was null, aborting.");
            return;
        }
        switch (action) {
            case AccountTransfer.ACTION_ACCOUNT_IMPORT_DATA_AVAILABLE:
                this.importAccount();
                break;
            case AccountTransfer.ACTION_START_ACCOUNT_EXPORT:
            case AccountTransfer.ACTION_ACCOUNT_EXPORT_DATA_AVAILABLE:
                this.exportAccount();
                break;
            default:
                Log.w(TAG, "onHandleIntent: unknown action: " + action);
        }
    }

    /**
     * Export the account.
     */
    private void exportAccount() {
        AccountManager accountManager = AccountManager.get(this);
        AccountTransferClient client = AccountTransfer.getAccountTransferClient(this);
        Account[] accounts = accountManager.getAccountsByType(MinervaConfig.ACCOUNT_TYPE);
        if (accounts.length < 1) {
            // If there is no account, we send nothing and are done.
            client.notifyCompletion(MinervaConfig.ACCOUNT_TYPE, AuthenticatorTransferCompletionStatus.COMPLETED_SUCCESS);
            return;
        }

        // Create the object and turn it into bytes.
        Account account = accounts[0];
        AccountInformation information = new AccountInformation();
        information.accountType = account.type;
        information.accountVersion = ACCOUNT_TRANSFER_PROTOCOL_VERSION;
        information.accountName = account.name;
        information.accountPassword = accountManager.getPassword(account);

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<AccountInformation> informationJsonAdapter = moshi.adapter(AccountInformation.class);
        byte[] bytes = informationJsonAdapter.toJson(information).getBytes(Charset.forName("UTF-8"));
        // Send the data over to the other device.
        Task<Void> exportTask = client.sendData(MinervaConfig.ACCOUNT_TYPE, bytes);

        try {
            // We wait 10 seconds, otherwise we fail.
            Tasks.await(exportTask, 10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.w(TAG, "Failure while exporting account.", e);
            client.notifyCompletion(MinervaConfig.ACCOUNT_TYPE, AuthenticatorTransferCompletionStatus.COMPLETED_FAILURE);
        }
    }

    private void importAccount() {

        AccountTransferClient client = AccountTransfer.getAccountTransferClient(this);

        Task<byte[]> importTask = client.retrieveData(MinervaConfig.ACCOUNT_TYPE);

        try {
            byte[] data = Tasks.await(importTask, 10, TimeUnit.SECONDS);
            importAccountFrom(this, data);
            client.notifyCompletion(MinervaConfig.ACCOUNT_TYPE, AuthenticatorTransferCompletionStatus.COMPLETED_SUCCESS);
        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            Log.w(TAG, "Failure while importing account.", e);
            client.notifyCompletion(MinervaConfig.ACCOUNT_TYPE, AuthenticatorTransferCompletionStatus.COMPLETED_FAILURE);
        }
    }

    /**
     * Collects the information we send to the new device.
     *
     * As of version 1, we send following information:
     *   - The account type as an extra check.
     *   - The version of the transfer object.
     *   - The name of the account.
     *   - The password, containing the refresh token.
     *
     * The access token and related expiration date are not sent.
     */
    private static class AccountInformation {
        private String accountType;
        private int accountVersion;
        private String accountName;
        private String accountPassword;
    }

    /**
     * Import an account from given bytes. This method does just that, and nothing else.
     *
     * @param context The context.
     * @param data The data. Can be null.
     */
    private static void importAccountFrom(Context context, byte[] data) throws IOException {
        if (data == null) {
            Log.w(TAG, "Data bytes are null, aborting import.");
            return;
        }

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<AccountInformation> informationJsonAdapter = moshi.adapter(AccountInformation.class);
        AccountInformation information = informationJsonAdapter.fromJson(new String(data, Charset.forName("UTF-8")));
        if (information == null) {
            throw new IOException("Wrong data.");
        }
        AccountManager accountManager = AccountManager.get(context);
        if (information.accountVersion != ACCOUNT_TRANSFER_PROTOCOL_VERSION) {
            Log.w(TAG, "Protocol version does not match, aborting import.");
            return;
        }
        if (!MinervaConfig.ACCOUNT_TYPE.equals(information.accountType)) {
            Log.w(TAG, "Unknown account type, aborting import.");
            return;
        }

        Account recovered = new Account(information.accountName, information.accountType);
        accountManager.addAccountExplicitly(recovered, information.accountPassword, null);
    }
}
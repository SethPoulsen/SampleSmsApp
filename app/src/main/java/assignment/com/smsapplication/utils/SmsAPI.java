package assignment.com.smsapplication.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;


import assignment.com.smsapplication.sms.model.Sms;
import assignment.com.smsapplication.sms.model.SmsResponse;
import io.reactivex.Observable;

public class SmsAPI {
    private Context context;
    private long currentTime = 0L;
    private int count = -1;
    private boolean countHoursAgo = true;

    public SmsAPI(Context context) {
        this.context = context;
    }

    public Observable<SmsResponse> fetchAllInboxSms() {
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, null,
                null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        int totalSMS = 0;
        List<Sms> smsList = new ArrayList<>();
        currentTime = System.currentTimeMillis();
        count = 0;
        countHoursAgo = true;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    Sms sms = new Sms();
                    sms.setDate(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE)));
                    sms.setSender(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS)));
                    sms.setMessage(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)));
                    sms.setRead(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.READ)));
                    String hourAgo = getHoursAgo(c.getString(c.getColumnIndexOrThrow(Telephony.
                            Sms.Inbox.DATE)));
                    sms.setHoursAgo(hourAgo);
                    c.moveToNext();

                    if (sms.getSender().contains("phone number here")) {
                        smsList.add(sms);
                    }
//                    smsList.add(sms);
                }
            }
            c.close();

            Gson gson = new Gson();
            String messagesJson = gson.toJson(smsList);
            Log.d("SethDebug", messagesJson);

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outFile = new File(downloads, "text-message-log.json");

            Log.d("sethdebug", outFile.toString());
//            outFile.delete();

            try(FileOutputStream fos = new FileOutputStream(outFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                //convert string to byte array
                byte[] bytes = messagesJson.getBytes();
                //write byte array to file
                bos.write(bytes);
                bos.close();
                fos.close();
                Log.d("SethDebug", "Data written to file successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Log.i("Send email", "");
//            String[] TO = {""};
//            String[] CC = {""};
//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//
//            emailIntent.setData(Uri.parse("mailto:poulsenseth@yahoo.com"));
//            emailIntent.setType("text/plain");
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//            emailIntent.putExtra(Intent.EXTRA_CC, CC);
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Jeff Journal");
//            emailIntent.putExtra(Intent.EXTRA_TEXT, messagesJson);
//
//            try {
//                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//                finish();
//                Log.i("Finished sending email...", "");
//            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
//            }

        } else {
            Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
        }
        return getSmsResponse(smsList);
    }


    public String fetchAllInboxSmsJson() {
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, null,
                null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        int totalSMS = 0;
        List<Sms> smsList = new ArrayList<>();
        currentTime = System.currentTimeMillis();
        count = 0;
        countHoursAgo = true;
        String messagesJson = "[]";
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    Sms sms = new Sms();
                    sms.setDate(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE)));
                    sms.setSender(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS)));
                    sms.setMessage(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)));
                    sms.setRead(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.READ)));
                    String hourAgo = getHoursAgo(c.getString(c.getColumnIndexOrThrow(Telephony.
                            Sms.Inbox.DATE)));
                    sms.setHoursAgo(hourAgo);
                    c.moveToNext();
                    smsList.add(sms);
                }
            }
            c.close();

            Gson gson = new Gson();
            messagesJson = gson.toJson(smsList);
            Log.d("SethDebug", messagesJson);

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outFile = new File(downloads, "text-message-log.json");

//            outFile.delete();

//            try(FileOutputStream fos = new FileOutputStream(outFile);
//                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
//                //convert string to byte array
//                byte[] bytes = messagesJson.getBytes();
//                //write byte array to file
//                bos.write(bytes);
//                bos.close();
//                fos.close();
//                Log.d("SethDebug", "Data written to file successfully.");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        } else {
            Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
        }
        return messagesJson;
    }

    private String getHoursAgo(String time) {
        if(countHoursAgo) {
            long diff = currentTime - Long.valueOf(time);
            int hours = (int) diff / (60000 * 60);
            if (hours < 13 && count != hours) {
                count = hours;
                return hours + "hours ago";
            } else if (hours > 24 && count != hours) {
                countHoursAgo = false;
                return 1 + "day ago";
            }
        }
        return "";
    }

    private Observable<SmsResponse> getSmsResponse(List<Sms> smsList) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSmsList(smsList);
        return Observable.just(smsResponse);
    }

}

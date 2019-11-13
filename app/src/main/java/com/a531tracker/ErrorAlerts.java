package com.a531tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Layout;
import android.view.Gravity;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ErrorAlerts extends AlertDialog.Builder {

    private String message;
    private String title;
    private boolean cancelable;
    private boolean negButton;
    private String extraMessageValue;
    private boolean failedLift;

    public ErrorAlerts(Context context) {
        super(context);
    }

    public void setErrorAlertsValues(boolean setNeg, boolean cancelable, String title, String message, String extraMessage, boolean failedLift){
        setMessage(message);
        setTitle(title);
        setCancelableStatus(cancelable);
        setNegButton(setNeg);
        setExtraMessageValue(extraMessage);
        setFailedLift(failedLift);
    }

    public AlertDialog.Builder preformattedAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setMessage(getMessage())
                .setTitle(getTitle())
                .setCancelable(isCancelable())
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        if(failedLift){
            TextView tv = new TextView(context);
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlue));
            tv.setText(getExtraMessageValue());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tv.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
            builder.setView(tv);
        }

        if(isNegButton()) {
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder;
    }

    public AlertDialog.Builder blankAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setMessage(getMessage())
                .setTitle(getTitle())
                .setCancelable(isCancelable());

        if(isNegButton()) {
            builder.setNeutralButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder;
    }


    private void setMessage(String message){
        this.message = message;
    }

    private String getMessage(){
        return message;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private String getTitle() {
        return title;
    }

    private void setCancelableStatus(boolean bool){
        this.cancelable = bool;
    }

    private boolean isCancelable() {
        return cancelable;
    }

    private void setNegButton(boolean negButton) {
        this.negButton = negButton;
    }

    private boolean isNegButton() {
        return negButton;
    }

    private void setExtraMessageValue(String extraMessageValue) {
        this.extraMessageValue = extraMessageValue;
    }

    private String getExtraMessageValue() {
        return extraMessageValue;
    }

    private void setFailedLift(boolean failedLift) {
        this.failedLift = failedLift;
    }
}
